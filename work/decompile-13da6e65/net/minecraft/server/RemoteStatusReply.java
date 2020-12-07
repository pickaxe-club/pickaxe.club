package net.minecraft.server;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RemoteStatusReply {

    private final ByteArrayOutputStream a;
    private final DataOutputStream b;

    public RemoteStatusReply(int i) {
        this.a = new ByteArrayOutputStream(i);
        this.b = new DataOutputStream(this.a);
    }

    public void a(byte[] abyte) throws IOException {
        this.b.write(abyte, 0, abyte.length);
    }

    public void a(String s) throws IOException {
        this.b.writeBytes(s);
        this.b.write(0);
    }

    public void a(int i) throws IOException {
        this.b.write(i);
    }

    public void a(short short0) throws IOException {
        this.b.writeShort(Short.reverseBytes(short0));
    }

    public byte[] a() {
        return this.a.toByteArray();
    }

    public void b() {
        this.a.reset();
    }
}
