package net.minecraft.server;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;

public class EntityEnderman extends EntityMonster implements IEntityAngerable {

    private static final UUID b = UUID.fromString("020E0DFB-87AE-4653-9556-831010E291A0");
    private static final AttributeModifier c = new AttributeModifier(EntityEnderman.b, "Attacking speed boost", 0.15000000596046448D, AttributeModifier.Operation.ADDITION);
    private static final DataWatcherObject<Optional<IBlockData>> d = DataWatcher.a(EntityEnderman.class, DataWatcherRegistry.h);
    private static final DataWatcherObject<Boolean> bv = DataWatcher.a(EntityEnderman.class, DataWatcherRegistry.i);
    private static final DataWatcherObject<Boolean> bw = DataWatcher.a(EntityEnderman.class, DataWatcherRegistry.i);
    private static final Predicate<EntityLiving> bx = (entityliving) -> {
        return entityliving instanceof EntityEndermite && ((EntityEndermite) entityliving).isPlayerSpawned();
    };
    private int by = Integer.MIN_VALUE;
    private int bz;
    private static final IntRange bA = TimeRange.a(20, 39);
    private int bB;
    private UUID bC;

    public EntityEnderman(EntityTypes<? extends EntityEnderman> entitytypes, World world) {
        super(entitytypes, world);
        this.G = 1.0F;
        this.a(PathType.WATER, -1.0F);
    }

    @Override
    protected void initPathfinder() {
        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(1, new EntityEnderman.a(this));
        this.goalSelector.a(2, new PathfinderGoalMeleeAttack(this, 1.0D, false));
        this.goalSelector.a(7, new PathfinderGoalRandomStrollLand(this, 1.0D, 0.0F));
        this.goalSelector.a(8, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
        this.goalSelector.a(10, new EntityEnderman.PathfinderGoalEndermanPlaceBlock(this));
        this.goalSelector.a(11, new EntityEnderman.PathfinderGoalEndermanPickupBlock(this));
        this.targetSelector.a(1, new PathfinderGoalNearestAttackableTarget<>(this, EntityHuman.class, 10, true, false, this::b));
        this.targetSelector.a(2, new EntityEnderman.PathfinderGoalPlayerWhoLookedAtTarget(this));
        this.targetSelector.a(3, new PathfinderGoalHurtByTarget(this, new Class[0]));
        this.targetSelector.a(4, new PathfinderGoalNearestAttackableTarget<>(this, EntityEndermite.class, 10, true, false, EntityEnderman.bx));
        this.targetSelector.a(5, new PathfinderGoalUniversalAngerReset<>(this, false));
    }

    public static AttributeProvider.Builder m() {
        return EntityMonster.eS().a(GenericAttributes.MAX_HEALTH, 40.0D).a(GenericAttributes.MOVEMENT_SPEED, 0.30000001192092896D).a(GenericAttributes.ATTACK_DAMAGE, 7.0D).a(GenericAttributes.FOLLOW_RANGE, 64.0D);
    }

    @Override
    public void setGoalTarget(@Nullable EntityLiving entityliving) {
        super.setGoalTarget(entityliving);
        AttributeModifiable attributemodifiable = this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED);

        if (entityliving == null) {
            this.bz = 0;
            this.datawatcher.set(EntityEnderman.bv, false);
            this.datawatcher.set(EntityEnderman.bw, false);
            attributemodifiable.removeModifier(EntityEnderman.c);
        } else {
            this.bz = this.ticksLived;
            this.datawatcher.set(EntityEnderman.bv, true);
            if (!attributemodifiable.a(EntityEnderman.c)) {
                attributemodifiable.b(EntityEnderman.c);
            }
        }

    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.datawatcher.register(EntityEnderman.d, Optional.empty());
        this.datawatcher.register(EntityEnderman.bv, false);
        this.datawatcher.register(EntityEnderman.bw, false);
    }

    @Override
    public void anger() {
        this.setAnger(EntityEnderman.bA.a(this.random));
    }

    @Override
    public void setAnger(int i) {
        this.bB = i;
    }

    @Override
    public int getAnger() {
        return this.bB;
    }

    @Override
    public void setAngerTarget(@Nullable UUID uuid) {
        this.bC = uuid;
    }

    @Override
    public UUID getAngerTarget() {
        return this.bC;
    }

    public void eL() {
        if (this.ticksLived >= this.by + 400) {
            this.by = this.ticksLived;
            if (!this.isSilent()) {
                this.world.a(this.locX(), this.getHeadY(), this.locZ(), SoundEffects.ENTITY_ENDERMAN_STARE, this.getSoundCategory(), 2.5F, 1.0F, false);
            }
        }

    }

    @Override
    public void a(DataWatcherObject<?> datawatcherobject) {
        if (EntityEnderman.bv.equals(datawatcherobject) && this.eP() && this.world.isClientSide) {
            this.eL();
        }

        super.a(datawatcherobject);
    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);
        IBlockData iblockdata = this.getCarried();

        if (iblockdata != null) {
            nbttagcompound.set("carriedBlockState", GameProfileSerializer.a(iblockdata));
        }

        this.c(nbttagcompound);
    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        IBlockData iblockdata = null;

        if (nbttagcompound.hasKeyOfType("carriedBlockState", 10)) {
            iblockdata = GameProfileSerializer.c(nbttagcompound.getCompound("carriedBlockState"));
            if (iblockdata.isAir()) {
                iblockdata = null;
            }
        }

        this.setCarried(iblockdata);
        this.a((WorldServer) this.world, nbttagcompound);
    }

    private boolean g(EntityHuman entityhuman) {
        ItemStack itemstack = (ItemStack) entityhuman.inventory.armor.get(3);

        if (itemstack.getItem() == Blocks.CARVED_PUMPKIN.getItem()) {
            return false;
        } else {
            Vec3D vec3d = entityhuman.f(1.0F).d();
            Vec3D vec3d1 = new Vec3D(this.locX() - entityhuman.locX(), this.getHeadY() - entityhuman.getHeadY(), this.locZ() - entityhuman.locZ());
            double d0 = vec3d1.f();

            vec3d1 = vec3d1.d();
            double d1 = vec3d.b(vec3d1);

            return d1 > 1.0D - 0.025D / d0 ? entityhuman.hasLineOfSight(this) : false;
        }
    }

    @Override
    protected float b(EntityPose entitypose, EntitySize entitysize) {
        return 2.55F;
    }

    @Override
    public void movementTick() {
        if (this.world.isClientSide) {
            for (int i = 0; i < 2; ++i) {
                this.world.addParticle(Particles.PORTAL, this.d(0.5D), this.cE() - 0.25D, this.g(0.5D), (this.random.nextDouble() - 0.5D) * 2.0D, -this.random.nextDouble(), (this.random.nextDouble() - 0.5D) * 2.0D);
            }
        }

        this.jumping = false;
        if (!this.world.isClientSide) {
            this.a((WorldServer) this.world, true);
        }

        super.movementTick();
    }

    @Override
    public boolean dN() {
        return true;
    }

    @Override
    protected void mobTick() {
        if (this.world.isDay() && this.ticksLived >= this.bz + 600) {
            float f = this.aO();

            if (f > 0.5F && this.world.f(this.getChunkCoordinates()) && this.random.nextFloat() * 30.0F < (f - 0.4F) * 2.0F) {
                this.setGoalTarget((EntityLiving) null);
                this.eM();
            }
        }

        super.mobTick();
    }

    protected boolean eM() {
        if (!this.world.s_() && this.isAlive()) {
            double d0 = this.locX() + (this.random.nextDouble() - 0.5D) * 64.0D;
            double d1 = this.locY() + (double) (this.random.nextInt(64) - 32);
            double d2 = this.locZ() + (this.random.nextDouble() - 0.5D) * 64.0D;

            return this.o(d0, d1, d2);
        } else {
            return false;
        }
    }

    private boolean a(Entity entity) {
        Vec3D vec3d = new Vec3D(this.locX() - entity.locX(), this.e(0.5D) - entity.getHeadY(), this.locZ() - entity.locZ());

        vec3d = vec3d.d();
        double d0 = 16.0D;
        double d1 = this.locX() + (this.random.nextDouble() - 0.5D) * 8.0D - vec3d.x * 16.0D;
        double d2 = this.locY() + (double) (this.random.nextInt(16) - 8) - vec3d.y * 16.0D;
        double d3 = this.locZ() + (this.random.nextDouble() - 0.5D) * 8.0D - vec3d.z * 16.0D;

        return this.o(d1, d2, d3);
    }

    private boolean o(double d0, double d1, double d2) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition(d0, d1, d2);

        while (blockposition_mutableblockposition.getY() > 0 && !this.world.getType(blockposition_mutableblockposition).getMaterial().isSolid()) {
            blockposition_mutableblockposition.c(EnumDirection.DOWN);
        }

        IBlockData iblockdata = this.world.getType(blockposition_mutableblockposition);
        boolean flag = iblockdata.getMaterial().isSolid();
        boolean flag1 = iblockdata.getFluid().a((Tag) TagsFluid.WATER);

        if (flag && !flag1) {
            boolean flag2 = this.a(d0, d1, d2, true);

            if (flag2 && !this.isSilent()) {
                this.world.playSound((EntityHuman) null, this.lastX, this.lastY, this.lastZ, SoundEffects.ENTITY_ENDERMAN_TELEPORT, this.getSoundCategory(), 1.0F, 1.0F);
                this.playSound(SoundEffects.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
            }

            return flag2;
        } else {
            return false;
        }
    }

    @Override
    protected SoundEffect getSoundAmbient() {
        return this.eO() ? SoundEffects.ENTITY_ENDERMAN_SCREAM : SoundEffects.ENTITY_ENDERMAN_AMBIENT;
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.ENTITY_ENDERMAN_HURT;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.ENTITY_ENDERMAN_DEATH;
    }

    @Override
    protected void dropDeathLoot(DamageSource damagesource, int i, boolean flag) {
        super.dropDeathLoot(damagesource, i, flag);
        IBlockData iblockdata = this.getCarried();

        if (iblockdata != null) {
            this.a((IMaterial) iblockdata.getBlock());
        }

    }

    public void setCarried(@Nullable IBlockData iblockdata) {
        this.datawatcher.set(EntityEnderman.d, Optional.ofNullable(iblockdata));
    }

    @Nullable
    public IBlockData getCarried() {
        return (IBlockData) ((Optional) this.datawatcher.get(EntityEnderman.d)).orElse((Object) null);
    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        if (this.isInvulnerable(damagesource)) {
            return false;
        } else if (damagesource instanceof EntityDamageSourceIndirect) {
            for (int i = 0; i < 64; ++i) {
                if (this.eM()) {
                    return true;
                }
            }

            return false;
        } else {
            boolean flag = super.damageEntity(damagesource, f);

            if (!this.world.s_() && this.random.nextInt(10) != 0) {
                this.eM();
            }

            return flag;
        }
    }

    public boolean eO() {
        return (Boolean) this.datawatcher.get(EntityEnderman.bv);
    }

    public boolean eP() {
        return (Boolean) this.datawatcher.get(EntityEnderman.bw);
    }

    public void eQ() {
        this.datawatcher.set(EntityEnderman.bw, true);
    }

    @Override
    public boolean isSpecialPersistence() {
        return super.isSpecialPersistence() || this.getCarried() != null;
    }

    static class PathfinderGoalEndermanPickupBlock extends PathfinderGoal {

        private final EntityEnderman enderman;

        public PathfinderGoalEndermanPickupBlock(EntityEnderman entityenderman) {
            this.enderman = entityenderman;
        }

        @Override
        public boolean a() {
            return this.enderman.getCarried() != null ? false : (!this.enderman.world.getGameRules().getBoolean(GameRules.MOB_GRIEFING) ? false : this.enderman.getRandom().nextInt(20) == 0);
        }

        @Override
        public void e() {
            Random random = this.enderman.getRandom();
            World world = this.enderman.world;
            int i = MathHelper.floor(this.enderman.locX() - 2.0D + random.nextDouble() * 4.0D);
            int j = MathHelper.floor(this.enderman.locY() + random.nextDouble() * 3.0D);
            int k = MathHelper.floor(this.enderman.locZ() - 2.0D + random.nextDouble() * 4.0D);
            BlockPosition blockposition = new BlockPosition(i, j, k);
            IBlockData iblockdata = world.getType(blockposition);
            Block block = iblockdata.getBlock();
            Vec3D vec3d = new Vec3D((double) MathHelper.floor(this.enderman.locX()) + 0.5D, (double) j + 0.5D, (double) MathHelper.floor(this.enderman.locZ()) + 0.5D);
            Vec3D vec3d1 = new Vec3D((double) i + 0.5D, (double) j + 0.5D, (double) k + 0.5D);
            MovingObjectPositionBlock movingobjectpositionblock = world.rayTrace(new RayTrace(vec3d, vec3d1, RayTrace.BlockCollisionOption.OUTLINE, RayTrace.FluidCollisionOption.NONE, this.enderman));
            boolean flag = movingobjectpositionblock.getBlockPosition().equals(blockposition);

            if (block.a((Tag) TagsBlock.ENDERMAN_HOLDABLE) && flag) {
                this.enderman.setCarried(iblockdata);
                world.a(blockposition, false);
            }

        }
    }

    static class PathfinderGoalEndermanPlaceBlock extends PathfinderGoal {

        private final EntityEnderman a;

        public PathfinderGoalEndermanPlaceBlock(EntityEnderman entityenderman) {
            this.a = entityenderman;
        }

        @Override
        public boolean a() {
            return this.a.getCarried() == null ? false : (!this.a.world.getGameRules().getBoolean(GameRules.MOB_GRIEFING) ? false : this.a.getRandom().nextInt(2000) == 0);
        }

        @Override
        public void e() {
            Random random = this.a.getRandom();
            World world = this.a.world;
            int i = MathHelper.floor(this.a.locX() - 1.0D + random.nextDouble() * 2.0D);
            int j = MathHelper.floor(this.a.locY() + random.nextDouble() * 2.0D);
            int k = MathHelper.floor(this.a.locZ() - 1.0D + random.nextDouble() * 2.0D);
            BlockPosition blockposition = new BlockPosition(i, j, k);
            IBlockData iblockdata = world.getType(blockposition);
            BlockPosition blockposition1 = blockposition.down();
            IBlockData iblockdata1 = world.getType(blockposition1);
            IBlockData iblockdata2 = this.a.getCarried();

            if (iblockdata2 != null && this.a(world, blockposition, iblockdata2, iblockdata, iblockdata1, blockposition1)) {
                world.setTypeAndData(blockposition, iblockdata2, 3);
                this.a.setCarried((IBlockData) null);
            }

        }

        private boolean a(IWorldReader iworldreader, BlockPosition blockposition, IBlockData iblockdata, IBlockData iblockdata1, IBlockData iblockdata2, BlockPosition blockposition1) {
            return iblockdata1.isAir() && !iblockdata2.isAir() && iblockdata2.r(iworldreader, blockposition1) && iblockdata.canPlace(iworldreader, blockposition);
        }
    }

    static class a extends PathfinderGoal {

        private final EntityEnderman a;
        private EntityLiving b;

        public a(EntityEnderman entityenderman) {
            this.a = entityenderman;
            this.a(EnumSet.of(PathfinderGoal.Type.JUMP, PathfinderGoal.Type.MOVE));
        }

        @Override
        public boolean a() {
            this.b = this.a.getGoalTarget();
            if (!(this.b instanceof EntityHuman)) {
                return false;
            } else {
                double d0 = this.b.h((Entity) this.a);

                return d0 > 256.0D ? false : this.a.g((EntityHuman) this.b);
            }
        }

        @Override
        public void c() {
            this.a.getNavigation().o();
        }

        @Override
        public void e() {
            this.a.getControllerLook().a(this.b.locX(), this.b.getHeadY(), this.b.locZ());
        }
    }

    static class PathfinderGoalPlayerWhoLookedAtTarget extends PathfinderGoalNearestAttackableTarget<EntityHuman> {

        private final EntityEnderman i;
        private EntityHuman j;
        private int k;
        private int l;
        private final PathfinderTargetCondition m;
        private final PathfinderTargetCondition n = (new PathfinderTargetCondition()).c();

        public PathfinderGoalPlayerWhoLookedAtTarget(EntityEnderman entityenderman) {
            super(entityenderman, EntityHuman.class, false);
            this.i = entityenderman;
            this.m = (new PathfinderTargetCondition()).a(this.k()).a((entityliving) -> {
                return entityenderman.g((EntityHuman) entityliving);
            });
        }

        @Override
        public boolean a() {
            this.j = this.i.world.a(this.m, (EntityLiving) this.i);
            return this.j != null;
        }

        @Override
        public void c() {
            this.k = 5;
            this.l = 0;
            this.i.eQ();
        }

        @Override
        public void d() {
            this.j = null;
            super.d();
        }

        @Override
        public boolean b() {
            if (this.j != null) {
                if (!this.i.g(this.j)) {
                    return false;
                } else {
                    this.i.a((Entity) this.j, 10.0F, 10.0F);
                    return true;
                }
            } else {
                return this.c != null && this.n.a(this.i, this.c) ? true : super.b();
            }
        }

        @Override
        public void e() {
            if (this.i.getGoalTarget() == null) {
                super.a((EntityLiving) null);
            }

            if (this.j != null) {
                if (--this.k <= 0) {
                    this.c = this.j;
                    this.j = null;
                    super.c();
                }
            } else {
                if (this.c != null && !this.i.isPassenger()) {
                    if (this.i.g((EntityHuman) this.c)) {
                        if (this.c.h((Entity) this.i) < 16.0D) {
                            this.i.eM();
                        }

                        this.l = 0;
                    } else if (this.c.h((Entity) this.i) > 256.0D && this.l++ >= 30 && this.i.a((Entity) this.c)) {
                        this.l = 0;
                    }
                }

                super.e();
            }

        }
    }
}
