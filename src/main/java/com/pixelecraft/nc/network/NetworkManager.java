package com.pixelecraft.nc.network;

import com.google.common.collect.Queues;
import com.pixelecraft.nc.network.controller.NettyCompressionDecoder;
import com.pixelecraft.nc.network.controller.NettyCompressionEncoder;
import com.pixelecraft.nc.network.controller.NettyEncryptingDecoder;
import com.pixelecraft.nc.network.controller.NettyEncryptingEncoder;
import com.pixelecraft.nc.network.packet.handle.ITickable;
import com.pixelecraft.nc.network.packet.handle.NetHandle;
import com.pixelecraft.nc.util.CryptManager;
import com.pixelecraft.nc.util.IChatComponent;
import com.pixelecraft.nc.util.text.ChatComponentString;
import io.netty.channel.*;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.local.LocalServerChannel;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.apache.commons.lang3.ArrayUtils;

import javax.crypto.SecretKey;
import java.net.SocketAddress;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;

public class NetworkManager extends SimpleChannelInboundHandler<ServerProtocolManager.Packet> {
    NetworkServer server;
    Channel channel;
    SocketAddress socketAddress;
    NetHandle netHandle;
    IChatComponent terminationReason;
    private boolean disconnected;
    private final ReentrantReadWriteLock writeLock = new ReentrantReadWriteLock();
    public static final AttributeKey<ServerProtocolManager> attrKeyConnectionState = AttributeKey.<ServerProtocolManager>valueOf("protocol");
    private final Queue<InboundHandlerTuplePacketListener> outboundPacketsQueue = Queues.<NetworkManager.InboundHandlerTuplePacketListener>newConcurrentLinkedQueue();
    private boolean isEncrypted;

    public NetworkManager(NetworkServer server) {
        this.server = server;
    }

    public void setNetHandle(NetHandle netHandle) {
        this.netHandle = netHandle;
    }

    public void sendPacket(ServerProtocolManager.Packet packetIn) {
        if (this.isChannelOpen()) {
            this.flushOutboundQueue();
            this.dispatchPacket(packetIn, null);
        } else {
            this.writeLock.writeLock().lock();

            try {
                this.outboundPacketsQueue.add(new NetworkManager.InboundHandlerTuplePacketListener(packetIn, (GenericFutureListener[]) null));
            } finally {
                this.writeLock.writeLock().unlock();
            }
        }
    }

    public void sendPacket(ServerProtocolManager.Packet packetIn, GenericFutureListener<? extends Future<? super Void>> listener, GenericFutureListener<? extends Future<? super Void>>... listeners) {
        if (this.isChannelOpen()) {
            this.flushOutboundQueue();
            this.dispatchPacket(packetIn, ArrayUtils.add(listeners, 0, listener));
        } else {
            this.writeLock.writeLock().lock();

            try {
                this.outboundPacketsQueue.add(new NetworkManager.InboundHandlerTuplePacketListener(packetIn, ArrayUtils.add(listeners, 0, listener)));
            } finally {
                this.writeLock.writeLock().unlock();
            }
        }
    }

    public void setCompressionThreshold(int threshold) {
        if (threshold >= 0) {
            if (this.channel.pipeline().get("decompress") instanceof NettyCompressionDecoder) {
                ((NettyCompressionDecoder) this.channel.pipeline().get("decompress")).setCompressionThreshold(threshold);
            } else {
                this.channel.pipeline().addBefore("decoder", "decompress", new NettyCompressionDecoder(threshold));
            }

            if (this.channel.pipeline().get("compress") instanceof NettyCompressionEncoder) {
                ((NettyCompressionEncoder) this.channel.pipeline().get("compress")).setCompressionThreshold(threshold);
            } else {
                this.channel.pipeline().addBefore("encoder", "compress", new NettyCompressionEncoder(threshold));
            }
        } else {
            if (this.channel.pipeline().get("decompress") instanceof NettyCompressionDecoder) {
                this.channel.pipeline().remove("decompress");
            }

            if (this.channel.pipeline().get("compress") instanceof NettyCompressionEncoder) {
                this.channel.pipeline().remove("compress");
            }
        }
    }

    public SocketAddress getSocketAddress() {
        return socketAddress;
    }

    public void disableAutoRead() {
        this.channel.config().setAutoRead(false);
    }

    public boolean hasNoChannel() {
        return this.channel == null;
    }

    public void processReceivedPackets() {
        this.flushOutboundQueue();

        if (this.netHandle instanceof ITickable) {
            ((ITickable) this.netHandle).update();
        }

        this.channel.flush();
    }

    public boolean isConnected() {
        return this.channel != null && this.channel.isOpen();
    }

    public boolean getIsencrypted() {
        return this.isEncrypted;
    }

    public void enableEncryption(SecretKey key) {
        this.isEncrypted = true;
        this.channel.pipeline().addBefore("splitter", "decrypt", new NettyEncryptingDecoder(CryptManager.createNetCipherInstance(2, key)));
        this.channel.pipeline().addBefore("prepender", "encrypt", new NettyEncryptingEncoder(CryptManager.createNetCipherInstance(1, key)));
    }

    public boolean isLocalChannel() {
        return this.channel instanceof LocalChannel || this.channel instanceof LocalServerChannel;
    }

    public SocketAddress getRemoteAddress() {
        return this.socketAddress;
    }

    public IChatComponent getExitMessage() {
        return this.terminationReason;
    }

    public void closeChannel(IChatComponent message) {
        if (this.channel.isOpen()) {
            this.channel.close().awaitUninterruptibly();
            this.terminationReason = message;
        }
    }

    public void checkDisconnected() {
        if (this.channel != null && !this.channel.isOpen()) {
            if (!this.disconnected) {
                this.disconnected = true;

                if (this.getExitMessage() != null) {
                    this.getNetHandler().onDisconnect(this.getExitMessage());
                } else if (this.getNetHandler() != null) {
                    this.getNetHandler().onDisconnect(new ChatComponentString("Disconnected"));
                }
            } else {
                server.getLogger().log(Level.WARNING,"handleDisconnection() called twice");
            }
        }
    }

    private NetHandle getNetHandler() {
        return netHandle;
    }


    public boolean isChannelOpen() {
        return this.channel != null && this.channel.isOpen();
    }

    private void flushOutboundQueue() {
        if (this.channel != null && this.channel.isOpen()) {
            this.writeLock.readLock().lock();

            try {
                while (!this.outboundPacketsQueue.isEmpty()) {
                    NetworkManager.InboundHandlerTuplePacketListener networkmanager$inboundhandlertuplepacketlistener = this.outboundPacketsQueue.poll();
                    this.dispatchPacket(networkmanager$inboundhandlertuplepacketlistener.packet, networkmanager$inboundhandlertuplepacketlistener.futureListeners);
                }
            } finally {
                this.writeLock.readLock().unlock();
            }
        }
    }

    private void dispatchPacket(final ServerProtocolManager.Packet inPacket, final GenericFutureListener<? extends Future<? super Void>>[] futureListeners) {
        final ServerProtocolManager enumconnectionstate = ServerProtocolManager.getFromPacket(inPacket);
        final ServerProtocolManager enumconnectionstate1 = this.channel.attr(attrKeyConnectionState).get();

        if (enumconnectionstate1 != enumconnectionstate) {
            this.channel.config().setAutoRead(false);
        }

        if (this.channel.eventLoop().inEventLoop()) {
            if (enumconnectionstate != enumconnectionstate1) {
                this.setConnectionState(enumconnectionstate);
            }

            ChannelFuture channelfuture = this.channel.writeAndFlush(inPacket);

            if (futureListeners != null) {
                channelfuture.addListeners(futureListeners);
            }

            channelfuture.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        } else {
            this.channel.eventLoop().execute(new Runnable() {
                public void run() {
                    if (enumconnectionstate != enumconnectionstate1) {
                        NetworkManager.this.setConnectionState(enumconnectionstate);
                    }

                    ChannelFuture channelfuture1 = NetworkManager.this.channel.writeAndFlush(inPacket);

                    if (futureListeners != null) {
                        channelfuture1.addListeners(futureListeners);
                    }

                    channelfuture1.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
                }
            });
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext p_channelActive_1_) throws Exception {
        super.channelActive(p_channelActive_1_);
        this.channel = p_channelActive_1_.channel();
        this.socketAddress = this.channel.remoteAddress();

        try {
            this.setConnectionState(ServerProtocolManager.HANDSHAKING);
            this.channel.config().setAutoRead(true);
        } catch (Throwable throwable) {
            server.getLogger().log(Level.SEVERE,throwable.getLocalizedMessage(), throwable);
        }
    }

    public void setConnectionState(ServerProtocolManager newState) {
        this.channel.attr(attrKeyConnectionState).set(newState);
        this.channel.config().setAutoRead(true);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ServerProtocolManager.Packet packet) throws Exception {
        if (this.channel.isOpen()) {
            packet.processPacket(this, netHandle);
        }
    }

    static class InboundHandlerTuplePacketListener {
        private final ServerProtocolManager.Packet packet;
        private final GenericFutureListener<? extends Future<? super Void>>[] futureListeners;

        public InboundHandlerTuplePacketListener(ServerProtocolManager.Packet inPacket, GenericFutureListener<? extends Future<? super Void>>... inFutureListeners) {
            this.packet = inPacket;
            this.futureListeners = inFutureListeners;
        }
    }
}
