package net.minecraft.server;

import javax.annotation.Nullable;

public class BlockTNT extends Block {

    public static final BlockStateBoolean a = BlockProperties.B;

    public BlockTNT(Block.Info block_info) {
        super(block_info);
        this.p((IBlockData) this.getBlockData().set(BlockTNT.a, false));
    }

    @Override
    public void onPlace(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (iblockdata1.getBlock() != iblockdata.getBlock()) {
            if (world.isBlockIndirectlyPowered(blockposition)) {
                a(world, blockposition);
                world.a(blockposition, false);
            }

        }
    }

    @Override
    public void doPhysics(IBlockData iblockdata, World world, BlockPosition blockposition, Block block, BlockPosition blockposition1, boolean flag) {
        if (world.isBlockIndirectlyPowered(blockposition)) {
            a(world, blockposition);
            world.a(blockposition, false);
        }

    }

    @Override
    public void a(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman) {
        if (!world.p_() && !entityhuman.isCreative() && (Boolean) iblockdata.get(BlockTNT.a)) {
            a(world, blockposition);
        }

        super.a(world, blockposition, iblockdata, entityhuman);
    }

    @Override
    public void wasExploded(World world, BlockPosition blockposition, Explosion explosion) {
        if (!world.isClientSide) {
            EntityTNTPrimed entitytntprimed = new EntityTNTPrimed(world, (double) ((float) blockposition.getX() + 0.5F), (double) blockposition.getY(), (double) ((float) blockposition.getZ() + 0.5F), explosion.getSource());

            entitytntprimed.setFuseTicks((short) (world.random.nextInt(entitytntprimed.getFuseTicks() / 4) + entitytntprimed.getFuseTicks() / 8));
            world.addEntity(entitytntprimed);
        }
    }

    public static void a(World world, BlockPosition blockposition) {
        a(world, blockposition, (EntityLiving) null);
    }

    private static void a(World world, BlockPosition blockposition, @Nullable EntityLiving entityliving) {
        if (!world.isClientSide) {
            EntityTNTPrimed entitytntprimed = new EntityTNTPrimed(world, (double) blockposition.getX() + 0.5D, (double) blockposition.getY(), (double) blockposition.getZ() + 0.5D, entityliving);

            world.addEntity(entitytntprimed);
            world.playSound((EntityHuman) null, entitytntprimed.locX(), entitytntprimed.locY(), entitytntprimed.locZ(), SoundEffects.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
        }
    }

    @Override
    public EnumInteractionResult interact(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        ItemStack itemstack = entityhuman.b(enumhand);
        Item item = itemstack.getItem();

        if (item != Items.FLINT_AND_STEEL && item != Items.FIRE_CHARGE) {
            return super.interact(iblockdata, world, blockposition, entityhuman, enumhand, movingobjectpositionblock);
        } else {
            a(world, blockposition, (EntityLiving) entityhuman);
            world.setTypeAndData(blockposition, Blocks.AIR.getBlockData(), 11);
            if (!entityhuman.isCreative()) {
                if (item == Items.FLINT_AND_STEEL) {
                    itemstack.damage(1, entityhuman, (entityhuman1) -> {
                        entityhuman1.broadcastItemBreak(enumhand);
                    });
                } else {
                    itemstack.subtract(1);
                }
            }

            return EnumInteractionResult.SUCCESS;
        }
    }

    @Override
    public void a(World world, IBlockData iblockdata, MovingObjectPositionBlock movingobjectpositionblock, Entity entity) {
        if (!world.isClientSide && entity instanceof EntityArrow) {
            EntityArrow entityarrow = (EntityArrow) entity;
            Entity entity1 = entityarrow.getShooter();

            if (entityarrow.isBurning()) {
                BlockPosition blockposition = movingobjectpositionblock.getBlockPosition();
                // CraftBukkit start
                if (org.bukkit.craftbukkit.event.CraftEventFactory.callEntityChangeBlockEvent(entityarrow, blockposition, Blocks.AIR.getBlockData()).isCancelled()) {
                    return;
                }
                // CraftBukkit end

                a(world, blockposition, entity1 instanceof EntityLiving ? (EntityLiving) entity1 : null);
                world.a(blockposition, false);
            }
        }

    }

    @Override
    public boolean a(Explosion explosion) {
        return false;
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockTNT.a);
    }
}
