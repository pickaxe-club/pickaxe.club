package net.minecraft.server;

import java.io.IOException;

public class PacketPlayOutUpdateHealth implements Packet<PacketListenerPlayOut> {

    private float a;
    private int b;
    private float c;

    public PacketPlayOutUpdateHealth() {}

    public PacketPlayOutUpdateHealth(float f, int i, float f1) {
        this.a = f;
        this.b = i;
        this.c = f1;
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.readFloat();
        this.b = packetdataserializer.i();
        this.c = packetdataserializer.readFloat();
    }

    @Override
    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.writeFloat(this.a);
        packetdataserializer.d(this.b);
        packetdataserializer.writeFloat(this.c);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }
}
