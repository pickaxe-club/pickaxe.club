package net.minecraft.server;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;

public class EntityPolarBear extends EntityAnimal {

    private static final DataWatcherObject<Boolean> bw = DataWatcher.a(EntityPolarBear.class, DataWatcherRegistry.i);
    private float bx;
    private float by;
    private int bz;

    public EntityPolarBear(EntityTypes<? extends EntityPolarBear> entitytypes, World world) {
        super(entitytypes, world);
    }

    @Override
    public EntityAgeable createChild(EntityAgeable entityageable) {
        return (EntityAgeable) EntityTypes.POLAR_BEAR.a(this.world);
    }

    @Override
    public boolean i(ItemStack itemstack) {
        return false;
    }

    @Override
    protected void initPathfinder() {
        super.initPathfinder();
        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(1, new EntityPolarBear.c());
        this.goalSelector.a(1, new EntityPolarBear.d());
        this.goalSelector.a(4, new PathfinderGoalFollowParent(this, 1.25D));
        this.goalSelector.a(5, new PathfinderGoalRandomStroll(this, 1.0D));
        this.goalSelector.a(6, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 6.0F));
        this.goalSelector.a(7, new PathfinderGoalRandomLookaround(this));
        this.targetSelector.a(1, new EntityPolarBear.b());
        this.targetSelector.a(2, new EntityPolarBear.a());
        this.targetSelector.a(3, new PathfinderGoalNearestAttackableTarget<>(this, EntityFox.class, 10, true, true, (Predicate) null));
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.MAX_HEALTH).setValue(30.0D);
        this.getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(20.0D);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.25D);
        this.getAttributeMap().b(GenericAttributes.ATTACK_DAMAGE);
        this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(6.0D);
    }

    public static boolean c(EntityTypes<EntityPolarBear> entitytypes, GeneratorAccess generatoraccess, EnumMobSpawn enummobspawn, BlockPosition blockposition, Random random) {
        BiomeBase biomebase = generatoraccess.getBiome(blockposition);

        return biomebase != Biomes.FROZEN_OCEAN && biomebase != Biomes.DEEP_FROZEN_OCEAN ? b(entitytypes, generatoraccess, enummobspawn, blockposition, random) : generatoraccess.getLightLevel(blockposition, 0) > 8 && generatoraccess.getType(blockposition.down()).getBlock() == Blocks.ICE;
    }

    @Override
    protected SoundEffect getSoundAmbient() {
        return this.isBaby() ? SoundEffects.ENTITY_POLAR_BEAR_AMBIENT_BABY : SoundEffects.ENTITY_POLAR_BEAR_AMBIENT;
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.ENTITY_POLAR_BEAR_HURT;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.ENTITY_POLAR_BEAR_DEATH;
    }

    @Override
    protected void a(BlockPosition blockposition, IBlockData iblockdata) {
        this.a(SoundEffects.ENTITY_POLAR_BEAR_STEP, 0.15F, 1.0F);
    }

    protected void eq() {
        if (this.bz <= 0) {
            this.a(SoundEffects.ENTITY_POLAR_BEAR_WARNING, 1.0F, this.dn());
            this.bz = 40;
        }

    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.datawatcher.register(EntityPolarBear.bw, false);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.world.isClientSide) {
            if (this.by != this.bx) {
                this.updateSize();
            }

            this.bx = this.by;
            if (this.er()) {
                this.by = MathHelper.a(this.by + 1.0F, 0.0F, 6.0F);
            } else {
                this.by = MathHelper.a(this.by - 1.0F, 0.0F, 6.0F);
            }
        }

        if (this.bz > 0) {
            --this.bz;
        }

    }

    @Override
    public EntitySize a(EntityPose entitypose) {
        if (this.by > 0.0F) {
            float f = this.by / 6.0F;
            float f1 = 1.0F + f;

            return super.a(entitypose).a(1.0F, f1);
        } else {
            return super.a(entitypose);
        }
    }

    @Override
    public boolean B(Entity entity) {
        boolean flag = entity.damageEntity(DamageSource.mobAttack(this), (float) ((int) this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).getValue()));

        if (flag) {
            this.a((EntityLiving) this, entity);
        }

        return flag;
    }

    public boolean er() {
        return (Boolean) this.datawatcher.get(EntityPolarBear.bw);
    }

    public void r(boolean flag) {
        this.datawatcher.set(EntityPolarBear.bw, flag);
    }

    @Override
    protected float ds() {
        return 0.98F;
    }

    @Override
    public GroupDataEntity prepare(GeneratorAccess generatoraccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        if (groupdataentity == null) {
            groupdataentity = new EntityAgeable.a();
            ((EntityAgeable.a) groupdataentity).a(1.0F);
        }

        return super.prepare(generatoraccess, difficultydamagescaler, enummobspawn, (GroupDataEntity) groupdataentity, nbttagcompound);
    }

    class d extends PathfinderGoalPanic {

        public d() {
            super(EntityPolarBear.this, 2.0D);
        }

        @Override
        public boolean a() {
            return !EntityPolarBear.this.isBaby() && !EntityPolarBear.this.isBurning() ? false : super.a();
        }
    }

    class c extends PathfinderGoalMeleeAttack {

        public c() {
            super(EntityPolarBear.this, 1.25D, true);
        }

        @Override
        protected void a(EntityLiving entityliving, double d0) {
            double d1 = this.a(entityliving);

            if (d0 <= d1 && this.b <= 0) {
                this.b = 20;
                this.a.B(entityliving);
                EntityPolarBear.this.r(false);
            } else if (d0 <= d1 * 2.0D) {
                if (this.b <= 0) {
                    EntityPolarBear.this.r(false);
                    this.b = 20;
                }

                if (this.b <= 10) {
                    EntityPolarBear.this.r(true);
                    EntityPolarBear.this.eq();
                }
            } else {
                this.b = 20;
                EntityPolarBear.this.r(false);
            }

        }

        @Override
        public void d() {
            EntityPolarBear.this.r(false);
            super.d();
        }

        @Override
        protected double a(EntityLiving entityliving) {
            return (double) (4.0F + entityliving.getWidth());
        }
    }

    class a extends PathfinderGoalNearestAttackableTarget<EntityHuman> {

        public a() {
            super(EntityPolarBear.this, EntityHuman.class, 20, true, true, (Predicate) null);
        }

        @Override
        public boolean a() {
            if (EntityPolarBear.this.isBaby()) {
                return false;
            } else {
                if (super.a()) {
                    List<EntityPolarBear> list = EntityPolarBear.this.world.a(EntityPolarBear.class, EntityPolarBear.this.getBoundingBox().grow(8.0D, 4.0D, 8.0D));
                    Iterator iterator = list.iterator();

                    while (iterator.hasNext()) {
                        EntityPolarBear entitypolarbear = (EntityPolarBear) iterator.next();

                        if (entitypolarbear.isBaby()) {
                            return true;
                        }
                    }
                }

                return false;
            }
        }

        @Override
        protected double k() {
            return super.k() * 0.5D;
        }
    }

    class b extends PathfinderGoalHurtByTarget {

        public b() {
            super(EntityPolarBear.this);
        }

        @Override
        public void c() {
            super.c();
            if (EntityPolarBear.this.isBaby()) {
                this.g();
                this.d();
            }

        }

        @Override
        protected void a(EntityInsentient entityinsentient, EntityLiving entityliving) {
            if (entityinsentient instanceof EntityPolarBear && !entityinsentient.isBaby()) {
                super.a(entityinsentient, entityliving);
            }

        }
    }
}
