package net.minecraft.server;

public class BlockConcretePowder extends BlockFalling {

    private final IBlockData a;

    public BlockConcretePowder(Block block, Block.Info block_info) {
        super(block_info);
        this.a = block.getBlockData();
    }

    @Override
    public void a(World world, BlockPosition blockposition, IBlockData iblockdata, IBlockData iblockdata1) {
        if (canHarden(world, blockposition, iblockdata1)) {
            world.setTypeAndData(blockposition, this.a, 3);
        }

    }

    @Override
    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        World world = blockactioncontext.getWorld();
        BlockPosition blockposition = blockactioncontext.getClickPosition();
        IBlockData iblockdata = world.getType(blockposition);

        return canHarden(world, blockposition, iblockdata) ? this.a : super.getPlacedState(blockactioncontext);
    }

    private static boolean canHarden(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata) {
        return r(iblockdata) || a(iblockaccess, blockposition);
    }

    private static boolean a(IBlockAccess iblockaccess, BlockPosition blockposition) {
        boolean flag = false;
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition(blockposition);
        EnumDirection[] aenumdirection = EnumDirection.values();
        int i = aenumdirection.length;

        for (int j = 0; j < i; ++j) {
            EnumDirection enumdirection = aenumdirection[j];
            IBlockData iblockdata = iblockaccess.getType(blockposition_mutableblockposition);

            if (enumdirection != EnumDirection.DOWN || r(iblockdata)) {
                blockposition_mutableblockposition.g(blockposition).c(enumdirection);
                iblockdata = iblockaccess.getType(blockposition_mutableblockposition);
                if (r(iblockdata) && !iblockdata.d(iblockaccess, blockposition, enumdirection.opposite())) {
                    flag = true;
                    break;
                }
            }
        }

        return flag;
    }

    private static boolean r(IBlockData iblockdata) {
        return iblockdata.getFluid().a(TagsFluid.WATER);
    }

    @Override
    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        return a((IBlockAccess) generatoraccess, blockposition) ? this.a : super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }
}
