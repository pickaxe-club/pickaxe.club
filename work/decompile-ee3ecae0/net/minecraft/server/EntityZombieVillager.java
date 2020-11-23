package net.minecraft.server;

import com.mojang.datafixers.Dynamic;
import java.util.UUID;
import javax.annotation.Nullable;

public class EntityZombieVillager extends EntityZombie implements VillagerDataHolder {

    public static final DataWatcherObject<Boolean> CONVERTING = DataWatcher.a(EntityZombieVillager.class, DataWatcherRegistry.i);
    private static final DataWatcherObject<VillagerData> c = DataWatcher.a(EntityZombieVillager.class, DataWatcherRegistry.q);
    public int conversionTime;
    public UUID conversionPlayer;
    private NBTBase by;
    private NBTTagCompound bz;
    private int bA;

    public EntityZombieVillager(EntityTypes<? extends EntityZombieVillager> entitytypes, World world) {
        super(entitytypes, world);
        this.setVillagerData(this.getVillagerData().withProfession((VillagerProfession) IRegistry.VILLAGER_PROFESSION.a(this.random)));
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.datawatcher.register(EntityZombieVillager.CONVERTING, false);
        this.datawatcher.register(EntityZombieVillager.c, new VillagerData(VillagerType.PLAINS, VillagerProfession.NONE, 1));
    }

    @Override
    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.set("VillagerData", (NBTBase) this.getVillagerData().a(DynamicOpsNBT.a));
        if (this.bz != null) {
            nbttagcompound.set("Offers", this.bz);
        }

        if (this.by != null) {
            nbttagcompound.set("Gossips", this.by);
        }

        nbttagcompound.setInt("ConversionTime", this.isConverting() ? this.conversionTime : -1);
        if (this.conversionPlayer != null) {
            nbttagcompound.a("ConversionPlayer", this.conversionPlayer);
        }

        nbttagcompound.setInt("Xp", this.bA);
    }

    @Override
    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        if (nbttagcompound.hasKeyOfType("VillagerData", 10)) {
            this.setVillagerData(new VillagerData(new Dynamic(DynamicOpsNBT.a, nbttagcompound.get("VillagerData"))));
        }

        if (nbttagcompound.hasKeyOfType("Offers", 10)) {
            this.bz = nbttagcompound.getCompound("Offers");
        }

        if (nbttagcompound.hasKeyOfType("Gossips", 10)) {
            this.by = nbttagcompound.getList("Gossips", 10);
        }

        if (nbttagcompound.hasKeyOfType("ConversionTime", 99) && nbttagcompound.getInt("ConversionTime") > -1) {
            this.startConversion(nbttagcompound.b("ConversionPlayer") ? nbttagcompound.a("ConversionPlayer") : null, nbttagcompound.getInt("ConversionTime"));
        }

        if (nbttagcompound.hasKeyOfType("Xp", 3)) {
            this.bA = nbttagcompound.getInt("Xp");
        }

    }

    @Override
    public void tick() {
        if (!this.world.isClientSide && this.isAlive() && this.isConverting()) {
            int i = this.getConversionProgress();

            this.conversionTime -= i;
            if (this.conversionTime <= 0) {
                this.a((WorldServer) this.world);
            }
        }

        super.tick();
    }

    @Override
    public boolean a(EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);

        if (itemstack.getItem() == Items.GOLDEN_APPLE && this.hasEffect(MobEffects.WEAKNESS)) {
            if (!entityhuman.abilities.canInstantlyBuild) {
                itemstack.subtract(1);
            }

            if (!this.world.isClientSide) {
                this.startConversion(entityhuman.getUniqueID(), this.random.nextInt(2401) + 3600);
                entityhuman.a(enumhand, true);
            }

            return true;
        } else {
            return super.a(entityhuman, enumhand);
        }
    }

    @Override
    protected boolean et() {
        return false;
    }

    @Override
    public boolean isTypeNotPersistent(double d0) {
        return !this.isConverting();
    }

    public boolean isConverting() {
        return (Boolean) this.getDataWatcher().get(EntityZombieVillager.CONVERTING);
    }

    public void startConversion(@Nullable UUID uuid, int i) {
        this.conversionPlayer = uuid;
        this.conversionTime = i;
        this.getDataWatcher().set(EntityZombieVillager.CONVERTING, true);
        this.removeEffect(MobEffects.WEAKNESS);
        this.addEffect(new MobEffect(MobEffects.INCREASE_DAMAGE, i, Math.min(this.world.getDifficulty().a() - 1, 0)));
        this.world.broadcastEntityEffect(this, (byte) 16);
    }

    private void a(WorldServer worldserver) {
        EntityVillager entityvillager = (EntityVillager) EntityTypes.VILLAGER.a((World) worldserver);
        EnumItemSlot[] aenumitemslot = EnumItemSlot.values();
        int i = aenumitemslot.length;

        for (int j = 0; j < i; ++j) {
            EnumItemSlot enumitemslot = aenumitemslot[j];
            ItemStack itemstack = this.getEquipment(enumitemslot);

            if (!itemstack.isEmpty()) {
                if (EnchantmentManager.d(itemstack)) {
                    entityvillager.a_(enumitemslot.b() + 300, itemstack);
                } else {
                    double d0 = (double) this.d(enumitemslot);

                    if (d0 > 1.0D) {
                        this.a(itemstack);
                    }
                }
            }
        }

        entityvillager.u(this);
        entityvillager.setVillagerData(this.getVillagerData());
        if (this.by != null) {
            entityvillager.a(this.by);
        }

        if (this.bz != null) {
            entityvillager.b(new MerchantRecipeList(this.bz));
        }

        entityvillager.setExperience(this.bA);
        entityvillager.prepare(worldserver, worldserver.getDamageScaler(new BlockPosition(entityvillager)), EnumMobSpawn.CONVERSION, (GroupDataEntity) null, (NBTTagCompound) null);
        if (this.isBaby()) {
            entityvillager.setAgeRaw(-24000);
        }

        this.die();
        entityvillager.setNoAI(this.isNoAI());
        if (this.hasCustomName()) {
            entityvillager.setCustomName(this.getCustomName());
            entityvillager.setCustomNameVisible(this.getCustomNameVisible());
        }

        if (this.isPersistent()) {
            entityvillager.setPersistent();
        }

        entityvillager.setInvulnerable(this.isInvulnerable());
        worldserver.addEntity(entityvillager);
        if (this.conversionPlayer != null) {
            EntityHuman entityhuman = worldserver.b(this.conversionPlayer);

            if (entityhuman instanceof EntityPlayer) {
                CriterionTriggers.r.a((EntityPlayer) entityhuman, this, entityvillager);
                worldserver.a(ReputationEvent.a, (Entity) entityhuman, (ReputationHandler) entityvillager);
            }
        }

        entityvillager.addEffect(new MobEffect(MobEffects.CONFUSION, 200, 0));
        worldserver.a((EntityHuman) null, 1027, new BlockPosition(this), 0);
    }

    private int getConversionProgress() {
        int i = 1;

        if (this.random.nextFloat() < 0.01F) {
            int j = 0;
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

            for (int k = (int) this.locX() - 4; k < (int) this.locX() + 4 && j < 14; ++k) {
                for (int l = (int) this.locY() - 4; l < (int) this.locY() + 4 && j < 14; ++l) {
                    for (int i1 = (int) this.locZ() - 4; i1 < (int) this.locZ() + 4 && j < 14; ++i1) {
                        Block block = this.world.getType(blockposition_mutableblockposition.d(k, l, i1)).getBlock();

                        if (block == Blocks.IRON_BARS || block instanceof BlockBed) {
                            if (this.random.nextFloat() < 0.3F) {
                                ++i;
                            }

                            ++j;
                        }
                    }
                }
            }
        }

        return i;
    }

    @Override
    protected float dn() {
        return this.isBaby() ? (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 2.0F : (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F;
    }

    @Override
    public SoundEffect getSoundAmbient() {
        return SoundEffects.ENTITY_ZOMBIE_VILLAGER_AMBIENT;
    }

    @Override
    public SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.ENTITY_ZOMBIE_VILLAGER_HURT;
    }

    @Override
    public SoundEffect getSoundDeath() {
        return SoundEffects.ENTITY_ZOMBIE_VILLAGER_DEATH;
    }

    @Override
    public SoundEffect getSoundStep() {
        return SoundEffects.ENTITY_ZOMBIE_VILLAGER_STEP;
    }

    @Override
    protected ItemStack es() {
        return ItemStack.a;
    }

    public void setOffers(NBTTagCompound nbttagcompound) {
        this.bz = nbttagcompound;
    }

    public void a(NBTBase nbtbase) {
        this.by = nbtbase;
    }

    @Nullable
    @Override
    public GroupDataEntity prepare(GeneratorAccess generatoraccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        this.setVillagerData(this.getVillagerData().withType(VillagerType.a(generatoraccess.getBiome(new BlockPosition(this)))));
        return super.prepare(generatoraccess, difficultydamagescaler, enummobspawn, groupdataentity, nbttagcompound);
    }

    public void setVillagerData(VillagerData villagerdata) {
        VillagerData villagerdata1 = this.getVillagerData();

        if (villagerdata1.getProfession() != villagerdata.getProfession()) {
            this.bz = null;
        }

        this.datawatcher.set(EntityZombieVillager.c, villagerdata);
    }

    @Override
    public VillagerData getVillagerData() {
        return (VillagerData) this.datawatcher.get(EntityZombieVillager.c);
    }

    public void a(int i) {
        this.bA = i;
    }
}
