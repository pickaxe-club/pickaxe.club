package net.minecraft.server;

public abstract class EntityProjectileThrowable extends EntityProjectile {

    private static final DataWatcherObject<ItemStack> e = DataWatcher.a(EntityProjectileThrowable.class, DataWatcherRegistry.g);

    public EntityProjectileThrowable(EntityTypes<? extends EntityProjectileThrowable> entitytypes, World world) {
        super(entitytypes, world);
    }

    public EntityProjectileThrowable(EntityTypes<? extends EntityProjectileThrowable> entitytypes, double d0, double d1, double d2, World world) {
        super(entitytypes, d0, d1, d2, world);
    }

    public EntityProjectileThrowable(EntityTypes<? extends EntityProjectileThrowable> entitytypes, EntityLiving entityliving, World world) {
        super(entitytypes, entityliving, world);
    }

    public void setItem(ItemStack itemstack) {
        if (itemstack.getItem() != this.i() || itemstack.hasTag()) {
            this.getDataWatcher().set(EntityProjectileThrowable.e, SystemUtils.a(itemstack.cloneItemStack(), (itemstack1) -> { // CraftBukkit - decompile error
                if (!itemstack1.isEmpty()) itemstack1.setCount(1); // CraftBukkit
            }));
        }

    }

    protected abstract Item i();

    // CraftBukkit start
    public Item getDefaultItem() {
        return i();
    }
    // CraftBukkit end

    public ItemStack getItem() { // PAIL protected -> public
        return (ItemStack) this.getDataWatcher().get(EntityProjectileThrowable.e);
    }

    @Override
    protected void initDatawatcher() {
        this.getDataWatcher().register(EntityProjectileThrowable.e, ItemStack.a);
    }

    @Override
    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        ItemStack itemstack = this.getItem();

        if (!itemstack.isEmpty()) {
            nbttagcompound.set("Item", itemstack.save(new NBTTagCompound()));
        }

    }

    @Override
    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        ItemStack itemstack = ItemStack.a(nbttagcompound.getCompound("Item"));

        this.setItem(itemstack);
    }
}
