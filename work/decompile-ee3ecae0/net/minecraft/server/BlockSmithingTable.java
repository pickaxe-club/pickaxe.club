package net.minecraft.server;

public class BlockSmithingTable extends BlockWorkbench {

    protected BlockSmithingTable(Block.Info block_info) {
        super(block_info);
    }

    @Override
    public EnumInteractionResult interact(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        return EnumInteractionResult.PASS;
    }
}
