package net.minecraft.server;

import java.io.IOException;
import java.security.PrivateKey;
import javax.crypto.SecretKey;

public class PacketLoginInEncryptionBegin implements Packet<PacketLoginInListener> {

    private byte[] a = new byte[0];
    private byte[] b = new byte[0];

    public PacketLoginInEncryptionBegin() {}

    @Override
    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.a();
        this.b = packetdataserializer.a();
    }

    @Override
    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.a(this.a);
        packetdataserializer.a(this.b);
    }

    public void a(PacketLoginInListener packetlogininlistener) {
        packetlogininlistener.a(this);
    }

    public SecretKey a(PrivateKey privatekey) throws CryptographyException {
        return MinecraftEncryption.a(privatekey, this.a);
    }

    public byte[] b(PrivateKey privatekey) throws CryptographyException {
        return MinecraftEncryption.b(privatekey, this.b);
    }
}
