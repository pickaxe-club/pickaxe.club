package net.minecraft.server;

import java.util.List;
import javax.annotation.Nullable;

public class EntityBoat extends Entity {

    private static final DataWatcherObject<Integer> b = DataWatcher.a(EntityBoat.class, DataWatcherRegistry.b);
    private static final DataWatcherObject<Integer> c = DataWatcher.a(EntityBoat.class, DataWatcherRegistry.b);
    private static final DataWatcherObject<Float> d = DataWatcher.a(EntityBoat.class, DataWatcherRegistry.c);
    private static final DataWatcherObject<Integer> e = DataWatcher.a(EntityBoat.class, DataWatcherRegistry.b);
    private static final DataWatcherObject<Boolean> f = DataWatcher.a(EntityBoat.class, DataWatcherRegistry.i);
    private static final DataWatcherObject<Boolean> g = DataWatcher.a(EntityBoat.class, DataWatcherRegistry.i);
    private static final DataWatcherObject<Integer> ao = DataWatcher.a(EntityBoat.class, DataWatcherRegistry.b);
    private final float[] ap;
    private float aq;
    private float ar;
    private float as;
    private int at;
    private double au;
    private double av;
    private double aw;
    private double ax;
    private double ay;
    private boolean az;
    private boolean aA;
    private boolean aB;
    private boolean aC;
    private double aD;
    private float aE;
    private EntityBoat.EnumStatus aF;
    private EntityBoat.EnumStatus aG;
    private double aH;
    private boolean aI;
    private boolean aJ;
    private float aK;
    private float aL;
    private float aM;

    public EntityBoat(EntityTypes<? extends EntityBoat> entitytypes, World world) {
        super(entitytypes, world);
        this.ap = new float[2];
        this.i = true;
    }

    public EntityBoat(World world, double d0, double d1, double d2) {
        this(EntityTypes.BOAT, world);
        this.setPosition(d0, d1, d2);
        this.setMot(Vec3D.a);
        this.lastX = d0;
        this.lastY = d1;
        this.lastZ = d2;
    }

    @Override
    protected boolean playStepSound() {
        return false;
    }

    @Override
    protected void initDatawatcher() {
        this.datawatcher.register(EntityBoat.b, 0);
        this.datawatcher.register(EntityBoat.c, 1);
        this.datawatcher.register(EntityBoat.d, 0.0F);
        this.datawatcher.register(EntityBoat.e, EntityBoat.EnumBoatType.OAK.ordinal());
        this.datawatcher.register(EntityBoat.f, false);
        this.datawatcher.register(EntityBoat.g, false);
        this.datawatcher.register(EntityBoat.ao, 0);
    }

    @Nullable
    @Override
    public AxisAlignedBB j(Entity entity) {
        return entity.isCollidable() ? entity.getBoundingBox() : null;
    }

    @Nullable
    @Override
    public AxisAlignedBB au() {
        return this.getBoundingBox();
    }

    @Override
    public boolean isCollidable() {
        return true;
    }

    @Override
    public double aS() {
        return -0.1D;
    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        if (this.isInvulnerable(damagesource)) {
            return false;
        } else if (!this.world.isClientSide && !this.dead) {
            if (damagesource instanceof EntityDamageSourceIndirect && damagesource.getEntity() != null && this.w(damagesource.getEntity())) {
                return false;
            } else {
                this.c(-this.o());
                this.b(10);
                this.setDamage(this.getDamage() + f * 10.0F);
                this.velocityChanged();
                boolean flag = damagesource.getEntity() instanceof EntityHuman && ((EntityHuman) damagesource.getEntity()).abilities.canInstantlyBuild;

                if (flag || this.getDamage() > 40.0F) {
                    if (!flag && this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
                        this.a((IMaterial) this.f());
                    }

                    this.die();
                }

                return true;
            }
        } else {
            return true;
        }
    }

    @Override
    public void j(boolean flag) {
        if (!this.world.isClientSide) {
            this.aI = true;
            this.aJ = flag;
            if (this.A() == 0) {
                this.d(60);
            }
        }

        this.world.addParticle(Particles.SPLASH, this.locX() + (double) this.random.nextFloat(), this.locY() + 0.7D, this.locZ() + (double) this.random.nextFloat(), 0.0D, 0.0D, 0.0D);
        if (this.random.nextInt(20) == 0) {
            this.world.a(this.locX(), this.locY(), this.locZ(), this.getSoundSplash(), this.getSoundCategory(), 1.0F, 0.8F + 0.4F * this.random.nextFloat(), false);
        }

    }

    @Override
    public void collide(Entity entity) {
        if (entity instanceof EntityBoat) {
            if (entity.getBoundingBox().minY < this.getBoundingBox().maxY) {
                super.collide(entity);
            }
        } else if (entity.getBoundingBox().minY <= this.getBoundingBox().minY) {
            super.collide(entity);
        }

    }

    public Item f() {
        switch (this.getType()) {
            case OAK:
            default:
                return Items.OAK_BOAT;
            case SPRUCE:
                return Items.SPRUCE_BOAT;
            case BIRCH:
                return Items.BIRCH_BOAT;
            case JUNGLE:
                return Items.JUNGLE_BOAT;
            case ACACIA:
                return Items.ACACIA_BOAT;
            case DARK_OAK:
                return Items.DARK_OAK_BOAT;
        }
    }

    @Override
    public boolean isInteractable() {
        return !this.dead;
    }

    @Override
    public EnumDirection getAdjustedDirection() {
        return this.getDirection().f();
    }

    @Override
    public void tick() {
        this.aG = this.aF;
        this.aF = this.s();
        if (this.aF != EntityBoat.EnumStatus.UNDER_WATER && this.aF != EntityBoat.EnumStatus.UNDER_FLOWING_WATER) {
            this.ar = 0.0F;
        } else {
            ++this.ar;
        }

        if (!this.world.isClientSide && this.ar >= 60.0F) {
            this.ejectPassengers();
        }

        if (this.n() > 0) {
            this.b(this.n() - 1);
        }

        if (this.getDamage() > 0.0F) {
            this.setDamage(this.getDamage() - 1.0F);
        }

        super.tick();
        this.r();
        if (this.cj()) {
            if (this.getPassengers().isEmpty() || !(this.getPassengers().get(0) instanceof EntityHuman)) {
                this.a(false, false);
            }

            this.w();
            if (this.world.isClientSide) {
                this.z();
                this.world.a((Packet) (new PacketPlayInBoatMove(this.a(0), this.a(1))));
            }

            this.move(EnumMoveType.SELF, this.getMot());
        } else {
            this.setMot(Vec3D.a);
        }

        this.q();

        for (int i = 0; i <= 1; ++i) {
            if (this.a(i)) {
                if (!this.isSilent() && (double) (this.ap[i] % 6.2831855F) <= 0.7853981852531433D && ((double) this.ap[i] + 0.39269909262657166D) % 6.2831854820251465D >= 0.7853981852531433D) {
                    SoundEffect soundeffect = this.i();

                    if (soundeffect != null) {
                        Vec3D vec3d = this.f(1.0F);
                        double d0 = i == 1 ? -vec3d.z : vec3d.z;
                        double d1 = i == 1 ? vec3d.x : -vec3d.x;

                        this.world.playSound((EntityHuman) null, this.locX() + d0, this.locY(), this.locZ() + d1, soundeffect, this.getSoundCategory(), 1.0F, 0.8F + 0.4F * this.random.nextFloat());
                    }
                }

                this.ap[i] = (float) ((double) this.ap[i] + 0.39269909262657166D);
            } else {
                this.ap[i] = 0.0F;
            }
        }

        this.checkBlockCollisions();
        List<Entity> list = this.world.getEntities(this, this.getBoundingBox().grow(0.20000000298023224D, -0.009999999776482582D, 0.20000000298023224D), IEntitySelector.a(this));

        if (!list.isEmpty()) {
            boolean flag = !this.world.isClientSide && !(this.getRidingPassenger() instanceof EntityHuman);

            for (int j = 0; j < list.size(); ++j) {
                Entity entity = (Entity) list.get(j);

                if (!entity.w(this)) {
                    if (flag && this.getPassengers().size() < 2 && !entity.isPassenger() && entity.getWidth() < this.getWidth() && entity instanceof EntityLiving && !(entity instanceof EntityWaterAnimal) && !(entity instanceof EntityHuman)) {
                        entity.startRiding(this);
                    } else {
                        this.collide(entity);
                    }
                }
            }
        }

    }

    private void q() {
        int i;

        if (this.world.isClientSide) {
            i = this.A();
            if (i > 0) {
                this.aK += 0.05F;
            } else {
                this.aK -= 0.1F;
            }

            this.aK = MathHelper.a(this.aK, 0.0F, 1.0F);
            this.aM = this.aL;
            this.aL = 10.0F * (float) Math.sin((double) (0.5F * (float) this.world.getTime())) * this.aK;
        } else {
            if (!this.aI) {
                this.d(0);
            }

            i = this.A();
            if (i > 0) {
                --i;
                this.d(i);
                int j = 60 - i - 1;

                if (j > 0 && i == 0) {
                    this.d(0);
                    Vec3D vec3d = this.getMot();

                    if (this.aJ) {
                        this.setMot(vec3d.add(0.0D, -0.7D, 0.0D));
                        this.ejectPassengers();
                    } else {
                        this.setMot(vec3d.x, this.a(EntityHuman.class) ? 2.7D : 0.6D, vec3d.z);
                    }
                }

                this.aI = false;
            }
        }

    }

    @Nullable
    protected SoundEffect i() {
        switch (this.s()) {
            case IN_WATER:
            case UNDER_WATER:
            case UNDER_FLOWING_WATER:
                return SoundEffects.ENTITY_BOAT_PADDLE_WATER;
            case ON_LAND:
                return SoundEffects.ENTITY_BOAT_PADDLE_LAND;
            case IN_AIR:
            default:
                return null;
        }
    }

    private void r() {
        if (this.cj()) {
            this.at = 0;
            this.c(this.locX(), this.locY(), this.locZ());
        }

        if (this.at > 0) {
            double d0 = this.locX() + (this.au - this.locX()) / (double) this.at;
            double d1 = this.locY() + (this.av - this.locY()) / (double) this.at;
            double d2 = this.locZ() + (this.aw - this.locZ()) / (double) this.at;
            double d3 = MathHelper.g(this.ax - (double) this.yaw);

            this.yaw = (float) ((double) this.yaw + d3 / (double) this.at);
            this.pitch = (float) ((double) this.pitch + (this.ay - (double) this.pitch) / (double) this.at);
            --this.at;
            this.setPosition(d0, d1, d2);
            this.setYawPitch(this.yaw, this.pitch);
        }
    }

    public void a(boolean flag, boolean flag1) {
        this.datawatcher.set(EntityBoat.f, flag);
        this.datawatcher.set(EntityBoat.g, flag1);
    }

    private EntityBoat.EnumStatus s() {
        EntityBoat.EnumStatus entityboat_enumstatus = this.v();

        if (entityboat_enumstatus != null) {
            this.aD = this.getBoundingBox().maxY;
            return entityboat_enumstatus;
        } else if (this.u()) {
            return EntityBoat.EnumStatus.IN_WATER;
        } else {
            float f = this.l();

            if (f > 0.0F) {
                this.aE = f;
                return EntityBoat.EnumStatus.ON_LAND;
            } else {
                return EntityBoat.EnumStatus.IN_AIR;
            }
        }
    }

    public float k() {
        AxisAlignedBB axisalignedbb = this.getBoundingBox();
        int i = MathHelper.floor(axisalignedbb.minX);
        int j = MathHelper.f(axisalignedbb.maxX);
        int k = MathHelper.floor(axisalignedbb.maxY);
        int l = MathHelper.f(axisalignedbb.maxY - this.aH);
        int i1 = MathHelper.floor(axisalignedbb.minZ);
        int j1 = MathHelper.f(axisalignedbb.maxZ);
        BlockPosition.PooledBlockPosition blockposition_pooledblockposition = BlockPosition.PooledBlockPosition.r();
        Throwable throwable = null;

        try {
            label159:
            for (int k1 = k; k1 < l; ++k1) {
                float f = 0.0F;

                for (int l1 = i; l1 < j; ++l1) {
                    for (int i2 = i1; i2 < j1; ++i2) {
                        blockposition_pooledblockposition.d(l1, k1, i2);
                        Fluid fluid = this.world.getFluid(blockposition_pooledblockposition);

                        if (fluid.a(TagsFluid.WATER)) {
                            f = Math.max(f, fluid.getHeight(this.world, blockposition_pooledblockposition));
                        }

                        if (f >= 1.0F) {
                            continue label159;
                        }
                    }
                }

                if (f < 1.0F) {
                    float f1 = (float) blockposition_pooledblockposition.getY() + f;

                    return f1;
                }
            }

            float f2 = (float) (l + 1);

            return f2;
        } catch (Throwable throwable1) {
            throwable = throwable1;
            throw throwable1;
        } finally {
            if (blockposition_pooledblockposition != null) {
                if (throwable != null) {
                    try {
                        blockposition_pooledblockposition.close();
                    } catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                } else {
                    blockposition_pooledblockposition.close();
                }
            }

        }
    }

    public float l() {
        AxisAlignedBB axisalignedbb = this.getBoundingBox();
        AxisAlignedBB axisalignedbb1 = new AxisAlignedBB(axisalignedbb.minX, axisalignedbb.minY - 0.001D, axisalignedbb.minZ, axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ);
        int i = MathHelper.floor(axisalignedbb1.minX) - 1;
        int j = MathHelper.f(axisalignedbb1.maxX) + 1;
        int k = MathHelper.floor(axisalignedbb1.minY) - 1;
        int l = MathHelper.f(axisalignedbb1.maxY) + 1;
        int i1 = MathHelper.floor(axisalignedbb1.minZ) - 1;
        int j1 = MathHelper.f(axisalignedbb1.maxZ) + 1;
        VoxelShape voxelshape = VoxelShapes.a(axisalignedbb1);
        float f = 0.0F;
        int k1 = 0;
        BlockPosition.PooledBlockPosition blockposition_pooledblockposition = BlockPosition.PooledBlockPosition.r();
        Throwable throwable = null;

        try {
            for (int l1 = i; l1 < j; ++l1) {
                for (int i2 = i1; i2 < j1; ++i2) {
                    int j2 = (l1 != i && l1 != j - 1 ? 0 : 1) + (i2 != i1 && i2 != j1 - 1 ? 0 : 1);

                    if (j2 != 2) {
                        for (int k2 = k; k2 < l; ++k2) {
                            if (j2 <= 0 || k2 != k && k2 != l - 1) {
                                blockposition_pooledblockposition.d(l1, k2, i2);
                                IBlockData iblockdata = this.world.getType(blockposition_pooledblockposition);

                                if (!(iblockdata.getBlock() instanceof BlockWaterLily) && VoxelShapes.c(iblockdata.getCollisionShape(this.world, blockposition_pooledblockposition).a((double) l1, (double) k2, (double) i2), voxelshape, OperatorBoolean.AND)) {
                                    f += iblockdata.getBlock().l();
                                    ++k1;
                                }
                            }
                        }
                    }
                }
            }
        } catch (Throwable throwable1) {
            throwable = throwable1;
            throw throwable1;
        } finally {
            if (blockposition_pooledblockposition != null) {
                if (throwable != null) {
                    try {
                        blockposition_pooledblockposition.close();
                    } catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                } else {
                    blockposition_pooledblockposition.close();
                }
            }

        }

        return f / (float) k1;
    }

    private boolean u() {
        AxisAlignedBB axisalignedbb = this.getBoundingBox();
        int i = MathHelper.floor(axisalignedbb.minX);
        int j = MathHelper.f(axisalignedbb.maxX);
        int k = MathHelper.floor(axisalignedbb.minY);
        int l = MathHelper.f(axisalignedbb.minY + 0.001D);
        int i1 = MathHelper.floor(axisalignedbb.minZ);
        int j1 = MathHelper.f(axisalignedbb.maxZ);
        boolean flag = false;

        this.aD = Double.MIN_VALUE;
        BlockPosition.PooledBlockPosition blockposition_pooledblockposition = BlockPosition.PooledBlockPosition.r();
        Throwable throwable = null;

        try {
            for (int k1 = i; k1 < j; ++k1) {
                for (int l1 = k; l1 < l; ++l1) {
                    for (int i2 = i1; i2 < j1; ++i2) {
                        blockposition_pooledblockposition.d(k1, l1, i2);
                        Fluid fluid = this.world.getFluid(blockposition_pooledblockposition);

                        if (fluid.a(TagsFluid.WATER)) {
                            float f = (float) l1 + fluid.getHeight(this.world, blockposition_pooledblockposition);

                            this.aD = Math.max((double) f, this.aD);
                            flag |= axisalignedbb.minY < (double) f;
                        }
                    }
                }
            }
        } catch (Throwable throwable1) {
            throwable = throwable1;
            throw throwable1;
        } finally {
            if (blockposition_pooledblockposition != null) {
                if (throwable != null) {
                    try {
                        blockposition_pooledblockposition.close();
                    } catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                } else {
                    blockposition_pooledblockposition.close();
                }
            }

        }

        return flag;
    }

    @Nullable
    private EntityBoat.EnumStatus v() {
        AxisAlignedBB axisalignedbb = this.getBoundingBox();
        double d0 = axisalignedbb.maxY + 0.001D;
        int i = MathHelper.floor(axisalignedbb.minX);
        int j = MathHelper.f(axisalignedbb.maxX);
        int k = MathHelper.floor(axisalignedbb.maxY);
        int l = MathHelper.f(d0);
        int i1 = MathHelper.floor(axisalignedbb.minZ);
        int j1 = MathHelper.f(axisalignedbb.maxZ);
        boolean flag = false;
        BlockPosition.PooledBlockPosition blockposition_pooledblockposition = BlockPosition.PooledBlockPosition.r();
        Throwable throwable = null;

        try {
            for (int k1 = i; k1 < j; ++k1) {
                for (int l1 = k; l1 < l; ++l1) {
                    for (int i2 = i1; i2 < j1; ++i2) {
                        blockposition_pooledblockposition.d(k1, l1, i2);
                        Fluid fluid = this.world.getFluid(blockposition_pooledblockposition);

                        if (fluid.a(TagsFluid.WATER) && d0 < (double) ((float) blockposition_pooledblockposition.getY() + fluid.getHeight(this.world, blockposition_pooledblockposition))) {
                            if (!fluid.isSource()) {
                                EntityBoat.EnumStatus entityboat_enumstatus = EntityBoat.EnumStatus.UNDER_FLOWING_WATER;

                                return entityboat_enumstatus;
                            }

                            flag = true;
                        }
                    }
                }
            }

            return flag ? EntityBoat.EnumStatus.UNDER_WATER : null;
        } catch (Throwable throwable1) {
            throwable = throwable1;
            throw throwable1;
        } finally {
            if (blockposition_pooledblockposition != null) {
                if (throwable != null) {
                    try {
                        blockposition_pooledblockposition.close();
                    } catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                } else {
                    blockposition_pooledblockposition.close();
                }
            }

        }
    }

    private void w() {
        double d0 = -0.03999999910593033D;
        double d1 = this.isNoGravity() ? 0.0D : -0.03999999910593033D;
        double d2 = 0.0D;

        this.aq = 0.05F;
        if (this.aG == EntityBoat.EnumStatus.IN_AIR && this.aF != EntityBoat.EnumStatus.IN_AIR && this.aF != EntityBoat.EnumStatus.ON_LAND) {
            this.aD = this.e(1.0D);
            this.setPosition(this.locX(), (double) (this.k() - this.getHeight()) + 0.101D, this.locZ());
            this.setMot(this.getMot().d(1.0D, 0.0D, 1.0D));
            this.aH = 0.0D;
            this.aF = EntityBoat.EnumStatus.IN_WATER;
        } else {
            if (this.aF == EntityBoat.EnumStatus.IN_WATER) {
                d2 = (this.aD - this.locY()) / (double) this.getHeight();
                this.aq = 0.9F;
            } else if (this.aF == EntityBoat.EnumStatus.UNDER_FLOWING_WATER) {
                d1 = -7.0E-4D;
                this.aq = 0.9F;
            } else if (this.aF == EntityBoat.EnumStatus.UNDER_WATER) {
                d2 = 0.009999999776482582D;
                this.aq = 0.45F;
            } else if (this.aF == EntityBoat.EnumStatus.IN_AIR) {
                this.aq = 0.9F;
            } else if (this.aF == EntityBoat.EnumStatus.ON_LAND) {
                this.aq = this.aE;
                if (this.getRidingPassenger() instanceof EntityHuman) {
                    this.aE /= 2.0F;
                }
            }

            Vec3D vec3d = this.getMot();

            this.setMot(vec3d.x * (double) this.aq, vec3d.y + d1, vec3d.z * (double) this.aq);
            this.as *= this.aq;
            if (d2 > 0.0D) {
                Vec3D vec3d1 = this.getMot();

                this.setMot(vec3d1.x, (vec3d1.y + d2 * 0.06153846016296973D) * 0.75D, vec3d1.z);
            }
        }

    }

    private void z() {
        if (this.isVehicle()) {
            float f = 0.0F;

            if (this.az) {
                --this.as;
            }

            if (this.aA) {
                ++this.as;
            }

            if (this.aA != this.az && !this.aB && !this.aC) {
                f += 0.005F;
            }

            this.yaw += this.as;
            if (this.aB) {
                f += 0.04F;
            }

            if (this.aC) {
                f -= 0.005F;
            }

            this.setMot(this.getMot().add((double) (MathHelper.sin(-this.yaw * 0.017453292F) * f), 0.0D, (double) (MathHelper.cos(this.yaw * 0.017453292F) * f)));
            this.a(this.aA && !this.az || this.aB, this.az && !this.aA || this.aB);
        }
    }

    @Override
    public void k(Entity entity) {
        if (this.w(entity)) {
            float f = 0.0F;
            float f1 = (float) ((this.dead ? 0.009999999776482582D : this.aS()) + entity.aR());

            if (this.getPassengers().size() > 1) {
                int i = this.getPassengers().indexOf(entity);

                if (i == 0) {
                    f = 0.2F;
                } else {
                    f = -0.6F;
                }

                if (entity instanceof EntityAnimal) {
                    f = (float) ((double) f + 0.2D);
                }
            }

            Vec3D vec3d = (new Vec3D((double) f, 0.0D, 0.0D)).b(-this.yaw * 0.017453292F - 1.5707964F);

            entity.setPosition(this.locX() + vec3d.x, this.locY() + (double) f1, this.locZ() + vec3d.z);
            entity.yaw += this.as;
            entity.setHeadRotation(entity.getHeadRotation() + this.as);
            this.a(entity);
            if (entity instanceof EntityAnimal && this.getPassengers().size() > 1) {
                int j = entity.getId() % 2 == 0 ? 90 : 270;

                entity.l(((EntityAnimal) entity).aI + (float) j);
                entity.setHeadRotation(entity.getHeadRotation() + (float) j);
            }

        }
    }

    protected void a(Entity entity) {
        entity.l(this.yaw);
        float f = MathHelper.g(entity.yaw - this.yaw);
        float f1 = MathHelper.a(f, -105.0F, 105.0F);

        entity.lastYaw += f1 - f;
        entity.yaw += f1 - f;
        entity.setHeadRotation(entity.yaw);
    }

    @Override
    protected void b(NBTTagCompound nbttagcompound) {
        nbttagcompound.setString("Type", this.getType().a());
    }

    @Override
    protected void a(NBTTagCompound nbttagcompound) {
        if (nbttagcompound.hasKeyOfType("Type", 8)) {
            this.setType(EntityBoat.EnumBoatType.a(nbttagcompound.getString("Type")));
        }

    }

    @Override
    public boolean b(EntityHuman entityhuman, EnumHand enumhand) {
        return entityhuman.dT() ? false : (!this.world.isClientSide && this.ar < 60.0F ? entityhuman.startRiding(this) : false);
    }

    @Override
    protected void a(double d0, boolean flag, IBlockData iblockdata, BlockPosition blockposition) {
        this.aH = this.getMot().y;
        if (!this.isPassenger()) {
            if (flag) {
                if (this.fallDistance > 3.0F) {
                    if (this.aF != EntityBoat.EnumStatus.ON_LAND) {
                        this.fallDistance = 0.0F;
                        return;
                    }

                    this.b(this.fallDistance, 1.0F);
                    if (!this.world.isClientSide && !this.dead) {
                        this.die();
                        if (this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
                            int i;

                            for (i = 0; i < 3; ++i) {
                                this.a((IMaterial) this.getType().b());
                            }

                            for (i = 0; i < 2; ++i) {
                                this.a((IMaterial) Items.STICK);
                            }
                        }
                    }
                }

                this.fallDistance = 0.0F;
            } else if (!this.world.getFluid((new BlockPosition(this)).down()).a(TagsFluid.WATER) && d0 < 0.0D) {
                this.fallDistance = (float) ((double) this.fallDistance - d0);
            }

        }
    }

    public boolean a(int i) {
        return (Boolean) this.datawatcher.get(i == 0 ? EntityBoat.f : EntityBoat.g) && this.getRidingPassenger() != null;
    }

    public void setDamage(float f) {
        this.datawatcher.set(EntityBoat.d, f);
    }

    public float getDamage() {
        return (Float) this.datawatcher.get(EntityBoat.d);
    }

    public void b(int i) {
        this.datawatcher.set(EntityBoat.b, i);
    }

    public int n() {
        return (Integer) this.datawatcher.get(EntityBoat.b);
    }

    private void d(int i) {
        this.datawatcher.set(EntityBoat.ao, i);
    }

    private int A() {
        return (Integer) this.datawatcher.get(EntityBoat.ao);
    }

    public void c(int i) {
        this.datawatcher.set(EntityBoat.c, i);
    }

    public int o() {
        return (Integer) this.datawatcher.get(EntityBoat.c);
    }

    public void setType(EntityBoat.EnumBoatType entityboat_enumboattype) {
        this.datawatcher.set(EntityBoat.e, entityboat_enumboattype.ordinal());
    }

    public EntityBoat.EnumBoatType getType() {
        return EntityBoat.EnumBoatType.a((Integer) this.datawatcher.get(EntityBoat.e));
    }

    @Override
    protected boolean q(Entity entity) {
        return this.getPassengers().size() < 2 && !this.a(TagsFluid.WATER);
    }

    @Nullable
    @Override
    public Entity getRidingPassenger() {
        List<Entity> list = this.getPassengers();

        return list.isEmpty() ? null : (Entity) list.get(0);
    }

    @Override
    public Packet<?> L() {
        return new PacketPlayOutSpawnEntity(this);
    }

    public static enum EnumBoatType {

        OAK(Blocks.OAK_PLANKS, "oak"), SPRUCE(Blocks.SPRUCE_PLANKS, "spruce"), BIRCH(Blocks.BIRCH_PLANKS, "birch"), JUNGLE(Blocks.JUNGLE_PLANKS, "jungle"), ACACIA(Blocks.ACACIA_PLANKS, "acacia"), DARK_OAK(Blocks.DARK_OAK_PLANKS, "dark_oak");

        private final String g;
        private final Block h;

        private EnumBoatType(Block block, String s) {
            this.g = s;
            this.h = block;
        }

        public String a() {
            return this.g;
        }

        public Block b() {
            return this.h;
        }

        public String toString() {
            return this.g;
        }

        public static EntityBoat.EnumBoatType a(int i) {
            EntityBoat.EnumBoatType[] aentityboat_enumboattype = values();

            if (i < 0 || i >= aentityboat_enumboattype.length) {
                i = 0;
            }

            return aentityboat_enumboattype[i];
        }

        public static EntityBoat.EnumBoatType a(String s) {
            EntityBoat.EnumBoatType[] aentityboat_enumboattype = values();

            for (int i = 0; i < aentityboat_enumboattype.length; ++i) {
                if (aentityboat_enumboattype[i].a().equals(s)) {
                    return aentityboat_enumboattype[i];
                }
            }

            return aentityboat_enumboattype[0];
        }
    }

    public static enum EnumStatus {

        IN_WATER, UNDER_WATER, UNDER_FLOWING_WATER, ON_LAND, IN_AIR;

        private EnumStatus() {}
    }
}
