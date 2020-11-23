package net.minecraft.server;

import java.util.EnumSet;
import java.util.Random;
import javax.annotation.Nullable;

public class EntitySlime extends EntityInsentient implements IMonster {

    private static final DataWatcherObject<Integer> bv = DataWatcher.a(EntitySlime.class, DataWatcherRegistry.b);
    public float b;
    public float c;
    public float d;
    private boolean bw;

    public EntitySlime(EntityTypes<? extends EntitySlime> entitytypes, World world) {
        super(entitytypes, world);
        this.moveController = new EntitySlime.ControllerMoveSlime(this);
    }

    @Override
    protected void initPathfinder() {
        this.goalSelector.a(1, new EntitySlime.PathfinderGoalSlimeRandomJump(this));
        this.goalSelector.a(2, new EntitySlime.PathfinderGoalSlimeNearestPlayer(this));
        this.goalSelector.a(3, new EntitySlime.PathfinderGoalSlimeRandomDirection(this));
        this.goalSelector.a(5, new EntitySlime.PathfinderGoalSlimeIdle(this));
        this.targetSelector.a(1, new PathfinderGoalNearestAttackableTarget<>(this, EntityHuman.class, 10, true, false, (entityliving) -> {
            return Math.abs(entityliving.locY() - this.locY()) <= 4.0D;
        }));
        this.targetSelector.a(3, new PathfinderGoalNearestAttackableTarget<>(this, EntityIronGolem.class, true));
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.datawatcher.register(EntitySlime.bv, 1);
    }

    public void setSize(int i, boolean flag) {
        this.datawatcher.set(EntitySlime.bv, i);
        this.ac();
        this.updateSize();
        this.getAttributeInstance(GenericAttributes.MAX_HEALTH).setValue((double) (i * i));
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue((double) (0.2F + 0.1F * (float) i));
        this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue((double) i);
        if (flag) {
            this.setHealth(this.getMaxHealth());
        }

        this.f = i;
    }

    public int getSize() {
        return (Integer) this.datawatcher.get(EntitySlime.bv);
    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);
        nbttagcompound.setInt("Size", this.getSize() - 1);
        nbttagcompound.setBoolean("wasOnGround", this.bw);
    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        int i = nbttagcompound.getInt("Size");

        if (i < 0) {
            i = 0;
        }

        this.setSize(i + 1, false);
        super.loadData(nbttagcompound);
        this.bw = nbttagcompound.getBoolean("wasOnGround");
    }

    public boolean eR() {
        return this.getSize() <= 1;
    }

    protected ParticleParam eJ() {
        return Particles.ITEM_SLIME;
    }

    @Override
    protected boolean L() {
        return this.getSize() > 0;
    }

    @Override
    public void tick() {
        this.c += (this.b - this.c) * 0.5F;
        this.d = this.c;
        super.tick();
        if (this.onGround && !this.bw) {
            int i = this.getSize();

            for (int j = 0; j < i * 8; ++j) {
                float f = this.random.nextFloat() * 6.2831855F;
                float f1 = this.random.nextFloat() * 0.5F + 0.5F;
                float f2 = MathHelper.sin(f) * (float) i * 0.5F * f1;
                float f3 = MathHelper.cos(f) * (float) i * 0.5F * f1;

                this.world.addParticle(this.eJ(), this.locX() + (double) f2, this.locY(), this.locZ() + (double) f3, 0.0D, 0.0D, 0.0D);
            }

            this.playSound(this.getSoundSquish(), this.getSoundVolume(), ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) / 0.8F);
            this.b = -0.5F;
        } else if (!this.onGround && this.bw) {
            this.b = 1.0F;
        }

        this.bw = this.onGround;
        this.eL();
    }

    protected void eL() {
        this.b *= 0.6F;
    }

    protected int eK() {
        return this.random.nextInt(20) + 10;
    }

    @Override
    public void updateSize() {
        double d0 = this.locX();
        double d1 = this.locY();
        double d2 = this.locZ();

        super.updateSize();
        this.setPosition(d0, d1, d2);
    }

    @Override
    public void a(DataWatcherObject<?> datawatcherobject) {
        if (EntitySlime.bv.equals(datawatcherobject)) {
            this.updateSize();
            this.yaw = this.aJ;
            this.aH = this.aJ;
            if (this.isInWater() && this.random.nextInt(20) == 0) {
                this.aI();
            }
        }

        super.a(datawatcherobject);
    }

    @Override
    public EntityTypes<? extends EntitySlime> getEntityType() {
        return super.getEntityType();
    }

    @Override
    public void die() {
        int i = this.getSize();

        if (!this.world.isClientSide && i > 1 && this.dk()) {
            IChatBaseComponent ichatbasecomponent = this.getCustomName();
            boolean flag = this.isNoAI();
            float f = (float) i / 4.0F;
            int j = i / 2;
            int k = 2 + this.random.nextInt(3);

            for (int l = 0; l < k; ++l) {
                float f1 = ((float) (l % 2) - 0.5F) * f;
                float f2 = ((float) (l / 2) - 0.5F) * f;
                EntitySlime entityslime = (EntitySlime) this.getEntityType().a(this.world);

                if (this.isPersistent()) {
                    entityslime.setPersistent();
                }

                entityslime.setCustomName(ichatbasecomponent);
                entityslime.setNoAI(flag);
                entityslime.setInvulnerable(this.isInvulnerable());
                entityslime.setSize(j, true);
                entityslime.setPositionRotation(this.locX() + (double) f1, this.locY() + 0.5D, this.locZ() + (double) f2, this.random.nextFloat() * 360.0F, 0.0F);
                this.world.addEntity(entityslime);
            }
        }

        super.die();
    }

    @Override
    public void collide(Entity entity) {
        super.collide(entity);
        if (entity instanceof EntityIronGolem && this.eM()) {
            this.j((EntityLiving) entity);
        }

    }

    @Override
    public void pickup(EntityHuman entityhuman) {
        if (this.eM()) {
            this.j((EntityLiving) entityhuman);
        }

    }

    protected void j(EntityLiving entityliving) {
        if (this.isAlive()) {
            int i = this.getSize();

            if (this.h((Entity) entityliving) < 0.6D * (double) i * 0.6D * (double) i && this.hasLineOfSight(entityliving) && entityliving.damageEntity(DamageSource.mobAttack(this), this.eN())) {
                this.playSound(SoundEffects.ENTITY_SLIME_ATTACK, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
                this.a((EntityLiving) this, (Entity) entityliving);
            }
        }

    }

    @Override
    protected float b(EntityPose entitypose, EntitySize entitysize) {
        return 0.625F * entitysize.height;
    }

    protected boolean eM() {
        return !this.eR() && this.doAITick();
    }

    protected float eN() {
        return (float) this.b(GenericAttributes.ATTACK_DAMAGE);
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return this.eR() ? SoundEffects.ENTITY_SLIME_HURT_SMALL : SoundEffects.ENTITY_SLIME_HURT;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return this.eR() ? SoundEffects.ENTITY_SLIME_DEATH_SMALL : SoundEffects.ENTITY_SLIME_DEATH;
    }

    protected SoundEffect getSoundSquish() {
        return this.eR() ? SoundEffects.ENTITY_SLIME_SQUISH_SMALL : SoundEffects.ENTITY_SLIME_SQUISH;
    }

    @Override
    protected MinecraftKey getDefaultLootTable() {
        return this.getSize() == 1 ? this.getEntityType().i() : LootTables.a;
    }

    public static boolean c(EntityTypes<EntitySlime> entitytypes, GeneratorAccess generatoraccess, EnumMobSpawn enummobspawn, BlockPosition blockposition, Random random) {
        if (generatoraccess.getDifficulty() != EnumDifficulty.PEACEFUL) {
            BiomeBase biomebase = generatoraccess.getBiome(blockposition);

            if (biomebase == Biomes.SWAMP && blockposition.getY() > 50 && blockposition.getY() < 70 && random.nextFloat() < 0.5F && random.nextFloat() < generatoraccess.aa() && generatoraccess.getLightLevel(blockposition) <= random.nextInt(8)) {
                return a(entitytypes, generatoraccess, enummobspawn, blockposition, random);
            }

            if (!(generatoraccess instanceof GeneratorAccessSeed)) {
                return false;
            }

            ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(blockposition);
            boolean flag = SeededRandom.a(chunkcoordintpair.x, chunkcoordintpair.z, ((GeneratorAccessSeed) generatoraccess).getSeed(), 987234911L).nextInt(10) == 0;

            if (random.nextInt(10) == 0 && flag && blockposition.getY() < 40) {
                return a(entitytypes, generatoraccess, enummobspawn, blockposition, random);
            }
        }

        return false;
    }

    @Override
    protected float getSoundVolume() {
        return 0.4F * (float) this.getSize();
    }

    @Override
    public int eo() {
        return 0;
    }

    protected boolean eS() {
        return this.getSize() > 0;
    }

    @Override
    protected void jump() {
        Vec3D vec3d = this.getMot();

        this.setMot(vec3d.x, (double) this.dI(), vec3d.z);
        this.impulse = true;
    }

    @Nullable
    @Override
    public GroupDataEntity prepare(GeneratorAccess generatoraccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        int i = this.random.nextInt(3);

        if (i < 2 && this.random.nextFloat() < 0.5F * difficultydamagescaler.d()) {
            ++i;
        }

        int j = 1 << i;

        this.setSize(j, true);
        return super.prepare(generatoraccess, difficultydamagescaler, enummobspawn, groupdataentity, nbttagcompound);
    }

    private float m() {
        float f = this.eR() ? 1.4F : 0.8F;

        return ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) * f;
    }

    protected SoundEffect getSoundJump() {
        return this.eR() ? SoundEffects.ENTITY_SLIME_JUMP_SMALL : SoundEffects.ENTITY_SLIME_JUMP;
    }

    @Override
    public EntitySize a(EntityPose entitypose) {
        return super.a(entitypose).a(0.255F * (float) this.getSize());
    }

    static class PathfinderGoalSlimeIdle extends PathfinderGoal {

        private final EntitySlime a;

        public PathfinderGoalSlimeIdle(EntitySlime entityslime) {
            this.a = entityslime;
            this.a(EnumSet.of(PathfinderGoal.Type.JUMP, PathfinderGoal.Type.MOVE));
        }

        @Override
        public boolean a() {
            return !this.a.isPassenger();
        }

        @Override
        public void e() {
            ((EntitySlime.ControllerMoveSlime) this.a.getControllerMove()).a(1.0D);
        }
    }

    static class PathfinderGoalSlimeRandomJump extends PathfinderGoal {

        private final EntitySlime a;

        public PathfinderGoalSlimeRandomJump(EntitySlime entityslime) {
            this.a = entityslime;
            this.a(EnumSet.of(PathfinderGoal.Type.JUMP, PathfinderGoal.Type.MOVE));
            entityslime.getNavigation().d(true);
        }

        @Override
        public boolean a() {
            return (this.a.isInWater() || this.a.aN()) && this.a.getControllerMove() instanceof EntitySlime.ControllerMoveSlime;
        }

        @Override
        public void e() {
            if (this.a.getRandom().nextFloat() < 0.8F) {
                this.a.getControllerJump().jump();
            }

            ((EntitySlime.ControllerMoveSlime) this.a.getControllerMove()).a(1.2D);
        }
    }

    static class PathfinderGoalSlimeRandomDirection extends PathfinderGoal {

        private final EntitySlime a;
        private float b;
        private int c;

        public PathfinderGoalSlimeRandomDirection(EntitySlime entityslime) {
            this.a = entityslime;
            this.a(EnumSet.of(PathfinderGoal.Type.LOOK));
        }

        @Override
        public boolean a() {
            return this.a.getGoalTarget() == null && (this.a.onGround || this.a.isInWater() || this.a.aN() || this.a.hasEffect(MobEffects.LEVITATION)) && this.a.getControllerMove() instanceof EntitySlime.ControllerMoveSlime;
        }

        @Override
        public void e() {
            if (--this.c <= 0) {
                this.c = 40 + this.a.getRandom().nextInt(60);
                this.b = (float) this.a.getRandom().nextInt(360);
            }

            ((EntitySlime.ControllerMoveSlime) this.a.getControllerMove()).a(this.b, false);
        }
    }

    static class PathfinderGoalSlimeNearestPlayer extends PathfinderGoal {

        private final EntitySlime a;
        private int b;

        public PathfinderGoalSlimeNearestPlayer(EntitySlime entityslime) {
            this.a = entityslime;
            this.a(EnumSet.of(PathfinderGoal.Type.LOOK));
        }

        @Override
        public boolean a() {
            EntityLiving entityliving = this.a.getGoalTarget();

            return entityliving == null ? false : (!entityliving.isAlive() ? false : (entityliving instanceof EntityHuman && ((EntityHuman) entityliving).abilities.isInvulnerable ? false : this.a.getControllerMove() instanceof EntitySlime.ControllerMoveSlime));
        }

        @Override
        public void c() {
            this.b = 300;
            super.c();
        }

        @Override
        public boolean b() {
            EntityLiving entityliving = this.a.getGoalTarget();

            return entityliving == null ? false : (!entityliving.isAlive() ? false : (entityliving instanceof EntityHuman && ((EntityHuman) entityliving).abilities.isInvulnerable ? false : --this.b > 0));
        }

        @Override
        public void e() {
            this.a.a((Entity) this.a.getGoalTarget(), 10.0F, 10.0F);
            ((EntitySlime.ControllerMoveSlime) this.a.getControllerMove()).a(this.a.yaw, this.a.eM());
        }
    }

    static class ControllerMoveSlime extends ControllerMove {

        private float i;
        private int j;
        private final EntitySlime k;
        private boolean l;

        public ControllerMoveSlime(EntitySlime entityslime) {
            super(entityslime);
            this.k = entityslime;
            this.i = 180.0F * entityslime.yaw / 3.1415927F;
        }

        public void a(float f, boolean flag) {
            this.i = f;
            this.l = flag;
        }

        public void a(double d0) {
            this.e = d0;
            this.h = ControllerMove.Operation.MOVE_TO;
        }

        @Override
        public void a() {
            this.a.yaw = this.a(this.a.yaw, this.i, 90.0F);
            this.a.aJ = this.a.yaw;
            this.a.aH = this.a.yaw;
            if (this.h != ControllerMove.Operation.MOVE_TO) {
                this.a.q(0.0F);
            } else {
                this.h = ControllerMove.Operation.WAIT;
                if (this.a.isOnGround()) {
                    this.a.n((float) (this.e * this.a.b(GenericAttributes.MOVEMENT_SPEED)));
                    if (this.j-- <= 0) {
                        this.j = this.k.eK();
                        if (this.l) {
                            this.j /= 3;
                        }

                        this.k.getControllerJump().jump();
                        if (this.k.eS()) {
                            this.k.playSound(this.k.getSoundJump(), this.k.getSoundVolume(), this.k.m());
                        }
                    } else {
                        this.k.aY = 0.0F;
                        this.k.ba = 0.0F;
                        this.a.n(0.0F);
                    }
                } else {
                    this.a.n((float) (this.e * this.a.b(GenericAttributes.MOVEMENT_SPEED)));
                }

            }
        }
    }
}
