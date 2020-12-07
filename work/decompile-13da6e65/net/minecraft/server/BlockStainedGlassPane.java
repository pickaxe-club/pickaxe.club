package net.minecraft.server;

public class BlockStainedGlassPane extends BlockIronBars implements IBeaconBeam {

    private final EnumColor color;

    public BlockStainedGlassPane(EnumColor enumcolor, BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.color = enumcolor;
        this.j((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockStainedGlassPane.NORTH, false)).set(BlockStainedGlassPane.EAST, false)).set(BlockStainedGlassPane.SOUTH, false)).set(BlockStainedGlassPane.WEST, false)).set(BlockStainedGlassPane.e, false));
    }

    @Override
    public EnumColor a() {
        return this.color;
    }
}
