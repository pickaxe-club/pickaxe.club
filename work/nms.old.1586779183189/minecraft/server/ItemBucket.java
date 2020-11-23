package net.minecraft.server;

import javax.annotation.Nullable;
// CraftBukkit start
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.util.DummyGeneratorAccess;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
// CraftBukkit end

public class ItemBucket extends Item {

    public final FluidType fluidType;

    public ItemBucket(FluidType fluidtype, Item.Info item_info) {
        super(item_info);
        this.fluidType = fluidtype;
    }

    @Override
    public InteractionResultWrapper<ItemStack> a(World world, EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);
        MovingObjectPosition movingobjectposition = a(world, entityhuman, this.fluidType == FluidTypes.EMPTY ? RayTrace.FluidCollisionOption.SOURCE_ONLY : RayTrace.FluidCollisionOption.NONE);

        if (movingobjectposition.getType() == MovingObjectPosition.EnumMovingObjectType.MISS) {
            return InteractionResultWrapper.pass(itemstack);
        } else if (movingobjectposition.getType() != MovingObjectPosition.EnumMovingObjectType.BLOCK) {
            return InteractionResultWrapper.pass(itemstack);
        } else {
            MovingObjectPositionBlock movingobjectpositionblock = (MovingObjectPositionBlock) movingobjectposition;
            BlockPosition blockposition = movingobjectpositionblock.getBlockPosition();
            EnumDirection enumdirection = movingobjectpositionblock.getDirection();
            BlockPosition blockposition1 = blockposition.shift(enumdirection);

            if (world.a(entityhuman, blockposition) && entityhuman.a(blockposition1, enumdirection, itemstack)) {
                IBlockData iblockdata;

                if (this.fluidType == FluidTypes.EMPTY) {
                    iblockdata = world.getType(blockposition);
                    if (iblockdata.getBlock() instanceof IFluidSource) {
                        // CraftBukkit start
                        FluidType dummyFluid = ((IFluidSource) iblockdata.getBlock()).removeFluid(DummyGeneratorAccess.INSTANCE, blockposition, iblockdata);
                        PlayerBucketFillEvent event = CraftEventFactory.callPlayerBucketFillEvent(world, entityhuman, blockposition, blockposition, movingobjectpositionblock.getDirection(), itemstack, dummyFluid.a());

                        if (event.isCancelled()) {
                            ((EntityPlayer) entityhuman).playerConnection.sendPacket(new PacketPlayOutBlockChange(world, blockposition)); // SPIGOT-5163 (see PlayerInteractManager)
                            ((EntityPlayer) entityhuman).getBukkitEntity().updateInventory(); // SPIGOT-4541
                            return new InteractionResultWrapper(EnumInteractionResult.FAIL, itemstack);
                        }
                        // CraftBukkit end
                        FluidType fluidtype = ((IFluidSource) iblockdata.getBlock()).removeFluid(world, blockposition, iblockdata);

                        if (fluidtype != FluidTypes.EMPTY) {
                            entityhuman.b(StatisticList.ITEM_USED.b(this));
                            entityhuman.a(fluidtype.a(TagsFluid.LAVA) ? SoundEffects.ITEM_BUCKET_FILL_LAVA : SoundEffects.ITEM_BUCKET_FILL, 1.0F, 1.0F);
                            ItemStack itemstack1 = this.a(itemstack, entityhuman, fluidtype.a(), event.getItemStack()); // CraftBukkit

                            if (!world.isClientSide) {
                                CriterionTriggers.j.a((EntityPlayer) entityhuman, new ItemStack(fluidtype.a()));
                            }

                            return InteractionResultWrapper.success(itemstack1);
                        }
                    }

                    return InteractionResultWrapper.fail(itemstack);
                } else {
                    iblockdata = world.getType(blockposition);
                    BlockPosition blockposition2 = iblockdata.getBlock() instanceof IFluidContainer && this.fluidType == FluidTypes.WATER ? blockposition : blockposition1;

                    if (this.a(entityhuman, world, blockposition2, movingobjectpositionblock, movingobjectpositionblock.getDirection(), blockposition, itemstack)) { // CraftBukkit
                        this.a(world, itemstack, blockposition2);
                        if (entityhuman instanceof EntityPlayer) {
                            CriterionTriggers.y.a((EntityPlayer) entityhuman, blockposition2, itemstack);
                        }

                        entityhuman.b(StatisticList.ITEM_USED.b(this));
                        return InteractionResultWrapper.success(this.a(itemstack, entityhuman));
                    } else {
                        return InteractionResultWrapper.fail(itemstack);
                    }
                }
            } else {
                return InteractionResultWrapper.fail(itemstack);
            }
        }
    }

    protected ItemStack a(ItemStack itemstack, EntityHuman entityhuman) {
        return !entityhuman.abilities.canInstantlyBuild ? new ItemStack(Items.BUCKET) : itemstack;
    }

    public void a(World world, ItemStack itemstack, BlockPosition blockposition) {}

    // CraftBukkit - added ob.ItemStack result - TODO: Is this... the right way to handle this?
    private ItemStack a(ItemStack itemstack, EntityHuman entityhuman, Item item, org.bukkit.inventory.ItemStack result) {
        if (entityhuman.abilities.canInstantlyBuild) {
            return itemstack;
        } else {
            itemstack.subtract(1);
            if (itemstack.isEmpty()) {
                // CraftBukkit start
                return CraftItemStack.asNMSCopy(result);
            } else {
                if (!entityhuman.inventory.pickup(CraftItemStack.asNMSCopy(result))) {
                    entityhuman.drop(CraftItemStack.asNMSCopy(result), false);
                    // CraftBukkit end
                }

                return itemstack;
            }
        }
    }

    // CraftBukkit start
    public boolean a(@Nullable EntityHuman entityhuman, World world, BlockPosition blockposition, @Nullable MovingObjectPositionBlock movingobjectpositionblock) {
        return a(entityhuman, world, blockposition, movingobjectpositionblock, null, null, null);
    }

    public boolean a(EntityHuman entityhuman, World world, BlockPosition blockposition, @Nullable MovingObjectPositionBlock movingobjectpositionblock, EnumDirection enumdirection, BlockPosition clicked, ItemStack itemstack) {
        // CraftBukkit end
        if (!(this.fluidType instanceof FluidTypeFlowing)) {
            return false;
        } else {
            IBlockData iblockdata = world.getType(blockposition);
            Material material = iblockdata.getMaterial();
            boolean flag = iblockdata.a(this.fluidType);

            if (!iblockdata.isAir() && !flag && (!(iblockdata.getBlock() instanceof IFluidContainer) || !((IFluidContainer) iblockdata.getBlock()).canPlace(world, blockposition, iblockdata, this.fluidType))) {
                return movingobjectpositionblock == null ? false : this.a(entityhuman, world, movingobjectpositionblock.getBlockPosition().shift(movingobjectpositionblock.getDirection()), (MovingObjectPositionBlock) null, enumdirection, clicked, itemstack); // CraftBukkit
            } else {
                // CraftBukkit start
                if (entityhuman != null) {
                    PlayerBucketEmptyEvent event = CraftEventFactory.callPlayerBucketEmptyEvent(world, entityhuman, blockposition, clicked, enumdirection, itemstack);
                    if (event.isCancelled()) {
                        ((EntityPlayer) entityhuman).playerConnection.sendPacket(new PacketPlayOutBlockChange(world, blockposition)); // SPIGOT-4238: needed when looking through entity
                        ((EntityPlayer) entityhuman).getBukkitEntity().updateInventory(); // SPIGOT-4541
                        return false;
                    }
                }
                // CraftBukkit end
                if (world.worldProvider.isNether() && this.fluidType.a(TagsFluid.WATER)) {
                    int i = blockposition.getX();
                    int j = blockposition.getY();
                    int k = blockposition.getZ();

                    world.playSound(entityhuman, blockposition, SoundEffects.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (world.random.nextFloat() - world.random.nextFloat()) * 0.8F);

                    for (int l = 0; l < 8; ++l) {
                        world.addParticle(Particles.LARGE_SMOKE, (double) i + Math.random(), (double) j + Math.random(), (double) k + Math.random(), 0.0D, 0.0D, 0.0D);
                    }
                } else if (iblockdata.getBlock() instanceof IFluidContainer && this.fluidType == FluidTypes.WATER) {
                    if (((IFluidContainer) iblockdata.getBlock()).place(world, blockposition, iblockdata, ((FluidTypeFlowing) this.fluidType).a(false))) {
                        this.a(entityhuman, (GeneratorAccess) world, blockposition);
                    }
                } else {
                    if (!world.isClientSide && flag && !material.isLiquid()) {
                        world.b(blockposition, true);
                    }

                    this.a(entityhuman, (GeneratorAccess) world, blockposition);
                    world.setTypeAndData(blockposition, this.fluidType.h().getBlockData(), 11);
                }

                return true;
            }
        }
    }

    protected void a(@Nullable EntityHuman entityhuman, GeneratorAccess generatoraccess, BlockPosition blockposition) {
        SoundEffect soundeffect = this.fluidType.a(TagsFluid.LAVA) ? SoundEffects.ITEM_BUCKET_EMPTY_LAVA : SoundEffects.ITEM_BUCKET_EMPTY;

        generatoraccess.playSound(entityhuman, blockposition, soundeffect, SoundCategory.BLOCKS, 1.0F, 1.0F);
    }
}
