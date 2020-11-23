package net.minecraft.server;

public abstract class EntityHorseChestedAbstract extends EntityHorseAbstract {

    private static final DataWatcherObject<Boolean> bF = DataWatcher.a(EntityHorseChestedAbstract.class, DataWatcherRegistry.i);

    protected EntityHorseChestedAbstract(EntityTypes<? extends EntityHorseChestedAbstract> entitytypes, World world) {
        super(entitytypes, world);
        this.bD = false;
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.datawatcher.register(EntityHorseChestedAbstract.bF, false);
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.MAX_HEALTH).setValue((double) this.eS());
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.17499999701976776D);
        this.getAttributeInstance(EntityHorseChestedAbstract.attributeJumpStrength).setValue(0.5D);
    }

    public boolean isCarryingChest() {
        return (Boolean) this.datawatcher.get(EntityHorseChestedAbstract.bF);
    }

    public void setCarryingChest(boolean flag) {
        this.datawatcher.set(EntityHorseChestedAbstract.bF, flag);
    }

    @Override
    protected int getChestSlots() {
        return this.isCarryingChest() ? 17 : super.getChestSlots();
    }

    @Override
    public double aS() {
        return super.aS() - 0.25D;
    }

    @Override
    protected SoundEffect getSoundAngry() {
        super.getSoundAngry();
        return SoundEffects.ENTITY_DONKEY_ANGRY;
    }

    @Override
    protected void dropInventory() {
        super.dropInventory();
        if (this.isCarryingChest()) {
            if (!this.world.isClientSide) {
                this.a((IMaterial) Blocks.CHEST);
            }

            this.setCarryingChest(false);
        }

    }

    @Override
    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setBoolean("ChestedHorse", this.isCarryingChest());
        if (this.isCarryingChest()) {
            NBTTagList nbttaglist = new NBTTagList();

            for (int i = 2; i < this.inventoryChest.getSize(); ++i) {
                ItemStack itemstack = this.inventoryChest.getItem(i);

                if (!itemstack.isEmpty()) {
                    NBTTagCompound nbttagcompound1 = new NBTTagCompound();

                    nbttagcompound1.setByte("Slot", (byte) i);
                    itemstack.save(nbttagcompound1);
                    nbttaglist.add(nbttagcompound1);
                }
            }

            nbttagcompound.set("Items", nbttaglist);
        }

    }

    @Override
    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.setCarryingChest(nbttagcompound.getBoolean("ChestedHorse"));
        if (this.isCarryingChest()) {
            NBTTagList nbttaglist = nbttagcompound.getList("Items", 10);

            this.loadChest();

            for (int i = 0; i < nbttaglist.size(); ++i) {
                NBTTagCompound nbttagcompound1 = nbttaglist.getCompound(i);
                int j = nbttagcompound1.getByte("Slot") & 255;

                if (j >= 2 && j < this.inventoryChest.getSize()) {
                    this.inventoryChest.setItem(j, ItemStack.a(nbttagcompound1));
                }
            }
        }

        this.eI();
    }

    @Override
    public boolean a_(int i, ItemStack itemstack) {
        if (i == 499) {
            if (this.isCarryingChest() && itemstack.isEmpty()) {
                this.setCarryingChest(false);
                this.loadChest();
                return true;
            }

            if (!this.isCarryingChest() && itemstack.getItem() == Blocks.CHEST.getItem()) {
                this.setCarryingChest(true);
                this.loadChest();
                return true;
            }
        }

        return super.a_(i, itemstack);
    }

    @Override
    public boolean a(EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);

        if (itemstack.getItem() instanceof ItemMonsterEgg) {
            return super.a(entityhuman, enumhand);
        } else {
            if (!this.isBaby()) {
                if (this.isTamed() && entityhuman.dT()) {
                    this.e(entityhuman);
                    return true;
                }

                if (this.isVehicle()) {
                    return super.a(entityhuman, enumhand);
                }
            }

            if (!itemstack.isEmpty()) {
                boolean flag = this.b(entityhuman, itemstack);

                if (!flag) {
                    if (!this.isTamed() || itemstack.getItem() == Items.NAME_TAG) {
                        if (itemstack.a(entityhuman, (EntityLiving) this, enumhand)) {
                            return true;
                        }

                        this.eP();
                        return true;
                    }

                    if (!this.isCarryingChest() && itemstack.getItem() == Blocks.CHEST.getItem()) {
                        this.setCarryingChest(true);
                        this.et();
                        flag = true;
                        this.loadChest();
                    }

                    if (!this.isBaby() && !this.eL() && itemstack.getItem() == Items.SADDLE) {
                        this.e(entityhuman);
                        return true;
                    }
                }

                if (flag) {
                    if (!entityhuman.abilities.canInstantlyBuild) {
                        itemstack.subtract(1);
                    }

                    return true;
                }
            }

            if (this.isBaby()) {
                return super.a(entityhuman, enumhand);
            } else {
                this.g(entityhuman);
                return true;
            }
        }
    }

    protected void et() {
        this.a(SoundEffects.ENTITY_DONKEY_CHEST, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
    }

    public int eu() {
        return 5;
    }
}
