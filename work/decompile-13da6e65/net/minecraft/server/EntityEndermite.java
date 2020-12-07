package net.minecraft.server;

import java.util.Random;

public class EntityEndermite extends EntityMonster {

    private int b;
    private boolean c;

    public EntityEndermite(EntityTypes<? extends EntityEndermite> entitytypes, World world) {
        super(entitytypes, world);
        this.f = 3;
    }

    @Override
    protected void initPathfinder() {
        this.goalSelector.a(1, new PathfinderGoalFloat(this));
        this.goalSelector.a(2, new PathfinderGoalMeleeAttack(this, 1.0D, false));
        this.goalSelector.a(3, new PathfinderGoalRandomStrollLand(this, 1.0D));
        this.goalSelector.a(7, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
        this.targetSelector.a(1, (new PathfinderGoalHurtByTarget(this, new Class[0])).a());
        this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget<>(this, EntityHuman.class, true));
    }

    @Override
    protected float b(EntityPose entitypose, EntitySize entitysize) {
        return 0.13F;
    }

    public static AttributeProvider.Builder m() {
        return EntityMonster.eR().a(GenericAttributes.MAX_HEALTH, 8.0D).a(GenericAttributes.MOVEMENT_SPEED, 0.25D).a(GenericAttributes.ATTACK_DAMAGE, 2.0D);
    }

    @Override
    protected boolean playStepSound() {
        return false;
    }

    @Override
    protected SoundEffect getSoundAmbient() {
        return SoundEffects.ENTITY_ENDERMITE_AMBIENT;
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.ENTITY_ENDERMITE_HURT;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.ENTITY_ENDERMITE_DEATH;
    }

    @Override
    protected void b(BlockPosition blockposition, IBlockData iblockdata) {
        this.playSound(SoundEffects.ENTITY_ENDERMITE_STEP, 0.15F, 1.0F);
    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        this.b = nbttagcompound.getInt("Lifetime");
        this.c = nbttagcompound.getBoolean("PlayerSpawned");
    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);
        nbttagcompound.setInt("Lifetime", this.b);
        nbttagcompound.setBoolean("PlayerSpawned", this.c);
    }

    @Override
    public void tick() {
        this.aA = this.yaw;
        super.tick();
    }

    @Override
    public void n(float f) {
        this.yaw = f;
        super.n(f);
    }

    @Override
    public double bb() {
        return 0.1D;
    }

    public boolean isPlayerSpawned() {
        return this.c;
    }

    public void setPlayerSpawned(boolean flag) {
        this.c = flag;
    }

    @Override
    public void movementTick() {
        super.movementTick();
        if (this.world.isClientSide) {
            for (int i = 0; i < 2; ++i) {
                this.world.addParticle(Particles.PORTAL, this.d(0.5D), this.cF(), this.g(0.5D), (this.random.nextDouble() - 0.5D) * 2.0D, -this.random.nextDouble(), (this.random.nextDouble() - 0.5D) * 2.0D);
            }
        } else {
            if (!this.isPersistent()) {
                ++this.b;
            }

            if (this.b >= 2400) {
                this.die();
            }
        }

    }

    public static boolean b(EntityTypes<EntityEndermite> entitytypes, GeneratorAccess generatoraccess, EnumMobSpawn enummobspawn, BlockPosition blockposition, Random random) {
        if (c(entitytypes, generatoraccess, enummobspawn, blockposition, random)) {
            EntityHuman entityhuman = generatoraccess.a((double) blockposition.getX() + 0.5D, (double) blockposition.getY() + 0.5D, (double) blockposition.getZ() + 0.5D, 5.0D, true);

            return entityhuman == null;
        } else {
            return false;
        }
    }

    @Override
    public EnumMonsterType getMonsterType() {
        return EnumMonsterType.ARTHROPOD;
    }
}
