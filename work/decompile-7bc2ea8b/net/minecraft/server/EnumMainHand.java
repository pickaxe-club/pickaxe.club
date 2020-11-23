package net.minecraft.server;

public enum EnumMainHand {

    LEFT(new ChatMessage("options.mainHand.left")), RIGHT(new ChatMessage("options.mainHand.right"));

    private final IChatBaseComponent c;

    private EnumMainHand(IChatBaseComponent ichatbasecomponent) {
        this.c = ichatbasecomponent;
    }

    public String toString() {
        return this.c.getString();
    }
}
