package net.minecraft.server;

import com.mojang.serialization.DynamicOps;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;

public class EntityZombie extends EntityMonster {

    private static final UUID b = UUID.fromString("B9766B59-9566-4402-BC1F-2EE2A276D836");
    private static final AttributeModifier c = new AttributeModifier(EntityZombie.b, "Baby speed boost", 0.5D, AttributeModifier.Operation.MULTIPLY_BASE);
    private static final DataWatcherObject<Boolean> d = DataWatcher.a(EntityZombie.class, DataWatcherRegistry.i);
    private static final DataWatcherObject<Integer> bv = DataWatcher.a(EntityZombie.class, DataWatcherRegistry.b);
    public static final DataWatcherObject<Boolean> DROWN_CONVERTING = DataWatcher.a(EntityZombie.class, DataWatcherRegistry.i);
    private static final Predicate<EnumDifficulty> bx = (enumdifficulty) -> {
        return enumdifficulty == EnumDifficulty.HARD;
    };
    private final PathfinderGoalBreakDoor by;
    private boolean bz;
    private int bA;
    public int drownedConversionTime;

    public EntityZombie(EntityTypes<? extends EntityZombie> entitytypes, World world) {
        super(entitytypes, world);
        this.by = new PathfinderGoalBreakDoor(this, EntityZombie.bx);
    }

    public EntityZombie(World world) {
        this(EntityTypes.ZOMBIE, world);
    }

    @Override
    protected void initPathfinder() {
        this.goalSelector.a(4, new EntityZombie.a(this, 1.0D, 3));
        this.goalSelector.a(8, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
        this.m();
    }

    protected void m() {
        this.goalSelector.a(2, new PathfinderGoalZombieAttack(this, 1.0D, false));
        this.goalSelector.a(6, new PathfinderGoalMoveThroughVillage(this, 1.0D, true, 4, this::eV));
        this.goalSelector.a(7, new PathfinderGoalRandomStrollLand(this, 1.0D));
        this.targetSelector.a(1, (new PathfinderGoalHurtByTarget(this, new Class[0])).a(EntityPigZombie.class));
        this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget<>(this, EntityHuman.class, true));
        this.targetSelector.a(3, new PathfinderGoalNearestAttackableTarget<>(this, EntityVillagerAbstract.class, false));
        this.targetSelector.a(3, new PathfinderGoalNearestAttackableTarget<>(this, EntityIronGolem.class, true));
        this.targetSelector.a(5, new PathfinderGoalNearestAttackableTarget<>(this, EntityTurtle.class, 10, true, false, EntityTurtle.bv));
    }

    public static AttributeProvider.Builder eT() {
        return EntityMonster.eS().a(GenericAttributes.FOLLOW_RANGE, 35.0D).a(GenericAttributes.MOVEMENT_SPEED, 0.23000000417232513D).a(GenericAttributes.ATTACK_DAMAGE, 3.0D).a(GenericAttributes.ARMOR, 2.0D).a(GenericAttributes.SPAWN_REINFORCEMENTS);
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.getDataWatcher().register(EntityZombie.d, false);
        this.getDataWatcher().register(EntityZombie.bv, 0);
        this.getDataWatcher().register(EntityZombie.DROWN_CONVERTING, false);
    }

    public boolean isDrownConverting() {
        return (Boolean) this.getDataWatcher().get(EntityZombie.DROWN_CONVERTING);
    }

    public boolean eV() {
        return this.bz;
    }

    public void u(boolean flag) {
        if (this.eL()) {
            if (this.bz != flag) {
                this.bz = flag;
                ((Navigation) this.getNavigation()).a(flag);
                if (flag) {
                    this.goalSelector.a(1, this.by);
                } else {
                    this.goalSelector.a((PathfinderGoal) this.by);
                }
            }
        } else if (this.bz) {
            this.goalSelector.a((PathfinderGoal) this.by);
            this.bz = false;
        }

    }

    protected boolean eL() {
        return true;
    }

    @Override
    public boolean isBaby() {
        return (Boolean) this.getDataWatcher().get(EntityZombie.d);
    }

    @Override
    protected int getExpValue(EntityHuman entityhuman) {
        if (this.isBaby()) {
            this.f = (int) ((float) this.f * 2.5F);
        }

        return super.getExpValue(entityhuman);
    }

    public void setBaby(boolean flag) {
        this.getDataWatcher().set(EntityZombie.d, flag);
        if (this.world != null && !this.world.isClientSide) {
            AttributeModifiable attributemodifiable = this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED);

            attributemodifiable.removeModifier(EntityZombie.c);
            if (flag) {
                attributemodifiable.b(EntityZombie.c);
            }
        }

    }

    @Override
    public void a(DataWatcherObject<?> datawatcherobject) {
        if (EntityZombie.d.equals(datawatcherobject)) {
            this.updateSize();
        }

        super.a(datawatcherobject);
    }

    protected boolean eO() {
        return true;
    }

    @Override
    public void tick() {
        if (!this.world.isClientSide && this.isAlive() && !this.isNoAI()) {
            if (this.isDrownConverting()) {
                --this.drownedConversionTime;
                if (this.drownedConversionTime < 0) {
                    this.eQ();
                }
            } else if (this.eO()) {
                if (this.a((Tag) TagsFluid.WATER)) {
                    ++this.bA;
                    if (this.bA >= 600) {
                        this.startDrownedConversion(300);
                    }
                } else {
                    this.bA = -1;
                }
            }
        }

        super.tick();
    }

    @Override
    public void movementTick() {
        if (this.isAlive()) {
            boolean flag = this.U_() && this.eH();

            if (flag) {
                ItemStack itemstack = this.getEquipment(EnumItemSlot.HEAD);

                if (!itemstack.isEmpty()) {
                    if (itemstack.e()) {
                        itemstack.setDamage(itemstack.getDamage() + this.random.nextInt(2));
                        if (itemstack.getDamage() >= itemstack.h()) {
                            this.broadcastItemBreak(EnumItemSlot.HEAD);
                            this.setSlot(EnumItemSlot.HEAD, ItemStack.b);
                        }
                    }

                    flag = false;
                }

                if (flag) {
                    this.setOnFire(8);
                }
            }
        }

        super.movementTick();
    }

    public void startDrownedConversion(int i) {
        this.drownedConversionTime = i;
        this.getDataWatcher().set(EntityZombie.DROWN_CONVERTING, true);
    }

    protected void eQ() {
        this.c(EntityTypes.DROWNED);
        if (!this.isSilent()) {
            this.world.a((EntityHuman) null, 1040, this.getChunkCoordinates(), 0);
        }

    }

    protected void c(EntityTypes<? extends EntityZombie> entitytypes) {
        EntityZombie entityzombie = (EntityZombie) this.b(entitytypes);

        if (entityzombie != null) {
            entityzombie.u(entityzombie.world.getDamageScaler(entityzombie.getChunkCoordinates()).d());
            entityzombie.u(entityzombie.eL() && this.eV());
        }

    }

    protected boolean U_() {
        return true;
    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        if (super.damageEntity(damagesource, f)) {
            EntityLiving entityliving = this.getGoalTarget();

            if (entityliving == null && damagesource.getEntity() instanceof EntityLiving) {
                entityliving = (EntityLiving) damagesource.getEntity();
            }

            if (entityliving != null && this.world.getDifficulty() == EnumDifficulty.HARD && (double) this.random.nextFloat() < this.b(GenericAttributes.SPAWN_REINFORCEMENTS) && this.world.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING)) {
                int i = MathHelper.floor(this.locX());
                int j = MathHelper.floor(this.locY());
                int k = MathHelper.floor(this.locZ());
                EntityZombie entityzombie = new EntityZombie(this.world);

                for (int l = 0; l < 50; ++l) {
                    int i1 = i + MathHelper.nextInt(this.random, 7, 40) * MathHelper.nextInt(this.random, -1, 1);
                    int j1 = j + MathHelper.nextInt(this.random, 7, 40) * MathHelper.nextInt(this.random, -1, 1);
                    int k1 = k + MathHelper.nextInt(this.random, 7, 40) * MathHelper.nextInt(this.random, -1, 1);
                    BlockPosition blockposition = new BlockPosition(i1, j1, k1);
                    EntityTypes<?> entitytypes = entityzombie.getEntityType();
                    EntityPositionTypes.Surface entitypositiontypes_surface = EntityPositionTypes.a(entitytypes);

                    if (SpawnerCreature.a(entitypositiontypes_surface, (IWorldReader) this.world, blockposition, entitytypes) && EntityPositionTypes.a(entitytypes, this.world, EnumMobSpawn.REINFORCEMENT, blockposition, this.world.random)) {
                        entityzombie.setPosition((double) i1, (double) j1, (double) k1);
                        if (!this.world.isPlayerNearby((double) i1, (double) j1, (double) k1, 7.0D) && this.world.i(entityzombie) && this.world.getCubes(entityzombie) && !this.world.containsLiquid(entityzombie.getBoundingBox())) {
                            this.world.addEntity(entityzombie);
                            entityzombie.setGoalTarget(entityliving);
                            entityzombie.prepare(this.world, this.world.getDamageScaler(entityzombie.getChunkCoordinates()), EnumMobSpawn.REINFORCEMENT, (GroupDataEntity) null, (NBTTagCompound) null);
                            this.getAttributeInstance(GenericAttributes.SPAWN_REINFORCEMENTS).addModifier(new AttributeModifier("Zombie reinforcement caller charge", -0.05000000074505806D, AttributeModifier.Operation.ADDITION));
                            entityzombie.getAttributeInstance(GenericAttributes.SPAWN_REINFORCEMENTS).addModifier(new AttributeModifier("Zombie reinforcement callee charge", -0.05000000074505806D, AttributeModifier.Operation.ADDITION));
                            break;
                        }
                    }
                }
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean attackEntity(Entity entity) {
        boolean flag = super.attackEntity(entity);

        if (flag) {
            float f = this.world.getDamageScaler(this.getChunkCoordinates()).b();

            if (this.getItemInMainHand().isEmpty() && this.isBurning() && this.random.nextFloat() < f * 0.3F) {
                entity.setOnFire(2 * (int) f);
            }
        }

        return flag;
    }

    @Override
    protected SoundEffect getSoundAmbient() {
        return SoundEffects.ENTITY_ZOMBIE_AMBIENT;
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.ENTITY_ZOMBIE_HURT;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.ENTITY_ZOMBIE_DEATH;
    }

    protected SoundEffect getSoundStep() {
        return SoundEffects.ENTITY_ZOMBIE_STEP;
    }

    @Override
    protected void a(BlockPosition blockposition, IBlockData iblockdata) {
        this.playSound(this.getSoundStep(), 0.15F, 1.0F);
    }

    @Override
    public EnumMonsterType getMonsterType() {
        return EnumMonsterType.UNDEAD;
    }

    @Override
    protected void a(DifficultyDamageScaler difficultydamagescaler) {
        super.a(difficultydamagescaler);
        if (this.random.nextFloat() < (this.world.getDifficulty() == EnumDifficulty.HARD ? 0.05F : 0.01F)) {
            int i = this.random.nextInt(3);

            if (i == 0) {
                this.setSlot(EnumItemSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
            } else {
                this.setSlot(EnumItemSlot.MAINHAND, new ItemStack(Items.IRON_SHOVEL));
            }
        }

    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);
        if (this.isBaby()) {
            nbttagcompound.setBoolean("IsBaby", true);
        }

        nbttagcompound.setBoolean("CanBreakDoors", this.eV());
        nbttagcompound.setInt("InWaterTime", this.isInWater() ? this.bA : -1);
        nbttagcompound.setInt("DrownedConversionTime", this.isDrownConverting() ? this.drownedConversionTime : -1);
    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        if (nbttagcompound.getBoolean("IsBaby")) {
            this.setBaby(true);
        }

        this.u(nbttagcompound.getBoolean("CanBreakDoors"));
        this.bA = nbttagcompound.getInt("InWaterTime");
        if (nbttagcompound.hasKeyOfType("DrownedConversionTime", 99) && nbttagcompound.getInt("DrownedConversionTime") > -1) {
            this.startDrownedConversion(nbttagcompound.getInt("DrownedConversionTime"));
        }

    }

    @Override
    public void a_(EntityLiving entityliving) {
        super.a_(entityliving);
        if ((this.world.getDifficulty() == EnumDifficulty.NORMAL || this.world.getDifficulty() == EnumDifficulty.HARD) && entityliving instanceof EntityVillager) {
            if (this.world.getDifficulty() != EnumDifficulty.HARD && this.random.nextBoolean()) {
                return;
            }

            EntityVillager entityvillager = (EntityVillager) entityliving;
            EntityZombieVillager entityzombievillager = (EntityZombieVillager) EntityTypes.ZOMBIE_VILLAGER.a(this.world);

            entityzombievillager.u(entityvillager);
            entityvillager.die();
            entityzombievillager.prepare(this.world, this.world.getDamageScaler(entityzombievillager.getChunkCoordinates()), EnumMobSpawn.CONVERSION, new EntityZombie.GroupDataZombie(false, true), (NBTTagCompound) null);
            entityzombievillager.setVillagerData(entityvillager.getVillagerData());
            entityzombievillager.a((NBTBase) entityvillager.fj().a((DynamicOps) DynamicOpsNBT.a).getValue());
            entityzombievillager.setOffers(entityvillager.getOffers().a());
            entityzombievillager.a(entityvillager.getExperience());
            entityzombievillager.setBaby(entityvillager.isBaby());
            entityzombievillager.setNoAI(entityvillager.isNoAI());
            if (entityvillager.hasCustomName()) {
                entityzombievillager.setCustomName(entityvillager.getCustomName());
                entityzombievillager.setCustomNameVisible(entityvillager.getCustomNameVisible());
            }

            if (entityvillager.isPersistent()) {
                entityzombievillager.setPersistent();
            }

            entityzombievillager.setInvulnerable(this.isInvulnerable());
            this.world.addEntity(entityzombievillager);
            if (!this.isSilent()) {
                this.world.a((EntityHuman) null, 1026, this.getChunkCoordinates(), 0);
            }
        }

    }

    @Override
    protected float b(EntityPose entitypose, EntitySize entitysize) {
        return this.isBaby() ? 0.93F : 1.74F;
    }

    @Override
    public boolean canPickup(ItemStack itemstack) {
        return itemstack.getItem() == Items.EGG && this.isBaby() && this.isPassenger() ? false : super.canPickup(itemstack);
    }

    @Nullable
    @Override
    public GroupDataEntity prepare(GeneratorAccess generatoraccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        Object object = super.prepare(generatoraccess, difficultydamagescaler, enummobspawn, groupdataentity, nbttagcompound);
        float f = difficultydamagescaler.d();

        this.setCanPickupLoot(this.random.nextFloat() < 0.55F * f);
        if (object == null) {
            object = new EntityZombie.GroupDataZombie(a(generatoraccess.getRandom()), true);
        }

        if (object instanceof EntityZombie.GroupDataZombie) {
            EntityZombie.GroupDataZombie entityzombie_groupdatazombie = (EntityZombie.GroupDataZombie) object;

            if (entityzombie_groupdatazombie.a) {
                this.setBaby(true);
                if (entityzombie_groupdatazombie.b) {
                    if ((double) generatoraccess.getRandom().nextFloat() < 0.05D) {
                        List<EntityChicken> list = generatoraccess.a(EntityChicken.class, this.getBoundingBox().grow(5.0D, 3.0D, 5.0D), IEntitySelector.c);

                        if (!list.isEmpty()) {
                            EntityChicken entitychicken = (EntityChicken) list.get(0);

                            entitychicken.setChickenJockey(true);
                            this.startRiding(entitychicken);
                        }
                    } else if ((double) generatoraccess.getRandom().nextFloat() < 0.05D) {
                        EntityChicken entitychicken1 = (EntityChicken) EntityTypes.CHICKEN.a(this.world);

                        entitychicken1.setPositionRotation(this.locX(), this.locY(), this.locZ(), this.yaw, 0.0F);
                        entitychicken1.prepare(generatoraccess, difficultydamagescaler, EnumMobSpawn.JOCKEY, (GroupDataEntity) null, (NBTTagCompound) null);
                        entitychicken1.setChickenJockey(true);
                        this.startRiding(entitychicken1);
                        generatoraccess.addEntity(entitychicken1);
                    }
                }
            }

            this.u(this.eL() && this.random.nextFloat() < f * 0.1F);
            this.a(difficultydamagescaler);
            this.b(difficultydamagescaler);
        }

        if (this.getEquipment(EnumItemSlot.HEAD).isEmpty()) {
            LocalDate localdate = LocalDate.now();
            int i = localdate.get(ChronoField.DAY_OF_MONTH);
            int j = localdate.get(ChronoField.MONTH_OF_YEAR);

            if (j == 10 && i == 31 && this.random.nextFloat() < 0.25F) {
                this.setSlot(EnumItemSlot.HEAD, new ItemStack(this.random.nextFloat() < 0.1F ? Blocks.JACK_O_LANTERN : Blocks.CARVED_PUMPKIN));
                this.dropChanceArmor[EnumItemSlot.HEAD.b()] = 0.0F;
            }
        }

        this.u(f);
        return (GroupDataEntity) object;
    }

    public static boolean a(Random random) {
        return random.nextFloat() < 0.05F;
    }

    protected void u(float f) {
        this.eW();
        this.getAttributeInstance(GenericAttributes.KNOCKBACK_RESISTANCE).addModifier(new AttributeModifier("Random spawn bonus", this.random.nextDouble() * 0.05000000074505806D, AttributeModifier.Operation.ADDITION));
        double d0 = this.random.nextDouble() * 1.5D * (double) f;

        if (d0 > 1.0D) {
            this.getAttributeInstance(GenericAttributes.FOLLOW_RANGE).addModifier(new AttributeModifier("Random zombie-spawn bonus", d0, AttributeModifier.Operation.MULTIPLY_TOTAL));
        }

        if (this.random.nextFloat() < f * 0.05F) {
            this.getAttributeInstance(GenericAttributes.SPAWN_REINFORCEMENTS).addModifier(new AttributeModifier("Leader zombie bonus", this.random.nextDouble() * 0.25D + 0.5D, AttributeModifier.Operation.ADDITION));
            this.getAttributeInstance(GenericAttributes.MAX_HEALTH).addModifier(new AttributeModifier("Leader zombie bonus", this.random.nextDouble() * 3.0D + 1.0D, AttributeModifier.Operation.MULTIPLY_TOTAL));
            this.u(this.eL());
        }

    }

    protected void eW() {
        this.getAttributeInstance(GenericAttributes.SPAWN_REINFORCEMENTS).setValue(this.random.nextDouble() * 0.10000000149011612D);
    }

    @Override
    public double aX() {
        return this.isBaby() ? 0.0D : -0.45D;
    }

    @Override
    protected void dropDeathLoot(DamageSource damagesource, int i, boolean flag) {
        super.dropDeathLoot(damagesource, i, flag);
        Entity entity = damagesource.getEntity();

        if (entity instanceof EntityCreeper) {
            EntityCreeper entitycreeper = (EntityCreeper) entity;

            if (entitycreeper.canCauseHeadDrop()) {
                entitycreeper.setCausedHeadDrop();
                ItemStack itemstack = this.eN();

                if (!itemstack.isEmpty()) {
                    this.a(itemstack);
                }
            }
        }

    }

    protected ItemStack eN() {
        return new ItemStack(Items.ZOMBIE_HEAD);
    }

    class a extends PathfinderGoalRemoveBlock {

        a(EntityCreature entitycreature, double d0, int i) {
            super(Blocks.TURTLE_EGG, entitycreature, d0, i);
        }

        @Override
        public void a(GeneratorAccess generatoraccess, BlockPosition blockposition) {
            generatoraccess.playSound((EntityHuman) null, blockposition, SoundEffects.ENTITY_ZOMBIE_DESTROY_EGG, SoundCategory.HOSTILE, 0.5F, 0.9F + EntityZombie.this.random.nextFloat() * 0.2F);
        }

        @Override
        public void a(World world, BlockPosition blockposition) {
            world.playSound((EntityHuman) null, blockposition, SoundEffects.ENTITY_TURTLE_EGG_BREAK, SoundCategory.BLOCKS, 0.7F, 0.9F + world.random.nextFloat() * 0.2F);
        }

        @Override
        public double h() {
            return 1.14D;
        }
    }

    public static class GroupDataZombie implements GroupDataEntity {

        public final boolean a;
        public final boolean b;

        public GroupDataZombie(boolean flag, boolean flag1) {
            this.a = flag;
            this.b = flag1;
        }
    }
}
