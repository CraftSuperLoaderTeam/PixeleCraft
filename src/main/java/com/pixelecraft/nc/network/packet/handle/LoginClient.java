package com.pixelecraft.nc.network.packet.handle;

import api.pixelecraft.event.player.AsyncPlayerPreLoginEvent;
import com.google.common.base.Charsets;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.exceptions.InvalidCredentialsException;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.pixelecraft.nc.craft.CraftServer;
import com.pixelecraft.nc.entity.EntityPlayer;
import com.pixelecraft.nc.network.NetworkManager;
import com.pixelecraft.nc.network.ServerProtocolManager;
import com.pixelecraft.nc.network.packet.*;
import com.pixelecraft.nc.util.CryptManager;
import com.pixelecraft.nc.util.IChatComponent;
import com.pixelecraft.nc.util.text.ChatComponentString;
import com.pixelecraft.nc.util.text.ChatComponentTranslation;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.apache.commons.lang3.Validate;

import javax.crypto.SecretKey;
import java.math.BigInteger;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

public class LoginClient implements NetHandle, ITickable {
    NetworkManager manager;
    CraftServer server;
    private int connectionTimer;
    private static final Random RANDOM = new Random();
    private static final AtomicInteger AUTHENTICATOR_THREAD_ID = new AtomicInteger(0);
    private final byte[] verifyToken = new byte[4];
    private GameProfile loginGameProfile;
    private LoginState currentLoginState = LoginState.HELLO;
    private String serverId = "";
    private EntityPlayer player;
    private SecretKey secretKey;

    public LoginClient(NetworkManager manager, CraftServer server) {
        this.manager = manager;
        this.server = server;
        RANDOM.nextBytes(this.verifyToken);
    }

    public void handleDisconnect(PacketDisconnect packetIn) {
        this.manager.closeChannel(packetIn.getReason());
    }

    @Override
    public void onDisconnect(IChatComponent reason) {
        server.getLogger().info(this.getConnectionInfo() + " lost connection: " + reason.getUnformattedText());
    }

    public String getConnectionInfo() {
        return this.loginGameProfile != null ? this.loginGameProfile.toString() + " (" + this.manager.getRemoteAddress().toString() + ")" : String.valueOf((Object) this.manager.getRemoteAddress());
    }

    @Override
    public void update() {
        if (this.currentLoginState == LoginState.READY_TO_ACCEPT) {
            this.tryAcceptPlayer();
        } else if (this.currentLoginState == LoginState.DELAY_ACCEPT) {
            EntityPlayer entityplayermp = this.server.getPlayerList().getPlayerByUUID(this.loginGameProfile.getId());

            if (entityplayermp == null) {
                this.currentLoginState = LoginState.READY_TO_ACCEPT;
                this.server.getPlayerList().initializeConnectionToPlayer(this.manager, this.player);
                this.player = null;
            }
        }

        if (this.connectionTimer++ == 600) {
            this.closeConnection("Took too long to log in");
        }
    }

    public void closeConnection(String reason) {
        try {
            server.getLogger().info("Disconnecting " + this.getConnectionInfo() + ": " + reason);
            ChatComponentString chatcomponenttext = new ChatComponentString(reason);
            this.manager.sendPacket(new PacketDisconnect(chatcomponenttext));
            this.manager.closeChannel(chatcomponenttext);
        } catch (Exception exception) {
            server.getLogger().log(Level.SEVERE,(String) "Error whilst disconnecting player", (Throwable) exception);
        }
    }

    public void tryAcceptPlayer() {
        if (!this.loginGameProfile.isComplete()) {
            this.loginGameProfile = this.getOfflineProfile(this.loginGameProfile);
        }

        this.currentLoginState = LoginState.ACCEPTED;
        if (this.server.getNetworkCompressionTreshold() >= 0 && !this.manager.isLocalChannel()) {
            this.manager.sendPacket(new PacketEnableCompression(this.server.getNetworkCompressionTreshold()), new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    LoginClient.this.manager.setCompressionThreshold(LoginClient.this.server.getNetworkCompressionTreshold());
                }
            });
        }

        this.manager.sendPacket(new PacketLoginSuccess());
    }

    public void handleLoginSuccess(PacketLoginSuccess packetIn) {
        this.loginGameProfile = packetIn.getProfile();

        PlayerService service = new PlayerService(server,manager,loginGameProfile);
    }

    public void processEncryptionResponse(PacketEncryptionResponse packetIn) {
        Validate.validState(this.currentLoginState == LoginState.KEY, "Unexpected key packet", new Object[0]);
        PrivateKey privatekey = this.server.getKeyPair().getPrivate();

        if (!Arrays.equals(this.verifyToken, packetIn.getVerifyToken(privatekey))) {
            throw new IllegalStateException("Invalid nonce!");
        } else {
            this.secretKey = packetIn.getSecretKey(privatekey);
            this.currentLoginState = LoginState.AUTHENTICATING;
            this.manager.enableEncryption(this.secretKey);
            (new Thread("User Authenticator #" + AUTHENTICATOR_THREAD_ID.incrementAndGet()) {
                public void run() {
                    GameProfile gameprofile = LoginClient.this.loginGameProfile;

                    try {
                        String s = (new BigInteger(CryptManager.getServerIdHash(LoginClient.this.serverId, LoginClient.this.server.getKeyPair().getPublic(), LoginClient.this.secretKey))).toString(16);
                        LoginClient.this.loginGameProfile = LoginClient.this.server.getMinecraftSessionService().hasJoinedServer(new GameProfile((UUID) null, gameprofile.getName()), s);

                        if (LoginClient.this.loginGameProfile != null) {
                            server.getLogger().info("UUID of player " + LoginClient.this.loginGameProfile.getName() + " is " + LoginClient.this.loginGameProfile.getId());
                            LoginClient.this.currentLoginState = LoginState.READY_TO_ACCEPT;
                        } else if (LoginClient.this.server.isSinglePlayer()) {
                            server.getLogger().log(Level.WARNING,"Failed to verify username but will let them in anyway!");
                            LoginClient.this.loginGameProfile = LoginClient.this.getOfflineProfile(gameprofile);
                            LoginClient.this.currentLoginState = LoginState.READY_TO_ACCEPT;
                        } else {
                            LoginClient.this.closeConnection("Failed to verify username!");
                            server.getLogger().log(Level.SEVERE,"Username \'" + LoginClient.this.loginGameProfile.getName() + "\' tried to join with an invalid session");
                        }
                    } catch (AuthenticationUnavailableException var3) {
                        if (server.isSinglePlayer()) {
                            server.getLogger().log(Level.WARNING,"Authentication servers are down but will let them in anyway!");
                            LoginClient.this.loginGameProfile = LoginClient.this.getOfflineProfile(gameprofile);
                            LoginClient.this.currentLoginState = LoginState.READY_TO_ACCEPT;
                        } else {
                            LoginClient.this.closeConnection("Authentication servers are down. Please try again later, sorry!");
                            server.getLogger().log(Level.SEVERE,"Couldn\'t verify username because servers are unavailable");
                        }
                    }
                }
            }).start();
        }
    }

    public void handleEnableCompression(PacketEnableCompression packetIn) {
        if (!this.manager.isLocalChannel()) {
            this.manager.setCompressionThreshold(packetIn.getCompressionThreshold());
        }
    }

    public void processLoginStart(PacketLoginStart packetIn) {
        Validate.validState(this.currentLoginState == LoginState.HELLO, "Unexpected hello packet", new Object[0]);
        this.loginGameProfile = packetIn.getProfile();

        if (this.server.isServerInOnlineMode() && !this.manager.isLocalChannel()) {
            this.currentLoginState = LoginState.KEY;
            this.manager.sendPacket(new PacketEncryptionRequest(this.serverId, this.server.getKeyPair().getPublic(), this.verifyToken));
        } else {
            new Thread("User Authenticator #" + LoginClient.AUTHENTICATOR_THREAD_ID.incrementAndGet()) {

                @Override
                public void run() {
                    try {
                        initUUID();
                        String playerName = loginGameProfile.getName();
                        InetAddress address = ((java.net.InetSocketAddress) manager.getSocketAddress()).getAddress();
                        UUID uniqueId = loginGameProfile.getId();

                        AsyncPlayerPreLoginEvent event = new AsyncPlayerPreLoginEvent(playerName, address, uniqueId);
                        server.getPluginManager().callEvent(event);

                        if (event.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) {
                            closeConnection(event.getKickMessage());
                            return;
                        }

                        server.getLogger().info("UUID of player " + loginGameProfile.getName() + " is " + loginGameProfile.getId());
                        LoginClient.this.currentLoginState = LoginState.READY_TO_ACCEPT;

                    } catch (Exception ex) {
                        closeConnection("Failed to verify username!");
                        server.getLogger().log(Level.SEVERE,"Exception verifying " + loginGameProfile.getName(), ex);
                    }
                }
            }.start();
        }
    }

    public void handleEncryptionRequest(PacketEncryptionRequest packetIn) {
    }

    private MinecraftSessionService getSessionService() {
        return this.server.getMinecraftSessionService();
    }

    public void initUUID() {
        UUID uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + this.loginGameProfile.getName()).getBytes(StandardCharsets.UTF_8));
        this.loginGameProfile = new GameProfile(uuid, this.loginGameProfile.getName());
    }

    protected GameProfile getOfflineProfile(GameProfile original) {
        UUID uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + original.getName()).getBytes(Charsets.UTF_8));
        return new GameProfile(uuid, original.getName());
    }

    static enum LoginState {
        HELLO,
        KEY,
        AUTHENTICATING,
        READY_TO_ACCEPT,
        DELAY_ACCEPT,
        ACCEPTED;
    }
}
