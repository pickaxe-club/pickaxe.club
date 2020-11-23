package net.minecraft.server;

public enum EnumMainHand {

    LEFT(new ChatMessage("options.mainHand.left", new Object[0])), RIGHT(new ChatMessage("options.mainHand.right", new Object[0]));

    private final IChatBaseComponent c;

    private EnumMainHand(IChatBaseComponent ichatbasecomponent) {
        this.c = ichatbasecomponent;
    }

    public String toString() {
        return this.c.getString();
    }
}
