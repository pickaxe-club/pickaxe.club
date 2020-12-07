package net.minecraft.server;

public enum BlockPropertyStructureMode implements INamable {

    SAVE("save"), LOAD("load"), CORNER("corner"), DATA("data");

    private final String e;
    private final IChatBaseComponent f;

    private BlockPropertyStructureMode(String s) {
        this.e = s;
        this.f = new ChatMessage("structure_block.mode_info." + s);
    }

    @Override
    public String getName() {
        return this.e;
    }
}
