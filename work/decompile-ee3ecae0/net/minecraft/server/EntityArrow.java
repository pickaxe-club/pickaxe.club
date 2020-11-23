package net.minecraft.server;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;

public abstract class EntityArrow extends Entity implements IProjectile {

    private static final DataWatcherObject<Byte> ao = DataWatcher.a(EntityArrow.class, DataWatcherRegistry.a);
    protected static final DataWatcherObject<Optional<UUID>> b = DataWatcher.a(EntityArrow.class, DataWatcherRegistry.o);
    private static final DataWatcherObject<Byte> ap = DataWatcher.a(EntityArrow.class, DataWatcherRegistry.a);
    @Nullable
    private IBlockData aq;
    public boolean inGround;
    protected int d;
    public EntityArrow.PickupStatus fromPlayer;
    public int shake;
    public UUID shooter;
    public int despawnCounter;
    private int as;
    private double damage;
    public int knockbackStrength;
    private SoundEffect av;
    private IntOpenHashSet aw;
    private List<Entity> ax;

    protected EntityArrow(EntityTypes<? extends EntityArrow> entitytypes, World world) {
        super(entitytypes, world);
        this.fromPlayer = EntityArrow.PickupStatus.DISALLOWED;
        this.damage = 2.0D;
        this.av = this.k();
    }

    protected EntityArrow(EntityTypes<? extends EntityArrow> entitytypes, double d0, double d1, double d2, World world) {
        this(entitytypes, world);
        this.setPosition(d0, d1, d2);
    }

    protected EntityArrow(EntityTypes<? extends EntityArrow> entitytypes, EntityLiving entityliving, World world) {
        this(entitytypes, entityliving.locX(), entityliving.getHeadY() - 0.10000000149011612D, entityliving.locZ(), world);
        this.setShooter(entityliving);
        if (entityliving instanceof EntityHuman) {
            this.fromPlayer = EntityArrow.PickupStatus.ALLOWED;
        }

    }

    public void a(SoundEffect soundeffect) {
        this.av = soundeffect;
    }

    @Override
    protected void initDatawatcher() {
        this.datawatcher.register(EntityArrow.ao, (byte) 0);
        this.datawatcher.register(EntityArrow.b, Optional.empty());
        this.datawatcher.register(EntityArrow.ap, (byte) 0);
    }

    public void a(Entity entity, float f, float f1, float f2, float f3, float f4) {
        float f5 = -MathHelper.sin(f1 * 0.017453292F) * MathHelper.cos(f * 0.017453292F);
        float f6 = -MathHelper.sin(f * 0.017453292F);
        float f7 = MathHelper.cos(f1 * 0.017453292F) * MathHelper.cos(f * 0.017453292F);

        this.shoot((double) f5, (double) f6, (double) f7, f3, f4);
        this.setMot(this.getMot().add(entity.getMot().x, entity.onGround ? 0.0D : entity.getMot().y, entity.getMot().z));
    }

    @Override
    public void shoot(double d0, double d1, double d2, float f, float f1) {
        Vec3D vec3d = (new Vec3D(d0, d1, d2)).d().add(this.random.nextGaussian() * 0.007499999832361937D * (double) f1, this.random.nextGaussian() * 0.007499999832361937D * (double) f1, this.random.nextGaussian() * 0.007499999832361937D * (double) f1).a((double) f);

        this.setMot(vec3d);
        float f2 = MathHelper.sqrt(b(vec3d));

        this.yaw = (float) (MathHelper.d(vec3d.x, vec3d.z) * 57.2957763671875D);
        this.pitch = (float) (MathHelper.d(vec3d.y, (double) f2) * 57.2957763671875D);
        this.lastYaw = this.yaw;
        this.lastPitch = this.pitch;
        this.despawnCounter = 0;
    }

    @Override
    public void tick() {
        super.tick();
        boolean flag = this.v();
        Vec3D vec3d = this.getMot();

        if (this.lastPitch == 0.0F && this.lastYaw == 0.0F) {
            float f = MathHelper.sqrt(b(vec3d));

            this.yaw = (float) (MathHelper.d(vec3d.x, vec3d.z) * 57.2957763671875D);
            this.pitch = (float) (MathHelper.d(vec3d.y, (double) f) * 57.2957763671875D);
            this.lastYaw = this.yaw;
            this.lastPitch = this.pitch;
        }

        BlockPosition blockposition = new BlockPosition(this);
        IBlockData iblockdata = this.world.getType(blockposition);
        Vec3D vec3d1;

        if (!iblockdata.isAir() && !flag) {
            VoxelShape voxelshape = iblockdata.getCollisionShape(this.world, blockposition);

            if (!voxelshape.isEmpty()) {
                vec3d1 = this.getPositionVector();
                Iterator iterator = voxelshape.d().iterator();

                while (iterator.hasNext()) {
                    AxisAlignedBB axisalignedbb = (AxisAlignedBB) iterator.next();

                    if (axisalignedbb.a(blockposition).c(vec3d1)) {
                        this.inGround = true;
                        break;
                    }
                }
            }
        }

        if (this.shake > 0) {
            --this.shake;
        }

        if (this.isInWaterOrRain()) {
            this.extinguish();
        }

        if (this.inGround && !flag) {
            if (this.aq != iblockdata && this.world.a(this.getBoundingBox().g(0.06D))) {
                this.inGround = false;
                this.setMot(vec3d.d((double) (this.random.nextFloat() * 0.2F), (double) (this.random.nextFloat() * 0.2F), (double) (this.random.nextFloat() * 0.2F)));
                this.despawnCounter = 0;
                this.as = 0;
            } else if (!this.world.isClientSide) {
                this.i();
            }

            ++this.d;
        } else {
            this.d = 0;
            ++this.as;
            Vec3D vec3d2 = this.getPositionVector();

            vec3d1 = vec3d2.e(vec3d);
            Object object = this.world.rayTrace(new RayTrace(vec3d2, vec3d1, RayTrace.BlockCollisionOption.COLLIDER, RayTrace.FluidCollisionOption.NONE, this));

            if (((MovingObjectPosition) object).getType() != MovingObjectPosition.EnumMovingObjectType.MISS) {
                vec3d1 = ((MovingObjectPosition) object).getPos();
            }

            while (!this.dead) {
                MovingObjectPositionEntity movingobjectpositionentity = this.a(vec3d2, vec3d1);

                if (movingobjectpositionentity != null) {
                    object = movingobjectpositionentity;
                }

                if (object != null && ((MovingObjectPosition) object).getType() == MovingObjectPosition.EnumMovingObjectType.ENTITY) {
                    Entity entity = ((MovingObjectPositionEntity) object).getEntity();
                    Entity entity1 = this.getShooter();

                    if (entity instanceof EntityHuman && entity1 instanceof EntityHuman && !((EntityHuman) entity1).a((EntityHuman) entity)) {
                        object = null;
                        movingobjectpositionentity = null;
                    }
                }

                if (object != null && !flag) {
                    this.a((MovingObjectPosition) object);
                    this.impulse = true;
                }

                if (movingobjectpositionentity == null || this.getPierceLevel() <= 0) {
                    break;
                }

                object = null;
            }

            vec3d = this.getMot();
            double d0 = vec3d.x;
            double d1 = vec3d.y;
            double d2 = vec3d.z;

            if (this.isCritical()) {
                for (int i = 0; i < 4; ++i) {
                    this.world.addParticle(Particles.CRIT, this.locX() + d0 * (double) i / 4.0D, this.locY() + d1 * (double) i / 4.0D, this.locZ() + d2 * (double) i / 4.0D, -d0, -d1 + 0.2D, -d2);
                }
            }

            double d3 = this.locX() + d0;
            double d4 = this.locY() + d1;
            double d5 = this.locZ() + d2;
            float f1 = MathHelper.sqrt(b(vec3d));

            if (flag) {
                this.yaw = (float) (MathHelper.d(-d0, -d2) * 57.2957763671875D);
            } else {
                this.yaw = (float) (MathHelper.d(d0, d2) * 57.2957763671875D);
            }

            for (this.pitch = (float) (MathHelper.d(d1, (double) f1) * 57.2957763671875D); this.pitch - this.lastPitch < -180.0F; this.lastPitch -= 360.0F) {
                ;
            }

            while (this.pitch - this.lastPitch >= 180.0F) {
                this.lastPitch += 360.0F;
            }

            while (this.yaw - this.lastYaw < -180.0F) {
                this.lastYaw -= 360.0F;
            }

            while (this.yaw - this.lastYaw >= 180.0F) {
                this.lastYaw += 360.0F;
            }

            this.pitch = MathHelper.g(0.2F, this.lastPitch, this.pitch);
            this.yaw = MathHelper.g(0.2F, this.lastYaw, this.yaw);
            float f2 = 0.99F;
            float f3 = 0.05F;

            if (this.isInWater()) {
                for (int j = 0; j < 4; ++j) {
                    float f4 = 0.25F;

                    this.world.addParticle(Particles.BUBBLE, d3 - d0 * 0.25D, d4 - d1 * 0.25D, d5 - d2 * 0.25D, d0, d1, d2);
                }

                f2 = this.u();
            }

            this.setMot(vec3d.a((double) f2));
            if (!this.isNoGravity() && !flag) {
                Vec3D vec3d3 = this.getMot();

                this.setMot(vec3d3.x, vec3d3.y - 0.05000000074505806D, vec3d3.z);
            }

            this.setPosition(d3, d4, d5);
            this.checkBlockCollisions();
        }
    }

    protected void i() {
        ++this.despawnCounter;
        if (this.despawnCounter >= 1200) {
            this.die();
        }

    }

    protected void a(MovingObjectPosition movingobjectposition) {
        MovingObjectPosition.EnumMovingObjectType movingobjectposition_enummovingobjecttype = movingobjectposition.getType();

        if (movingobjectposition_enummovingobjecttype == MovingObjectPosition.EnumMovingObjectType.ENTITY) {
            this.a((MovingObjectPositionEntity) movingobjectposition);
        } else if (movingobjectposition_enummovingobjecttype == MovingObjectPosition.EnumMovingObjectType.BLOCK) {
            MovingObjectPositionBlock movingobjectpositionblock = (MovingObjectPositionBlock) movingobjectposition;
            IBlockData iblockdata = this.world.getType(movingobjectpositionblock.getBlockPosition());

            this.aq = iblockdata;
            Vec3D vec3d = movingobjectpositionblock.getPos().a(this.locX(), this.locY(), this.locZ());

            this.setMot(vec3d);
            Vec3D vec3d1 = vec3d.d().a(0.05000000074505806D);

            this.setPositionRaw(this.locX() - vec3d1.x, this.locY() - vec3d1.y, this.locZ() - vec3d1.z);
            this.a(this.getSoundHit(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
            this.inGround = true;
            this.shake = 7;
            this.setCritical(false);
            this.setPierceLevel((byte) 0);
            this.a(SoundEffects.ENTITY_ARROW_HIT);
            this.o(false);
            this.w();
            iblockdata.a(this.world, iblockdata, movingobjectpositionblock, this);
        }

    }

    private void w() {
        if (this.ax != null) {
            this.ax.clear();
        }

        if (this.aw != null) {
            this.aw.clear();
        }

    }

    protected void a(MovingObjectPositionEntity movingobjectpositionentity) {
        Entity entity = movingobjectpositionentity.getEntity();
        float f = (float) this.getMot().f();
        int i = MathHelper.f(Math.max((double) f * this.damage, 0.0D));

        if (this.getPierceLevel() > 0) {
            if (this.aw == null) {
                this.aw = new IntOpenHashSet(5);
            }

            if (this.ax == null) {
                this.ax = Lists.newArrayListWithCapacity(5);
            }

            if (this.aw.size() >= this.getPierceLevel() + 1) {
                this.die();
                return;
            }

            this.aw.add(entity.getId());
        }

        if (this.isCritical()) {
            i += this.random.nextInt(i / 2 + 2);
        }

        Entity entity1 = this.getShooter();
        DamageSource damagesource;

        if (entity1 == null) {
            damagesource = DamageSource.arrow(this, this);
        } else {
            damagesource = DamageSource.arrow(this, entity1);
            if (entity1 instanceof EntityLiving) {
                ((EntityLiving) entity1).z(entity);
            }
        }

        boolean flag = entity.getEntityType() == EntityTypes.ENDERMAN;
        int j = entity.ad();

        if (this.isBurning() && !flag) {
            entity.setOnFire(5);
        }

        if (entity.damageEntity(damagesource, (float) i)) {
            if (flag) {
                return;
            }

            if (entity instanceof EntityLiving) {
                EntityLiving entityliving = (EntityLiving) entity;

                if (!this.world.isClientSide && this.getPierceLevel() <= 0) {
                    entityliving.setArrowCount(entityliving.getArrowCount() + 1);
                }

                if (this.knockbackStrength > 0) {
                    Vec3D vec3d = this.getMot().d(1.0D, 0.0D, 1.0D).d().a((double) this.knockbackStrength * 0.6D);

                    if (vec3d.g() > 0.0D) {
                        entityliving.h(vec3d.x, 0.1D, vec3d.z);
                    }
                }

                if (!this.world.isClientSide && entity1 instanceof EntityLiving) {
                    EnchantmentManager.a(entityliving, entity1);
                    EnchantmentManager.b((EntityLiving) entity1, (Entity) entityliving);
                }

                this.a(entityliving);
                if (entity1 != null && entityliving != entity1 && entityliving instanceof EntityHuman && entity1 instanceof EntityPlayer) {
                    ((EntityPlayer) entity1).playerConnection.sendPacket(new PacketPlayOutGameStateChange(6, 0.0F));
                }

                if (!entity.isAlive() && this.ax != null) {
                    this.ax.add(entityliving);
                }

                if (!this.world.isClientSide && entity1 instanceof EntityPlayer) {
                    EntityPlayer entityplayer = (EntityPlayer) entity1;

                    if (this.ax != null && this.r()) {
                        CriterionTriggers.G.a(entityplayer, this.ax, this.ax.size());
                    } else if (!entity.isAlive() && this.r()) {
                        CriterionTriggers.G.a(entityplayer, Arrays.asList(entity), 0);
                    }
                }
            }

            this.a(this.av, 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
            if (this.getPierceLevel() <= 0) {
                this.die();
            }
        } else {
            entity.g(j);
            this.setMot(this.getMot().a(-0.1D));
            this.yaw += 180.0F;
            this.lastYaw += 180.0F;
            this.as = 0;
            if (!this.world.isClientSide && this.getMot().g() < 1.0E-7D) {
                if (this.fromPlayer == EntityArrow.PickupStatus.ALLOWED) {
                    this.a(this.getItemStack(), 0.1F);
                }

                this.die();
            }
        }

    }

    protected SoundEffect k() {
        return SoundEffects.ENTITY_ARROW_HIT;
    }

    protected final SoundEffect getSoundHit() {
        return this.av;
    }

    protected void a(EntityLiving entityliving) {}

    @Nullable
    protected MovingObjectPositionEntity a(Vec3D vec3d, Vec3D vec3d1) {
        return ProjectileHelper.a(this.world, this, vec3d, vec3d1, this.getBoundingBox().a(this.getMot()).g(1.0D), (entity) -> {
            return !entity.isSpectator() && entity.isAlive() && entity.isInteractable() && (entity != this.getShooter() || this.as >= 5) && (this.aw == null || !this.aw.contains(entity.getId()));
        });
    }

    @Override
    public void b(NBTTagCompound nbttagcompound) {
        nbttagcompound.setShort("life", (short) this.despawnCounter);
        if (this.aq != null) {
            nbttagcompound.set("inBlockState", GameProfileSerializer.a(this.aq));
        }

        nbttagcompound.setByte("shake", (byte) this.shake);
        nbttagcompound.setBoolean("inGround", this.inGround);
        nbttagcompound.setByte("pickup", (byte) this.fromPlayer.ordinal());
        nbttagcompound.setDouble("damage", this.damage);
        nbttagcompound.setBoolean("crit", this.isCritical());
        nbttagcompound.setByte("PierceLevel", this.getPierceLevel());
        if (this.shooter != null) {
            nbttagcompound.a("OwnerUUID", this.shooter);
        }

        nbttagcompound.setString("SoundEvent", IRegistry.SOUND_EVENT.getKey(this.av).toString());
        nbttagcompound.setBoolean("ShotFromCrossbow", this.r());
    }

    @Override
    public void a(NBTTagCompound nbttagcompound) {
        this.despawnCounter = nbttagcompound.getShort("life");
        if (nbttagcompound.hasKeyOfType("inBlockState", 10)) {
            this.aq = GameProfileSerializer.d(nbttagcompound.getCompound("inBlockState"));
        }

        this.shake = nbttagcompound.getByte("shake") & 255;
        this.inGround = nbttagcompound.getBoolean("inGround");
        if (nbttagcompound.hasKeyOfType("damage", 99)) {
            this.damage = nbttagcompound.getDouble("damage");
        }

        if (nbttagcompound.hasKeyOfType("pickup", 99)) {
            this.fromPlayer = EntityArrow.PickupStatus.a(nbttagcompound.getByte("pickup"));
        } else if (nbttagcompound.hasKeyOfType("player", 99)) {
            this.fromPlayer = nbttagcompound.getBoolean("player") ? EntityArrow.PickupStatus.ALLOWED : EntityArrow.PickupStatus.DISALLOWED;
        }

        this.setCritical(nbttagcompound.getBoolean("crit"));
        this.setPierceLevel(nbttagcompound.getByte("PierceLevel"));
        if (nbttagcompound.b("OwnerUUID")) {
            this.shooter = nbttagcompound.a("OwnerUUID");
        }

        if (nbttagcompound.hasKeyOfType("SoundEvent", 8)) {
            this.av = (SoundEffect) IRegistry.SOUND_EVENT.getOptional(new MinecraftKey(nbttagcompound.getString("SoundEvent"))).orElse(this.k());
        }

        this.o(nbttagcompound.getBoolean("ShotFromCrossbow"));
    }

    public void setShooter(@Nullable Entity entity) {
        this.shooter = entity == null ? null : entity.getUniqueID();
        if (entity instanceof EntityHuman) {
            this.fromPlayer = ((EntityHuman) entity).abilities.canInstantlyBuild ? EntityArrow.PickupStatus.CREATIVE_ONLY : EntityArrow.PickupStatus.ALLOWED;
        }

    }

    @Nullable
    public Entity getShooter() {
        return this.shooter != null && this.world instanceof WorldServer ? ((WorldServer) this.world).getEntity(this.shooter) : null;
    }

    @Override
    public void pickup(EntityHuman entityhuman) {
        if (!this.world.isClientSide && (this.inGround || this.v()) && this.shake <= 0) {
            boolean flag = this.fromPlayer == EntityArrow.PickupStatus.ALLOWED || this.fromPlayer == EntityArrow.PickupStatus.CREATIVE_ONLY && entityhuman.abilities.canInstantlyBuild || this.v() && this.getShooter().getUniqueID() == entityhuman.getUniqueID();

            if (this.fromPlayer == EntityArrow.PickupStatus.ALLOWED && !entityhuman.inventory.pickup(this.getItemStack())) {
                flag = false;
            }

            if (flag) {
                entityhuman.receive(this, 1);
                this.die();
            }

        }
    }

    protected abstract ItemStack getItemStack();

    @Override
    protected boolean playStepSound() {
        return false;
    }

    public void setDamage(double d0) {
        this.damage = d0;
    }

    public double getDamage() {
        return this.damage;
    }

    public void setKnockbackStrength(int i) {
        this.knockbackStrength = i;
    }

    @Override
    public boolean bA() {
        return false;
    }

    @Override
    protected float getHeadHeight(EntityPose entitypose, EntitySize entitysize) {
        return 0.0F;
    }

    public void setCritical(boolean flag) {
        this.a(1, flag);
    }

    public void setPierceLevel(byte b0) {
        this.datawatcher.set(EntityArrow.ap, b0);
    }

    private void a(int i, boolean flag) {
        byte b0 = (Byte) this.datawatcher.get(EntityArrow.ao);

        if (flag) {
            this.datawatcher.set(EntityArrow.ao, (byte) (b0 | i));
        } else {
            this.datawatcher.set(EntityArrow.ao, (byte) (b0 & ~i));
        }

    }

    public boolean isCritical() {
        byte b0 = (Byte) this.datawatcher.get(EntityArrow.ao);

        return (b0 & 1) != 0;
    }

    public boolean r() {
        byte b0 = (Byte) this.datawatcher.get(EntityArrow.ao);

        return (b0 & 4) != 0;
    }

    public byte getPierceLevel() {
        return (Byte) this.datawatcher.get(EntityArrow.ap);
    }

    public void a(EntityLiving entityliving, float f) {
        int i = EnchantmentManager.a(Enchantments.ARROW_DAMAGE, entityliving);
        int j = EnchantmentManager.a(Enchantments.ARROW_KNOCKBACK, entityliving);

        this.setDamage((double) (f * 2.0F) + this.random.nextGaussian() * 0.25D + (double) ((float) this.world.getDifficulty().a() * 0.11F));
        if (i > 0) {
            this.setDamage(this.getDamage() + (double) i * 0.5D + 0.5D);
        }

        if (j > 0) {
            this.setKnockbackStrength(j);
        }

        if (EnchantmentManager.a(Enchantments.ARROW_FIRE, entityliving) > 0) {
            this.setOnFire(100);
        }

    }

    protected float u() {
        return 0.6F;
    }

    public void n(boolean flag) {
        this.noclip = flag;
        this.a(2, flag);
    }

    public boolean v() {
        return !this.world.isClientSide ? this.noclip : ((Byte) this.datawatcher.get(EntityArrow.ao) & 2) != 0;
    }

    public void o(boolean flag) {
        this.a(4, flag);
    }

    @Override
    public Packet<?> L() {
        Entity entity = this.getShooter();

        return new PacketPlayOutSpawnEntity(this, entity == null ? 0 : entity.getId());
    }

    public static enum PickupStatus {

        DISALLOWED, ALLOWED, CREATIVE_ONLY;

        private PickupStatus() {}

        public static EntityArrow.PickupStatus a(int i) {
            if (i < 0 || i > values().length) {
                i = 0;
            }

            return values()[i];
        }
    }
}
