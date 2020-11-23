package net.minecraft.server;

import javax.annotation.Nullable;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityItemFrame extends EntityHanging {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final DataWatcherObject<ItemStack> ITEM = DataWatcher.a(EntityItemFrame.class, DataWatcherRegistry.g);
    private static final DataWatcherObject<Integer> g = DataWatcher.a(EntityItemFrame.class, DataWatcherRegistry.b);
    private float ao = 1.0F;

    public EntityItemFrame(EntityTypes<? extends EntityItemFrame> entitytypes, World world) {
        super(entitytypes, world);
    }

    public EntityItemFrame(World world, BlockPosition blockposition, EnumDirection enumdirection) {
        super(EntityTypes.ITEM_FRAME, world, blockposition);
        this.setDirection(enumdirection);
    }

    @Override
    protected float getHeadHeight(EntityPose entitypose, EntitySize entitysize) {
        return 0.0F;
    }

    @Override
    protected void initDatawatcher() {
        this.getDataWatcher().register(EntityItemFrame.ITEM, ItemStack.a);
        this.getDataWatcher().register(EntityItemFrame.g, 0);
    }

    @Override
    public void setDirection(EnumDirection enumdirection) {
        Validate.notNull(enumdirection);
        this.direction = enumdirection;
        if (enumdirection.m().c()) {
            this.pitch = 0.0F;
            this.yaw = (float) (this.direction.get2DRotationValue() * 90);
        } else {
            this.pitch = (float) (-90 * enumdirection.d().a());
            this.yaw = 0.0F;
        }

        this.lastPitch = this.pitch;
        this.lastYaw = this.yaw;
        this.updateBoundingBox();
    }

    @Override
    protected void updateBoundingBox() {
        if (this.direction != null) {
            // CraftBukkit start code moved in to calculateBoundingBox
            this.a(calculateBoundingBox(this, this.blockPosition, this.direction, this.getHangingWidth(), this.getHangingHeight()));
            // CraftBukkit end
        }
    }

    // CraftBukkit start - break out BB calc into own method
    public static AxisAlignedBB calculateBoundingBox(@Nullable Entity entity, BlockPosition blockPosition, EnumDirection direction, int width, int height) {
        {
            double d0 = 0.46875D;
            double d1 = (double) blockPosition.getX() + 0.5D - (double) direction.getAdjacentX() * 0.46875D;
            double d2 = (double) blockPosition.getY() + 0.5D - (double) direction.getAdjacentY() * 0.46875D;
            double d3 = (double) blockPosition.getZ() + 0.5D - (double) direction.getAdjacentZ() * 0.46875D;

            if (entity != null) {
                entity.setPositionRaw(d1, d2, d3);
            }
            double d4 = (double) width;
            double d5 = (double) height;
            double d6 = (double) width;
            EnumDirection.EnumAxis enumdirection_enumaxis = direction.m();

            switch (enumdirection_enumaxis) {
                case X:
                    d4 = 1.0D;
                    break;
                case Y:
                    d5 = 1.0D;
                    break;
                case Z:
                    d6 = 1.0D;
            }

            d4 /= 32.0D;
            d5 /= 32.0D;
            d6 /= 32.0D;
            return new AxisAlignedBB(d1 - d4, d2 - d5, d3 - d6, d1 + d4, d2 + d5, d3 + d6);
        }
    }
    // CraftBukkit end

    @Override
    public boolean survives() {
        if (!this.world.getCubes(this)) {
            return false;
        } else {
            IBlockData iblockdata = this.world.getType(this.blockPosition.shift(this.direction.opposite()));

            return !iblockdata.getMaterial().isBuildable() && (!this.direction.m().c() || !BlockDiodeAbstract.isDiode(iblockdata)) ? false : this.world.getEntities(this, this.getBoundingBox(), EntityItemFrame.b).isEmpty();
        }
    }

    @Override
    public float aV() {
        return 0.0F;
    }

    @Override
    public void killEntity() {
        this.c(this.getItem());
        super.killEntity();
    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        if (this.isInvulnerable(damagesource)) {
            return false;
        } else if (!damagesource.isExplosion() && !this.getItem().isEmpty()) {
            if (!this.world.isClientSide) {
                // CraftBukkit start - fire EntityDamageEvent
                if (org.bukkit.craftbukkit.event.CraftEventFactory.handleNonLivingEntityDamageEvent(this, damagesource, f, false) || this.dead) {
                    return true;
                }
                // CraftBukkit end
                this.b(damagesource.getEntity(), false);
                this.a(SoundEffects.ENTITY_ITEM_FRAME_REMOVE_ITEM, 1.0F, 1.0F);
            }

            return true;
        } else {
            return super.damageEntity(damagesource, f);
        }
    }

    @Override
    public int getHangingWidth() {
        return 12;
    }

    @Override
    public int getHangingHeight() {
        return 12;
    }

    @Override
    public void a(@Nullable Entity entity) {
        this.a(SoundEffects.ENTITY_ITEM_FRAME_BREAK, 1.0F, 1.0F);
        this.b(entity, true);
    }

    @Override
    public void playPlaceSound() {
        this.a(SoundEffects.ENTITY_ITEM_FRAME_PLACE, 1.0F, 1.0F);
    }

    private void b(@Nullable Entity entity, boolean flag) {
        if (!this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
            if (entity == null) {
                this.c(this.getItem());
            }

        } else {
            ItemStack itemstack = this.getItem();

            this.setItem(ItemStack.a);
            if (entity instanceof EntityHuman) {
                EntityHuman entityhuman = (EntityHuman) entity;

                if (entityhuman.abilities.canInstantlyBuild) {
                    this.c(itemstack);
                    return;
                }
            }

            if (flag) {
                this.a((IMaterial) Items.ITEM_FRAME);
            }

            if (!itemstack.isEmpty()) {
                itemstack = itemstack.cloneItemStack();
                this.c(itemstack);
                if (this.random.nextFloat() < this.ao) {
                    this.a(itemstack);
                }
            }

        }
    }

    private void c(ItemStack itemstack) {
        if (itemstack.getItem() == Items.FILLED_MAP) {
            WorldMap worldmap = ItemWorldMap.getSavedMap(itemstack, this.world);

            worldmap.a(this.blockPosition, this.getId());
            worldmap.a(true);
        }

        itemstack.a((EntityItemFrame) null);
    }

    public ItemStack getItem() {
        return (ItemStack) this.getDataWatcher().get(EntityItemFrame.ITEM);
    }

    public void setItem(ItemStack itemstack) {
        this.setItem(itemstack, true);
    }

    public void setItem(ItemStack itemstack, boolean flag) {
        // CraftBukkit start
        this.setItem(itemstack, flag, true);
    }

    public void setItem(ItemStack itemstack, boolean flag, boolean playSound) {
        // CraftBukkit end
        if (!itemstack.isEmpty()) {
            itemstack = itemstack.cloneItemStack();
            itemstack.setCount(1);
            itemstack.a(this);
        }

        this.getDataWatcher().set(EntityItemFrame.ITEM, itemstack);
        if (!itemstack.isEmpty() && playSound) { // CraftBukkit
            this.a(SoundEffects.ENTITY_ITEM_FRAME_ADD_ITEM, 1.0F, 1.0F);
        }

        if (flag && this.blockPosition != null) {
            this.world.updateAdjacentComparators(this.blockPosition, Blocks.AIR);
        }

    }

    @Override
    public boolean a_(int i, ItemStack itemstack) {
        if (i == 0) {
            this.setItem(itemstack);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void a(DataWatcherObject<?> datawatcherobject) {
        if (datawatcherobject.equals(EntityItemFrame.ITEM)) {
            ItemStack itemstack = this.getItem();

            if (!itemstack.isEmpty() && itemstack.z() != this) {
                itemstack.a(this);
            }
        }

    }

    public int getRotation() {
        return (Integer) this.getDataWatcher().get(EntityItemFrame.g);
    }

    public void setRotation(int i) {
        this.setRotation(i, true);
    }

    private void setRotation(int i, boolean flag) {
        this.getDataWatcher().set(EntityItemFrame.g, i % 8);
        if (flag && this.blockPosition != null) {
            this.world.updateAdjacentComparators(this.blockPosition, Blocks.AIR);
        }

    }

    @Override
    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        if (!this.getItem().isEmpty()) {
            nbttagcompound.set("Item", this.getItem().save(new NBTTagCompound()));
            nbttagcompound.setByte("ItemRotation", (byte) this.getRotation());
            nbttagcompound.setFloat("ItemDropChance", this.ao);
        }

        nbttagcompound.setByte("Facing", (byte) this.direction.b());
    }

    @Override
    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("Item");

        if (nbttagcompound1 != null && !nbttagcompound1.isEmpty()) {
            ItemStack itemstack = ItemStack.a(nbttagcompound1);

            if (itemstack.isEmpty()) {
                EntityItemFrame.LOGGER.warn("Unable to load item from: {}", nbttagcompound1);
            }

            ItemStack itemstack1 = this.getItem();

            if (!itemstack1.isEmpty() && !ItemStack.matches(itemstack, itemstack1)) {
                this.c(itemstack1);
            }

            this.setItem(itemstack, false);
            this.setRotation(nbttagcompound.getByte("ItemRotation"), false);
            if (nbttagcompound.hasKeyOfType("ItemDropChance", 99)) {
                this.ao = nbttagcompound.getFloat("ItemDropChance");
            }
        }

        this.setDirection(EnumDirection.fromType1(nbttagcompound.getByte("Facing")));
    }

    @Override
    public boolean b(EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);
        boolean flag = !this.getItem().isEmpty();
        boolean flag1 = !itemstack.isEmpty();

        if (!this.world.isClientSide) {
            if (!flag) {
                if (flag1) {
                    this.setItem(itemstack);
                    if (!entityhuman.abilities.canInstantlyBuild) {
                        itemstack.subtract(1);
                    }
                }
            } else {
                this.a(SoundEffects.ENTITY_ITEM_FRAME_ROTATE_ITEM, 1.0F, 1.0F);
                this.setRotation(this.getRotation() + 1);
            }

            return true;
        } else {
            return flag || flag1;
        }
    }

    public int q() {
        return this.getItem().isEmpty() ? 0 : this.getRotation() % 8 + 1;
    }

    @Override
    public Packet<?> L() {
        return new PacketPlayOutSpawnEntity(this, this.getEntityType(), this.direction.b(), this.getBlockPosition());
    }
}
