package net.minecraft.server;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public class BlockRedstoneWire extends Block {

    public static final BlockStateEnum<BlockPropertyRedstoneSide> NORTH = BlockProperties.R;
    public static final BlockStateEnum<BlockPropertyRedstoneSide> EAST = BlockProperties.Q;
    public static final BlockStateEnum<BlockPropertyRedstoneSide> SOUTH = BlockProperties.S;
    public static final BlockStateEnum<BlockPropertyRedstoneSide> WEST = BlockProperties.T;
    public static final BlockStateInteger POWER = BlockProperties.at;
    public static final Map<EnumDirection, BlockStateEnum<BlockPropertyRedstoneSide>> f = Maps.newEnumMap(ImmutableMap.of(EnumDirection.NORTH, BlockRedstoneWire.NORTH, EnumDirection.EAST, BlockRedstoneWire.EAST, EnumDirection.SOUTH, BlockRedstoneWire.SOUTH, EnumDirection.WEST, BlockRedstoneWire.WEST));
    protected static final VoxelShape[] g = new VoxelShape[]{Block.a(3.0D, 0.0D, 3.0D, 13.0D, 1.0D, 13.0D), Block.a(3.0D, 0.0D, 3.0D, 13.0D, 1.0D, 16.0D), Block.a(0.0D, 0.0D, 3.0D, 13.0D, 1.0D, 13.0D), Block.a(0.0D, 0.0D, 3.0D, 13.0D, 1.0D, 16.0D), Block.a(3.0D, 0.0D, 0.0D, 13.0D, 1.0D, 13.0D), Block.a(3.0D, 0.0D, 0.0D, 13.0D, 1.0D, 16.0D), Block.a(0.0D, 0.0D, 0.0D, 13.0D, 1.0D, 13.0D), Block.a(0.0D, 0.0D, 0.0D, 13.0D, 1.0D, 16.0D), Block.a(3.0D, 0.0D, 3.0D, 16.0D, 1.0D, 13.0D), Block.a(3.0D, 0.0D, 3.0D, 16.0D, 1.0D, 16.0D), Block.a(0.0D, 0.0D, 3.0D, 16.0D, 1.0D, 13.0D), Block.a(0.0D, 0.0D, 3.0D, 16.0D, 1.0D, 16.0D), Block.a(3.0D, 0.0D, 0.0D, 16.0D, 1.0D, 13.0D), Block.a(3.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D), Block.a(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 13.0D), Block.a(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D)};
    private boolean h = true;
    private final Set<BlockPosition> i = Sets.newHashSet();

    public BlockRedstoneWire(Block.Info block_info) {
        super(block_info);
        this.p((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockRedstoneWire.NORTH, BlockPropertyRedstoneSide.NONE)).set(BlockRedstoneWire.EAST, BlockPropertyRedstoneSide.NONE)).set(BlockRedstoneWire.SOUTH, BlockPropertyRedstoneSide.NONE)).set(BlockRedstoneWire.WEST, BlockPropertyRedstoneSide.NONE)).set(BlockRedstoneWire.POWER, 0));
    }

    @Override
    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return BlockRedstoneWire.g[i(iblockdata)];
    }

    private static int i(IBlockData iblockdata) {
        int i = 0;
        boolean flag = iblockdata.get(BlockRedstoneWire.NORTH) != BlockPropertyRedstoneSide.NONE;
        boolean flag1 = iblockdata.get(BlockRedstoneWire.EAST) != BlockPropertyRedstoneSide.NONE;
        boolean flag2 = iblockdata.get(BlockRedstoneWire.SOUTH) != BlockPropertyRedstoneSide.NONE;
        boolean flag3 = iblockdata.get(BlockRedstoneWire.WEST) != BlockPropertyRedstoneSide.NONE;

        if (flag || flag2 && !flag && !flag1 && !flag3) {
            i |= 1 << EnumDirection.NORTH.get2DRotationValue();
        }

        if (flag1 || flag3 && !flag && !flag1 && !flag2) {
            i |= 1 << EnumDirection.EAST.get2DRotationValue();
        }

        if (flag2 || flag && !flag1 && !flag2 && !flag3) {
            i |= 1 << EnumDirection.SOUTH.get2DRotationValue();
        }

        if (flag3 || flag1 && !flag && !flag2 && !flag3) {
            i |= 1 << EnumDirection.WEST.get2DRotationValue();
        }

        return i;
    }

    @Override
    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        World world = blockactioncontext.getWorld();
        BlockPosition blockposition = blockactioncontext.getClickPosition();

        return (IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.getBlockData().set(BlockRedstoneWire.WEST, this.a((IBlockAccess) world, blockposition, EnumDirection.WEST))).set(BlockRedstoneWire.EAST, this.a((IBlockAccess) world, blockposition, EnumDirection.EAST))).set(BlockRedstoneWire.NORTH, this.a((IBlockAccess) world, blockposition, EnumDirection.NORTH))).set(BlockRedstoneWire.SOUTH, this.a((IBlockAccess) world, blockposition, EnumDirection.SOUTH));
    }

    @Override
    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        return enumdirection == EnumDirection.DOWN ? iblockdata : (enumdirection == EnumDirection.UP ? (IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) iblockdata.set(BlockRedstoneWire.WEST, this.a((IBlockAccess) generatoraccess, blockposition, EnumDirection.WEST))).set(BlockRedstoneWire.EAST, this.a((IBlockAccess) generatoraccess, blockposition, EnumDirection.EAST))).set(BlockRedstoneWire.NORTH, this.a((IBlockAccess) generatoraccess, blockposition, EnumDirection.NORTH))).set(BlockRedstoneWire.SOUTH, this.a((IBlockAccess) generatoraccess, blockposition, EnumDirection.SOUTH)) : (IBlockData) iblockdata.set((IBlockState) BlockRedstoneWire.f.get(enumdirection), this.a((IBlockAccess) generatoraccess, blockposition, enumdirection)));
    }

    @Override
    public void b(IBlockData iblockdata, GeneratorAccess generatoraccess, BlockPosition blockposition, int i) {
        BlockPosition.PooledBlockPosition blockposition_pooledblockposition = BlockPosition.PooledBlockPosition.r();
        Throwable throwable = null;

        try {
            Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

            while (iterator.hasNext()) {
                EnumDirection enumdirection = (EnumDirection) iterator.next();
                BlockPropertyRedstoneSide blockpropertyredstoneside = (BlockPropertyRedstoneSide) iblockdata.get((IBlockState) BlockRedstoneWire.f.get(enumdirection));

                if (blockpropertyredstoneside != BlockPropertyRedstoneSide.NONE && generatoraccess.getType(blockposition_pooledblockposition.g(blockposition).c(enumdirection)).getBlock() != this) {
                    blockposition_pooledblockposition.c(EnumDirection.DOWN);
                    IBlockData iblockdata1 = generatoraccess.getType(blockposition_pooledblockposition);

                    if (iblockdata1.getBlock() != Blocks.OBSERVER) {
                        BlockPosition blockposition1 = blockposition_pooledblockposition.shift(enumdirection.opposite());
                        IBlockData iblockdata2 = iblockdata1.updateState(enumdirection.opposite(), generatoraccess.getType(blockposition1), generatoraccess, blockposition_pooledblockposition, blockposition1);

                        a(iblockdata1, iblockdata2, generatoraccess, blockposition_pooledblockposition, i);
                    }

                    blockposition_pooledblockposition.g(blockposition).c(enumdirection).c(EnumDirection.UP);
                    IBlockData iblockdata3 = generatoraccess.getType(blockposition_pooledblockposition);

                    if (iblockdata3.getBlock() != Blocks.OBSERVER) {
                        BlockPosition blockposition2 = blockposition_pooledblockposition.shift(enumdirection.opposite());
                        IBlockData iblockdata4 = iblockdata3.updateState(enumdirection.opposite(), generatoraccess.getType(blockposition2), generatoraccess, blockposition_pooledblockposition, blockposition2);

                        a(iblockdata3, iblockdata4, generatoraccess, blockposition_pooledblockposition, i);
                    }
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

    }

    private BlockPropertyRedstoneSide a(IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        BlockPosition blockposition1 = blockposition.shift(enumdirection);
        IBlockData iblockdata = iblockaccess.getType(blockposition1);
        BlockPosition blockposition2 = blockposition.up();
        IBlockData iblockdata1 = iblockaccess.getType(blockposition2);

        if (!iblockdata1.isOccluding(iblockaccess, blockposition2)) {
            boolean flag = iblockdata.d(iblockaccess, blockposition1, EnumDirection.UP) || iblockdata.getBlock() == Blocks.HOPPER;

            if (flag && h(iblockaccess.getType(blockposition1.up()))) {
                if (iblockdata.p(iblockaccess, blockposition1)) {
                    return BlockPropertyRedstoneSide.UP;
                }

                return BlockPropertyRedstoneSide.SIDE;
            }
        }

        return !a(iblockdata, enumdirection) && (iblockdata.isOccluding(iblockaccess, blockposition1) || !h(iblockaccess.getType(blockposition1.down()))) ? BlockPropertyRedstoneSide.NONE : BlockPropertyRedstoneSide.SIDE;
    }

    @Override
    public boolean canPlace(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        BlockPosition blockposition1 = blockposition.down();
        IBlockData iblockdata1 = iworldreader.getType(blockposition1);

        return iblockdata1.d(iworldreader, blockposition1, EnumDirection.UP) || iblockdata1.getBlock() == Blocks.HOPPER;
    }

    private IBlockData a(World world, BlockPosition blockposition, IBlockData iblockdata) {
        iblockdata = this.b(world, blockposition, iblockdata);
        List<BlockPosition> list = Lists.newArrayList(this.i);

        this.i.clear();
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            BlockPosition blockposition1 = (BlockPosition) iterator.next();

            world.applyPhysics(blockposition1, this);
        }

        return iblockdata;
    }

    private IBlockData b(World world, BlockPosition blockposition, IBlockData iblockdata) {
        IBlockData iblockdata1 = iblockdata;
        int i = (Integer) iblockdata.get(BlockRedstoneWire.POWER);

        this.h = false;
        int j = world.q(blockposition);

        this.h = true;
        int k = 0;

        if (j < 15) {
            Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

            while (iterator.hasNext()) {
                EnumDirection enumdirection = (EnumDirection) iterator.next();
                BlockPosition blockposition1 = blockposition.shift(enumdirection);
                IBlockData iblockdata2 = world.getType(blockposition1);

                k = this.getPower(k, iblockdata2);
                BlockPosition blockposition2 = blockposition.up();

                if (iblockdata2.isOccluding(world, blockposition1) && !world.getType(blockposition2).isOccluding(world, blockposition2)) {
                    k = this.getPower(k, world.getType(blockposition1.up()));
                } else if (!iblockdata2.isOccluding(world, blockposition1)) {
                    k = this.getPower(k, world.getType(blockposition1.down()));
                }
            }
        }

        int l = k - 1;

        if (j > l) {
            l = j;
        }

        if (i != l) {
            iblockdata = (IBlockData) iblockdata.set(BlockRedstoneWire.POWER, l);
            if (world.getType(blockposition) == iblockdata1) {
                world.setTypeAndData(blockposition, iblockdata, 2);
            }

            this.i.add(blockposition);
            EnumDirection[] aenumdirection = EnumDirection.values();
            int i1 = aenumdirection.length;

            for (int j1 = 0; j1 < i1; ++j1) {
                EnumDirection enumdirection1 = aenumdirection[j1];

                this.i.add(blockposition.shift(enumdirection1));
            }
        }

        return iblockdata;
    }

    private void a(World world, BlockPosition blockposition) {
        if (world.getType(blockposition).getBlock() == this) {
            world.applyPhysics(blockposition, this);
            EnumDirection[] aenumdirection = EnumDirection.values();
            int i = aenumdirection.length;

            for (int j = 0; j < i; ++j) {
                EnumDirection enumdirection = aenumdirection[j];

                world.applyPhysics(blockposition.shift(enumdirection), this);
            }

        }
    }

    @Override
    public void onPlace(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (iblockdata1.getBlock() != iblockdata.getBlock() && !world.isClientSide) {
            this.a(world, blockposition, iblockdata);
            Iterator iterator = EnumDirection.EnumDirectionLimit.VERTICAL.iterator();

            EnumDirection enumdirection;

            while (iterator.hasNext()) {
                enumdirection = (EnumDirection) iterator.next();
                world.applyPhysics(blockposition.shift(enumdirection), this);
            }

            iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

            while (iterator.hasNext()) {
                enumdirection = (EnumDirection) iterator.next();
                this.a(world, blockposition.shift(enumdirection));
            }

            iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

            while (iterator.hasNext()) {
                enumdirection = (EnumDirection) iterator.next();
                BlockPosition blockposition1 = blockposition.shift(enumdirection);

                if (world.getType(blockposition1).isOccluding(world, blockposition1)) {
                    this.a(world, blockposition1.up());
                } else {
                    this.a(world, blockposition1.down());
                }
            }

        }
    }

    @Override
    public void remove(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (!flag && iblockdata.getBlock() != iblockdata1.getBlock()) {
            super.remove(iblockdata, world, blockposition, iblockdata1, flag);
            if (!world.isClientSide) {
                EnumDirection[] aenumdirection = EnumDirection.values();
                int i = aenumdirection.length;

                for (int j = 0; j < i; ++j) {
                    EnumDirection enumdirection = aenumdirection[j];

                    world.applyPhysics(blockposition.shift(enumdirection), this);
                }

                this.a(world, blockposition, iblockdata);
                Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

                EnumDirection enumdirection1;

                while (iterator.hasNext()) {
                    enumdirection1 = (EnumDirection) iterator.next();
                    this.a(world, blockposition.shift(enumdirection1));
                }

                iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

                while (iterator.hasNext()) {
                    enumdirection1 = (EnumDirection) iterator.next();
                    BlockPosition blockposition1 = blockposition.shift(enumdirection1);

                    if (world.getType(blockposition1).isOccluding(world, blockposition1)) {
                        this.a(world, blockposition1.up());
                    } else {
                        this.a(world, blockposition1.down());
                    }
                }

            }
        }
    }

    public int getPower(int i, IBlockData iblockdata) {
        if (iblockdata.getBlock() != this) {
            return i;
        } else {
            int j = (Integer) iblockdata.get(BlockRedstoneWire.POWER);

            return j > i ? j : i;
        }
    }

    @Override
    public void doPhysics(IBlockData iblockdata, World world, BlockPosition blockposition, Block block, BlockPosition blockposition1, boolean flag) {
        if (!world.isClientSide) {
            if (iblockdata.canPlace(world, blockposition)) {
                this.a(world, blockposition, iblockdata);
            } else {
                c(iblockdata, world, blockposition);
                world.a(blockposition, false);
            }

        }
    }

    @Override
    public int b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return !this.h ? 0 : iblockdata.b(iblockaccess, blockposition, enumdirection);
    }

    @Override
    public int a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        if (!this.h) {
            return 0;
        } else {
            int i = (Integer) iblockdata.get(BlockRedstoneWire.POWER);

            if (i == 0) {
                return 0;
            } else if (enumdirection == EnumDirection.UP) {
                return i;
            } else {
                EnumSet<EnumDirection> enumset = EnumSet.noneOf(EnumDirection.class);
                Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

                while (iterator.hasNext()) {
                    EnumDirection enumdirection1 = (EnumDirection) iterator.next();

                    if (this.b(iblockaccess, blockposition, enumdirection1)) {
                        enumset.add(enumdirection1);
                    }
                }

                if (enumdirection.m().c() && enumset.isEmpty()) {
                    return i;
                } else if (enumset.contains(enumdirection) && !enumset.contains(enumdirection.g()) && !enumset.contains(enumdirection.f())) {
                    return i;
                } else {
                    return 0;
                }
            }
        }
    }

    private boolean b(IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        BlockPosition blockposition1 = blockposition.shift(enumdirection);
        IBlockData iblockdata = iblockaccess.getType(blockposition1);
        boolean flag = iblockdata.isOccluding(iblockaccess, blockposition1);
        BlockPosition blockposition2 = blockposition.up();
        boolean flag1 = iblockaccess.getType(blockposition2).isOccluding(iblockaccess, blockposition2);

        return !flag1 && flag && a(iblockaccess, blockposition1.up()) ? true : (a(iblockdata, enumdirection) ? true : (iblockdata.getBlock() == Blocks.REPEATER && (Boolean) iblockdata.get(BlockDiodeAbstract.c) && iblockdata.get(BlockDiodeAbstract.FACING) == enumdirection ? true : !flag && a(iblockaccess, blockposition1.down())));
    }

    protected static boolean a(IBlockAccess iblockaccess, BlockPosition blockposition) {
        return h(iblockaccess.getType(blockposition));
    }

    protected static boolean h(IBlockData iblockdata) {
        return a(iblockdata, (EnumDirection) null);
    }

    protected static boolean a(IBlockData iblockdata, @Nullable EnumDirection enumdirection) {
        Block block = iblockdata.getBlock();

        if (block == Blocks.REDSTONE_WIRE) {
            return true;
        } else if (iblockdata.getBlock() == Blocks.REPEATER) {
            EnumDirection enumdirection1 = (EnumDirection) iblockdata.get(BlockRepeater.FACING);

            return enumdirection1 == enumdirection || enumdirection1.opposite() == enumdirection;
        } else {
            return Blocks.OBSERVER == iblockdata.getBlock() ? enumdirection == iblockdata.get(BlockObserver.FACING) : iblockdata.isPowerSource() && enumdirection != null;
        }
    }

    @Override
    public boolean isPowerSource(IBlockData iblockdata) {
        return this.h;
    }

    @Override
    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        switch (enumblockrotation) {
            case CLOCKWISE_180:
                return (IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) iblockdata.set(BlockRedstoneWire.NORTH, iblockdata.get(BlockRedstoneWire.SOUTH))).set(BlockRedstoneWire.EAST, iblockdata.get(BlockRedstoneWire.WEST))).set(BlockRedstoneWire.SOUTH, iblockdata.get(BlockRedstoneWire.NORTH))).set(BlockRedstoneWire.WEST, iblockdata.get(BlockRedstoneWire.EAST));
            case COUNTERCLOCKWISE_90:
                return (IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) iblockdata.set(BlockRedstoneWire.NORTH, iblockdata.get(BlockRedstoneWire.EAST))).set(BlockRedstoneWire.EAST, iblockdata.get(BlockRedstoneWire.SOUTH))).set(BlockRedstoneWire.SOUTH, iblockdata.get(BlockRedstoneWire.WEST))).set(BlockRedstoneWire.WEST, iblockdata.get(BlockRedstoneWire.NORTH));
            case CLOCKWISE_90:
                return (IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) iblockdata.set(BlockRedstoneWire.NORTH, iblockdata.get(BlockRedstoneWire.WEST))).set(BlockRedstoneWire.EAST, iblockdata.get(BlockRedstoneWire.NORTH))).set(BlockRedstoneWire.SOUTH, iblockdata.get(BlockRedstoneWire.EAST))).set(BlockRedstoneWire.WEST, iblockdata.get(BlockRedstoneWire.SOUTH));
            default:
                return iblockdata;
        }
    }

    @Override
    public IBlockData a(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        switch (enumblockmirror) {
            case LEFT_RIGHT:
                return (IBlockData) ((IBlockData) iblockdata.set(BlockRedstoneWire.NORTH, iblockdata.get(BlockRedstoneWire.SOUTH))).set(BlockRedstoneWire.SOUTH, iblockdata.get(BlockRedstoneWire.NORTH));
            case FRONT_BACK:
                return (IBlockData) ((IBlockData) iblockdata.set(BlockRedstoneWire.EAST, iblockdata.get(BlockRedstoneWire.WEST))).set(BlockRedstoneWire.WEST, iblockdata.get(BlockRedstoneWire.EAST));
            default:
                return super.a(iblockdata, enumblockmirror);
        }
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockRedstoneWire.NORTH, BlockRedstoneWire.EAST, BlockRedstoneWire.SOUTH, BlockRedstoneWire.WEST, BlockRedstoneWire.POWER);
    }
}
