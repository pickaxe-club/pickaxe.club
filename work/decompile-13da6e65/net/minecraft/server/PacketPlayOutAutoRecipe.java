package net.minecraft.server;

import java.io.IOException;

public class PacketPlayOutAutoRecipe implements Packet<PacketListenerPlayOut> {

    private int a;
    private MinecraftKey b;

    public PacketPlayOutAutoRecipe() {}

    public PacketPlayOutAutoRecipe(int i, IRecipe<?> irecipe) {
        this.a = i;
        this.b = irecipe.getKey();
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.readByte();
        this.b = packetdataserializer.p();
    }

    @Override
    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.writeByte(this.a);
        packetdataserializer.a(this.b);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }
}
