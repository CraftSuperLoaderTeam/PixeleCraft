package com.pixelecraft.nc.craft;

import api.pixelecraft.Piexele;
import api.pixelecraft.Server;
import api.pixelecraft.World;
import api.pixelecraft.command.CommandSender;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.pixelecraft.nc.command.ConsoleSender;
import com.pixelecraft.nc.MetaData;
import com.pixelecraft.nc.command.CommandSystem;
import com.pixelecraft.nc.command.minecraft.HelpCommand;
import com.pixelecraft.nc.command.minecraft.StopCommand;
import com.pixelecraft.nc.plugin.PluginManagerIml;
import com.pixelecraft.nc.network.NetworkServer;
import com.pixelecraft.nc.util.CryptManager;
import com.pixelecraft.nc.util.ServerPing;
import io.github.csl.logging.LogManager;
import io.github.csl.logging.Logger;
import joptsimple.OptionSet;
import org.fusesource.jansi.AnsiConsole;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.File;
import java.io.IOException;
import java.net.Proxy;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.util.UUID;
import java.util.logging.Level;


public class CraftServer implements Server{

    String serverName, version, minecraftVersion, motd;
    PluginManagerIml pluginManagerIml;
    int port = 25565, maxPlayer = 100;
    NetworkServer server;
    PlayerList playerList;
    ServerPing info;
    LineReader reader;
    CommandSystem commandSystem;
    MinecraftSessionService sessionService;
    GameProfileRepository gameProfileRepository;
    ConsoleSender sender;
    CraftTickThreadGroup tickThreadGroup;
    KeyPair serverKeyPair;
    File icon;
    boolean onlineMode;
    boolean isRunning;
    private static final Logger log = Logger.getLogger("Minecraft");

    public CraftServer(File icon,MinecraftSessionService sessionService,GameProfileRepository gameProfileRepository) {
        this.serverName = MetaData.servername;
        this.version = MetaData.version;
        this.sessionService = sessionService;
        this.gameProfileRepository = gameProfileRepository;
        this.minecraftVersion = MetaData.minecraft_version;
        this.motd = "A Minecraft Server";
        this.playerList = new PlayerList(this);
        this.tickThreadGroup = new CraftTickThreadGroup(this);
        pluginManagerIml = new PluginManagerIml(this);
        this.icon = icon;
    }

    public void start() {
        Thread.currentThread().setName("Server Initialization Task");
        isRunning = true;
        this.run();
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void init() {
        try {
            AnsiConsole.systemInstall();
            Terminal terminal = TerminalBuilder.builder()
                    .jansi(true)
                    .encoding(StandardCharsets.UTF_8)
                    .system(false)
                    .streams(System.in, System.out)
                    .name("Terminal")
                    .jna(false)
                    .build();
            reader = LineReaderBuilder.builder().terminal(terminal).build();
        } catch (Throwable e) {
            MetaData.jline_use = false;
            log.log(Level.WARNING,"(String) null", e);
        }

        this.sender = new ConsoleSender(this);
        commandSystem = new CommandSystem(this);
        initCommand();

        Thread thread = new Thread("Server console handler") {
            public void run() {
                LineReader bufferedreader = reader;
                String s;
                try {
                    while (isRunning()) {
                        if (MetaData.jline_use) {
                            s = bufferedreader.readLine(">", null);
                        } else {
                            s = bufferedreader.readLine();
                        }
                        if (s != null && s.trim().length() > 0) {
                            commandSystem.issueCommand(s);
                        }
                    }
                } catch (Exception ioexception) {
                    log.log(Level.SEVERE,"Exception handling console input", ioexception);
                }
            }
        };

        thread.setDaemon(true);
        thread.start();

        if (Runtime.getRuntime().maxMemory() / 1024L / 1024L < 512L) {
            log.log(Level.WARNING,"To start the server with more ram, launch it as \"java -Xmx1024M -Xms1024M -jar minecraft_server.jar\"");
        }

        Runtime.getRuntime().addShutdownHook(new CraftServerShutdownHook(this));

        Piexele.setServer(this);
        this.setKeyPair(CryptManager.generateKeyPair());
        this.info = new ServerPing();
        info.setProtocolVersionInfo(new ServerPing.Version(serverName + " " + minecraftVersion, MetaData.PROTCOL_VERSION));
        int threads = 4;
        System.setProperty("io.netty.eventLoopThreads", Integer.toString(threads));
        log.info(String.format("Using %d threads for Netty based I/O.", threads));
        try {
            server = new NetworkServer(this, port);
        } catch (IOException i) {
            log.log(Level.SEVERE,"**** FAILED TO BIND TO PORT! ****");
            log.log(Level.SEVERE,String.format("The exception was: %s", i.getLocalizedMessage()));
            log.log(Level.SEVERE,"Perhaps a server is already running on that port?");
            System.exit(-1);
        }
    }

    public void initCommand() {
        commandSystem.registerCommand(new StopCommand());
        commandSystem.registerCommand(new HelpCommand(commandSystem));
    }

    public ServerPing getServerInfo() {
        return info;
    }

    public File getIcon() {
        return icon;
    }

    public void setKeyPair(KeyPair keyPair) {
        this.serverKeyPair = keyPair;
    }

    public KeyPair getKeyPair() {
        return serverKeyPair;
    }

    public boolean isSinglePlayer() {
        return true;
    }

    public MinecraftSessionService getMinecraftSessionService(){
        return sessionService;
    }

    public PlayerList getPlayerList() {
        return playerList;
    }

    public static void main(OptionSet options) {
        LogManager.init();

        if (System.console() == null && System.getProperty("jline.terminal") == null) {
            System.setProperty("jline.terminal", "jline.UnsupportedTerminal");
            MetaData.jline_use = false;
        }

        io.netty.util.ResourceLeakDetector.setEnabled(false);

        File icon = new File("server-icon.png");

        YggdrasilAuthenticationService yggdrasilauthenticationservice = new YggdrasilAuthenticationService(Proxy.NO_PROXY, UUID.randomUUID().toString());
        MinecraftSessionService minecraftsessionservice = yggdrasilauthenticationservice.createMinecraftSessionService();
        GameProfileRepository gameprofilerepository = yggdrasilauthenticationservice.createProfileRepository();

        final CraftServer craftServer = new CraftServer(icon,minecraftsessionservice,gameprofilerepository);
        if (options.has("port")) {
            int port = (Integer) options.valueOf("port");
            if (port > 0) {
                craftServer.port = port;
            }
        }
        if (options.has("nojline")) {
            MetaData.jline_use = false;
        }

        craftServer.start();
    }

    @Override
    public PluginManagerIml getPluginManager() {
        return pluginManagerIml;
    }

    @Override
    public CommandSender getConsoleSender() {
        return sender;
    }

    @Override
    public int port() {
        return port;
    }

    @Override
    public String getVersion() {
        return minecraftVersion;
    }

    @Override
    public String getServerName() {
        return serverName;
    }

    @Override
    public String getServerVersion() {
        return version;
    }

    @Override
    public String getMotd() {
        return motd;
    }

    @Override
    public World getWorld(String name) {
        return null;
    }

    @Override
    public Logger getLogger() {
        return log;
    }

    @Override
    public int getMaxPlayer() {
        return maxPlayer;
    }

    public boolean isServerInOnlineMode() {
        return onlineMode;
    }

    public int getNetworkCompressionTreshold() {
        return 256;
    }

    @Override
    public void stop() {
        log.info("Stopping server...");
        isRunning = false;
        tickThreadGroup.stop();

        if(server == null){
            return;
        }

        server.shutdown();
    }

    public void run() {
        try {
            log.info("Initialization server...");
            init();
            tickThreadGroup.init(this);
        } catch (Throwable var0) {
            log.log(Level.SEVERE,"Server was throw exception: " + var0.getLocalizedMessage(), var0);
            stop();
        }
    }
}
