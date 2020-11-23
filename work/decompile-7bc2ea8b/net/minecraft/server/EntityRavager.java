package net.minecraft.server;

import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;

public class EntityRavager extends EntityRaider {

    private static final Predicate<Entity> b = (entity) -> {
        return entity.isAlive() && !(entity instanceof EntityRavager);
    };
    private int bv;
    private int bw;
    private int bx;

    public EntityRavager(EntityTypes<? extends EntityRavager> entitytypes, World world) {
        super(entitytypes, world);
        this.G = 1.0F;
        this.f = 20;
    }

    @Override
    protected void initPathfinder() {
        super.initPathfinder();
        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(4, new EntityRavager.a());
        this.goalSelector.a(5, new PathfinderGoalRandomStrollLand(this, 0.4D));
        this.goalSelector.a(6, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 6.0F));
        this.goalSelector.a(10, new PathfinderGoalLookAtPlayer(this, EntityInsentient.class, 8.0F));
        this.targetSelector.a(2, (new PathfinderGoalHurtByTarget(this, new Class[]{EntityRaider.class})).a());
        this.targetSelector.a(3, new PathfinderGoalNearestAttackableTarget<>(this, EntityHuman.class, true));
        this.targetSelector.a(4, new PathfinderGoalNearestAttackableTarget<>(this, EntityVillagerAbstract.class, true));
        this.targetSelector.a(4, new PathfinderGoalNearestAttackableTarget<>(this, EntityIronGolem.class, true));
    }

    @Override
    protected void H() {
        boolean flag = !(this.getRidingPassenger() instanceof EntityInsentient) || this.getRidingPassenger().getEntityType().a((Tag) TagsEntity.RADIERS);
        boolean flag1 = !(this.getVehicle() instanceof EntityBoat);

        this.goalSelector.a(PathfinderGoal.Type.MOVE, flag);
        this.goalSelector.a(PathfinderGoal.Type.JUMP, flag && flag1);
        this.goalSelector.a(PathfinderGoal.Type.LOOK, flag);
        this.goalSelector.a(PathfinderGoal.Type.TARGET, flag);
    }

    public static AttributeProvider.Builder m() {
        return EntityMonster.eS().a(GenericAttributes.MAX_HEALTH, 100.0D).a(GenericAttributes.MOVEMENT_SPEED, 0.3D).a(GenericAttributes.KNOCKBACK_RESISTANCE, 0.75D).a(GenericAttributes.ATTACK_DAMAGE, 12.0D).a(GenericAttributes.ATTACK_KNOCKBACK, 1.5D).a(GenericAttributes.FOLLOW_RANGE, 32.0D);
    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);
        nbttagcompound.setInt("AttackTick", this.bv);
        nbttagcompound.setInt("StunTick", this.bw);
        nbttagcompound.setInt("RoarTick", this.bx);
    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        this.bv = nbttagcompound.getInt("AttackTick");
        this.bw = nbttagcompound.getInt("StunTick");
        this.bx = nbttagcompound.getInt("RoarTick");
    }

    @Override
    public SoundEffect eM() {
        return SoundEffects.ENTITY_RAVAGER_CELEBRATE;
    }

    @Override
    protected NavigationAbstract b(World world) {
        return new EntityRavager.b(this, world);
    }

    @Override
    public int ep() {
        return 45;
    }

    @Override
    public double aY() {
        return 2.1D;
    }

    @Override
    public boolean es() {
        return !this.isNoAI() && this.getRidingPassenger() instanceof EntityLiving;
    }

    @Nullable
    @Override
    public Entity getRidingPassenger() {
        return this.getPassengers().isEmpty() ? null : (Entity) this.getPassengers().get(0);
    }

    @Override
    public void movementTick() {
        super.movementTick();
        if (this.isAlive()) {
            if (this.isFrozen()) {
                this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.0D);
            } else {
                double d0 = this.getGoalTarget() != null ? 0.35D : 0.3D;
                double d1 = this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).getBaseValue();

                this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(MathHelper.d(0.1D, d1, d0));
            }

            if (this.positionChanged && this.world.getGameRules().getBoolean(GameRules.MOB_GRIEFING)) {
                boolean flag = false;
                AxisAlignedBB axisalignedbb = this.getBoundingBox().g(0.2D);
                Iterator iterator = BlockPosition.b(MathHelper.floor(axisalignedbb.minX), MathHelper.floor(axisalignedbb.minY), MathHelper.floor(axisalignedbb.minZ), MathHelper.floor(axisalignedbb.maxX), MathHelper.floor(axisalignedbb.maxY), MathHelper.floor(axisalignedbb.maxZ)).iterator();

                while (iterator.hasNext()) {
                    BlockPosition blockposition = (BlockPosition) iterator.next();
                    IBlockData iblockdata = this.world.getType(blockposition);
                    Block block = iblockdata.getBlock();

                    if (block instanceof BlockLeaves) {
                        flag = this.world.a(blockposition, true, this) || flag;
                    }
                }

                if (!flag && this.onGround) {
                    this.jump();
                }
            }

            if (this.bx > 0) {
                --this.bx;
                if (this.bx == 10) {
                    this.eZ();
                }
            }

            if (this.bv > 0) {
                --this.bv;
            }

            if (this.bw > 0) {
                --this.bw;
                this.eY();
                if (this.bw == 0) {
                    this.playSound(SoundEffects.ENTITY_RAVAGER_ROAR, 1.0F, 1.0F);
                    this.bx = 20;
                }
            }

        }
    }

    private void eY() {
        if (this.random.nextInt(6) == 0) {
            double d0 = this.locX() - (double) this.getWidth() * Math.sin((double) (this.aH * 0.017453292F)) + (this.random.nextDouble() * 0.6D - 0.3D);
            double d1 = this.locY() + (double) this.getHeight() - 0.3D;
            double d2 = this.locZ() + (double) this.getWidth() * Math.cos((double) (this.aH * 0.017453292F)) + (this.random.nextDouble() * 0.6D - 0.3D);

            this.world.addParticle(Particles.ENTITY_EFFECT, d0, d1, d2, 0.4980392156862745D, 0.5137254901960784D, 0.5725490196078431D);
        }

    }

    @Override
    protected boolean isFrozen() {
        return super.isFrozen() || this.bv > 0 || this.bw > 0 || this.bx > 0;
    }

    @Override
    public boolean hasLineOfSight(Entity entity) {
        return this.bw <= 0 && this.bx <= 0 ? super.hasLineOfSight(entity) : false;
    }

    @Override
    protected void f(EntityLiving entityliving) {
        if (this.bx == 0) {
            if (this.random.nextDouble() < 0.5D) {
                this.bw = 40;
                this.playSound(SoundEffects.ENTITY_RAVAGER_STUNNED, 1.0F, 1.0F);
                this.world.broadcastEntityEffect(this, (byte) 39);
                entityliving.collide(this);
            } else {
                this.a((Entity) entityliving);
            }

            entityliving.velocityChanged = true;
        }

    }

    private void eZ() {
        if (this.isAlive()) {
            List<Entity> list = this.world.a(EntityLiving.class, this.getBoundingBox().g(4.0D), EntityRavager.b);

            Entity entity;

            for (Iterator iterator = list.iterator(); iterator.hasNext(); this.a(entity)) {
                entity = (Entity) iterator.next();
                if (!(entity instanceof EntityIllagerAbstract)) {
                    entity.damageEntity(DamageSource.mobAttack(this), 6.0F);
                }
            }

            Vec3D vec3d = this.getBoundingBox().f();

            for (int i = 0; i < 40; ++i) {
                double d0 = this.random.nextGaussian() * 0.2D;
                double d1 = this.random.nextGaussian() * 0.2D;
                double d2 = this.random.nextGaussian() * 0.2D;

                this.world.addParticle(Particles.POOF, vec3d.x, vec3d.y, vec3d.z, d0, d1, d2);
            }
        }

    }

    private void a(Entity entity) {
        double d0 = entity.locX() - this.locX();
        double d1 = entity.locZ() - this.locZ();
        double d2 = Math.max(d0 * d0 + d1 * d1, 0.001D);

        entity.h(d0 / d2 * 4.0D, 0.2D, d1 / d2 * 4.0D);
    }

    @Override
    public boolean attackEntity(Entity entity) {
        this.bv = 10;
        this.world.broadcastEntityEffect(this, (byte) 4);
        this.playSound(SoundEffects.ENTITY_RAVAGER_ATTACK, 1.0F, 1.0F);
        return super.attackEntity(entity);
    }

    @Nullable
    @Override
    protected SoundEffect getSoundAmbient() {
        return SoundEffects.ENTITY_RAVAGER_AMBIENT;
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.ENTITY_RAVAGER_HURT;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.ENTITY_RAVAGER_DEATH;
    }

    @Override
    protected void a(BlockPosition blockposition, IBlockData iblockdata) {
        this.playSound(SoundEffects.ENTITY_RAVAGER_STEP, 0.15F, 1.0F);
    }

    @Override
    public boolean a(IWorldReader iworldreader) {
        return !iworldreader.containsLiquid(this.getBoundingBox());
    }

    @Override
    public void a(int i, boolean flag) {}

    @Override
    public boolean eO() {
        return false;
    }

    static class c extends PathfinderNormal {

        private c() {}

        @Override
        protected PathType a(IBlockAccess iblockaccess, boolean flag, boolean flag1, BlockPosition blockposition, PathType pathtype) {
            return pathtype == PathType.LEAVES ? PathType.OPEN : super.a(iblockaccess, flag, flag1, blockposition, pathtype);
        }
    }

    static class b extends Navigation {

        public b(EntityInsentient entityinsentient, World world) {
            super(entityinsentient, world);
        }

        @Override
        protected Pathfinder a(int i) {
            this.o = new EntityRavager.c();
            return new Pathfinder(this.o, i);
        }
    }

    class a extends PathfinderGoalMeleeAttack {

        public a() {
            super(EntityRavager.this, 1.0D, true);
        }

        @Override
        protected double a(EntityLiving entityliving) {
            float f = EntityRavager.this.getWidth() - 0.1F;

            return (double) (f * 2.0F * f * 2.0F + entityliving.getWidth());
        }
    }
}
