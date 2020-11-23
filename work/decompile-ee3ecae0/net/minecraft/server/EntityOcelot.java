package net.minecraft.server;

import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;

public class EntityOcelot extends EntityAnimal {

    private static final RecipeItemStack bw = RecipeItemStack.a(Items.COD, Items.SALMON);
    private static final DataWatcherObject<Boolean> bx = DataWatcher.a(EntityOcelot.class, DataWatcherRegistry.i);
    private EntityOcelot.a<EntityHuman> by;
    private EntityOcelot.b bz;

    public EntityOcelot(EntityTypes<? extends EntityOcelot> entitytypes, World world) {
        super(entitytypes, world);
        this.eq();
    }

    private boolean isTrusting() {
        return (Boolean) this.datawatcher.get(EntityOcelot.bx);
    }

    private void setTrusting(boolean flag) {
        this.datawatcher.set(EntityOcelot.bx, flag);
        this.eq();
    }

    @Override
    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setBoolean("Trusting", this.isTrusting());
    }

    @Override
    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.setTrusting(nbttagcompound.getBoolean("Trusting"));
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.datawatcher.register(EntityOcelot.bx, false);
    }

    @Override
    protected void initPathfinder() {
        this.bz = new EntityOcelot.b(this, 0.6D, EntityOcelot.bw, true);
        this.goalSelector.a(1, new PathfinderGoalFloat(this));
        this.goalSelector.a(3, this.bz);
        this.goalSelector.a(7, new PathfinderGoalLeapAtTarget(this, 0.3F));
        this.goalSelector.a(8, new PathfinderGoalOcelotAttack(this));
        this.goalSelector.a(9, new PathfinderGoalBreed(this, 0.8D));
        this.goalSelector.a(10, new PathfinderGoalRandomStrollLand(this, 0.8D, 1.0000001E-5F));
        this.goalSelector.a(11, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 10.0F));
        this.targetSelector.a(1, new PathfinderGoalNearestAttackableTarget<>(this, EntityChicken.class, false));
        this.targetSelector.a(1, new PathfinderGoalNearestAttackableTarget<>(this, EntityTurtle.class, 10, false, false, EntityTurtle.bw));
    }

    @Override
    public void mobTick() {
        if (this.getControllerMove().b()) {
            double d0 = this.getControllerMove().c();

            if (d0 == 0.6D) {
                this.setPose(EntityPose.CROUCHING);
                this.setSprinting(false);
            } else if (d0 == 1.33D) {
                this.setPose(EntityPose.STANDING);
                this.setSprinting(true);
            } else {
                this.setPose(EntityPose.STANDING);
                this.setSprinting(false);
            }
        } else {
            this.setPose(EntityPose.STANDING);
            this.setSprinting(false);
        }

    }

    @Override
    public boolean isTypeNotPersistent(double d0) {
        return !this.isTrusting() && this.ticksLived > 2400;
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.MAX_HEALTH).setValue(10.0D);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.30000001192092896D);
        this.getAttributeMap().b(GenericAttributes.ATTACK_DAMAGE).setValue(3.0D);
    }

    @Override
    public boolean b(float f, float f1) {
        return false;
    }

    @Nullable
    @Override
    protected SoundEffect getSoundAmbient() {
        return SoundEffects.ENTITY_OCELOT_AMBIENT;
    }

    @Override
    public int A() {
        return 900;
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.ENTITY_OCELOT_HURT;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.ENTITY_OCELOT_DEATH;
    }

    private float es() {
        return (float) this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).getValue();
    }

    @Override
    public boolean B(Entity entity) {
        return entity.damageEntity(DamageSource.mobAttack(this), this.es());
    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        return this.isInvulnerable(damagesource) ? false : super.damageEntity(damagesource, f);
    }

    @Override
    public boolean a(EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);

        if ((this.bz == null || this.bz.h()) && !this.isTrusting() && this.i(itemstack) && entityhuman.h((Entity) this) < 9.0D) {
            this.a(entityhuman, itemstack);
            if (!this.world.isClientSide) {
                if (this.random.nextInt(3) == 0) {
                    this.setTrusting(true);
                    this.s(true);
                    this.world.broadcastEntityEffect(this, (byte) 41);
                } else {
                    this.s(false);
                    this.world.broadcastEntityEffect(this, (byte) 40);
                }
            }

            return true;
        } else {
            return super.a(entityhuman, enumhand);
        }
    }

    private void s(boolean flag) {
        ParticleType particletype = Particles.HEART;

        if (!flag) {
            particletype = Particles.SMOKE;
        }

        for (int i = 0; i < 7; ++i) {
            double d0 = this.random.nextGaussian() * 0.02D;
            double d1 = this.random.nextGaussian() * 0.02D;
            double d2 = this.random.nextGaussian() * 0.02D;

            this.world.addParticle(particletype, this.d(1.0D), this.cv() + 0.5D, this.g(1.0D), d0, d1, d2);
        }

    }

    protected void eq() {
        if (this.by == null) {
            this.by = new EntityOcelot.a<>(this, EntityHuman.class, 16.0F, 0.8D, 1.33D);
        }

        this.goalSelector.a((PathfinderGoal) this.by);
        if (!this.isTrusting()) {
            this.goalSelector.a(4, this.by);
        }

    }

    @Override
    public EntityOcelot createChild(EntityAgeable entityageable) {
        return (EntityOcelot) EntityTypes.OCELOT.a(this.world);
    }

    @Override
    public boolean i(ItemStack itemstack) {
        return EntityOcelot.bw.test(itemstack);
    }

    public static boolean c(EntityTypes<EntityOcelot> entitytypes, GeneratorAccess generatoraccess, EnumMobSpawn enummobspawn, BlockPosition blockposition, Random random) {
        return random.nextInt(3) != 0;
    }

    @Override
    public boolean a(IWorldReader iworldreader) {
        if (iworldreader.i(this) && !iworldreader.containsLiquid(this.getBoundingBox())) {
            BlockPosition blockposition = new BlockPosition(this);

            if (blockposition.getY() < iworldreader.getSeaLevel()) {
                return false;
            }

            IBlockData iblockdata = iworldreader.getType(blockposition.down());
            Block block = iblockdata.getBlock();

            if (block == Blocks.GRASS_BLOCK || iblockdata.a(TagsBlock.LEAVES)) {
                return true;
            }
        }

        return false;
    }

    @Nullable
    @Override
    public GroupDataEntity prepare(GeneratorAccess generatoraccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        if (groupdataentity == null) {
            groupdataentity = new EntityAgeable.a();
            ((EntityAgeable.a) groupdataentity).a(1.0F);
        }

        return super.prepare(generatoraccess, difficultydamagescaler, enummobspawn, (GroupDataEntity) groupdataentity, nbttagcompound);
    }

    static class b extends PathfinderGoalTempt {

        private final EntityOcelot c;

        public b(EntityOcelot entityocelot, double d0, RecipeItemStack recipeitemstack, boolean flag) {
            super(entityocelot, d0, recipeitemstack, flag);
            this.c = entityocelot;
        }

        @Override
        protected boolean g() {
            return super.g() && !this.c.isTrusting();
        }
    }

    static class a<T extends EntityLiving> extends PathfinderGoalAvoidTarget<T> {

        private final EntityOcelot i;

        public a(EntityOcelot entityocelot, Class<T> oclass, float f, double d0, double d1) {
            Predicate predicate = IEntitySelector.e;

            super(entityocelot, oclass, f, d0, d1, predicate::test);
            this.i = entityocelot;
        }

        @Override
        public boolean a() {
            return !this.i.isTrusting() && super.a();
        }

        @Override
        public boolean b() {
            return !this.i.isTrusting() && super.b();
        }
    }
}
