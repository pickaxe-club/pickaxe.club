package net.minecraft.server;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

public class EntityFox extends EntityAnimal {

    private static final DataWatcherObject<Integer> bv = DataWatcher.a(EntityFox.class, DataWatcherRegistry.b);
    private static final DataWatcherObject<Byte> bw = DataWatcher.a(EntityFox.class, DataWatcherRegistry.a);
    public static final DataWatcherObject<Optional<UUID>> FIRST_TRUSTED_PLAYER = DataWatcher.a(EntityFox.class, DataWatcherRegistry.o);
    public static final DataWatcherObject<Optional<UUID>> SECOND_TRUSTED_PLAYER = DataWatcher.a(EntityFox.class, DataWatcherRegistry.o);
    private static final Predicate<EntityItem> bz = (entityitem) -> {
        return !entityitem.p() && entityitem.isAlive();
    };
    private static final Predicate<Entity> bA = (entity) -> {
        if (!(entity instanceof EntityLiving)) {
            return false;
        } else {
            EntityLiving entityliving = (EntityLiving) entity;

            return entityliving.da() != null && entityliving.db() < entityliving.ticksLived + 600;
        }
    };
    private static final Predicate<Entity> bB = (entity) -> {
        return entity instanceof EntityChicken || entity instanceof EntityRabbit;
    };
    private static final Predicate<Entity> bC = (entity) -> {
        return !entity.bt() && IEntitySelector.e.test(entity);
    };
    private PathfinderGoal bD;
    private PathfinderGoal bE;
    private PathfinderGoal bF;
    private float bG;
    private float bH;
    private float bI;
    private float bJ;
    private int bK;

    public EntityFox(EntityTypes<? extends EntityFox> entitytypes, World world) {
        super(entitytypes, world);
        this.lookController = new EntityFox.k();
        this.moveController = new EntityFox.m();
        this.a(PathType.DANGER_OTHER, 0.0F);
        this.a(PathType.DAMAGE_OTHER, 0.0F);
        this.setCanPickupLoot(true);
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.datawatcher.register(EntityFox.FIRST_TRUSTED_PLAYER, Optional.empty());
        this.datawatcher.register(EntityFox.SECOND_TRUSTED_PLAYER, Optional.empty());
        this.datawatcher.register(EntityFox.bv, 0);
        this.datawatcher.register(EntityFox.bw, (byte) 0);
    }

    @Override
    protected void initPathfinder() {
        this.bD = new PathfinderGoalNearestAttackableTarget<>(this, EntityAnimal.class, 10, false, false, (entityliving) -> {
            return entityliving instanceof EntityChicken || entityliving instanceof EntityRabbit;
        });
        this.bE = new PathfinderGoalNearestAttackableTarget<>(this, EntityTurtle.class, 10, false, false, EntityTurtle.bv);
        this.bF = new PathfinderGoalNearestAttackableTarget<>(this, EntityFish.class, 20, false, false, (entityliving) -> {
            return entityliving instanceof EntityFishSchool;
        });
        this.goalSelector.a(0, new EntityFox.g());
        this.goalSelector.a(1, new EntityFox.b());
        this.goalSelector.a(2, new EntityFox.n(2.2D));
        this.goalSelector.a(3, new EntityFox.e(1.0D));
        this.goalSelector.a(4, new PathfinderGoalAvoidTarget<>(this, EntityHuman.class, 16.0F, 1.6D, 1.4D, (entityliving) -> {
            return EntityFox.bC.test(entityliving) && !this.c(entityliving.getUniqueID()) && !this.fc();
        }));
        this.goalSelector.a(4, new PathfinderGoalAvoidTarget<>(this, EntityWolf.class, 8.0F, 1.6D, 1.4D, (entityliving) -> {
            return !((EntityWolf) entityliving).isTamed() && !this.fc();
        }));
        this.goalSelector.a(4, new PathfinderGoalAvoidTarget<>(this, EntityPolarBear.class, 8.0F, 1.6D, 1.4D, (entityliving) -> {
            return !this.fc();
        }));
        this.goalSelector.a(5, new EntityFox.u());
        this.goalSelector.a(6, new EntityFox.o());
        this.goalSelector.a(6, new EntityFox.s(1.25D));
        this.goalSelector.a(7, new EntityFox.l(1.2000000476837158D, true));
        this.goalSelector.a(7, new EntityFox.t());
        this.goalSelector.a(8, new EntityFox.h(this, 1.25D));
        this.goalSelector.a(9, new EntityFox.q(32, 200));
        this.goalSelector.a(10, new EntityFox.f(1.2000000476837158D, 12, 2));
        this.goalSelector.a(10, new PathfinderGoalLeapAtTarget(this, 0.4F));
        this.goalSelector.a(11, new PathfinderGoalRandomStrollLand(this, 1.0D));
        this.goalSelector.a(11, new EntityFox.p());
        this.goalSelector.a(12, new EntityFox.j(this, EntityHuman.class, 24.0F));
        this.goalSelector.a(13, new EntityFox.r());
        this.targetSelector.a(3, new EntityFox.a(EntityLiving.class, false, false, (entityliving) -> {
            return EntityFox.bA.test(entityliving) && !this.c(entityliving.getUniqueID());
        }));
    }

    @Override
    public SoundEffect d(ItemStack itemstack) {
        return SoundEffects.ENTITY_FOX_EAT;
    }

    @Override
    public void movementTick() {
        if (!this.world.isClientSide && this.isAlive() && this.doAITick()) {
            ++this.bK;
            ItemStack itemstack = this.getEquipment(EnumItemSlot.MAINHAND);

            if (this.l(itemstack)) {
                if (this.bK > 600) {
                    ItemStack itemstack1 = itemstack.a(this.world, (EntityLiving) this);

                    if (!itemstack1.isEmpty()) {
                        this.setSlot(EnumItemSlot.MAINHAND, itemstack1);
                    }

                    this.bK = 0;
                } else if (this.bK > 560 && this.random.nextFloat() < 0.1F) {
                    this.playSound(this.d(itemstack), 1.0F, 1.0F);
                    this.world.broadcastEntityEffect(this, (byte) 45);
                }
            }

            EntityLiving entityliving = this.getGoalTarget();

            if (entityliving == null || !entityliving.isAlive()) {
                this.setCrouching(false);
                this.w(false);
            }
        }

        if (this.isSleeping() || this.isFrozen()) {
            this.jumping = false;
            this.aY = 0.0F;
            this.ba = 0.0F;
        }

        super.movementTick();
        if (this.fc() && this.random.nextFloat() < 0.05F) {
            this.playSound(SoundEffects.ENTITY_FOX_AGGRO, 1.0F, 1.0F);
        }

    }

    @Override
    protected boolean isFrozen() {
        return this.dk();
    }

    private boolean l(ItemStack itemstack) {
        return itemstack.getItem().isFood() && this.getGoalTarget() == null && this.onGround && !this.isSleeping();
    }

    @Override
    protected void a(DifficultyDamageScaler difficultydamagescaler) {
        if (this.random.nextFloat() < 0.2F) {
            float f = this.random.nextFloat();
            ItemStack itemstack;

            if (f < 0.05F) {
                itemstack = new ItemStack(Items.EMERALD);
            } else if (f < 0.2F) {
                itemstack = new ItemStack(Items.EGG);
            } else if (f < 0.4F) {
                itemstack = this.random.nextBoolean() ? new ItemStack(Items.RABBIT_FOOT) : new ItemStack(Items.RABBIT_HIDE);
            } else if (f < 0.6F) {
                itemstack = new ItemStack(Items.WHEAT);
            } else if (f < 0.8F) {
                itemstack = new ItemStack(Items.LEATHER);
            } else {
                itemstack = new ItemStack(Items.FEATHER);
            }

            this.setSlot(EnumItemSlot.MAINHAND, itemstack);
        }

    }

    public static AttributeProvider.Builder eL() {
        return EntityInsentient.p().a(GenericAttributes.MOVEMENT_SPEED, 0.30000001192092896D).a(GenericAttributes.MAX_HEALTH, 10.0D).a(GenericAttributes.FOLLOW_RANGE, 32.0D).a(GenericAttributes.ATTACK_DAMAGE, 2.0D);
    }

    @Override
    public EntityFox createChild(EntityAgeable entityageable) {
        EntityFox entityfox = (EntityFox) EntityTypes.FOX.a(this.world);

        entityfox.setFoxType(this.random.nextBoolean() ? this.getFoxType() : ((EntityFox) entityageable).getFoxType());
        return entityfox;
    }

    @Nullable
    @Override
    public GroupDataEntity prepare(GeneratorAccess generatoraccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        BiomeBase biomebase = generatoraccess.getBiome(this.getChunkCoordinates());
        EntityFox.Type entityfox_type = EntityFox.Type.a(biomebase);
        boolean flag = false;

        if (groupdataentity instanceof EntityFox.i) {
            entityfox_type = ((EntityFox.i) groupdataentity).a;
            if (((EntityFox.i) groupdataentity).a() >= 2) {
                flag = true;
            }
        } else {
            groupdataentity = new EntityFox.i(entityfox_type);
        }

        this.setFoxType(entityfox_type);
        if (flag) {
            this.setAgeRaw(-24000);
        }

        if (generatoraccess instanceof WorldServer) {
            this.initializePathFinderGoals();
        }

        this.a(difficultydamagescaler);
        return super.prepare(generatoraccess, difficultydamagescaler, enummobspawn, (GroupDataEntity) groupdataentity, nbttagcompound);
    }

    private void initializePathFinderGoals() {
        if (this.getFoxType() == EntityFox.Type.RED) {
            this.targetSelector.a(4, this.bD);
            this.targetSelector.a(4, this.bE);
            this.targetSelector.a(6, this.bF);
        } else {
            this.targetSelector.a(4, this.bF);
            this.targetSelector.a(6, this.bD);
            this.targetSelector.a(6, this.bE);
        }

    }

    @Override
    protected void a(EntityHuman entityhuman, ItemStack itemstack) {
        if (this.k(itemstack)) {
            this.playSound(this.d(itemstack), 1.0F, 1.0F);
        }

        super.a(entityhuman, itemstack);
    }

    @Override
    protected float b(EntityPose entitypose, EntitySize entitysize) {
        return this.isBaby() ? entitysize.height * 0.85F : 0.4F;
    }

    public EntityFox.Type getFoxType() {
        return EntityFox.Type.a((Integer) this.datawatcher.get(EntityFox.bv));
    }

    public void setFoxType(EntityFox.Type entityfox_type) {
        this.datawatcher.set(EntityFox.bv, entityfox_type.c());
    }

    private List<UUID> fb() {
        List<UUID> list = Lists.newArrayList();

        list.add(((Optional) this.datawatcher.get(EntityFox.FIRST_TRUSTED_PLAYER)).orElse((Object) null));
        list.add(((Optional) this.datawatcher.get(EntityFox.SECOND_TRUSTED_PLAYER)).orElse((Object) null));
        return list;
    }

    private void b(@Nullable UUID uuid) {
        if (((Optional) this.datawatcher.get(EntityFox.FIRST_TRUSTED_PLAYER)).isPresent()) {
            this.datawatcher.set(EntityFox.SECOND_TRUSTED_PLAYER, Optional.ofNullable(uuid));
        } else {
            this.datawatcher.set(EntityFox.FIRST_TRUSTED_PLAYER, Optional.ofNullable(uuid));
        }

    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);
        List<UUID> list = this.fb();
        NBTTagList nbttaglist = new NBTTagList();
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            UUID uuid = (UUID) iterator.next();

            if (uuid != null) {
                nbttaglist.add(GameProfileSerializer.a(uuid));
            }
        }

        nbttagcompound.set("Trusted", nbttaglist);
        nbttagcompound.setBoolean("Sleeping", this.isSleeping());
        nbttagcompound.setString("Type", this.getFoxType().a());
        nbttagcompound.setBoolean("Sitting", this.isSitting());
        nbttagcompound.setBoolean("Crouching", this.isCrouching());
    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        NBTTagList nbttaglist = nbttagcompound.getList("Trusted", 11);

        for (int i = 0; i < nbttaglist.size(); ++i) {
            this.b(GameProfileSerializer.a(nbttaglist.get(i)));
        }

        this.setSleeping(nbttagcompound.getBoolean("Sleeping"));
        this.setFoxType(EntityFox.Type.a(nbttagcompound.getString("Type")));
        this.setSitting(nbttagcompound.getBoolean("Sitting"));
        this.setCrouching(nbttagcompound.getBoolean("Crouching"));
        if (this.world instanceof WorldServer) {
            this.initializePathFinderGoals();
        }

    }

    public boolean isSitting() {
        return this.t(1);
    }

    public void setSitting(boolean flag) {
        this.d(1, flag);
    }

    public boolean eO() {
        return this.t(64);
    }

    private void x(boolean flag) {
        this.d(64, flag);
    }

    private boolean fc() {
        return this.t(128);
    }

    private void y(boolean flag) {
        this.d(128, flag);
    }

    @Override
    public boolean isSleeping() {
        return this.t(32);
    }

    public void setSleeping(boolean flag) {
        this.d(32, flag);
    }

    private void d(int i, boolean flag) {
        if (flag) {
            this.datawatcher.set(EntityFox.bw, (byte) ((Byte) this.datawatcher.get(EntityFox.bw) | i));
        } else {
            this.datawatcher.set(EntityFox.bw, (byte) ((Byte) this.datawatcher.get(EntityFox.bw) & ~i));
        }

    }

    private boolean t(int i) {
        return ((Byte) this.datawatcher.get(EntityFox.bw) & i) != 0;
    }

    @Override
    public boolean e(ItemStack itemstack) {
        EnumItemSlot enumitemslot = EntityInsentient.j(itemstack);

        return !this.getEquipment(enumitemslot).isEmpty() ? false : enumitemslot == EnumItemSlot.MAINHAND && super.e(itemstack);
    }

    @Override
    public boolean canPickup(ItemStack itemstack) {
        Item item = itemstack.getItem();
        ItemStack itemstack1 = this.getEquipment(EnumItemSlot.MAINHAND);

        return itemstack1.isEmpty() || this.bK > 0 && item.isFood() && !itemstack1.getItem().isFood();
    }

    private void m(ItemStack itemstack) {
        if (!itemstack.isEmpty() && !this.world.isClientSide) {
            EntityItem entityitem = new EntityItem(this.world, this.locX() + this.getLookDirection().x, this.locY() + 1.0D, this.locZ() + this.getLookDirection().z, itemstack);

            entityitem.setPickupDelay(40);
            entityitem.setThrower(this.getUniqueID());
            this.playSound(SoundEffects.ENTITY_FOX_SPIT, 1.0F, 1.0F);
            this.world.addEntity(entityitem);
        }
    }

    private void n(ItemStack itemstack) {
        EntityItem entityitem = new EntityItem(this.world, this.locX(), this.locY(), this.locZ(), itemstack);

        this.world.addEntity(entityitem);
    }

    @Override
    protected void b(EntityItem entityitem) {
        ItemStack itemstack = entityitem.getItemStack();

        if (this.canPickup(itemstack)) {
            int i = itemstack.getCount();

            if (i > 1) {
                this.n(itemstack.cloneAndSubtract(i - 1));
            }

            this.m(this.getEquipment(EnumItemSlot.MAINHAND));
            this.a(entityitem);
            this.setSlot(EnumItemSlot.MAINHAND, itemstack.cloneAndSubtract(1));
            this.dropChanceHand[EnumItemSlot.MAINHAND.b()] = 2.0F;
            this.receive(entityitem, itemstack.getCount());
            entityitem.die();
            this.bK = 0;
        }

    }

    @Override
    public void tick() {
        super.tick();
        if (this.doAITick()) {
            boolean flag = this.isInWater();

            if (flag || this.getGoalTarget() != null || this.world.T()) {
                this.fd();
            }

            if (flag || this.isSleeping()) {
                this.setSitting(false);
            }

            if (this.eO() && this.world.random.nextFloat() < 0.2F) {
                BlockPosition blockposition = this.getChunkCoordinates();
                IBlockData iblockdata = this.world.getType(blockposition);

                this.world.triggerEffect(2001, blockposition, Block.getCombinedId(iblockdata));
            }
        }

        this.bH = this.bG;
        if (this.eX()) {
            this.bG += (1.0F - this.bG) * 0.4F;
        } else {
            this.bG += (0.0F - this.bG) * 0.4F;
        }

        this.bJ = this.bI;
        if (this.isCrouching()) {
            this.bI += 0.2F;
            if (this.bI > 3.0F) {
                this.bI = 3.0F;
            }
        } else {
            this.bI = 0.0F;
        }

    }

    @Override
    public boolean k(ItemStack itemstack) {
        return itemstack.getItem() == Items.SWEET_BERRIES;
    }

    @Override
    protected void a(EntityHuman entityhuman, EntityInsentient entityinsentient) {
        ((EntityFox) entityinsentient).b(entityhuman.getUniqueID());
    }

    public boolean eP() {
        return this.t(16);
    }

    public void u(boolean flag) {
        this.d(16, flag);
    }

    public boolean eW() {
        return this.bI == 3.0F;
    }

    public void setCrouching(boolean flag) {
        this.d(4, flag);
    }

    public boolean isCrouching() {
        return this.t(4);
    }

    public void w(boolean flag) {
        this.d(8, flag);
    }

    public boolean eX() {
        return this.t(8);
    }

    @Override
    public void setGoalTarget(@Nullable EntityLiving entityliving) {
        if (this.fc() && entityliving == null) {
            this.y(false);
        }

        super.setGoalTarget(entityliving);
    }

    @Override
    protected int e(float f, float f1) {
        return MathHelper.f((f - 5.0F) * f1);
    }

    private void fd() {
        this.setSleeping(false);
    }

    private void fe() {
        this.w(false);
        this.setCrouching(false);
        this.setSitting(false);
        this.setSleeping(false);
        this.y(false);
        this.x(false);
    }

    private boolean ff() {
        return !this.isSleeping() && !this.isSitting() && !this.eO();
    }

    @Override
    public void F() {
        SoundEffect soundeffect = this.getSoundAmbient();

        if (soundeffect == SoundEffects.ENTITY_FOX_SCREECH) {
            this.playSound(soundeffect, 2.0F, this.dG());
        } else {
            super.F();
        }

    }

    @Nullable
    @Override
    protected SoundEffect getSoundAmbient() {
        if (this.isSleeping()) {
            return SoundEffects.ENTITY_FOX_SLEEP;
        } else {
            if (!this.world.isDay() && this.random.nextFloat() < 0.1F) {
                List<EntityHuman> list = this.world.a(EntityHuman.class, this.getBoundingBox().grow(16.0D, 16.0D, 16.0D), IEntitySelector.g);

                if (list.isEmpty()) {
                    return SoundEffects.ENTITY_FOX_SCREECH;
                }
            }

            return SoundEffects.ENTITY_FOX_AMBIENT;
        }
    }

    @Nullable
    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.ENTITY_FOX_HURT;
    }

    @Nullable
    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.ENTITY_FOX_DEATH;
    }

    private boolean c(UUID uuid) {
        return this.fb().contains(uuid);
    }

    @Override
    protected void d(DamageSource damagesource) {
        ItemStack itemstack = this.getEquipment(EnumItemSlot.MAINHAND);

        if (!itemstack.isEmpty()) {
            this.a(itemstack);
            this.setSlot(EnumItemSlot.MAINHAND, ItemStack.b);
        }

        super.d(damagesource);
    }

    public static boolean a(EntityFox entityfox, EntityLiving entityliving) {
        double d0 = entityliving.locZ() - entityfox.locZ();
        double d1 = entityliving.locX() - entityfox.locX();
        double d2 = d0 / d1;
        boolean flag = true;

        for (int i = 0; i < 6; ++i) {
            double d3 = d2 == 0.0D ? 0.0D : d0 * (double) ((float) i / 6.0F);
            double d4 = d2 == 0.0D ? d1 * (double) ((float) i / 6.0F) : d3 / d2;

            for (int j = 1; j < 4; ++j) {
                if (!entityfox.world.getType(new BlockPosition(entityfox.locX() + d4, entityfox.locY() + (double) j, entityfox.locZ() + d3)).getMaterial().isReplaceable()) {
                    return false;
                }
            }
        }

        return true;
    }

    class j extends PathfinderGoalLookAtPlayer {

        public j(EntityInsentient entityinsentient, Class oclass, float f) {
            super(entityinsentient, oclass, f);
        }

        @Override
        public boolean a() {
            return super.a() && !EntityFox.this.eO() && !EntityFox.this.eX();
        }

        @Override
        public boolean b() {
            return super.b() && !EntityFox.this.eO() && !EntityFox.this.eX();
        }
    }

    class h extends PathfinderGoalFollowParent {

        private final EntityFox b;

        public h(EntityFox entityfox, double d0) {
            super(entityfox, d0);
            this.b = entityfox;
        }

        @Override
        public boolean a() {
            return !this.b.fc() && super.a();
        }

        @Override
        public boolean b() {
            return !this.b.fc() && super.b();
        }

        @Override
        public void c() {
            this.b.fe();
            super.c();
        }
    }

    public class k extends ControllerLook {

        public k() {
            super(EntityFox.this);
        }

        @Override
        public void a() {
            if (!EntityFox.this.isSleeping()) {
                super.a();
            }

        }

        @Override
        protected boolean b() {
            return !EntityFox.this.eP() && !EntityFox.this.isCrouching() && !EntityFox.this.eX() & !EntityFox.this.eO();
        }
    }

    public class o extends PathfinderGoalWaterJumpAbstract {

        public o() {}

        @Override
        public boolean a() {
            if (!EntityFox.this.eW()) {
                return false;
            } else {
                EntityLiving entityliving = EntityFox.this.getGoalTarget();

                if (entityliving != null && entityliving.isAlive()) {
                    if (entityliving.getAdjustedDirection() != entityliving.getDirection()) {
                        return false;
                    } else {
                        boolean flag = EntityFox.a((EntityFox) EntityFox.this, entityliving);

                        if (!flag) {
                            EntityFox.this.getNavigation().a((Entity) entityliving, 0);
                            EntityFox.this.setCrouching(false);
                            EntityFox.this.w(false);
                        }

                        return flag;
                    }
                } else {
                    return false;
                }
            }
        }

        @Override
        public boolean b() {
            EntityLiving entityliving = EntityFox.this.getGoalTarget();

            if (entityliving != null && entityliving.isAlive()) {
                double d0 = EntityFox.this.getMot().y;

                return (d0 * d0 >= 0.05000000074505806D || Math.abs(EntityFox.this.pitch) >= 15.0F || !EntityFox.this.onGround) && !EntityFox.this.eO();
            } else {
                return false;
            }
        }

        @Override
        public boolean D_() {
            return false;
        }

        @Override
        public void c() {
            EntityFox.this.setJumping(true);
            EntityFox.this.u(true);
            EntityFox.this.w(false);
            EntityLiving entityliving = EntityFox.this.getGoalTarget();

            EntityFox.this.getControllerLook().a(entityliving, 60.0F, 30.0F);
            Vec3D vec3d = (new Vec3D(entityliving.locX() - EntityFox.this.locX(), entityliving.locY() - EntityFox.this.locY(), entityliving.locZ() - EntityFox.this.locZ())).d();

            EntityFox.this.setMot(EntityFox.this.getMot().add(vec3d.x * 0.8D, 0.9D, vec3d.z * 0.8D));
            EntityFox.this.getNavigation().o();
        }

        @Override
        public void d() {
            EntityFox.this.setCrouching(false);
            EntityFox.this.bI = 0.0F;
            EntityFox.this.bJ = 0.0F;
            EntityFox.this.w(false);
            EntityFox.this.u(false);
        }

        @Override
        public void e() {
            EntityLiving entityliving = EntityFox.this.getGoalTarget();

            if (entityliving != null) {
                EntityFox.this.getControllerLook().a(entityliving, 60.0F, 30.0F);
            }

            if (!EntityFox.this.eO()) {
                Vec3D vec3d = EntityFox.this.getMot();

                if (vec3d.y * vec3d.y < 0.029999999329447746D && EntityFox.this.pitch != 0.0F) {
                    EntityFox.this.pitch = MathHelper.j(EntityFox.this.pitch, 0.0F, 0.2F);
                } else {
                    double d0 = Math.sqrt(Entity.b(vec3d));
                    double d1 = Math.signum(-vec3d.y) * Math.acos(d0 / vec3d.f()) * 57.2957763671875D;

                    EntityFox.this.pitch = (float) d1;
                }
            }

            if (entityliving != null && EntityFox.this.g((Entity) entityliving) <= 2.0F) {
                EntityFox.this.attackEntity(entityliving);
            } else if (EntityFox.this.pitch > 0.0F && EntityFox.this.onGround && (float) EntityFox.this.getMot().y != 0.0F && EntityFox.this.world.getType(EntityFox.this.getChunkCoordinates()).a(Blocks.SNOW)) {
                EntityFox.this.pitch = 60.0F;
                EntityFox.this.setGoalTarget((EntityLiving) null);
                EntityFox.this.x(true);
            }

        }
    }

    class g extends PathfinderGoalFloat {

        public g() {
            super(EntityFox.this);
        }

        @Override
        public void c() {
            super.c();
            EntityFox.this.fe();
        }

        @Override
        public boolean a() {
            return EntityFox.this.isInWater() && EntityFox.this.b((Tag) TagsFluid.WATER) > 0.25D || EntityFox.this.aN();
        }
    }

    class q extends PathfinderGoalNearestVillage {

        public q(int i, int j) {
            super(EntityFox.this, j);
        }

        @Override
        public void c() {
            EntityFox.this.fe();
            super.c();
        }

        @Override
        public boolean a() {
            return super.a() && this.g();
        }

        @Override
        public boolean b() {
            return super.b() && this.g();
        }

        private boolean g() {
            return !EntityFox.this.isSleeping() && !EntityFox.this.isSitting() && !EntityFox.this.fc() && EntityFox.this.getGoalTarget() == null;
        }
    }

    class n extends PathfinderGoalPanic {

        public n(double d0) {
            super(EntityFox.this, d0);
        }

        @Override
        public boolean a() {
            return !EntityFox.this.fc() && super.a();
        }
    }

    class b extends PathfinderGoal {

        int a;

        public b() {
            this.a(EnumSet.of(PathfinderGoal.Type.LOOK, PathfinderGoal.Type.JUMP, PathfinderGoal.Type.MOVE));
        }

        @Override
        public boolean a() {
            return EntityFox.this.eO();
        }

        @Override
        public boolean b() {
            return this.a() && this.a > 0;
        }

        @Override
        public void c() {
            this.a = 40;
        }

        @Override
        public void d() {
            EntityFox.this.x(false);
        }

        @Override
        public void e() {
            --this.a;
        }
    }

    public static class i extends EntityAgeable.a {

        public final EntityFox.Type a;

        public i(EntityFox.Type entityfox_type) {
            this.a(false);
            this.a = entityfox_type;
        }
    }

    public class f extends PathfinderGoalGotoTarget {

        protected int g;

        public f(double d0, int i, int j) {
            super(EntityFox.this, d0, i, j);
        }

        @Override
        public double h() {
            return 2.0D;
        }

        @Override
        public boolean j() {
            return this.d % 100 == 0;
        }

        @Override
        protected boolean a(IWorldReader iworldreader, BlockPosition blockposition) {
            IBlockData iblockdata = iworldreader.getType(blockposition);

            return iblockdata.a(Blocks.SWEET_BERRY_BUSH) && (Integer) iblockdata.get(BlockSweetBerryBush.a) >= 2;
        }

        @Override
        public void e() {
            if (this.k()) {
                if (this.g >= 40) {
                    this.m();
                } else {
                    ++this.g;
                }
            } else if (!this.k() && EntityFox.this.random.nextFloat() < 0.05F) {
                EntityFox.this.playSound(SoundEffects.ENTITY_FOX_SNIFF, 1.0F, 1.0F);
            }

            super.e();
        }

        protected void m() {
            if (EntityFox.this.world.getGameRules().getBoolean(GameRules.MOB_GRIEFING)) {
                IBlockData iblockdata = EntityFox.this.world.getType(this.e);

                if (iblockdata.a(Blocks.SWEET_BERRY_BUSH)) {
                    int i = (Integer) iblockdata.get(BlockSweetBerryBush.a);

                    iblockdata.set(BlockSweetBerryBush.a, 1);
                    int j = 1 + EntityFox.this.world.random.nextInt(2) + (i == 3 ? 1 : 0);
                    ItemStack itemstack = EntityFox.this.getEquipment(EnumItemSlot.MAINHAND);

                    if (itemstack.isEmpty()) {
                        EntityFox.this.setSlot(EnumItemSlot.MAINHAND, new ItemStack(Items.SWEET_BERRIES));
                        --j;
                    }

                    if (j > 0) {
                        Block.a(EntityFox.this.world, this.e, new ItemStack(Items.SWEET_BERRIES, j));
                    }

                    EntityFox.this.playSound(SoundEffects.ITEM_SWEET_BERRIES_PICK_FROM_BUSH, 1.0F, 1.0F);
                    EntityFox.this.world.setTypeAndData(this.e, (IBlockData) iblockdata.set(BlockSweetBerryBush.a, 1), 2);
                }
            }
        }

        @Override
        public boolean a() {
            return !EntityFox.this.isSleeping() && super.a();
        }

        @Override
        public void c() {
            this.g = 0;
            EntityFox.this.setSitting(false);
            super.c();
        }
    }

    class r extends EntityFox.d {

        private double c;
        private double d;
        private int e;
        private int f;

        public r() {
            super(null);
            this.a(EnumSet.of(PathfinderGoal.Type.MOVE, PathfinderGoal.Type.LOOK));
        }

        @Override
        public boolean a() {
            return EntityFox.this.getLastDamager() == null && EntityFox.this.getRandom().nextFloat() < 0.02F && !EntityFox.this.isSleeping() && EntityFox.this.getGoalTarget() == null && EntityFox.this.getNavigation().m() && !this.h() && !EntityFox.this.eP() && !EntityFox.this.isCrouching();
        }

        @Override
        public boolean b() {
            return this.f > 0;
        }

        @Override
        public void c() {
            this.j();
            this.f = 2 + EntityFox.this.getRandom().nextInt(3);
            EntityFox.this.setSitting(true);
            EntityFox.this.getNavigation().o();
        }

        @Override
        public void d() {
            EntityFox.this.setSitting(false);
        }

        @Override
        public void e() {
            --this.e;
            if (this.e <= 0) {
                --this.f;
                this.j();
            }

            EntityFox.this.getControllerLook().a(EntityFox.this.locX() + this.c, EntityFox.this.getHeadY(), EntityFox.this.locZ() + this.d, (float) EntityFox.this.ep(), (float) EntityFox.this.eo());
        }

        private void j() {
            double d0 = 6.283185307179586D * EntityFox.this.getRandom().nextDouble();

            this.c = Math.cos(d0);
            this.d = Math.sin(d0);
            this.e = 80 + EntityFox.this.getRandom().nextInt(20);
        }
    }

    class t extends EntityFox.d {

        private int c;

        public t() {
            super(null);
            this.c = EntityFox.this.random.nextInt(140);
            this.a(EnumSet.of(PathfinderGoal.Type.MOVE, PathfinderGoal.Type.LOOK, PathfinderGoal.Type.JUMP));
        }

        @Override
        public boolean a() {
            return EntityFox.this.aY == 0.0F && EntityFox.this.aZ == 0.0F && EntityFox.this.ba == 0.0F ? this.j() || EntityFox.this.isSleeping() : false;
        }

        @Override
        public boolean b() {
            return this.j();
        }

        private boolean j() {
            if (this.c > 0) {
                --this.c;
                return false;
            } else {
                return EntityFox.this.world.isDay() && this.g() && !this.h();
            }
        }

        @Override
        public void d() {
            this.c = EntityFox.this.random.nextInt(140);
            EntityFox.this.fe();
        }

        @Override
        public void c() {
            EntityFox.this.setSitting(false);
            EntityFox.this.setCrouching(false);
            EntityFox.this.w(false);
            EntityFox.this.setJumping(false);
            EntityFox.this.setSleeping(true);
            EntityFox.this.getNavigation().o();
            EntityFox.this.getControllerMove().a(EntityFox.this.locX(), EntityFox.this.locY(), EntityFox.this.locZ(), 0.0D);
        }
    }

    abstract class d extends PathfinderGoal {

        private final PathfinderTargetCondition b;

        private d() {
            this.b = (new PathfinderTargetCondition()).a(12.0D).c().a(EntityFox.this.new c());
        }

        protected boolean g() {
            BlockPosition blockposition = new BlockPosition(EntityFox.this.locX(), EntityFox.this.getBoundingBox().maxY, EntityFox.this.locZ());

            return !EntityFox.this.world.f(blockposition) && EntityFox.this.f(blockposition) >= 0.0F;
        }

        protected boolean h() {
            return !EntityFox.this.world.a(EntityLiving.class, this.b, EntityFox.this, EntityFox.this.getBoundingBox().grow(12.0D, 6.0D, 12.0D)).isEmpty();
        }
    }

    public class c implements Predicate<EntityLiving> {

        public c() {}

        public boolean test(EntityLiving entityliving) {
            return entityliving instanceof EntityFox ? false : (!(entityliving instanceof EntityChicken) && !(entityliving instanceof EntityRabbit) && !(entityliving instanceof EntityMonster) ? (entityliving instanceof EntityTameableAnimal ? !((EntityTameableAnimal) entityliving).isTamed() : (entityliving instanceof EntityHuman && (entityliving.isSpectator() || ((EntityHuman) entityliving).isCreative()) ? false : (EntityFox.this.c(entityliving.getUniqueID()) ? false : !entityliving.isSleeping() && !entityliving.bt()))) : true);
        }
    }

    class s extends PathfinderGoalFleeSun {

        private int c = 100;

        public s(double d0) {
            super(EntityFox.this, d0);
        }

        @Override
        public boolean a() {
            if (!EntityFox.this.isSleeping() && this.a.getGoalTarget() == null) {
                if (EntityFox.this.world.T()) {
                    return true;
                } else if (this.c > 0) {
                    --this.c;
                    return false;
                } else {
                    this.c = 100;
                    BlockPosition blockposition = this.a.getChunkCoordinates();

                    return EntityFox.this.world.isDay() && EntityFox.this.world.f(blockposition) && !((WorldServer) EntityFox.this.world).b_(blockposition) && this.g();
                }
            } else {
                return false;
            }
        }

        @Override
        public void c() {
            EntityFox.this.fe();
            super.c();
        }
    }

    class a extends PathfinderGoalNearestAttackableTarget<EntityLiving> {

        @Nullable
        private EntityLiving j;
        private EntityLiving k;
        private int l;

        public a(Class oclass, boolean flag, boolean flag1, Predicate predicate) {
            super(EntityFox.this, oclass, 10, flag, flag1, predicate);
        }

        @Override
        public boolean a() {
            if (this.b > 0 && this.e.getRandom().nextInt(this.b) != 0) {
                return false;
            } else {
                Iterator iterator = EntityFox.this.fb().iterator();

                while (iterator.hasNext()) {
                    UUID uuid = (UUID) iterator.next();

                    if (uuid != null && EntityFox.this.world instanceof WorldServer) {
                        Entity entity = ((WorldServer) EntityFox.this.world).getEntity(uuid);

                        if (entity instanceof EntityLiving) {
                            EntityLiving entityliving = (EntityLiving) entity;

                            this.k = entityliving;
                            this.j = entityliving.getLastDamager();
                            int i = entityliving.cZ();

                            return i != this.l && this.a(this.j, this.d);
                        }
                    }
                }

                return false;
            }
        }

        @Override
        public void c() {
            this.a(this.j);
            this.c = this.j;
            if (this.k != null) {
                this.l = this.k.cZ();
            }

            EntityFox.this.playSound(SoundEffects.ENTITY_FOX_AGGRO, 1.0F, 1.0F);
            EntityFox.this.y(true);
            EntityFox.this.fd();
            super.c();
        }
    }

    class e extends PathfinderGoalBreed {

        public e(double d0) {
            super(EntityFox.this, d0);
        }

        @Override
        public void c() {
            ((EntityFox) this.animal).fe();
            ((EntityFox) this.partner).fe();
            super.c();
        }

        @Override
        protected void g() {
            EntityFox entityfox = (EntityFox) this.animal.createChild(this.partner);

            if (entityfox != null) {
                EntityPlayer entityplayer = this.animal.getBreedCause();
                EntityPlayer entityplayer1 = this.partner.getBreedCause();
                EntityPlayer entityplayer2 = entityplayer;

                if (entityplayer != null) {
                    entityfox.b(entityplayer.getUniqueID());
                } else {
                    entityplayer2 = entityplayer1;
                }

                if (entityplayer1 != null && entityplayer != entityplayer1) {
                    entityfox.b(entityplayer1.getUniqueID());
                }

                if (entityplayer2 != null) {
                    entityplayer2.a(StatisticList.ANIMALS_BRED);
                    CriterionTriggers.o.a(entityplayer2, this.animal, this.partner, (EntityAgeable) entityfox);
                }

                this.animal.setAgeRaw(6000);
                this.partner.setAgeRaw(6000);
                this.animal.resetLove();
                this.partner.resetLove();
                entityfox.setAgeRaw(-24000);
                entityfox.setPositionRotation(this.animal.locX(), this.animal.locY(), this.animal.locZ(), 0.0F, 0.0F);
                this.b.addEntity(entityfox);
                this.b.broadcastEntityEffect(this.animal, (byte) 18);
                if (this.b.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
                    this.b.addEntity(new EntityExperienceOrb(this.b, this.animal.locX(), this.animal.locY(), this.animal.locZ(), this.animal.getRandom().nextInt(7) + 1));
                }

            }
        }
    }

    class l extends PathfinderGoalMeleeAttack {

        public l(double d0, boolean flag) {
            super(EntityFox.this, d0, flag);
        }

        @Override
        protected void a(EntityLiving entityliving, double d0) {
            double d1 = this.a(entityliving);

            if (d0 <= d1 && this.h()) {
                this.g();
                this.a.attackEntity(entityliving);
                EntityFox.this.playSound(SoundEffects.ENTITY_FOX_BITE, 1.0F, 1.0F);
            }

        }

        @Override
        public void c() {
            EntityFox.this.w(false);
            super.c();
        }

        @Override
        public boolean a() {
            return !EntityFox.this.isSitting() && !EntityFox.this.isSleeping() && !EntityFox.this.isCrouching() && !EntityFox.this.eO() && super.a();
        }
    }

    class u extends PathfinderGoal {

        public u() {
            this.a(EnumSet.of(PathfinderGoal.Type.MOVE, PathfinderGoal.Type.LOOK));
        }

        @Override
        public boolean a() {
            if (EntityFox.this.isSleeping()) {
                return false;
            } else {
                EntityLiving entityliving = EntityFox.this.getGoalTarget();

                return entityliving != null && entityliving.isAlive() && EntityFox.bB.test(entityliving) && EntityFox.this.h((Entity) entityliving) > 36.0D && !EntityFox.this.isCrouching() && !EntityFox.this.eX() && !EntityFox.this.jumping;
            }
        }

        @Override
        public void c() {
            EntityFox.this.setSitting(false);
            EntityFox.this.x(false);
        }

        @Override
        public void d() {
            EntityLiving entityliving = EntityFox.this.getGoalTarget();

            if (entityliving != null && EntityFox.a((EntityFox) EntityFox.this, entityliving)) {
                EntityFox.this.w(true);
                EntityFox.this.setCrouching(true);
                EntityFox.this.getNavigation().o();
                EntityFox.this.getControllerLook().a(entityliving, (float) EntityFox.this.ep(), (float) EntityFox.this.eo());
            } else {
                EntityFox.this.w(false);
                EntityFox.this.setCrouching(false);
            }

        }

        @Override
        public void e() {
            EntityLiving entityliving = EntityFox.this.getGoalTarget();

            EntityFox.this.getControllerLook().a(entityliving, (float) EntityFox.this.ep(), (float) EntityFox.this.eo());
            if (EntityFox.this.h((Entity) entityliving) <= 36.0D) {
                EntityFox.this.w(true);
                EntityFox.this.setCrouching(true);
                EntityFox.this.getNavigation().o();
            } else {
                EntityFox.this.getNavigation().a((Entity) entityliving, 1.5D);
            }

        }
    }

    class m extends ControllerMove {

        public m() {
            super(EntityFox.this);
        }

        @Override
        public void a() {
            if (EntityFox.this.ff()) {
                super.a();
            }

        }
    }

    class p extends PathfinderGoal {

        public p() {
            this.a(EnumSet.of(PathfinderGoal.Type.MOVE));
        }

        @Override
        public boolean a() {
            if (!EntityFox.this.getEquipment(EnumItemSlot.MAINHAND).isEmpty()) {
                return false;
            } else if (EntityFox.this.getGoalTarget() == null && EntityFox.this.getLastDamager() == null) {
                if (!EntityFox.this.ff()) {
                    return false;
                } else if (EntityFox.this.getRandom().nextInt(10) != 0) {
                    return false;
                } else {
                    List<EntityItem> list = EntityFox.this.world.a(EntityItem.class, EntityFox.this.getBoundingBox().grow(8.0D, 8.0D, 8.0D), EntityFox.bz);

                    return !list.isEmpty() && EntityFox.this.getEquipment(EnumItemSlot.MAINHAND).isEmpty();
                }
            } else {
                return false;
            }
        }

        @Override
        public void e() {
            List<EntityItem> list = EntityFox.this.world.a(EntityItem.class, EntityFox.this.getBoundingBox().grow(8.0D, 8.0D, 8.0D), EntityFox.bz);
            ItemStack itemstack = EntityFox.this.getEquipment(EnumItemSlot.MAINHAND);

            if (itemstack.isEmpty() && !list.isEmpty()) {
                EntityFox.this.getNavigation().a((Entity) list.get(0), 1.2000000476837158D);
            }

        }

        @Override
        public void c() {
            List<EntityItem> list = EntityFox.this.world.a(EntityItem.class, EntityFox.this.getBoundingBox().grow(8.0D, 8.0D, 8.0D), EntityFox.bz);

            if (!list.isEmpty()) {
                EntityFox.this.getNavigation().a((Entity) list.get(0), 1.2000000476837158D);
            }

        }
    }

    public static enum Type {

        RED(0, "red", new BiomeBase[]{Biomes.TAIGA, Biomes.TAIGA_HILLS, Biomes.TAIGA_MOUNTAINS, Biomes.GIANT_TREE_TAIGA, Biomes.GIANT_SPRUCE_TAIGA, Biomes.GIANT_TREE_TAIGA_HILLS, Biomes.GIANT_SPRUCE_TAIGA_HILLS}), SNOW(1, "snow", new BiomeBase[]{Biomes.SNOWY_TAIGA, Biomes.SNOWY_TAIGA_HILLS, Biomes.SNOWY_TAIGA_MOUNTAINS});

        private static final EntityFox.Type[] c = (EntityFox.Type[]) Arrays.stream(values()).sorted(Comparator.comparingInt(EntityFox.Type::c)).toArray((i) -> {
            return new EntityFox.Type[i];
        });
        private static final Map<String, EntityFox.Type> d = (Map) Arrays.stream(values()).collect(Collectors.toMap(EntityFox.Type::a, (entityfox_type) -> {
            return entityfox_type;
        }));
        private final int e;
        private final String f;
        private final List<BiomeBase> g;

        private Type(int i, String s, BiomeBase... abiomebase) {
            this.e = i;
            this.f = s;
            this.g = Arrays.asList(abiomebase);
        }

        public String a() {
            return this.f;
        }

        public List<BiomeBase> b() {
            return this.g;
        }

        public int c() {
            return this.e;
        }

        public static EntityFox.Type a(String s) {
            return (EntityFox.Type) EntityFox.Type.d.getOrDefault(s, EntityFox.Type.RED);
        }

        public static EntityFox.Type a(int i) {
            if (i < 0 || i > EntityFox.Type.c.length) {
                i = 0;
            }

            return EntityFox.Type.c[i];
        }

        public static EntityFox.Type a(BiomeBase biomebase) {
            return EntityFox.Type.SNOW.b().contains(biomebase) ? EntityFox.Type.SNOW : EntityFox.Type.RED;
        }
    }
}
