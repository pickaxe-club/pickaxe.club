package net.minecraft.server;

public class BlockCoralDead extends BlockCoralBase {

    protected static final VoxelShape a = Block.a(2.0D, 0.0D, 2.0D, 14.0D, 15.0D, 14.0D);

    protected BlockCoralDead(BlockBase.Info blockbase_info) {
        super(blockbase_info);
    }

    @Override
    public VoxelShape b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return BlockCoralDead.a;
    }
}
