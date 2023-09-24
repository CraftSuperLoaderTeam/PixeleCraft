package com.pixelecraft.nc.network;

import com.google.common.collect.Lists;
import com.pixelecraft.nc.craft.CraftServer;
import com.pixelecraft.nc.crash.CrashReport;
import com.pixelecraft.nc.crash.CrashReportCategory;
import com.pixelecraft.nc.network.controller.LegacyPingHandle;
import com.pixelecraft.nc.network.controller.PacketDecoder;
import com.pixelecraft.nc.network.controller.PacketEncoder;
import com.pixelecraft.nc.network.controller.PacketPrepender;
import com.pixelecraft.nc.network.controller.PacketSplitter;
import com.pixelecraft.nc.network.packet.PacketDisconnect;
import com.pixelecraft.nc.network.packet.handle.HandshakeTCP;
import com.pixelecraft.nc.util.ReportedException;
import com.pixelecraft.nc.util.text.ChatComponentString;
import io.github.csl.logging.Logger;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Level;

public class NetworkServer extends ChannelInitializer<SocketChannel> {
    private static final Logger log = Logger.getLogger(NetworkServer.class.getSimpleName());
    private final List<NetworkManager> networkManagers = Collections.<NetworkManager>synchronizedList(Lists.<NetworkManager>newArrayList());
    EventLoopGroup bossGroup, workerGroup;
    ServerBootstrap bootstrap;
    CraftServer server;

    public NetworkServer(CraftServer server, int port) throws IOException {
        this.server = server;
        bootstrap = new ServerBootstrap();
        bossGroup = createBestEventLoopGroup();
        workerGroup = createBestEventLoopGroup();
        bootstrap.group(bossGroup, workerGroup);
        if (Epoll.isAvailable()) {
            log.info("Using epoll channel type.");
            bootstrap.channel(EpollServerSocketChannel.class);
        } else {
            log.info("Using default channel type.");
            bootstrap.channel(NioServerSocketChannel.class);
        }

        bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
        bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.childHandler(this).localAddress(port).bind().syncUninterruptibly();

        log.info("Start server port: " + port);
    }

    public void shutdown() {
        log.info("Closing network IO.");

        bootstrap.group().shutdownGracefully();
        bootstrap.childGroup().shutdownGracefully();

        try {
            bootstrap.group().terminationFuture().sync();
            bootstrap.childGroup().terminationFuture().sync();
        } catch (InterruptedException e) {
            log.log(Level.INFO,"Stopping network has exception:" + e.getLocalizedMessage(), e);
        }
    }


    public Logger getLogger() {
        return log;
    }

    @Override
    protected void initChannel(SocketChannel channel) {
        try {
            try {
                channel.config().setOption(ChannelOption.TCP_NODELAY, Boolean.TRUE);
            } catch (ChannelException ignored) {
                ;
            }
            channel.pipeline().addLast("timeout", new ReadTimeoutHandler(30))
                    .addLast("legacy_query", new LegacyPingHandle(this))
                    .addLast("splitter", new PacketSplitter())
                    .addLast("decoder", new PacketDecoder(ServerProtocolManager.Direction.SERVERBOUND, this))
                    .addLast("perpender", new PacketPrepender())
                    .addLast("encoder", new PacketEncoder(ServerProtocolManager.Direction.CLIENTBOUND, this));

            NetworkManager networkmanager = new NetworkManager(this);
            networkmanager.setNetHandle(new HandshakeTCP(networkmanager, server));
            this.networkManagers.add(networkmanager);
            channel.pipeline().addLast("packet_handler", networkmanager);

        } catch (Exception ignored) {

        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.log(Level.WARNING,"Netty IO has exception:" + cause.getClass().getSimpleName() + ": " + cause.getLocalizedMessage(),cause);
        ctx.close();
    }


    public CraftServer getServer() {
        return server;
    }

    public static EventLoopGroup createBestEventLoopGroup() {
        if (Epoll.isAvailable()) {
            return new EpollEventLoopGroup();
        } else {
            return new NioEventLoopGroup();
        }
    }

    public void networkTick() {
        synchronized (this.networkManagers) {
            Iterator<NetworkManager> iterator = this.networkManagers.iterator();

            while (iterator.hasNext()) {
                final NetworkManager networkmanager = (NetworkManager) iterator.next();

                if (!networkmanager.hasNoChannel()) {
                    if (!networkmanager.isChannelOpen()) {
                        iterator.remove();
                        networkmanager.checkDisconnected();
                    } else {
                        try {
                            networkmanager.processReceivedPackets();
                        } catch (Exception exception) {
                            if (networkmanager.isLocalChannel()) {
                                CrashReport crashreport = CrashReport.makeCrashReport(exception, "Ticking memory connection");
                                CrashReportCategory crashreportcategory = crashreport.makeCategory("Ticking connection");
                                crashreportcategory.addCrashSectionCallable("Connection", new Callable<String>() {
                                    public String call() throws Exception {
                                        return networkmanager.toString();
                                    }
                                });
                                throw new ReportedException(crashreport);
                            }

                            server.getLogger().log(Level.WARNING,(String) ("Failed to handle packet for " + networkmanager.getRemoteAddress()), (Throwable) exception);
                            final ChatComponentString chatcomponenttext = new ChatComponentString("Internal server error");
                            networkmanager.sendPacket(new PacketDisconnect(chatcomponenttext), new GenericFutureListener<Future<? super Void>>() {
                                public void operationComplete(Future<? super Void> p_operationComplete_1_) throws Exception {
                                    networkmanager.closeChannel(chatcomponenttext);
                                }
                            }, new GenericFutureListener[0]);
                            networkmanager.disableAutoRead();
                        }
                    }
                }
            }
        }
    }
}
