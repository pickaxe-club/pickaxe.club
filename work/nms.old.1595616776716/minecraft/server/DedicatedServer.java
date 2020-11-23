package net.minecraft.server;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.datafixers.DataFixer;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Random;
import java.util.function.BooleanSupplier;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// CraftBukkit start
import java.io.PrintStream;
import org.apache.logging.log4j.Level;

import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.LoggerOutputStream;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.craftbukkit.util.Waitable;
import org.bukkit.event.server.RemoteServerCommandEvent;
// CraftBukkit end

public class DedicatedServer extends MinecraftServer implements IMinecraftServer {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Pattern i = Pattern.compile("^[a-fA-F0-9]{40}$");
    private final List<ServerCommand> serverCommandQueue = Collections.synchronizedList(Lists.newArrayList());
    private RemoteStatusListener remoteStatusListener;
    public final RemoteControlCommandListener remoteControlCommandListener;
    private RemoteControlListener remoteControlListener;
    public DedicatedServerSettings propertyManager;
    private EnumGamemode o;
    @Nullable
    private ServerGUI p;

    // CraftBukkit start - Signature changed
    public DedicatedServer(joptsimple.OptionSet options, DedicatedServerSettings dedicatedserversettings, DataFixer datafixer, YggdrasilAuthenticationService yggdrasilauthenticationservice, MinecraftSessionService minecraftsessionservice, GameProfileRepository gameprofilerepository, UserCache usercache, WorldLoadListenerFactory worldloadlistenerfactory, String s) {
        super(options, Proxy.NO_PROXY, datafixer, new CommandDispatcher().init(true), yggdrasilauthenticationservice, minecraftsessionservice, gameprofilerepository, usercache, worldloadlistenerfactory, s);
        // CraftBukkit end
        this.propertyManager = dedicatedserversettings;
        this.remoteControlCommandListener = new RemoteControlCommandListener(this);
        Thread thread = new Thread("Server Infinisleeper") {
            {
                this.setDaemon(true);
                this.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(DedicatedServer.LOGGER));
                this.start();
            }

            public void run() {
                while (true) {
                    try {
                        Thread.sleep(2147483647L);
                    } catch (InterruptedException interruptedexception) {
                        ;
                    }
                }
            }
        };
    }

    @Override
    public boolean init() throws IOException {
        Thread thread = new Thread("Server console handler") {
            public void run() {
                // CraftBukkit start
                if (!org.bukkit.craftbukkit.Main.useConsole) {
                    return;
                }
                jline.console.ConsoleReader bufferedreader = reader;

                // MC-33041, SPIGOT-5538: if System.in is not valid due to javaw, then return
                try {
                    System.in.available();
                } catch (IOException ex) {
                    return;
                }
                // CraftBukkit end

                String s;

                try {
                    // CraftBukkit start - JLine disabling compatibility
                    while (!DedicatedServer.this.isStopped() && DedicatedServer.this.isRunning()) {
                        if (org.bukkit.craftbukkit.Main.useJline) {
                            s = bufferedreader.readLine(">", null);
                        } else {
                            s = bufferedreader.readLine();
                        }

                        // SPIGOT-5220: Throttle if EOF (ctrl^d) or stdin is /dev/null
                        if (s == null) {
                            try {
                                Thread.sleep(50L);
                            } catch (InterruptedException ex) {
                                Thread.currentThread().interrupt();
                            }
                            continue;
                        }
                        if (s.trim().length() > 0) { // Trim to filter lines which are just spaces
                            DedicatedServer.this.issueCommand(s, DedicatedServer.this.getServerCommandListener());
                        }
                        // CraftBukkit end
                    }
                } catch (IOException ioexception) {
                    DedicatedServer.LOGGER.error("Exception handling console input", ioexception);
                }

            }
        };

        // CraftBukkit start - TODO: handle command-line logging arguments
        java.util.logging.Logger global = java.util.logging.Logger.getLogger("");
        global.setUseParentHandlers(false);
        for (java.util.logging.Handler handler : global.getHandlers()) {
            global.removeHandler(handler);
        }
        global.addHandler(new org.bukkit.craftbukkit.util.ForwardLogHandler());

        final org.apache.logging.log4j.core.Logger logger = ((org.apache.logging.log4j.core.Logger) LogManager.getRootLogger());
        for (org.apache.logging.log4j.core.Appender appender : logger.getAppenders().values()) {
            if (appender instanceof org.apache.logging.log4j.core.appender.ConsoleAppender) {
                logger.removeAppender(appender);
            }
        }

        new org.bukkit.craftbukkit.util.TerminalConsoleWriterThread(System.out, this.reader).start();

        System.setOut(new PrintStream(new LoggerOutputStream(logger, Level.INFO), true));
        System.setErr(new PrintStream(new LoggerOutputStream(logger, Level.WARN), true));
        // CraftBukkit end

        thread.setDaemon(true);
        thread.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(DedicatedServer.LOGGER));
        thread.start();
        DedicatedServer.LOGGER.info("Starting minecraft server version " + SharedConstants.getGameVersion().getName());
        if (Runtime.getRuntime().maxMemory() / 1024L / 1024L < 512L) {
            DedicatedServer.LOGGER.warn("To start the server with more ram, launch it as \"java -Xmx1024M -Xms1024M -jar minecraft_server.jar\"");
        }

        DedicatedServer.LOGGER.info("Loading properties");
        DedicatedServerProperties dedicatedserverproperties = this.propertyManager.getProperties();

        if (this.isEmbeddedServer()) {
            this.b("127.0.0.1");
        } else {
            this.setOnlineMode(dedicatedserverproperties.onlineMode);
            this.h(dedicatedserverproperties.preventProxyConnections);
            this.b(dedicatedserverproperties.serverIp);
        }

        this.setSpawnAnimals(dedicatedserverproperties.spawnAnimals);
        this.setSpawnNPCs(dedicatedserverproperties.spawnNpcs);
        this.setPVP(dedicatedserverproperties.pvp);
        this.setAllowFlight(dedicatedserverproperties.allowFlight);
        this.setResourcePack(dedicatedserverproperties.resourcePack, this.aZ());
        this.setMotd(dedicatedserverproperties.motd);
        this.setForceGamemode(dedicatedserverproperties.forceGamemode);
        super.setIdleTimeout((Integer) dedicatedserverproperties.playerIdleTimeout.get());
        this.n(dedicatedserverproperties.enforceWhitelist);
        this.o = dedicatedserverproperties.gamemode;
        DedicatedServer.LOGGER.info("Default game type: {}", this.o);
        InetAddress inetaddress = null;

        if (!this.getServerIp().isEmpty()) {
            inetaddress = InetAddress.getByName(this.getServerIp());
        }

        if (this.getPort() < 0) {
            this.setPort(dedicatedserverproperties.serverPort);
        }

        DedicatedServer.LOGGER.info("Generating keypair");
        this.a(MinecraftEncryption.b());
        DedicatedServer.LOGGER.info("Starting Minecraft server on {}:{}", this.getServerIp().isEmpty() ? "*" : this.getServerIp(), this.getPort());

        try {
            this.getServerConnection().a(inetaddress, this.getPort());
        } catch (IOException ioexception) {
            DedicatedServer.LOGGER.warn("**** FAILED TO BIND TO PORT!");
            DedicatedServer.LOGGER.warn("The exception was: {}", ioexception.toString());
            DedicatedServer.LOGGER.warn("Perhaps a server is already running on that port?");
            return false;
        }

        // CraftBukkit start
        this.a((PlayerList) (new DedicatedPlayerList(this)));
        server.loadPlugins();
        server.enablePlugins(org.bukkit.plugin.PluginLoadOrder.STARTUP);
        // CraftBukkit end

        if (!this.getOnlineMode()) {
            DedicatedServer.LOGGER.warn("**** SERVER IS RUNNING IN OFFLINE/INSECURE MODE!");
            DedicatedServer.LOGGER.warn("The server will make no attempt to authenticate usernames. Beware.");
            DedicatedServer.LOGGER.warn("While this makes the game possible to play without internet access, it also opens up the ability for hackers to connect with any username they choose.");
            DedicatedServer.LOGGER.warn("To change this, set \"online-mode\" to \"true\" in the server.properties file.");
        }

        if (this.convertNames()) {
            this.getUserCache().c();
        }

        if (!NameReferencingFileConverter.e(this)) {
            return false;
        } else {
            this.convertable = new Convertable(server.getWorldContainer().toPath(), server.getWorldContainer().toPath().resolve("../backups"), this.dataConverterManager); // CraftBukkit - moved from MinecraftServer constructor
            long i = SystemUtils.getMonotonicNanos();
            String s = dedicatedserverproperties.levelSeed;
            String s1 = dedicatedserverproperties.generatorSettings;
            long j = (new Random()).nextLong();

            if (!s.isEmpty()) {
                try {
                    long k = Long.parseLong(s);

                    if (k != 0L) {
                        j = k;
                    }
                } catch (NumberFormatException numberformatexception) {
                    j = (long) s.hashCode();
                }
            }

            WorldType worldtype = dedicatedserverproperties.levelType;

            this.b(dedicatedserverproperties.maxBuildHeight);
            TileEntitySkull.a(this.getUserCache());
            TileEntitySkull.a(this.getMinecraftSessionService());
            UserCache.a(this.getOnlineMode());
            DedicatedServer.LOGGER.info("Preparing level \"{}\"", this.getWorld());
            JsonObject jsonobject = new JsonObject();

            if (worldtype == WorldType.FLAT) {
                jsonobject.addProperty("flat_world_options", s1);
            } else if (!s1.isEmpty()) {
                // CraftBukkit start
                try {
                    jsonobject = ChatDeserializer.a(s1);
                } catch (Exception ex) {
                    DedicatedServer.LOGGER.warn("Invalid generator-settings, ignoring", ex);
                }
                // CraftBukkit end
            }

            this.a(this.getWorld(), this.getWorld(), j, worldtype, jsonobject);
            long l = SystemUtils.getMonotonicNanos() - i;
            String s2 = String.format(Locale.ROOT, "%.3fs", (double) l / 1.0E9D);

            DedicatedServer.LOGGER.info("Done ({})! For help, type \"help\"", s2);
            if (dedicatedserverproperties.announcePlayerAchievements != null) {
                ((GameRules.GameRuleBoolean) this.getGameRules().get(GameRules.ANNOUNCE_ADVANCEMENTS)).a(dedicatedserverproperties.announcePlayerAchievements, (MinecraftServer) this);
            }

            if (dedicatedserverproperties.enableQuery) {
                DedicatedServer.LOGGER.info("Starting GS4 status listener");
                this.remoteStatusListener = new RemoteStatusListener(this);
                this.remoteStatusListener.a();
            }

            if (dedicatedserverproperties.enableRcon) {
                DedicatedServer.LOGGER.info("Starting remote control listener");
                this.remoteControlListener = new RemoteControlListener(this);
                this.remoteControlListener.a();
                this.remoteConsole = new org.bukkit.craftbukkit.command.CraftRemoteConsoleCommandSender(this.remoteControlCommandListener); // CraftBukkit
            }

            if (this.getMaxTickTime() > 0L) {
                Thread thread1 = new Thread(new ThreadWatchdog(this));

                thread1.setUncaughtExceptionHandler(new ThreadNamedUncaughtExceptionHandler(DedicatedServer.LOGGER));
                thread1.setName("Server Watchdog");
                thread1.setDaemon(true);
                thread1.start();
            }

            Items.AIR.a(CreativeModeTab.g, NonNullList.a());
            return true;
        }
    }

    public String aZ() {
        DedicatedServerProperties dedicatedserverproperties = this.propertyManager.getProperties();
        String s;

        if (!dedicatedserverproperties.resourcePackSha1.isEmpty()) {
            s = dedicatedserverproperties.resourcePackSha1;
            if (!Strings.isNullOrEmpty(dedicatedserverproperties.resourcePackHash)) {
                DedicatedServer.LOGGER.warn("resource-pack-hash is deprecated and found along side resource-pack-sha1. resource-pack-hash will be ignored.");
            }
        } else if (!Strings.isNullOrEmpty(dedicatedserverproperties.resourcePackHash)) {
            DedicatedServer.LOGGER.warn("resource-pack-hash is deprecated. Please use resource-pack-sha1 instead.");
            s = dedicatedserverproperties.resourcePackHash;
        } else {
            s = "";
        }

        if (!s.isEmpty() && !DedicatedServer.i.matcher(s).matches()) {
            DedicatedServer.LOGGER.warn("Invalid sha1 for ressource-pack-sha1");
        }

        if (!dedicatedserverproperties.resourcePack.isEmpty() && s.isEmpty()) {
            DedicatedServer.LOGGER.warn("You specified a resource pack without providing a sha1 hash. Pack will be updated on the client only if you change the name of the pack.");
        }

        return s;
    }

    @Override
    public void setGamemode(EnumGamemode enumgamemode) {
        super.setGamemode(enumgamemode);
        this.o = enumgamemode;
    }

    @Override
    public DedicatedServerProperties getDedicatedServerProperties() {
        return this.propertyManager.getProperties();
    }

    @Override
    public boolean getGenerateStructures() {
        return this.getDedicatedServerProperties().generateStructures;
    }

    @Override
    public EnumGamemode getGamemode() {
        return this.o;
    }

    @Override
    public EnumDifficulty getDifficulty() {
        return this.getDedicatedServerProperties().difficulty;
    }

    @Override
    public boolean isHardcore() {
        return this.getDedicatedServerProperties().hardcore;
    }

    @Override
    public CrashReport b(CrashReport crashreport) {
        crashreport = super.b(crashreport);
        crashreport.g().a("Is Modded", () -> {
            return (String) this.q().orElse("Unknown (can't tell)");
        });
        crashreport.g().a("Type", () -> {
            return "Dedicated Server (map_server.txt)";
        });
        return crashreport;
    }

    @Override
    public Optional<String> q() {
        String s = this.getServerModName();

        return !"vanilla".equals(s) ? Optional.of("Definitely; Server brand changed to '" + s + "'") : Optional.empty();
    }

    @Override
    public void exit() {
        if (this.p != null) {
            this.p.b();
        }

        if (this.remoteControlListener != null) {
            this.remoteControlListener.b();
        }

        if (this.remoteStatusListener != null) {
            this.remoteStatusListener.b();
        }

        System.exit(0); // CraftBukkit
    }

    @Override
    public void b(BooleanSupplier booleansupplier) {
        super.b(booleansupplier);
        this.handleCommandQueue();
    }

    @Override
    public boolean getAllowNether() {
        return this.getDedicatedServerProperties().allowNether;
    }

    @Override
    public boolean getSpawnMonsters() {
        return this.getDedicatedServerProperties().spawnMonsters;
    }

    @Override
    public void a(MojangStatisticsGenerator mojangstatisticsgenerator) {
        mojangstatisticsgenerator.a("whitelist_enabled", this.getPlayerList().getHasWhitelist());
        mojangstatisticsgenerator.a("whitelist_count", this.getPlayerList().getWhitelisted().length);
        super.a(mojangstatisticsgenerator);
    }

    public void issueCommand(String s, CommandListenerWrapper commandlistenerwrapper) {
        this.serverCommandQueue.add(new ServerCommand(s, commandlistenerwrapper));
    }

    public void handleCommandQueue() {
        while (!this.serverCommandQueue.isEmpty()) {
            ServerCommand servercommand = (ServerCommand) this.serverCommandQueue.remove(0);

            // CraftBukkit start - ServerCommand for preprocessing
            ServerCommandEvent event = new ServerCommandEvent(console, servercommand.command);
            server.getPluginManager().callEvent(event);
            if (event.isCancelled()) continue;
            servercommand = new ServerCommand(event.getCommand(), servercommand.source);

            // this.getCommandDispatcher().a(servercommand.source, servercommand.command); // Called in dispatchServerCommand
            server.dispatchServerCommand(console, servercommand);
            // CraftBukkit end
        }

    }

    @Override
    public boolean m() {
        return true;
    }

    @Override
    public boolean n() {
        return this.getDedicatedServerProperties().useNativeTransport;
    }

    @Override
    public DedicatedPlayerList getPlayerList() {
        return (DedicatedPlayerList) super.getPlayerList();
    }

    @Override
    public boolean p() {
        return true;
    }

    @Override
    public String e_() {
        return this.getServerIp();
    }

    @Override
    public int r() {
        return this.getPort();
    }

    @Override
    public String f_() {
        return this.getMotd();
    }

    public void bc() {
        if (this.p == null) {
            this.p = ServerGUI.a(this);
        }

    }

    @Override
    public boolean aj() {
        return this.p != null;
    }

    @Override
    public boolean a(EnumGamemode enumgamemode, boolean flag, int i) {
        return false;
    }

    @Override
    public boolean getEnableCommandBlock() {
        return this.getDedicatedServerProperties().enableCommandBlock;
    }

    @Override
    public int getSpawnProtection() {
        return this.getDedicatedServerProperties().spawnProtection;
    }

    @Override
    public boolean a(World world, BlockPosition blockposition, EntityHuman entityhuman) {
        if (world.worldProvider.getDimensionManager() != DimensionManager.OVERWORLD) {
            return false;
        } else if (this.getPlayerList().getOPs().isEmpty()) {
            return false;
        } else if (this.getPlayerList().isOp(entityhuman.getProfile())) {
            return false;
        } else if (this.getSpawnProtection() <= 0) {
            return false;
        } else {
            BlockPosition blockposition1 = world.getSpawn();
            int i = MathHelper.a(blockposition.getX() - blockposition1.getX());
            int j = MathHelper.a(blockposition.getZ() - blockposition1.getZ());
            int k = Math.max(i, j);

            return k <= this.getSpawnProtection();
        }
    }

    @Override
    public int j() {
        return this.getDedicatedServerProperties().opPermissionLevel;
    }

    @Override
    public int k() {
        return this.getDedicatedServerProperties().functionPermissionLevel;
    }

    @Override
    public void setIdleTimeout(int i) {
        super.setIdleTimeout(i);
        this.propertyManager.setProperty((dedicatedserverproperties) -> {
            return (DedicatedServerProperties) dedicatedserverproperties.playerIdleTimeout.set(i);
        });
    }

    @Override
    public boolean l() {
        return this.getDedicatedServerProperties().broadcastRconToOps;
    }

    @Override
    public boolean shouldBroadcastCommands() {
        return this.getDedicatedServerProperties().broadcastConsoleToOps;
    }

    @Override
    public int ax() {
        return this.getDedicatedServerProperties().maxWorldSize;
    }

    @Override
    public int aA() {
        return this.getDedicatedServerProperties().networkCompressionThreshold;
    }

    protected boolean convertNames() {
        boolean flag = false;

        int i;

        for (i = 0; !flag && i <= 2; ++i) {
            if (i > 0) {
                DedicatedServer.LOGGER.warn("Encountered a problem while converting the user banlist, retrying in a few seconds");
                this.bn();
            }

            flag = NameReferencingFileConverter.a((MinecraftServer) this);
        }

        boolean flag1 = false;

        for (i = 0; !flag1 && i <= 2; ++i) {
            if (i > 0) {
                DedicatedServer.LOGGER.warn("Encountered a problem while converting the ip banlist, retrying in a few seconds");
                this.bn();
            }

            flag1 = NameReferencingFileConverter.b((MinecraftServer) this);
        }

        boolean flag2 = false;

        for (i = 0; !flag2 && i <= 2; ++i) {
            if (i > 0) {
                DedicatedServer.LOGGER.warn("Encountered a problem while converting the op list, retrying in a few seconds");
                this.bn();
            }

            flag2 = NameReferencingFileConverter.c((MinecraftServer) this);
        }

        boolean flag3 = false;

        for (i = 0; !flag3 && i <= 2; ++i) {
            if (i > 0) {
                DedicatedServer.LOGGER.warn("Encountered a problem while converting the whitelist, retrying in a few seconds");
                this.bn();
            }

            flag3 = NameReferencingFileConverter.d(this);
        }

        boolean flag4 = false;

        for (i = 0; !flag4 && i <= 2; ++i) {
            if (i > 0) {
                DedicatedServer.LOGGER.warn("Encountered a problem while converting the player save files, retrying in a few seconds");
                this.bn();
            }

            flag4 = NameReferencingFileConverter.a(this);
        }

        return flag || flag1 || flag2 || flag3 || flag4;
    }

    private void bn() {
        try {
            Thread.sleep(5000L);
        } catch (InterruptedException interruptedexception) {
            ;
        }
    }

    public long getMaxTickTime() {
        return this.getDedicatedServerProperties().maxTickTime;
    }

    @Override
    public String getPlugins() {
        // CraftBukkit start - Whole method
        StringBuilder result = new StringBuilder();
        org.bukkit.plugin.Plugin[] plugins = server.getPluginManager().getPlugins();

        result.append(server.getName());
        result.append(" on Bukkit ");
        result.append(server.getBukkitVersion());

        if (plugins.length > 0 && server.getQueryPlugins()) {
            result.append(": ");

            for (int i = 0; i < plugins.length; i++) {
                if (i > 0) {
                    result.append("; ");
                }

                result.append(plugins[i].getDescription().getName());
                result.append(" ");
                result.append(plugins[i].getDescription().getVersion().replaceAll(";", ","));
            }
        }

        return result.toString();
        // CraftBukkit end
    }

    @Override
    public String executeRemoteCommand(String s) {
        this.remoteControlCommandListener.clearMessages();
        this.executeSync(() -> {
            // CraftBukkit start - fire RemoteServerCommandEvent
            RemoteServerCommandEvent event = new RemoteServerCommandEvent(remoteConsole, s);
            server.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return;
            }
            ServerCommand serverCommand = new ServerCommand(event.getCommand(), remoteControlCommandListener.getWrapper());
            server.dispatchServerCommand(remoteConsole, serverCommand);
            // CraftBukkit end
        });
        return this.remoteControlCommandListener.getMessages();
    }

    public void setHasWhitelist(boolean flag) {
        this.propertyManager.setProperty((dedicatedserverproperties) -> {
            return (DedicatedServerProperties) dedicatedserverproperties.whiteList.set(flag);
        });
    }

    @Override
    public void stop() {
        super.stop();
        SystemUtils.f();
    }

    @Override
    public boolean a(GameProfile gameprofile) {
        return false;
    }

    // CraftBukkit start
    @Override
    public boolean isDebugging() {
        return this.getDedicatedServerProperties().debug;
    }

    @Override
    public CommandSender getBukkitSender(CommandListenerWrapper wrapper) {
        return console;
    }
    // CraftBukkit end
}
