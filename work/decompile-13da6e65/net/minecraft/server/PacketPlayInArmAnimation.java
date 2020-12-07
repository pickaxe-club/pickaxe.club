package net.minecraft.server;

import java.io.IOException;

public class PacketPlayInArmAnimation implements Packet<PacketListenerPlayIn> {

    private EnumHand a;

    public PacketPlayInArmAnimation() {}

    public PacketPlayInArmAnimation(EnumHand enumhand) {
        this.a = enumhand;
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = (EnumHand) packetdataserializer.a(EnumHand.class);
    }

    @Override
    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.a((Enum) this.a);
    }

    public void a(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.a(this);
    }

    public EnumHand b() {
        return this.a;
    }
}
