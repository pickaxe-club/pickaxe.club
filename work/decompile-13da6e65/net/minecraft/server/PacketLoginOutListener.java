package net.minecraft.server;

public interface PacketLoginOutListener extends PacketListener {

    void a(PacketLoginOutEncryptionBegin packetloginoutencryptionbegin);

    void a(PacketLoginOutSuccess packetloginoutsuccess);

    void a(PacketLoginOutDisconnect packetloginoutdisconnect);

    void a(PacketLoginOutSetCompression packetloginoutsetcompression);

    void a(PacketLoginOutCustomPayload packetloginoutcustompayload);
}
