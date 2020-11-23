package net.minecraft.server;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class BlockFluids extends Block implements IFluidSource {

    public static final BlockStateInteger LEVEL = BlockProperties.ap;
    protected final FluidTypeFlowing b;
    private final List<Fluid> c;

    protected BlockFluids(FluidTypeFlowing fluidtypeflowing, Block.Info block_info) {
        super(block_info);
        this.b = fluidtypeflowing;
        this.c = Lists.newArrayList();
        this.c.add(fluidtypeflowing.a(false));

        for (int i = 1; i < 8; ++i) {
            this.c.add(fluidtypeflowing.a(8 - i, false));
        }

        this.c.add(fluidtypeflowing.a(8, true));
        this.p((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockFluids.LEVEL, 0));
    }

    @Override
    public void b(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, Random random) {
        worldserver.getFluid(blockposition).b(worldserver, blockposition, random);
    }

    @Override
    public boolean b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return false;
    }

    @Override
    public boolean a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        return !this.b.a(TagsFluid.LAVA);
    }

    @Override
    public Fluid a_(IBlockData iblockdata) {
        int i = (Integer) iblockdata.get(BlockFluids.LEVEL);

        return (Fluid) this.c.get(Math.min(i, 8));
    }

    @Override
    public EnumRenderType c(IBlockData iblockdata) {
        return EnumRenderType.INVISIBLE;
    }

    @Override
    public List<ItemStack> a(IBlockData iblockdata, LootTableInfo.Builder loottableinfo_builder) {
        return Collections.emptyList();
    }

    @Override
    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return VoxelShapes.a();
    }

    @Override
    public int a(IWorldReader iworldreader) {
        return this.b.a(iworldreader);
    }

    @Override
    public void onPlace(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (this.a(world, blockposition, iblockdata)) {
            world.getFluidTickList().a(blockposition, iblockdata.getFluid().getType(), this.a((IWorldReader) world));
        }

    }

    @Override
    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        if (iblockdata.getFluid().isSource() || iblockdata1.getFluid().isSource()) {
            generatoraccess.getFluidTickList().a(blockposition, iblockdata.getFluid().getType(), this.a((IWorldReader) generatoraccess));
        }

        return super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    @Override
    public void doPhysics(IBlockData iblockdata, World world, BlockPosition blockposition, Block block, BlockPosition blockposition1, boolean flag) {
        if (this.a(world, blockposition, iblockdata)) {
            world.getFluidTickList().a(blockposition, iblockdata.getFluid().getType(), this.a((IWorldReader) world));
        }

    }

    public boolean a(World world, BlockPosition blockposition, IBlockData iblockdata) {
        if (this.b.a(TagsFluid.LAVA)) {
            boolean flag = false;
            EnumDirection[] aenumdirection = EnumDirection.values();
            int i = aenumdirection.length;

            for (int j = 0; j < i; ++j) {
                EnumDirection enumdirection = aenumdirection[j];

                if (enumdirection != EnumDirection.DOWN && world.getFluid(blockposition.shift(enumdirection)).a(TagsFluid.WATER)) {
                    flag = true;
                    break;
                }
            }

            if (flag) {
                Fluid fluid = world.getFluid(blockposition);

                if (fluid.isSource()) {
                    world.setTypeUpdate(blockposition, Blocks.OBSIDIAN.getBlockData());
                    this.fizz(world, blockposition);
                    return false;
                }

                if (fluid.getHeight(world, blockposition) >= 0.44444445F) {
                    world.setTypeUpdate(blockposition, Blocks.COBBLESTONE.getBlockData());
                    this.fizz(world, blockposition);
                    return false;
                }
            }
        }

        return true;
    }

    private void fizz(GeneratorAccess generatoraccess, BlockPosition blockposition) {
        generatoraccess.triggerEffect(1501, blockposition, 0);
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockFluids.LEVEL);
    }

    @Override
    public FluidType removeFluid(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata) {
        if ((Integer) iblockdata.get(BlockFluids.LEVEL) == 0) {
            generatoraccess.setTypeAndData(blockposition, Blocks.AIR.getBlockData(), 11);
            return this.b;
        } else {
            return FluidTypes.EMPTY;
        }
    }

    @Override
    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Entity entity) {
        if (this.b.a(TagsFluid.LAVA)) {
            entity.aG();
        }

    }
}
