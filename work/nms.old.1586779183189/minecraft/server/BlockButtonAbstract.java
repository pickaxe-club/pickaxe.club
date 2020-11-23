package net.minecraft.server;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;

// CraftBukkit start
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.EntityInteractEvent;
// CraftBukkit end

public abstract class BlockButtonAbstract extends BlockAttachable {

    public static final BlockStateBoolean POWERED = BlockProperties.w;
    protected static final VoxelShape b = Block.a(6.0D, 14.0D, 5.0D, 10.0D, 16.0D, 11.0D);
    protected static final VoxelShape c = Block.a(5.0D, 14.0D, 6.0D, 11.0D, 16.0D, 10.0D);
    protected static final VoxelShape d = Block.a(6.0D, 0.0D, 5.0D, 10.0D, 2.0D, 11.0D);
    protected static final VoxelShape e = Block.a(5.0D, 0.0D, 6.0D, 11.0D, 2.0D, 10.0D);
    protected static final VoxelShape f = Block.a(5.0D, 6.0D, 14.0D, 11.0D, 10.0D, 16.0D);
    protected static final VoxelShape g = Block.a(5.0D, 6.0D, 0.0D, 11.0D, 10.0D, 2.0D);
    protected static final VoxelShape h = Block.a(14.0D, 6.0D, 5.0D, 16.0D, 10.0D, 11.0D);
    protected static final VoxelShape i = Block.a(0.0D, 6.0D, 5.0D, 2.0D, 10.0D, 11.0D);
    protected static final VoxelShape j = Block.a(6.0D, 15.0D, 5.0D, 10.0D, 16.0D, 11.0D);
    protected static final VoxelShape k = Block.a(5.0D, 15.0D, 6.0D, 11.0D, 16.0D, 10.0D);
    protected static final VoxelShape w = Block.a(6.0D, 0.0D, 5.0D, 10.0D, 1.0D, 11.0D);
    protected static final VoxelShape x = Block.a(5.0D, 0.0D, 6.0D, 11.0D, 1.0D, 10.0D);
    protected static final VoxelShape y = Block.a(5.0D, 6.0D, 15.0D, 11.0D, 10.0D, 16.0D);
    protected static final VoxelShape z = Block.a(5.0D, 6.0D, 0.0D, 11.0D, 10.0D, 1.0D);
    protected static final VoxelShape A = Block.a(15.0D, 6.0D, 5.0D, 16.0D, 10.0D, 11.0D);
    protected static final VoxelShape B = Block.a(0.0D, 6.0D, 5.0D, 1.0D, 10.0D, 11.0D);
    private final boolean D;

    protected BlockButtonAbstract(boolean flag, Block.Info block_info) {
        super(block_info);
        this.p((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockButtonAbstract.FACING, EnumDirection.NORTH)).set(BlockButtonAbstract.POWERED, false)).set(BlockButtonAbstract.FACE, BlockPropertyAttachPosition.WALL));
        this.D = flag;
    }

    @Override
    public int a(IWorldReader iworldreader) {
        return this.D ? 30 : 20;
    }

    @Override
    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        EnumDirection enumdirection = (EnumDirection) iblockdata.get(BlockButtonAbstract.FACING);
        boolean flag = (Boolean) iblockdata.get(BlockButtonAbstract.POWERED);

        switch ((BlockPropertyAttachPosition) iblockdata.get(BlockButtonAbstract.FACE)) {
            case FLOOR:
                if (enumdirection.m() == EnumDirection.EnumAxis.X) {
                    return flag ? BlockButtonAbstract.w : BlockButtonAbstract.d;
                }

                return flag ? BlockButtonAbstract.x : BlockButtonAbstract.e;
            case WALL:
                switch (enumdirection) {
                    case EAST:
                        return flag ? BlockButtonAbstract.B : BlockButtonAbstract.i;
                    case WEST:
                        return flag ? BlockButtonAbstract.A : BlockButtonAbstract.h;
                    case SOUTH:
                        return flag ? BlockButtonAbstract.z : BlockButtonAbstract.g;
                    case NORTH:
                    default:
                        return flag ? BlockButtonAbstract.y : BlockButtonAbstract.f;
                }
            case CEILING:
            default:
                return enumdirection.m() == EnumDirection.EnumAxis.X ? (flag ? BlockButtonAbstract.j : BlockButtonAbstract.b) : (flag ? BlockButtonAbstract.k : BlockButtonAbstract.c);
        }
    }

    @Override
    public EnumInteractionResult interact(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        if ((Boolean) iblockdata.get(BlockButtonAbstract.POWERED)) {
            return EnumInteractionResult.CONSUME;
        } else {
            // CraftBukkit start
            boolean powered = ((Boolean) iblockdata.get(POWERED));
            org.bukkit.block.Block block = world.getWorld().getBlockAt(blockposition.getX(), blockposition.getY(), blockposition.getZ());
            int old = (powered) ? 15 : 0;
            int current = (!powered) ? 15 : 0;

            BlockRedstoneEvent eventRedstone = new BlockRedstoneEvent(block, old, current);
            world.getServer().getPluginManager().callEvent(eventRedstone);

            if ((eventRedstone.getNewCurrent() > 0) != (!powered)) {
                return EnumInteractionResult.SUCCESS;
            }
            // CraftBukkit end
            this.d(iblockdata, world, blockposition);
            this.a(entityhuman, world, blockposition, true);
            return EnumInteractionResult.SUCCESS;
        }
    }

    public void d(IBlockData iblockdata, World world, BlockPosition blockposition) {
        world.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockButtonAbstract.POWERED, true), 3);
        this.f(iblockdata, world, blockposition);
        world.getBlockTickList().a(blockposition, this, this.a((IWorldReader) world));
    }

    protected void a(@Nullable EntityHuman entityhuman, GeneratorAccess generatoraccess, BlockPosition blockposition, boolean flag) {
        generatoraccess.playSound(flag ? entityhuman : null, blockposition, this.a(flag), SoundCategory.BLOCKS, 0.3F, flag ? 0.6F : 0.5F);
    }

    protected abstract SoundEffect a(boolean flag);

    @Override
    public void remove(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (!flag && iblockdata.getBlock() != iblockdata1.getBlock()) {
            if ((Boolean) iblockdata.get(BlockButtonAbstract.POWERED)) {
                this.f(iblockdata, world, blockposition);
            }

            super.remove(iblockdata, world, blockposition, iblockdata1, flag);
        }
    }

    @Override
    public int a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return (Boolean) iblockdata.get(BlockButtonAbstract.POWERED) ? 15 : 0;
    }

    @Override
    public int b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return (Boolean) iblockdata.get(BlockButtonAbstract.POWERED) && h(iblockdata) == enumdirection ? 15 : 0;
    }

    @Override
    public boolean isPowerSource(IBlockData iblockdata) {
        return true;
    }

    @Override
    public void tick(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, Random random) {
        if ((Boolean) iblockdata.get(BlockButtonAbstract.POWERED)) {
            if (this.D) {
                this.e(iblockdata, (World) worldserver, blockposition);
            } else {
                // CraftBukkit start
                org.bukkit.block.Block block = worldserver.getWorld().getBlockAt(blockposition.getX(), blockposition.getY(), blockposition.getZ());

                BlockRedstoneEvent eventRedstone = new BlockRedstoneEvent(block, 15, 0);
                worldserver.getServer().getPluginManager().callEvent(eventRedstone);

                if (eventRedstone.getNewCurrent() > 0) {
                    return;
                }
                // CraftBukkit end
                worldserver.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockButtonAbstract.POWERED, false), 3);
                this.f(iblockdata, worldserver, blockposition);
                this.a((EntityHuman) null, worldserver, blockposition, false);
            }

        }
    }

    @Override
    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Entity entity) {
        if (!world.isClientSide && this.D && !(Boolean) iblockdata.get(BlockButtonAbstract.POWERED)) {
            this.e(iblockdata, world, blockposition);
        }
    }

    private void e(IBlockData iblockdata, World world, BlockPosition blockposition) {
        List<? extends Entity> list = world.a(EntityArrow.class, iblockdata.getShape(world, blockposition).getBoundingBox().a(blockposition));
        boolean flag = !list.isEmpty();
        boolean flag1 = (Boolean) iblockdata.get(BlockButtonAbstract.POWERED);

        // CraftBukkit start - Call interact event when arrows turn on wooden buttons
        if (flag1 != flag && flag) {
            org.bukkit.block.Block block = world.getWorld().getBlockAt(blockposition.getX(), blockposition.getY(), blockposition.getZ());
            boolean allowed = false;

            // If all of the events are cancelled block the button press, else allow
            for (Object object : list) {
                if (object != null) {
                    EntityInteractEvent event = new EntityInteractEvent(((Entity) object).getBukkitEntity(), block);
                    world.getServer().getPluginManager().callEvent(event);

                    if (!event.isCancelled()) {
                        allowed = true;
                        break;
                    }
                }
            }

            if (!allowed) {
                return;
            }
        }
        // CraftBukkit end

        if (flag != flag1) {
            // CraftBukkit start
            boolean powered = flag1;
            org.bukkit.block.Block block = world.getWorld().getBlockAt(blockposition.getX(), blockposition.getY(), blockposition.getZ());
            int old = (powered) ? 15 : 0;
            int current = (!powered) ? 15 : 0;

            BlockRedstoneEvent eventRedstone = new BlockRedstoneEvent(block, old, current);
            world.getServer().getPluginManager().callEvent(eventRedstone);

            if ((flag && eventRedstone.getNewCurrent() <= 0) || (!flag && eventRedstone.getNewCurrent() > 0)) {
                return;
            }
            // CraftBukkit end
            world.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockButtonAbstract.POWERED, flag), 3);
            this.f(iblockdata, world, blockposition);
            this.a((EntityHuman) null, world, blockposition, flag);
        }

        if (flag) {
            world.getBlockTickList().a(new BlockPosition(blockposition), this, this.a((IWorldReader) world));
        }

    }

    private void f(IBlockData iblockdata, World world, BlockPosition blockposition) {
        world.applyPhysics(blockposition, this);
        world.applyPhysics(blockposition.shift(h(iblockdata).opposite()), this);
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockButtonAbstract.FACING, BlockButtonAbstract.POWERED, BlockButtonAbstract.FACE);
    }
}
