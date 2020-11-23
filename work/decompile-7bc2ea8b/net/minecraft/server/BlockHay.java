package net.minecraft.server;

public class BlockHay extends BlockRotatable {

    public BlockHay(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.j((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockHay.AXIS, EnumDirection.EnumAxis.Y));
    }

    @Override
    public void fallOn(World world, BlockPosition blockposition, Entity entity, float f) {
        entity.b(f, 0.2F);
    }
}
