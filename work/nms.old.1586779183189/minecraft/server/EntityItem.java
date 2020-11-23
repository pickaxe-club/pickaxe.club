package net.minecraft.server;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;
// CraftBukkit start
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
// CraftBukkit end

public class EntityItem extends Entity {

    private static final DataWatcherObject<ItemStack> ITEM = DataWatcher.a(EntityItem.class, DataWatcherRegistry.g);
    public int age;
    public int pickupDelay;
    private int f;
    private UUID thrower;
    private UUID owner;
    public final float b;
    private int lastTick = MinecraftServer.currentTick - 1; // CraftBukkit

    public EntityItem(EntityTypes<? extends EntityItem> entitytypes, World world) {
        super(entitytypes, world);
        this.f = 5;
        this.b = (float) (Math.random() * 3.141592653589793D * 2.0D);
    }

    public EntityItem(World world, double d0, double d1, double d2) {
        this(EntityTypes.ITEM, world);
        this.setPosition(d0, d1, d2);
        this.yaw = this.random.nextFloat() * 360.0F;
        this.setMot(this.random.nextDouble() * 0.2D - 0.1D, 0.2D, this.random.nextDouble() * 0.2D - 0.1D);
    }

    public EntityItem(World world, double d0, double d1, double d2, ItemStack itemstack) {
        this(world, d0, d1, d2);
        this.setItemStack(itemstack);
    }

    @Override
    protected boolean playStepSound() {
        return false;
    }

    @Override
    protected void initDatawatcher() {
        this.getDataWatcher().register(EntityItem.ITEM, ItemStack.a);
    }

    @Override
    public void tick() {
        if (this.getItemStack().isEmpty()) {
            this.die();
        } else {
            super.tick();
            // CraftBukkit start - Use wall time for pickup and despawn timers
            int elapsedTicks = MinecraftServer.currentTick - this.lastTick;
            if (this.pickupDelay != 32767) this.pickupDelay -= elapsedTicks;
            if (this.age != -32768) this.age += elapsedTicks;
            this.lastTick = MinecraftServer.currentTick;
            // CraftBukkit end

            this.lastX = this.locX();
            this.lastY = this.locY();
            this.lastZ = this.locZ();
            Vec3D vec3d = this.getMot();

            if (this.a(TagsFluid.WATER)) {
                this.u();
            } else if (!this.isNoGravity()) {
                this.setMot(this.getMot().add(0.0D, -0.04D, 0.0D));
            }

            if (this.world.isClientSide) {
                this.noclip = false;
            } else {
                this.noclip = !this.world.getCubes(this);
                if (this.noclip) {
                    this.k(this.locX(), (this.getBoundingBox().minY + this.getBoundingBox().maxY) / 2.0D, this.locZ());
                }
            }

            if (!this.onGround || b(this.getMot()) > 9.999999747378752E-6D || (this.ticksLived + this.getId()) % 4 == 0) {
                this.move(EnumMoveType.SELF, this.getMot());
                float f = 0.98F;

                if (this.onGround) {
                    f = this.world.getType(new BlockPosition(this.locX(), this.locY() - 1.0D, this.locZ())).getBlock().l() * 0.98F;
                }

                this.setMot(this.getMot().d((double) f, 0.98D, (double) f));
                if (this.onGround) {
                    this.setMot(this.getMot().d(1.0D, -0.5D, 1.0D));
                }
            }

            boolean flag = MathHelper.floor(this.lastX) != MathHelper.floor(this.locX()) || MathHelper.floor(this.lastY) != MathHelper.floor(this.locY()) || MathHelper.floor(this.lastZ) != MathHelper.floor(this.locZ());
            int i = flag ? 2 : 40;

            if (this.ticksLived % i == 0) {
                if (this.world.getFluid(new BlockPosition(this)).a(TagsFluid.LAVA)) {
                    this.setMot((double) ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F), 0.20000000298023224D, (double) ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F));
                    this.a(SoundEffects.ENTITY_GENERIC_BURN, 0.4F, 2.0F + this.random.nextFloat() * 0.4F);
                }

                if (!this.world.isClientSide && this.w()) {
                    this.mergeNearby();
                }
            }

            /* CraftBukkit start - moved up
            if (this.age != -32768) {
                ++this.age;
            }
            // CraftBukkit end */

            this.impulse |= this.aC();
            if (!this.world.isClientSide) {
                double d0 = this.getMot().d(vec3d).g();

                if (d0 > 0.01D) {
                    this.impulse = true;
                }
            }

            if (!this.world.isClientSide && this.age >= 6000) {
                // CraftBukkit start - fire ItemDespawnEvent
                if (org.bukkit.craftbukkit.event.CraftEventFactory.callItemDespawnEvent(this).isCancelled()) {
                    this.age = 0;
                    return;
                }
                // CraftBukkit end
                this.die();
            }

        }
    }

    private void u() {
        Vec3D vec3d = this.getMot();

        this.setMot(vec3d.x * 0.9900000095367432D, vec3d.y + (double) (vec3d.y < 0.05999999865889549D ? 5.0E-4F : 0.0F), vec3d.z * 0.9900000095367432D);
    }

    private void mergeNearby() {
        if (this.w()) {
            List<EntityItem> list = this.world.a(EntityItem.class, this.getBoundingBox().grow(0.5D, 0.0D, 0.5D), (entityitem) -> {
                return entityitem != this && entityitem.w();
            });
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                EntityItem entityitem = (EntityItem) iterator.next();

                if (entityitem.w()) {
                    this.a(entityitem);
                    if (this.dead) {
                        break;
                    }
                }
            }

        }
    }

    private boolean w() {
        ItemStack itemstack = this.getItemStack();

        return this.isAlive() && this.pickupDelay != 32767 && this.age != -32768 && this.age < 6000 && itemstack.getCount() < itemstack.getMaxStackSize();
    }

    private void a(EntityItem entityitem) {
        ItemStack itemstack = this.getItemStack();
        ItemStack itemstack1 = entityitem.getItemStack();

        if (Objects.equals(this.getOwner(), entityitem.getOwner()) && a(itemstack, itemstack1)) {
            if (itemstack1.getCount() < itemstack.getCount()) {
                a(this, itemstack, entityitem, itemstack1);
            } else {
                a(entityitem, itemstack1, this, itemstack);
            }

        }
    }

    public static boolean a(ItemStack itemstack, ItemStack itemstack1) {
        return itemstack1.getItem() != itemstack.getItem() ? false : (itemstack1.getCount() + itemstack.getCount() > itemstack1.getMaxStackSize() ? false : (itemstack1.hasTag() ^ itemstack.hasTag() ? false : !itemstack1.hasTag() || itemstack1.getTag().equals(itemstack.getTag())));
    }

    public static ItemStack a(ItemStack itemstack, ItemStack itemstack1, int i) {
        int j = Math.min(Math.min(itemstack.getMaxStackSize(), i) - itemstack.getCount(), itemstack1.getCount());
        ItemStack itemstack2 = itemstack.cloneItemStack();

        itemstack2.add(j);
        itemstack1.subtract(j);
        return itemstack2;
    }

    private static void a(EntityItem entityitem, ItemStack itemstack, ItemStack itemstack1) {
        ItemStack itemstack2 = a(itemstack, itemstack1, 64);

        if (!itemstack2.isEmpty()) entityitem.setItemStack(itemstack2); // CraftBukkit - don't set empty stacks
    }

    private static void a(EntityItem entityitem, ItemStack itemstack, EntityItem entityitem1, ItemStack itemstack1) {
        if (org.bukkit.craftbukkit.event.CraftEventFactory.callItemMergeEvent(entityitem1, entityitem).isCancelled()) return; // CraftBukkit
        a(entityitem, itemstack, itemstack1);
        entityitem.pickupDelay = Math.max(entityitem.pickupDelay, entityitem1.pickupDelay);
        entityitem.age = Math.min(entityitem.age, entityitem1.age);
        if (itemstack1.isEmpty()) {
            entityitem1.die();
        }

    }

    @Override
    protected void burn(float i) { // CraftBukkit - int -> float
        this.damageEntity(DamageSource.FIRE, (float) i);
    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        if (this.isInvulnerable(damagesource)) {
            return false;
        } else if (!this.getItemStack().isEmpty() && this.getItemStack().getItem() == Items.NETHER_STAR && damagesource.isExplosion()) {
            return false;
        } else {
            // CraftBukkit start
            if (org.bukkit.craftbukkit.event.CraftEventFactory.handleNonLivingEntityDamageEvent(this, damagesource, f)) {
                return false;
            }
            // CraftBukkit end
            this.velocityChanged();
            this.f = (int) ((float) this.f - f);
            if (this.f <= 0) {
                this.die();
            }

            return false;
        }
    }

    @Override
    public void b(NBTTagCompound nbttagcompound) {
        nbttagcompound.setShort("Health", (short) this.f);
        nbttagcompound.setShort("Age", (short) this.age);
        nbttagcompound.setShort("PickupDelay", (short) this.pickupDelay);
        if (this.getThrower() != null) {
            nbttagcompound.set("Thrower", GameProfileSerializer.a(this.getThrower()));
        }

        if (this.getOwner() != null) {
            nbttagcompound.set("Owner", GameProfileSerializer.a(this.getOwner()));
        }

        if (!this.getItemStack().isEmpty()) {
            nbttagcompound.set("Item", this.getItemStack().save(new NBTTagCompound()));
        }

    }

    @Override
    public void a(NBTTagCompound nbttagcompound) {
        this.f = nbttagcompound.getShort("Health");
        this.age = nbttagcompound.getShort("Age");
        if (nbttagcompound.hasKey("PickupDelay")) {
            this.pickupDelay = nbttagcompound.getShort("PickupDelay");
        }

        if (nbttagcompound.hasKeyOfType("Owner", 10)) {
            this.owner = GameProfileSerializer.b(nbttagcompound.getCompound("Owner"));
        }

        if (nbttagcompound.hasKeyOfType("Thrower", 10)) {
            this.thrower = GameProfileSerializer.b(nbttagcompound.getCompound("Thrower"));
        }

        NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("Item");

        this.setItemStack(ItemStack.a(nbttagcompound1));
        if (this.getItemStack().isEmpty()) {
            this.die();
        }

    }

    @Override
    public void pickup(EntityHuman entityhuman) {
        if (!this.world.isClientSide) {
            ItemStack itemstack = this.getItemStack();
            Item item = itemstack.getItem();
            int i = itemstack.getCount();

            // CraftBukkit start - fire PlayerPickupItemEvent
            int canHold = entityhuman.inventory.canHold(itemstack);
            int remaining = i - canHold;

            if (this.pickupDelay <= 0 && canHold > 0) {
                itemstack.setCount(canHold);
                // Call legacy event
                PlayerPickupItemEvent playerEvent = new PlayerPickupItemEvent((org.bukkit.entity.Player) entityhuman.getBukkitEntity(), (org.bukkit.entity.Item) this.getBukkitEntity(), remaining);
                playerEvent.setCancelled(!entityhuman.canPickUpLoot);
                this.world.getServer().getPluginManager().callEvent(playerEvent);
                if (playerEvent.isCancelled()) {
                    itemstack.setCount(i); // SPIGOT-5294 - restore count
                    return;
                }

                // Call newer event afterwards
                EntityPickupItemEvent entityEvent = new EntityPickupItemEvent((org.bukkit.entity.Player) entityhuman.getBukkitEntity(), (org.bukkit.entity.Item) this.getBukkitEntity(), remaining);
                entityEvent.setCancelled(!entityhuman.canPickUpLoot);
                this.world.getServer().getPluginManager().callEvent(entityEvent);
                if (entityEvent.isCancelled()) {
                    itemstack.setCount(i); // SPIGOT-5294 - restore count
                    return;
                }

                itemstack.setCount(canHold + remaining); // = i

                // Possibly < 0; fix here so we do not have to modify code below
                this.pickupDelay = 0;
            } else if (this.pickupDelay == 0) {
                // ensure that the code below isn't triggered if canHold says we can't pick the items up
                this.pickupDelay = -1;
            }
            // CraftBukkit end

            if (this.pickupDelay == 0 && (this.owner == null || this.owner.equals(entityhuman.getUniqueID())) && entityhuman.inventory.pickup(itemstack)) {
                entityhuman.receive(this, i);
                if (itemstack.isEmpty()) {
                    this.die();
                    itemstack.setCount(i);
                }

                entityhuman.a(StatisticList.ITEM_PICKED_UP.b(item), i);
            }

        }
    }

    @Override
    public IChatBaseComponent getDisplayName() {
        IChatBaseComponent ichatbasecomponent = this.getCustomName();

        return (IChatBaseComponent) (ichatbasecomponent != null ? ichatbasecomponent : new ChatMessage(this.getItemStack().j(), new Object[0]));
    }

    @Override
    public boolean bA() {
        return false;
    }

    @Nullable
    @Override
    public Entity a(DimensionManager dimensionmanager) {
        Entity entity = super.a(dimensionmanager);

        if (!this.world.isClientSide && entity instanceof EntityItem) {
            ((EntityItem) entity).mergeNearby();
        }

        return entity;
    }

    public ItemStack getItemStack() {
        return (ItemStack) this.getDataWatcher().get(EntityItem.ITEM);
    }

    public void setItemStack(ItemStack itemstack) {
        com.google.common.base.Preconditions.checkArgument(!itemstack.isEmpty(), "Cannot drop air"); // CraftBukkit
        this.getDataWatcher().set(EntityItem.ITEM, itemstack);
        this.getDataWatcher().markDirty(EntityItem.ITEM); // CraftBukkit - SPIGOT-4591, must mark dirty
    }

    @Nullable
    public UUID getOwner() {
        return this.owner;
    }

    public void setOwner(@Nullable UUID uuid) {
        this.owner = uuid;
    }

    @Nullable
    public UUID getThrower() {
        return this.thrower;
    }

    public void setThrower(@Nullable UUID uuid) {
        this.thrower = uuid;
    }

    public void defaultPickupDelay() {
        this.pickupDelay = 10;
    }

    public void n() {
        this.pickupDelay = 0;
    }

    public void o() {
        this.pickupDelay = 32767;
    }

    public void setPickupDelay(int i) {
        this.pickupDelay = i;
    }

    public boolean p() {
        return this.pickupDelay > 0;
    }

    public void r() {
        this.age = -6000;
    }

    public void s() {
        this.o();
        this.age = 5999;
    }

    @Override
    public Packet<?> L() {
        return new PacketPlayOutSpawnEntity(this);
    }
}
