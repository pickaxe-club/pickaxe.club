package net.minecraft.server;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;

public class EntityShulkerBullet extends Entity {

    private EntityLiving shooter;
    private Entity target;
    @Nullable
    private EnumDirection dir;
    private int e;
    private double f;
    private double g;
    private double ao;
    @Nullable
    private UUID ap;
    private BlockPosition aq;
    @Nullable
    private UUID ar;
    private BlockPosition as;

    public EntityShulkerBullet(EntityTypes<? extends EntityShulkerBullet> entitytypes, World world) {
        super(entitytypes, world);
        this.noclip = true;
    }

    public EntityShulkerBullet(World world, EntityLiving entityliving, Entity entity, EnumDirection.EnumAxis enumdirection_enumaxis) {
        this(EntityTypes.SHULKER_BULLET, world);
        this.shooter = entityliving;
        BlockPosition blockposition = new BlockPosition(entityliving);
        double d0 = (double) blockposition.getX() + 0.5D;
        double d1 = (double) blockposition.getY() + 0.5D;
        double d2 = (double) blockposition.getZ() + 0.5D;

        this.setPositionRotation(d0, d1, d2, this.yaw, this.pitch);
        this.target = entity;
        this.dir = EnumDirection.UP;
        this.a(enumdirection_enumaxis);
    }

    @Override
    public SoundCategory getSoundCategory() {
        return SoundCategory.HOSTILE;
    }

    @Override
    protected void b(NBTTagCompound nbttagcompound) {
        BlockPosition blockposition;
        NBTTagCompound nbttagcompound1;

        if (this.shooter != null) {
            blockposition = new BlockPosition(this.shooter);
            nbttagcompound1 = GameProfileSerializer.a(this.shooter.getUniqueID());
            nbttagcompound1.setInt("X", blockposition.getX());
            nbttagcompound1.setInt("Y", blockposition.getY());
            nbttagcompound1.setInt("Z", blockposition.getZ());
            nbttagcompound.set("Owner", nbttagcompound1);
        }

        if (this.target != null) {
            blockposition = new BlockPosition(this.target);
            nbttagcompound1 = GameProfileSerializer.a(this.target.getUniqueID());
            nbttagcompound1.setInt("X", blockposition.getX());
            nbttagcompound1.setInt("Y", blockposition.getY());
            nbttagcompound1.setInt("Z", blockposition.getZ());
            nbttagcompound.set("Target", nbttagcompound1);
        }

        if (this.dir != null) {
            nbttagcompound.setInt("Dir", this.dir.b());
        }

        nbttagcompound.setInt("Steps", this.e);
        nbttagcompound.setDouble("TXD", this.f);
        nbttagcompound.setDouble("TYD", this.g);
        nbttagcompound.setDouble("TZD", this.ao);
    }

    @Override
    protected void a(NBTTagCompound nbttagcompound) {
        this.e = nbttagcompound.getInt("Steps");
        this.f = nbttagcompound.getDouble("TXD");
        this.g = nbttagcompound.getDouble("TYD");
        this.ao = nbttagcompound.getDouble("TZD");
        if (nbttagcompound.hasKeyOfType("Dir", 99)) {
            this.dir = EnumDirection.fromType1(nbttagcompound.getInt("Dir"));
        }

        NBTTagCompound nbttagcompound1;

        if (nbttagcompound.hasKeyOfType("Owner", 10)) {
            nbttagcompound1 = nbttagcompound.getCompound("Owner");
            this.ap = GameProfileSerializer.b(nbttagcompound1);
            this.aq = new BlockPosition(nbttagcompound1.getInt("X"), nbttagcompound1.getInt("Y"), nbttagcompound1.getInt("Z"));
        }

        if (nbttagcompound.hasKeyOfType("Target", 10)) {
            nbttagcompound1 = nbttagcompound.getCompound("Target");
            this.ar = GameProfileSerializer.b(nbttagcompound1);
            this.as = new BlockPosition(nbttagcompound1.getInt("X"), nbttagcompound1.getInt("Y"), nbttagcompound1.getInt("Z"));
        }

    }

    @Override
    protected void initDatawatcher() {}

    private void a(@Nullable EnumDirection enumdirection) {
        this.dir = enumdirection;
    }

    private void a(@Nullable EnumDirection.EnumAxis enumdirection_enumaxis) {
        double d0 = 0.5D;
        BlockPosition blockposition;

        if (this.target == null) {
            blockposition = (new BlockPosition(this)).down();
        } else {
            d0 = (double) this.target.getHeight() * 0.5D;
            blockposition = new BlockPosition(this.target.locX(), this.target.locY() + d0, this.target.locZ());
        }

        double d1 = (double) blockposition.getX() + 0.5D;
        double d2 = (double) blockposition.getY() + d0;
        double d3 = (double) blockposition.getZ() + 0.5D;
        EnumDirection enumdirection = null;

        if (!blockposition.a((IPosition) this.getPositionVector(), 2.0D)) {
            BlockPosition blockposition1 = new BlockPosition(this);
            List<EnumDirection> list = Lists.newArrayList();

            if (enumdirection_enumaxis != EnumDirection.EnumAxis.X) {
                if (blockposition1.getX() < blockposition.getX() && this.world.isEmpty(blockposition1.east())) {
                    list.add(EnumDirection.EAST);
                } else if (blockposition1.getX() > blockposition.getX() && this.world.isEmpty(blockposition1.west())) {
                    list.add(EnumDirection.WEST);
                }
            }

            if (enumdirection_enumaxis != EnumDirection.EnumAxis.Y) {
                if (blockposition1.getY() < blockposition.getY() && this.world.isEmpty(blockposition1.up())) {
                    list.add(EnumDirection.UP);
                } else if (blockposition1.getY() > blockposition.getY() && this.world.isEmpty(blockposition1.down())) {
                    list.add(EnumDirection.DOWN);
                }
            }

            if (enumdirection_enumaxis != EnumDirection.EnumAxis.Z) {
                if (blockposition1.getZ() < blockposition.getZ() && this.world.isEmpty(blockposition1.south())) {
                    list.add(EnumDirection.SOUTH);
                } else if (blockposition1.getZ() > blockposition.getZ() && this.world.isEmpty(blockposition1.north())) {
                    list.add(EnumDirection.NORTH);
                }
            }

            enumdirection = EnumDirection.a(this.random);
            if (list.isEmpty()) {
                for (int i = 5; !this.world.isEmpty(blockposition1.shift(enumdirection)) && i > 0; --i) {
                    enumdirection = EnumDirection.a(this.random);
                }
            } else {
                enumdirection = (EnumDirection) list.get(this.random.nextInt(list.size()));
            }

            d1 = this.locX() + (double) enumdirection.getAdjacentX();
            d2 = this.locY() + (double) enumdirection.getAdjacentY();
            d3 = this.locZ() + (double) enumdirection.getAdjacentZ();
        }

        this.a(enumdirection);
        double d4 = d1 - this.locX();
        double d5 = d2 - this.locY();
        double d6 = d3 - this.locZ();
        double d7 = (double) MathHelper.sqrt(d4 * d4 + d5 * d5 + d6 * d6);

        if (d7 == 0.0D) {
            this.f = 0.0D;
            this.g = 0.0D;
            this.ao = 0.0D;
        } else {
            this.f = d4 / d7 * 0.15D;
            this.g = d5 / d7 * 0.15D;
            this.ao = d6 / d7 * 0.15D;
        }

        this.impulse = true;
        this.e = 10 + this.random.nextInt(5) * 10;
    }

    @Override
    public void checkDespawn() {
        if (this.world.getDifficulty() == EnumDifficulty.PEACEFUL) {
            this.die();
        }

    }

    @Override
    public void tick() {
        super.tick();
        Vec3D vec3d;

        if (!this.world.isClientSide) {
            List list;
            Iterator iterator;
            EntityLiving entityliving;

            if (this.target == null && this.ar != null) {
                list = this.world.a(EntityLiving.class, new AxisAlignedBB(this.as.b(-2, -2, -2), this.as.b(2, 2, 2)));
                iterator = list.iterator();

                while (iterator.hasNext()) {
                    entityliving = (EntityLiving) iterator.next();
                    if (entityliving.getUniqueID().equals(this.ar)) {
                        this.target = entityliving;
                        break;
                    }
                }

                this.ar = null;
            }

            if (this.shooter == null && this.ap != null) {
                list = this.world.a(EntityLiving.class, new AxisAlignedBB(this.aq.b(-2, -2, -2), this.aq.b(2, 2, 2)));
                iterator = list.iterator();

                while (iterator.hasNext()) {
                    entityliving = (EntityLiving) iterator.next();
                    if (entityliving.getUniqueID().equals(this.ap)) {
                        this.shooter = entityliving;
                        break;
                    }
                }

                this.ap = null;
            }

            if (this.target != null && this.target.isAlive() && (!(this.target instanceof EntityHuman) || !((EntityHuman) this.target).isSpectator())) {
                this.f = MathHelper.a(this.f * 1.025D, -1.0D, 1.0D);
                this.g = MathHelper.a(this.g * 1.025D, -1.0D, 1.0D);
                this.ao = MathHelper.a(this.ao * 1.025D, -1.0D, 1.0D);
                vec3d = this.getMot();
                this.setMot(vec3d.add((this.f - vec3d.x) * 0.2D, (this.g - vec3d.y) * 0.2D, (this.ao - vec3d.z) * 0.2D));
            } else if (!this.isNoGravity()) {
                this.setMot(this.getMot().add(0.0D, -0.04D, 0.0D));
            }

            MovingObjectPosition movingobjectposition = ProjectileHelper.a(this, true, false, this.shooter, RayTrace.BlockCollisionOption.COLLIDER);

            if (movingobjectposition.getType() != MovingObjectPosition.EnumMovingObjectType.MISS) {
                this.a(movingobjectposition);
            }
        }

        vec3d = this.getMot();
        this.setPosition(this.locX() + vec3d.x, this.locY() + vec3d.y, this.locZ() + vec3d.z);
        ProjectileHelper.a(this, 0.5F);
        if (this.world.isClientSide) {
            this.world.addParticle(Particles.END_ROD, this.locX() - vec3d.x, this.locY() - vec3d.y + 0.15D, this.locZ() - vec3d.z, 0.0D, 0.0D, 0.0D);
        } else if (this.target != null && !this.target.dead) {
            if (this.e > 0) {
                --this.e;
                if (this.e == 0) {
                    this.a(this.dir == null ? null : this.dir.m());
                }
            }

            if (this.dir != null) {
                BlockPosition blockposition = new BlockPosition(this);
                EnumDirection.EnumAxis enumdirection_enumaxis = this.dir.m();

                if (this.world.a(blockposition.shift(this.dir), (Entity) this)) {
                    this.a(enumdirection_enumaxis);
                } else {
                    BlockPosition blockposition1 = new BlockPosition(this.target);

                    if (enumdirection_enumaxis == EnumDirection.EnumAxis.X && blockposition.getX() == blockposition1.getX() || enumdirection_enumaxis == EnumDirection.EnumAxis.Z && blockposition.getZ() == blockposition1.getZ() || enumdirection_enumaxis == EnumDirection.EnumAxis.Y && blockposition.getY() == blockposition1.getY()) {
                        this.a(enumdirection_enumaxis);
                    }
                }
            }
        }

    }

    @Override
    public boolean isBurning() {
        return false;
    }

    @Override
    public float aI() {
        return 1.0F;
    }

    protected void a(MovingObjectPosition movingobjectposition) {
        if (movingobjectposition.getType() == MovingObjectPosition.EnumMovingObjectType.ENTITY) {
            Entity entity = ((MovingObjectPositionEntity) movingobjectposition).getEntity();
            boolean flag = entity.damageEntity(DamageSource.a(this, this.shooter).c(), 4.0F);

            if (flag) {
                this.a(this.shooter, entity);
                if (entity instanceof EntityLiving) {
                    ((EntityLiving) entity).addEffect(new MobEffect(MobEffects.LEVITATION, 200));
                }
            }
        } else {
            ((WorldServer) this.world).a(Particles.EXPLOSION, this.locX(), this.locY(), this.locZ(), 2, 0.2D, 0.2D, 0.2D, 0.0D);
            this.a(SoundEffects.ENTITY_SHULKER_BULLET_HIT, 1.0F, 1.0F);
        }

        this.die();
    }

    @Override
    public boolean isInteractable() {
        return true;
    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        if (!this.world.isClientSide) {
            this.a(SoundEffects.ENTITY_SHULKER_BULLET_HURT, 1.0F, 1.0F);
            ((WorldServer) this.world).a(Particles.CRIT, this.locX(), this.locY(), this.locZ(), 15, 0.2D, 0.2D, 0.2D, 0.0D);
            this.die();
        }

        return true;
    }

    @Override
    public Packet<?> L() {
        return new PacketPlayOutSpawnEntity(this);
    }
}
