package net.minecraft.server;

public interface IMinecraftServer {

    DedicatedServerProperties getDedicatedServerProperties();

    String h_();

    int o();

    String i_();

    String getVersion();

    int getPlayerCount();

    int getMaxPlayers();

    String[] getPlayers();

    String getWorld();

    String getPlugins();

    String executeRemoteCommand(String s);
}
