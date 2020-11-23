package net.minecraft.server;

public class BlockMobSpawner extends BlockTileEntity {

    protected BlockMobSpawner(Block.Info block_info) {
        super(block_info);
    }

    @Override
    public TileEntity createTile(IBlockAccess iblockaccess) {
        return new TileEntityMobSpawner();
    }

    @Override
    public void dropNaturally(IBlockData iblockdata, World world, BlockPosition blockposition, ItemStack itemstack) {
        super.dropNaturally(iblockdata, world, blockposition, itemstack);
        /* CraftBukkit start - Delegate to getExpDrop
        int i = 15 + world.random.nextInt(15) + world.random.nextInt(15);

        this.dropExperience(world, blockposition, i);
        */
    }

    @Override
    public int getExpDrop(IBlockData iblockdata, World world, BlockPosition blockposition, ItemStack itemstack) {
        int i = 15 + world.random.nextInt(15) + world.random.nextInt(15);

        return i;
        // CraftBukkit end
    }

    @Override
    public EnumRenderType c(IBlockData iblockdata) {
        return EnumRenderType.MODEL;
    }
}
