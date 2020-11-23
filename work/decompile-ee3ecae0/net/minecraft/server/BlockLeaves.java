package net.minecraft.server;

import java.util.Random;

public class BlockLeaves extends Block {

    public static final BlockStateInteger DISTANCE = BlockProperties.ah;
    public static final BlockStateBoolean PERSISTENT = BlockProperties.v;

    public BlockLeaves(Block.Info block_info) {
        super(block_info);
        this.p((IBlockData) ((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockLeaves.DISTANCE, 7)).set(BlockLeaves.PERSISTENT, false));
    }

    @Override
    public boolean isTicking(IBlockData iblockdata) {
        return (Integer) iblockdata.get(BlockLeaves.DISTANCE) == 7 && !(Boolean) iblockdata.get(BlockLeaves.PERSISTENT);
    }

    @Override
    public void b(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, Random random) {
        if (!(Boolean) iblockdata.get(BlockLeaves.PERSISTENT) && (Integer) iblockdata.get(BlockLeaves.DISTANCE) == 7) {
            c(iblockdata, (World) worldserver, blockposition);
            worldserver.a(blockposition, false);
        }

    }

    @Override
    public void tick(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, Random random) {
        worldserver.setTypeAndData(blockposition, a(iblockdata, (GeneratorAccess) worldserver, blockposition), 3);
    }

    @Override
    public int l(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return 1;
    }

    @Override
    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        int i = h(iblockdata1) + 1;

        if (i != 1 || (Integer) iblockdata.get(BlockLeaves.DISTANCE) != i) {
            generatoraccess.getBlockTickList().a(blockposition, this, 1);
        }

        return iblockdata;
    }

    private static IBlockData a(IBlockData iblockdata, GeneratorAccess generatoraccess, BlockPosition blockposition) {
        int i = 7;
        BlockPosition.PooledBlockPosition blockposition_pooledblockposition = BlockPosition.PooledBlockPosition.r();
        Throwable throwable = null;

        try {
            EnumDirection[] aenumdirection = EnumDirection.values();
            int j = aenumdirection.length;

            for (int k = 0; k < j; ++k) {
                EnumDirection enumdirection = aenumdirection[k];

                blockposition_pooledblockposition.g(blockposition).c(enumdirection);
                i = Math.min(i, h(generatoraccess.getType(blockposition_pooledblockposition)) + 1);
                if (i == 1) {
                    break;
                }
            }
        } catch (Throwable throwable1) {
            throwable = throwable1;
            throw throwable1;
        } finally {
            if (blockposition_pooledblockposition != null) {
                if (throwable != null) {
                    try {
                        blockposition_pooledblockposition.close();
                    } catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                } else {
                    blockposition_pooledblockposition.close();
                }
            }

        }

        return (IBlockData) iblockdata.set(BlockLeaves.DISTANCE, i);
    }

    private static int h(IBlockData iblockdata) {
        return TagsBlock.LOGS.isTagged(iblockdata.getBlock()) ? 0 : (iblockdata.getBlock() instanceof BlockLeaves ? (Integer) iblockdata.get(BlockLeaves.DISTANCE) : 7);
    }

    @Override
    public boolean c(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return false;
    }

    @Override
    public boolean a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EntityTypes<?> entitytypes) {
        return entitytypes == EntityTypes.OCELOT || entitytypes == EntityTypes.PARROT;
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockLeaves.DISTANCE, BlockLeaves.PERSISTENT);
    }

    @Override
    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        return a((IBlockData) this.getBlockData().set(BlockLeaves.PERSISTENT, true), (GeneratorAccess) blockactioncontext.getWorld(), blockactioncontext.getClickPosition());
    }
}
