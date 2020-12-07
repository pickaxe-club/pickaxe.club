package net.minecraft.server;

public class BlockWetSponge extends Block {

    protected BlockWetSponge(BlockBase.Info blockbase_info) {
        super(blockbase_info);
    }

    @Override
    public void onPlace(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (world.getDimensionManager().isNether()) {
            world.setTypeAndData(blockposition, Blocks.SPONGE.getBlockData(), 3);
            world.triggerEffect(2009, blockposition, 0);
            world.playSound((EntityHuman) null, blockposition, SoundEffects.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1.0F, (1.0F + world.getRandom().nextFloat() * 0.2F) * 0.7F);
        }

    }
}
