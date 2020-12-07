package net.minecraft.server;

public class HandshakeListener implements PacketHandshakingInListener {

    private static final IChatBaseComponent a = new ChatComponentText("Ignoring status request");
    private final MinecraftServer b;
    private final NetworkManager c;

    public HandshakeListener(MinecraftServer minecraftserver, NetworkManager networkmanager) {
        this.b = minecraftserver;
        this.c = networkmanager;
    }

    @Override
    public void a(PacketHandshakingInSetProtocol packethandshakinginsetprotocol) {
        switch (packethandshakinginsetprotocol.b()) {
            case LOGIN:
                this.c.setProtocol(EnumProtocol.LOGIN);
                if (packethandshakinginsetprotocol.c() != SharedConstants.getGameVersion().getProtocolVersion()) {
                    ChatMessage chatmessage;

                    if (packethandshakinginsetprotocol.c() < 754) {
                        chatmessage = new ChatMessage("multiplayer.disconnect.outdated_client", new Object[]{SharedConstants.getGameVersion().getName()});
                    } else {
                        chatmessage = new ChatMessage("multiplayer.disconnect.incompatible", new Object[]{SharedConstants.getGameVersion().getName()});
                    }

                    this.c.sendPacket(new PacketLoginOutDisconnect(chatmessage));
                    this.c.close(chatmessage);
                } else {
                    this.c.setPacketListener(new LoginListener(this.b, this.c));
                }
                break;
            case STATUS:
                if (this.b.am()) {
                    this.c.setProtocol(EnumProtocol.STATUS);
                    this.c.setPacketListener(new PacketStatusListener(this.b, this.c));
                } else {
                    this.c.close(HandshakeListener.a);
                }
                break;
            default:
                throw new UnsupportedOperationException("Invalid intention " + packethandshakinginsetprotocol.b());
        }

    }

    @Override
    public void a(IChatBaseComponent ichatbasecomponent) {}

    @Override
    public NetworkManager a() {
        return this.c;
    }
}
