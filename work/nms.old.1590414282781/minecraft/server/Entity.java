package net.minecraft.server;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// CraftBukkit start
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.vehicle.VehicleBlockCollisionEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.entity.Pose;
import org.bukkit.event.entity.EntityAirChangeEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.entity.EntityPoseChangeEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.PluginManager;
// CraftBukkit end

public abstract class Entity implements INamableTileEntity, ICommandListener {

    // CraftBukkit start
    private static final int CURRENT_LEVEL = 2;
    static boolean isLevelAtLeast(NBTTagCompound tag, int level) {
        return tag.hasKey("Bukkit.updateLevel") && tag.getInt("Bukkit.updateLevel") >= level;
    }

    private CraftEntity bukkitEntity;

    public CraftEntity getBukkitEntity() {
        if (bukkitEntity == null) {
            bukkitEntity = CraftEntity.getEntity(world.getServer(), this);
        }
        return bukkitEntity;
    }

    @Override
    public CommandSender getBukkitSender(CommandListenerWrapper wrapper) {
        return getBukkitEntity();
    }
    // CraftBukkit end

    protected static final Logger LOGGER = LogManager.getLogger();
    private static final AtomicInteger entityCount = new AtomicInteger();
    private static final List<ItemStack> c = Collections.emptyList();
    private static final AxisAlignedBB d = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
    private static double e = 1.0D;
    private final EntityTypes<?> f;
    private int id;
    public boolean i;
    public final List<Entity> passengers;
    protected int j;
    @Nullable
    private Entity vehicle;
    public boolean attachedToPlayer;
    public World world;
    public double lastX;
    public double lastY;
    public double lastZ;
    private double locX;
    private double locY;
    private double locZ;
    private Vec3D mot;
    public float yaw;
    public float pitch;
    public float lastYaw;
    public float lastPitch;
    private AxisAlignedBB boundingBox;
    public boolean onGround;
    public boolean positionChanged;
    public boolean v;
    public boolean w;
    public boolean velocityChanged;
    protected Vec3D y;
    public boolean dead;
    public float A;
    public float B;
    public float C;
    public float fallDistance;
    private float av;
    private float aw;
    public double E;
    public double F;
    public double G;
    public float H;
    public boolean noclip;
    public float J;
    protected final Random random;
    public int ticksLived;
    public int fireTicks;
    public boolean inWater;
    protected double N;
    protected boolean O;
    protected boolean inLava;
    public int noDamageTicks;
    protected boolean justCreated;
    protected final DataWatcher datawatcher;
    protected static final DataWatcherObject<Byte> T = DataWatcher.a(Entity.class, DataWatcherRegistry.a);
    private static final DataWatcherObject<Integer> AIR_TICKS = DataWatcher.a(Entity.class, DataWatcherRegistry.b);
    private static final DataWatcherObject<Optional<IChatBaseComponent>> az = DataWatcher.a(Entity.class, DataWatcherRegistry.f);
    private static final DataWatcherObject<Boolean> aA = DataWatcher.a(Entity.class, DataWatcherRegistry.i);
    private static final DataWatcherObject<Boolean> aB = DataWatcher.a(Entity.class, DataWatcherRegistry.i);
    private static final DataWatcherObject<Boolean> aC = DataWatcher.a(Entity.class, DataWatcherRegistry.i);
    protected static final DataWatcherObject<EntityPose> POSE = DataWatcher.a(Entity.class, DataWatcherRegistry.s);
    public boolean inChunk;
    public int chunkX;
    public int chunkY;
    public int chunkZ;
    public long Z;
    public long aa;
    public long ab;
    public boolean ac;
    public boolean impulse;
    public int portalCooldown;
    protected boolean af;
    protected int ag;
    public DimensionManager dimension;
    protected BlockPosition ai;
    protected Vec3D aj;
    protected EnumDirection ak;
    private boolean invulnerable;
    protected UUID uniqueID;
    protected String am;
    public boolean glowing;
    private final Set<String> aE;
    private boolean aF;
    private final double[] aG;
    private long aH;
    private EntitySize size;
    private float headHeight;
    // CraftBukkit start
    public boolean persist = true;
    public boolean valid;
    public org.bukkit.projectiles.ProjectileSource projectileSource; // For projectiles only
    public boolean forceExplosionKnockback; // SPIGOT-949

    public float getBukkitYaw() {
        return this.yaw;
    }

    public boolean isChunkLoaded() {
        return world.isChunkLoaded((int) Math.floor(this.locX) >> 4, (int) Math.floor(this.locZ) >> 4);
    }
    // CraftBukkit end

    public Entity(EntityTypes<?> entitytypes, World world) {
        this.id = Entity.entityCount.incrementAndGet();
        this.passengers = Lists.newArrayList();
        this.mot = Vec3D.a;
        this.boundingBox = Entity.d;
        this.y = Vec3D.a;
        this.av = 1.0F;
        this.aw = 1.0F;
        this.random = new Random();
        this.fireTicks = -this.getMaxFireTicks();
        this.justCreated = true;
        this.uniqueID = MathHelper.a(this.random);
        this.am = this.uniqueID.toString();
        this.aE = Sets.newHashSet();
        this.aG = new double[]{0.0D, 0.0D, 0.0D};
        this.f = entitytypes;
        this.world = world;
        this.size = entitytypes.k();
        this.setPosition(0.0D, 0.0D, 0.0D);
        if (world != null) {
            this.dimension = world.worldProvider.getDimensionManager();
        }

        this.datawatcher = new DataWatcher(this);
        this.datawatcher.register(Entity.T, (byte) 0);
        this.datawatcher.register(Entity.AIR_TICKS, this.bw());
        this.datawatcher.register(Entity.aA, false);
        this.datawatcher.register(Entity.az, Optional.empty());
        this.datawatcher.register(Entity.aB, false);
        this.datawatcher.register(Entity.aC, false);
        this.datawatcher.register(Entity.POSE, EntityPose.STANDING);
        this.initDatawatcher();
        this.headHeight = this.getHeadHeight(EntityPose.STANDING, this.size);
    }

    public boolean isSpectator() {
        return false;
    }

    public final void decouple() {
        if (this.isVehicle()) {
            this.ejectPassengers();
        }

        if (this.isPassenger()) {
            this.stopRiding();
        }

    }

    public void c(double d0, double d1, double d2) {
        this.Z = PacketPlayOutEntity.a(d0);
        this.aa = PacketPlayOutEntity.a(d1);
        this.ab = PacketPlayOutEntity.a(d2);
    }

    public EntityTypes<?> getEntityType() {
        return this.f;
    }

    public int getId() {
        return this.id;
    }

    public void e(int i) {
        this.id = i;
    }

    public Set<String> getScoreboardTags() {
        return this.aE;
    }

    public boolean addScoreboardTag(String s) {
        return this.aE.size() >= 1024 ? false : this.aE.add(s);
    }

    public boolean removeScoreboardTag(String s) {
        return this.aE.remove(s);
    }

    public void killEntity() {
        this.die();
    }

    protected abstract void initDatawatcher();

    public DataWatcher getDataWatcher() {
        return this.datawatcher;
    }

    public boolean equals(Object object) {
        return object instanceof Entity ? ((Entity) object).id == this.id : false;
    }

    public int hashCode() {
        return this.id;
    }

    public void die() {
        this.dead = true;
    }

    protected void setPose(EntityPose entitypose) {
        // CraftBukkit start
        if (entitypose == this.getPose()) {
            return;
        }
        this.world.getServer().getPluginManager().callEvent(new EntityPoseChangeEvent(this.getBukkitEntity(), Pose.values()[entitypose.ordinal()]));
        // CraftBukkit end
        this.datawatcher.set(Entity.POSE, entitypose);
    }

    public EntityPose getPose() {
        return (EntityPose) this.datawatcher.get(Entity.POSE);
    }

    protected void setYawPitch(float f, float f1) {
        // CraftBukkit start - yaw was sometimes set to NaN, so we need to set it back to 0
        if (Float.isNaN(f)) {
            f = 0;
        }

        if (f == Float.POSITIVE_INFINITY || f == Float.NEGATIVE_INFINITY) {
            if (this instanceof EntityPlayer) {
                this.world.getServer().getLogger().warning(this.getName() + " was caught trying to crash the server with an invalid yaw");
                ((CraftPlayer) this.getBukkitEntity()).kickPlayer("Infinite yaw (Hacking?)");
            }
            f = 0;
        }

        // pitch was sometimes set to NaN, so we need to set it back to 0
        if (Float.isNaN(f1)) {
            f1 = 0;
        }

        if (f1 == Float.POSITIVE_INFINITY || f1 == Float.NEGATIVE_INFINITY) {
            if (this instanceof EntityPlayer) {
                this.world.getServer().getLogger().warning(this.getName() + " was caught trying to crash the server with an invalid pitch");
                ((CraftPlayer) this.getBukkitEntity()).kickPlayer("Infinite pitch (Hacking?)");
            }
            f1 = 0;
        }
        // CraftBukkit end

        this.yaw = f % 360.0F;
        this.pitch = f1 % 360.0F;
    }

    public void setPosition(double d0, double d1, double d2) {
        this.setPositionRaw(d0, d1, d2);
        float f = this.size.width / 2.0F;
        float f1 = this.size.height;

        this.a(new AxisAlignedBB(d0 - (double) f, d1, d2 - (double) f, d0 + (double) f, d1 + (double) f1, d2 + (double) f));
        if (valid) ((WorldServer) world).chunkCheck(this); // CraftBukkit
    }

    protected void Z() {
        this.setPosition(this.locX, this.locY, this.locZ);
    }

    public void tick() {
        if (!this.world.isClientSide) {
            this.setFlag(6, this.bt());
        }

        this.entityBaseTick();
    }

    // CraftBukkit start
    public void postTick() {
        // No clean way to break out of ticking once the entity has been copied to a new world, so instead we move the portalling later in the tick cycle
        if (!(this instanceof EntityPlayer)) {
            this.doPortalTick();
        }
    }
    // CraftBukkit end

    public void entityBaseTick() {
        this.world.getMethodProfiler().enter("entityBaseTick");
        if (this.isPassenger() && this.getVehicle().dead) {
            this.stopRiding();
        }

        if (this.j > 0) {
            --this.j;
        }

        this.A = this.B;
        this.lastPitch = this.pitch;
        this.lastYaw = this.yaw;
        if (this instanceof EntityPlayer) this.doPortalTick(); // CraftBukkit - // Moved up to postTick
        this.aE();
        this.m();
        if (this.world.isClientSide) {
            this.extinguish();
        } else if (this.fireTicks > 0) {
            if (this.isFireProof()) {
                this.fireTicks -= 4;
                if (this.fireTicks < 0) {
                    this.extinguish();
                }
            } else {
                if (this.fireTicks % 20 == 0) {
                    this.damageEntity(DamageSource.BURN, 1.0F);
                }

                --this.fireTicks;
            }
        }

        if (this.aH()) {
            this.burnFromLava();
            this.fallDistance *= 0.5F;
        }

        if (this.locY() < -64.0D) {
            this.af();
        }

        if (!this.world.isClientSide) {
            this.setFlag(0, this.fireTicks > 0);
        }

        this.justCreated = false;
        this.world.getMethodProfiler().exit();
    }

    protected void E() {
        if (this.portalCooldown > 0) {
            --this.portalCooldown;
        }

    }

    public int ab() {
        return 1;
    }

    protected void burnFromLava() {
        if (!this.isFireProof()) {
            // CraftBukkit start - Fallen in lava TODO: this event spams!
            if (this instanceof EntityLiving && fireTicks <= 0) {
                // not on fire yet
                // TODO: shouldn't be sending null for the block
                org.bukkit.block.Block damager = null; // ((WorldServer) this.l).getWorld().getBlockAt(i, j, k);
                org.bukkit.entity.Entity damagee = this.getBukkitEntity();
                EntityCombustEvent combustEvent = new org.bukkit.event.entity.EntityCombustByBlockEvent(damager, damagee, 15);
                this.world.getServer().getPluginManager().callEvent(combustEvent);

                if (!combustEvent.isCancelled()) {
                    this.setOnFire(combustEvent.getDuration(), false);
                }
            } else {
                // This will be called every single tick the entity is in lava, so don't throw an event
                this.setOnFire(15, false);
            }
            // CraftBukkit end - we also don't throw an event unless the object in lava is living, to save on some event calls
            this.damageEntity(DamageSource.LAVA, 4.0F);
        }
    }

    public void setOnFire(int i) {
        // CraftBukkit start
        this.setOnFire(i, true);
    }

    public void setOnFire(int i, boolean callEvent) {
        if (callEvent) {
            EntityCombustEvent event = new EntityCombustEvent(this.getBukkitEntity(), i);
            this.world.getServer().getPluginManager().callEvent(event);

            if (event.isCancelled()) {
                return;
            }

            i = event.getDuration();
        }
        // CraftBukkit end
        int j = i * 20;

        if (this instanceof EntityLiving) {
            j = EnchantmentProtection.a((EntityLiving) this, j);
        }

        if (this.fireTicks < j) {
            this.fireTicks = j;
        }

    }

    public void g(int i) {
        this.fireTicks = i;
    }

    public int ad() {
        return this.fireTicks;
    }

    public void extinguish() {
        this.fireTicks = 0;
    }

    protected void af() {
        this.die();
    }

    public boolean e(double d0, double d1, double d2) {
        return this.b(this.getBoundingBox().d(d0, d1, d2));
    }

    private boolean b(AxisAlignedBB axisalignedbb) {
        return this.world.getCubes(this, axisalignedbb) && !this.world.containsLiquid(axisalignedbb);
    }

    public void move(EnumMoveType enummovetype, Vec3D vec3d) {
        if (this.noclip) {
            this.a(this.getBoundingBox().b(vec3d));
            this.recalcPosition();
        } else {
            if (enummovetype == EnumMoveType.PISTON) {
                vec3d = this.a(vec3d);
                if (vec3d.equals(Vec3D.a)) {
                    return;
                }
            }

            this.world.getMethodProfiler().enter("move");
            if (this.y.g() > 1.0E-7D) {
                vec3d = vec3d.h(this.y);
                this.y = Vec3D.a;
                this.setMot(Vec3D.a);
            }

            vec3d = this.a(vec3d, enummovetype);
            Vec3D vec3d1 = this.e(vec3d);

            if (vec3d1.g() > 1.0E-7D) {
                this.a(this.getBoundingBox().b(vec3d1));
                this.recalcPosition();
            }

            this.world.getMethodProfiler().exit();
            this.world.getMethodProfiler().enter("rest");
            this.positionChanged = !MathHelper.b(vec3d.x, vec3d1.x) || !MathHelper.b(vec3d.z, vec3d1.z);
            this.v = vec3d.y != vec3d1.y;
            this.onGround = this.v && vec3d.y < 0.0D;
            this.w = this.positionChanged || this.v;
            BlockPosition blockposition = this.ag();
            IBlockData iblockdata = this.world.getType(blockposition);

            this.a(vec3d1.y, this.onGround, iblockdata, blockposition);
            Vec3D vec3d2 = this.getMot();

            if (vec3d.x != vec3d1.x) {
                this.setMot(0.0D, vec3d2.y, vec3d2.z);
            }

            if (vec3d.z != vec3d1.z) {
                this.setMot(vec3d2.x, vec3d2.y, 0.0D);
            }

            Block block = iblockdata.getBlock();

            if (vec3d.y != vec3d1.y) {
                block.a((IBlockAccess) this.world, this);
            }

            // CraftBukkit start
            if (positionChanged && getBukkitEntity() instanceof Vehicle) {
                Vehicle vehicle = (Vehicle) this.getBukkitEntity();
                org.bukkit.block.Block bl = this.world.getWorld().getBlockAt(MathHelper.floor(this.locX), MathHelper.floor(this.locY), MathHelper.floor(this.locZ));

                if (vec3d.x > vec3d1.x) {
                    bl = bl.getRelative(BlockFace.EAST);
                } else if (vec3d.x < vec3d1.x) {
                    bl = bl.getRelative(BlockFace.WEST);
                } else if (vec3d.z > vec3d1.z) {
                    bl = bl.getRelative(BlockFace.SOUTH);
                } else if (vec3d.z < vec3d1.z) {
                    bl = bl.getRelative(BlockFace.NORTH);
                }

                if (!bl.getType().isAir()) {
                    VehicleBlockCollisionEvent event = new VehicleBlockCollisionEvent(vehicle, bl);
                    world.getServer().getPluginManager().callEvent(event);
                }
            }
            // CraftBukkit end

            if (this.onGround && !this.bk()) {
                block.stepOn(this.world, blockposition, this);
            }

            if (this.playStepSound() && !this.isPassenger()) {
                double d0 = vec3d1.x;
                double d1 = vec3d1.y;
                double d2 = vec3d1.z;

                if (block != Blocks.LADDER && block != Blocks.SCAFFOLDING) {
                    d1 = 0.0D;
                }

                this.B = (float) ((double) this.B + (double) MathHelper.sqrt(b(vec3d1)) * 0.6D);
                this.C = (float) ((double) this.C + (double) MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2) * 0.6D);
                if (this.C > this.av && !iblockdata.isAir()) {
                    this.av = this.ak();
                    if (this.isInWater()) {
                        Entity entity = this.isVehicle() && this.getRidingPassenger() != null ? this.getRidingPassenger() : this;
                        float f = entity == this ? 0.35F : 0.4F;
                        Vec3D vec3d3 = entity.getMot();
                        float f1 = MathHelper.sqrt(vec3d3.x * vec3d3.x * 0.20000000298023224D + vec3d3.y * vec3d3.y + vec3d3.z * vec3d3.z * 0.20000000298023224D) * f;

                        if (f1 > 1.0F) {
                            f1 = 1.0F;
                        }

                        this.d(f1);
                    } else {
                        this.a(blockposition, iblockdata);
                    }
                } else if (this.C > this.aw && this.aq() && iblockdata.isAir()) {
                    this.aw = this.e(this.C);
                }
            }

            try {
                this.inLava = false;
                this.checkBlockCollisions();
            } catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.a(throwable, "Checking entity block collision");
                CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Entity being checked for collision");

                this.appendEntityCrashDetails(crashreportsystemdetails);
                throw new ReportedException(crashreport);
            }

            this.setMot(this.getMot().d((double) this.ai(), 1.0D, (double) this.ai()));
            boolean flag = this.ay();

            if (this.world.c(this.getBoundingBox().shrink(0.001D))) {
                if (!flag) {
                    ++this.fireTicks;
                    if (this.fireTicks == 0) {
                        // CraftBukkit start
                        EntityCombustEvent event = new org.bukkit.event.entity.EntityCombustByBlockEvent(null, getBukkitEntity(), 8);
                        world.getServer().getPluginManager().callEvent(event);

                        if (!event.isCancelled()) {
                            this.setOnFire(event.getDuration(), false);
                        }
                        // CraftBukkit end
                    }
                }

                this.burn(1);
            } else if (this.fireTicks <= 0) {
                this.fireTicks = -this.getMaxFireTicks();
            }

            if (flag && this.isBurning()) {
                this.a(SoundEffects.ENTITY_GENERIC_EXTINGUISH_FIRE, 0.7F, 1.6F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
                this.fireTicks = -this.getMaxFireTicks();
            }

            this.world.getMethodProfiler().exit();
        }
    }

    protected BlockPosition ag() {
        int i = MathHelper.floor(this.locX);
        int j = MathHelper.floor(this.locY - 0.20000000298023224D);
        int k = MathHelper.floor(this.locZ);
        BlockPosition blockposition = new BlockPosition(i, j, k);

        if (this.world.getType(blockposition).isAir()) {
            BlockPosition blockposition1 = blockposition.down();
            IBlockData iblockdata = this.world.getType(blockposition1);
            Block block = iblockdata.getBlock();

            if (block.a(TagsBlock.FENCES) || block.a(TagsBlock.WALLS) || block instanceof BlockFenceGate) {
                return blockposition1;
            }
        }

        return blockposition;
    }

    protected float ah() {
        float f = this.world.getType(new BlockPosition(this)).getBlock().n();
        float f1 = this.world.getType(this.aj()).getBlock().n();

        return (double) f == 1.0D ? f1 : f;
    }

    protected float ai() {
        Block block = this.world.getType(new BlockPosition(this)).getBlock();
        float f = block.m();

        return block != Blocks.WATER && block != Blocks.BUBBLE_COLUMN ? ((double) f == 1.0D ? this.world.getType(this.aj()).getBlock().m() : f) : f;
    }

    protected BlockPosition aj() {
        return new BlockPosition(this.locX, this.getBoundingBox().minY - 0.5000001D, this.locZ);
    }

    protected Vec3D a(Vec3D vec3d, EnumMoveType enummovetype) {
        return vec3d;
    }

    protected Vec3D a(Vec3D vec3d) {
        if (vec3d.g() <= 1.0E-7D) {
            return vec3d;
        } else {
            long i = this.world.getTime();

            if (i != this.aH) {
                Arrays.fill(this.aG, 0.0D);
                this.aH = i;
            }

            double d0;

            if (vec3d.x != 0.0D) {
                d0 = this.a(EnumDirection.EnumAxis.X, vec3d.x);
                return Math.abs(d0) <= 9.999999747378752E-6D ? Vec3D.a : new Vec3D(d0, 0.0D, 0.0D);
            } else if (vec3d.y != 0.0D) {
                d0 = this.a(EnumDirection.EnumAxis.Y, vec3d.y);
                return Math.abs(d0) <= 9.999999747378752E-6D ? Vec3D.a : new Vec3D(0.0D, d0, 0.0D);
            } else if (vec3d.z != 0.0D) {
                d0 = this.a(EnumDirection.EnumAxis.Z, vec3d.z);
                return Math.abs(d0) <= 9.999999747378752E-6D ? Vec3D.a : new Vec3D(0.0D, 0.0D, d0);
            } else {
                return Vec3D.a;
            }
        }
    }

    private double a(EnumDirection.EnumAxis enumdirection_enumaxis, double d0) {
        int i = enumdirection_enumaxis.ordinal();
        double d1 = MathHelper.a(d0 + this.aG[i], -0.51D, 0.51D);

        d0 = d1 - this.aG[i];
        this.aG[i] = d1;
        return d0;
    }

    private Vec3D e(Vec3D vec3d) {
        AxisAlignedBB axisalignedbb = this.getBoundingBox();
        VoxelShapeCollision voxelshapecollision = VoxelShapeCollision.a(this);
        VoxelShape voxelshape = this.world.getWorldBorder().a();
        Stream<VoxelShape> stream = VoxelShapes.c(voxelshape, VoxelShapes.a(axisalignedbb.shrink(1.0E-7D)), OperatorBoolean.AND) ? Stream.empty() : Stream.of(voxelshape);
        Stream<VoxelShape> stream1 = this.world.b(this, axisalignedbb.a(vec3d), (Set) ImmutableSet.of());
        StreamAccumulator<VoxelShape> streamaccumulator = new StreamAccumulator<>(Stream.concat(stream1, stream));
        Vec3D vec3d1 = vec3d.g() == 0.0D ? vec3d : a(this, vec3d, axisalignedbb, this.world, voxelshapecollision, streamaccumulator);
        boolean flag = vec3d.x != vec3d1.x;
        boolean flag1 = vec3d.y != vec3d1.y;
        boolean flag2 = vec3d.z != vec3d1.z;
        boolean flag3 = this.onGround || flag1 && vec3d.y < 0.0D;

        if (this.H > 0.0F && flag3 && (flag || flag2)) {
            Vec3D vec3d2 = a(this, new Vec3D(vec3d.x, (double) this.H, vec3d.z), axisalignedbb, this.world, voxelshapecollision, streamaccumulator);
            Vec3D vec3d3 = a(this, new Vec3D(0.0D, (double) this.H, 0.0D), axisalignedbb.b(vec3d.x, 0.0D, vec3d.z), this.world, voxelshapecollision, streamaccumulator);

            if (vec3d3.y < (double) this.H) {
                Vec3D vec3d4 = a(this, new Vec3D(vec3d.x, 0.0D, vec3d.z), axisalignedbb.b(vec3d3), this.world, voxelshapecollision, streamaccumulator).e(vec3d3);

                if (b(vec3d4) > b(vec3d2)) {
                    vec3d2 = vec3d4;
                }
            }

            if (b(vec3d2) > b(vec3d1)) {
                return vec3d2.e(a(this, new Vec3D(0.0D, -vec3d2.y + vec3d.y, 0.0D), axisalignedbb.b(vec3d2), this.world, voxelshapecollision, streamaccumulator));
            }
        }

        return vec3d1;
    }

    public static double b(Vec3D vec3d) {
        return vec3d.x * vec3d.x + vec3d.z * vec3d.z;
    }

    public static Vec3D a(@Nullable Entity entity, Vec3D vec3d, AxisAlignedBB axisalignedbb, World world, VoxelShapeCollision voxelshapecollision, StreamAccumulator<VoxelShape> streamaccumulator) {
        boolean flag = vec3d.x == 0.0D;
        boolean flag1 = vec3d.y == 0.0D;
        boolean flag2 = vec3d.z == 0.0D;

        if ((!flag || !flag1) && (!flag || !flag2) && (!flag1 || !flag2)) {
            StreamAccumulator<VoxelShape> streamaccumulator1 = new StreamAccumulator<>(Stream.concat(streamaccumulator.a(), world.b(entity, axisalignedbb.a(vec3d))));

            return a(vec3d, axisalignedbb, streamaccumulator1);
        } else {
            return a(vec3d, axisalignedbb, world, voxelshapecollision, streamaccumulator);
        }
    }

    public static Vec3D a(Vec3D vec3d, AxisAlignedBB axisalignedbb, StreamAccumulator<VoxelShape> streamaccumulator) {
        double d0 = vec3d.x;
        double d1 = vec3d.y;
        double d2 = vec3d.z;

        if (d1 != 0.0D) {
            d1 = VoxelShapes.a(EnumDirection.EnumAxis.Y, axisalignedbb, streamaccumulator.a(), d1);
            if (d1 != 0.0D) {
                axisalignedbb = axisalignedbb.d(0.0D, d1, 0.0D);
            }
        }

        boolean flag = Math.abs(d0) < Math.abs(d2);

        if (flag && d2 != 0.0D) {
            d2 = VoxelShapes.a(EnumDirection.EnumAxis.Z, axisalignedbb, streamaccumulator.a(), d2);
            if (d2 != 0.0D) {
                axisalignedbb = axisalignedbb.d(0.0D, 0.0D, d2);
            }
        }

        if (d0 != 0.0D) {
            d0 = VoxelShapes.a(EnumDirection.EnumAxis.X, axisalignedbb, streamaccumulator.a(), d0);
            if (!flag && d0 != 0.0D) {
                axisalignedbb = axisalignedbb.d(d0, 0.0D, 0.0D);
            }
        }

        if (!flag && d2 != 0.0D) {
            d2 = VoxelShapes.a(EnumDirection.EnumAxis.Z, axisalignedbb, streamaccumulator.a(), d2);
        }

        return new Vec3D(d0, d1, d2);
    }

    public static Vec3D a(Vec3D vec3d, AxisAlignedBB axisalignedbb, IWorldReader iworldreader, VoxelShapeCollision voxelshapecollision, StreamAccumulator<VoxelShape> streamaccumulator) {
        double d0 = vec3d.x;
        double d1 = vec3d.y;
        double d2 = vec3d.z;

        if (d1 != 0.0D) {
            d1 = VoxelShapes.a(EnumDirection.EnumAxis.Y, axisalignedbb, iworldreader, d1, voxelshapecollision, streamaccumulator.a());
            if (d1 != 0.0D) {
                axisalignedbb = axisalignedbb.d(0.0D, d1, 0.0D);
            }
        }

        boolean flag = Math.abs(d0) < Math.abs(d2);

        if (flag && d2 != 0.0D) {
            d2 = VoxelShapes.a(EnumDirection.EnumAxis.Z, axisalignedbb, iworldreader, d2, voxelshapecollision, streamaccumulator.a());
            if (d2 != 0.0D) {
                axisalignedbb = axisalignedbb.d(0.0D, 0.0D, d2);
            }
        }

        if (d0 != 0.0D) {
            d0 = VoxelShapes.a(EnumDirection.EnumAxis.X, axisalignedbb, iworldreader, d0, voxelshapecollision, streamaccumulator.a());
            if (!flag && d0 != 0.0D) {
                axisalignedbb = axisalignedbb.d(d0, 0.0D, 0.0D);
            }
        }

        if (!flag && d2 != 0.0D) {
            d2 = VoxelShapes.a(EnumDirection.EnumAxis.Z, axisalignedbb, iworldreader, d2, voxelshapecollision, streamaccumulator.a());
        }

        return new Vec3D(d0, d1, d2);
    }

    protected float ak() {
        return (float) ((int) this.C + 1);
    }

    public void recalcPosition() {
        AxisAlignedBB axisalignedbb = this.getBoundingBox();

        this.setPositionRaw((axisalignedbb.minX + axisalignedbb.maxX) / 2.0D, axisalignedbb.minY, (axisalignedbb.minZ + axisalignedbb.maxZ) / 2.0D);
        if (valid) ((WorldServer) world).chunkCheck(this); // CraftBukkit
    }

    protected SoundEffect getSoundSwim() {
        return SoundEffects.ENTITY_GENERIC_SWIM;
    }

    protected SoundEffect getSoundSplash() {
        return SoundEffects.ENTITY_GENERIC_SPLASH;
    }

    protected SoundEffect getSoundSplashHighSpeed() {
        return SoundEffects.ENTITY_GENERIC_SPLASH;
    }

    protected void checkBlockCollisions() {
        AxisAlignedBB axisalignedbb = this.getBoundingBox();
        BlockPosition.PooledBlockPosition blockposition_pooledblockposition = BlockPosition.PooledBlockPosition.d(axisalignedbb.minX + 0.001D, axisalignedbb.minY + 0.001D, axisalignedbb.minZ + 0.001D);
        Throwable throwable = null;

        try {
            BlockPosition.PooledBlockPosition blockposition_pooledblockposition1 = BlockPosition.PooledBlockPosition.d(axisalignedbb.maxX - 0.001D, axisalignedbb.maxY - 0.001D, axisalignedbb.maxZ - 0.001D);
            Throwable throwable1 = null;

            try {
                BlockPosition.PooledBlockPosition blockposition_pooledblockposition2 = BlockPosition.PooledBlockPosition.r();
                Throwable throwable2 = null;

                try {
                    if (this.world.areChunksLoadedBetween(blockposition_pooledblockposition, blockposition_pooledblockposition1)) {
                        for (int i = blockposition_pooledblockposition.getX(); i <= blockposition_pooledblockposition1.getX(); ++i) {
                            for (int j = blockposition_pooledblockposition.getY(); j <= blockposition_pooledblockposition1.getY(); ++j) {
                                for (int k = blockposition_pooledblockposition.getZ(); k <= blockposition_pooledblockposition1.getZ(); ++k) {
                                    blockposition_pooledblockposition2.d(i, j, k);
                                    IBlockData iblockdata = this.world.getType(blockposition_pooledblockposition2);

                                    try {
                                        iblockdata.a(this.world, blockposition_pooledblockposition2, this);
                                        this.a(iblockdata);
                                    } catch (Throwable throwable3) {
                                        CrashReport crashreport = CrashReport.a(throwable3, "Colliding entity with block");
                                        CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Block being collided with");

                                        CrashReportSystemDetails.a(crashreportsystemdetails, blockposition_pooledblockposition2, iblockdata);
                                        throw new ReportedException(crashreport);
                                    }
                                }
                            }
                        }
                    }
                } catch (Throwable throwable4) {
                    throwable2 = throwable4;
                    throw throwable4;
                } finally {
                    if (blockposition_pooledblockposition2 != null) {
                        if (throwable2 != null) {
                            try {
                                blockposition_pooledblockposition2.close();
                            } catch (Throwable throwable5) {
                                throwable2.addSuppressed(throwable5);
                            }
                        } else {
                            blockposition_pooledblockposition2.close();
                        }
                    }

                }
            } catch (Throwable throwable6) {
                throwable1 = throwable6;
                throw throwable6;
            } finally {
                if (blockposition_pooledblockposition1 != null) {
                    if (throwable1 != null) {
                        try {
                            blockposition_pooledblockposition1.close();
                        } catch (Throwable throwable7) {
                            throwable1.addSuppressed(throwable7);
                        }
                    } else {
                        blockposition_pooledblockposition1.close();
                    }
                }

            }
        } catch (Throwable throwable8) {
            throwable = throwable8;
            throw throwable8;
        } finally {
            if (blockposition_pooledblockposition != null) {
                if (throwable != null) {
                    try {
                        blockposition_pooledblockposition.close();
                    } catch (Throwable throwable9) {
                        throwable.addSuppressed(throwable9);
                    }
                } else {
                    blockposition_pooledblockposition.close();
                }
            }

        }

    }

    protected void a(IBlockData iblockdata) {}

    protected void a(BlockPosition blockposition, IBlockData iblockdata) {
        if (!iblockdata.getMaterial().isLiquid()) {
            IBlockData iblockdata1 = this.world.getType(blockposition.up());
            SoundEffectType soundeffecttype = iblockdata1.getBlock() == Blocks.SNOW ? iblockdata1.r() : iblockdata.r();

            this.a(soundeffecttype.d(), soundeffecttype.a() * 0.15F, soundeffecttype.b());
        }
    }

    protected void d(float f) {
        this.a(this.getSoundSwim(), f, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
    }

    protected float e(float f) {
        return 0.0F;
    }

    protected boolean aq() {
        return false;
    }

    public void a(SoundEffect soundeffect, float f, float f1) {
        if (!this.isSilent()) {
            this.world.playSound((EntityHuman) null, this.locX(), this.locY(), this.locZ(), soundeffect, this.getSoundCategory(), f, f1);
        }

    }

    public boolean isSilent() {
        return (Boolean) this.datawatcher.get(Entity.aB);
    }

    public void setSilent(boolean flag) {
        this.datawatcher.set(Entity.aB, flag);
    }

    public boolean isNoGravity() {
        return (Boolean) this.datawatcher.get(Entity.aC);
    }

    public void setNoGravity(boolean flag) {
        this.datawatcher.set(Entity.aC, flag);
    }

    protected boolean playStepSound() {
        return true;
    }

    protected void a(double d0, boolean flag, IBlockData iblockdata, BlockPosition blockposition) {
        if (flag) {
            if (this.fallDistance > 0.0F) {
                iblockdata.getBlock().fallOn(this.world, blockposition, this, this.fallDistance);
            }

            this.fallDistance = 0.0F;
        } else if (d0 < 0.0D) {
            this.fallDistance = (float) ((double) this.fallDistance - d0);
        }

    }

    @Nullable
    public AxisAlignedBB au() {
        return null;
    }

    protected void burn(float i) { // CraftBukkit - int -> float
        if (!this.isFireProof()) {
            this.damageEntity(DamageSource.FIRE, (float) i);
        }

    }

    public final boolean isFireProof() {
        return this.getEntityType().c();
    }

    public boolean b(float f, float f1) {
        if (this.isVehicle()) {
            Iterator iterator = this.getPassengers().iterator();

            while (iterator.hasNext()) {
                Entity entity = (Entity) iterator.next();

                entity.b(f, f1);
            }
        }

        return false;
    }

    public boolean isInWater() {
        return this.inWater;
    }

    private boolean isInRain() {
        BlockPosition.PooledBlockPosition blockposition_pooledblockposition = BlockPosition.PooledBlockPosition.b(this);
        Throwable throwable = null;

        boolean flag;

        try {
            flag = this.world.isRainingAt(blockposition_pooledblockposition) || this.world.isRainingAt(blockposition_pooledblockposition.c(this.locX(), this.locY() + (double) this.size.height, this.locZ()));
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

    private boolean l() {
        return this.world.getType(new BlockPosition(this)).getBlock() == Blocks.BUBBLE_COLUMN;
    }

    public boolean isInWaterOrRain() {
        return this.isInWater() || this.isInRain();
    }

    public boolean ay() {
        return this.isInWater() || this.isInRain() || this.l();
    }

    public boolean az() {
        return this.isInWater() || this.l();
    }

    public boolean aA() {
        return this.O && this.isInWater();
    }

    private void m() {
        this.aC();
        this.n();
        this.aB();
    }

    public void aB() {
        if (this.isSwimming()) {
            this.setSwimming(this.isSprinting() && this.isInWater() && !this.isPassenger());
        } else {
            this.setSwimming(this.isSprinting() && this.aA() && !this.isPassenger());
        }

    }

    public boolean aC() {
        if (this.getVehicle() instanceof EntityBoat) {
            this.inWater = false;
        } else if (this.b(TagsFluid.WATER)) {
            if (!this.inWater && !this.justCreated) {
                this.aD();
            }

            this.fallDistance = 0.0F;
            this.inWater = true;
            this.extinguish();
        } else {
            this.inWater = false;
        }

        return this.inWater;
    }

    private void n() {
        this.O = this.a(TagsFluid.WATER, true);
    }

    protected void aD() {
        Entity entity = this.isVehicle() && this.getRidingPassenger() != null ? this.getRidingPassenger() : this;
        float f = entity == this ? 0.2F : 0.9F;
        Vec3D vec3d = entity.getMot();
        float f1 = MathHelper.sqrt(vec3d.x * vec3d.x * 0.20000000298023224D + vec3d.y * vec3d.y + vec3d.z * vec3d.z * 0.20000000298023224D) * f;

        if (f1 > 1.0F) {
            f1 = 1.0F;
        }

        if ((double) f1 < 0.25D) {
            this.a(this.getSoundSplash(), f1, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
        } else {
            this.a(this.getSoundSplashHighSpeed(), f1, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
        }

        float f2 = (float) MathHelper.floor(this.locY());

        float f3;
        float f4;
        int i;

        for (i = 0; (float) i < 1.0F + this.size.width * 20.0F; ++i) {
            f3 = (this.random.nextFloat() * 2.0F - 1.0F) * this.size.width;
            f4 = (this.random.nextFloat() * 2.0F - 1.0F) * this.size.width;
            this.world.addParticle(Particles.BUBBLE, this.locX() + (double) f3, (double) (f2 + 1.0F), this.locZ() + (double) f4, vec3d.x, vec3d.y - (double) (this.random.nextFloat() * 0.2F), vec3d.z);
        }

        for (i = 0; (float) i < 1.0F + this.size.width * 20.0F; ++i) {
            f3 = (this.random.nextFloat() * 2.0F - 1.0F) * this.size.width;
            f4 = (this.random.nextFloat() * 2.0F - 1.0F) * this.size.width;
            this.world.addParticle(Particles.SPLASH, this.locX() + (double) f3, (double) (f2 + 1.0F), this.locZ() + (double) f4, vec3d.x, vec3d.y, vec3d.z);
        }

    }

    public void aE() {
        if (this.isSprinting() && !this.isInWater()) {
            this.aF();
        }

    }

    protected void aF() {
        int i = MathHelper.floor(this.locX());
        int j = MathHelper.floor(this.locY() - 0.20000000298023224D);
        int k = MathHelper.floor(this.locZ());
        BlockPosition blockposition = new BlockPosition(i, j, k);
        IBlockData iblockdata = this.world.getType(blockposition);

        if (iblockdata.j() != EnumRenderType.INVISIBLE) {
            Vec3D vec3d = this.getMot();

            this.world.addParticle(new ParticleParamBlock(Particles.BLOCK, iblockdata), this.locX() + ((double) this.random.nextFloat() - 0.5D) * (double) this.size.width, this.locY() + 0.1D, this.locZ() + ((double) this.random.nextFloat() - 0.5D) * (double) this.size.width, vec3d.x * -4.0D, 1.5D, vec3d.z * -4.0D);
        }

    }

    public boolean a(Tag<FluidType> tag) {
        return this.a(tag, false);
    }

    public boolean a(Tag<FluidType> tag, boolean flag) {
        if (this.getVehicle() instanceof EntityBoat) {
            return false;
        } else {
            double d0 = this.getHeadY();
            BlockPosition blockposition = new BlockPosition(this.locX(), d0, this.locZ());

            if (flag && !this.world.isChunkLoaded(blockposition.getX() >> 4, blockposition.getZ() >> 4)) {
                return false;
            } else {
                Fluid fluid = this.world.getFluid(blockposition);

                return fluid.a(tag) && d0 < (double) ((float) blockposition.getY() + fluid.getHeight(this.world, blockposition) + 0.11111111F);
            }
        }
    }

    public void aG() {
        this.inLava = true;
    }

    public boolean aH() {
        return this.inLava;
    }

    public void a(float f, Vec3D vec3d) {
        Vec3D vec3d1 = a(vec3d, f, this.yaw);

        this.setMot(this.getMot().e(vec3d1));
    }

    private static Vec3D a(Vec3D vec3d, float f, float f1) {
        double d0 = vec3d.g();

        if (d0 < 1.0E-7D) {
            return Vec3D.a;
        } else {
            Vec3D vec3d1 = (d0 > 1.0D ? vec3d.d() : vec3d).a((double) f);
            float f2 = MathHelper.sin(f1 * 0.017453292F);
            float f3 = MathHelper.cos(f1 * 0.017453292F);

            return new Vec3D(vec3d1.x * (double) f3 - vec3d1.z * (double) f2, vec3d1.y, vec3d1.z * (double) f3 + vec3d1.x * (double) f2);
        }
    }

    public float aI() {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition(this.locX(), 0.0D, this.locZ());

        if (this.world.isLoaded(blockposition_mutableblockposition)) {
            blockposition_mutableblockposition.p(MathHelper.floor(this.getHeadY()));
            return this.world.w(blockposition_mutableblockposition);
        } else {
            return 0.0F;
        }
    }

    public void spawnIn(World world) {
        // CraftBukkit start
        if (world == null) {
            die();
            this.world = ((CraftWorld) Bukkit.getServer().getWorlds().get(0)).getHandle();
            return;
        }
        // CraftBukkit end
        this.world = world;
    }

    public void setLocation(double d0, double d1, double d2, float f, float f1) {
        double d3 = MathHelper.a(d0, -3.0E7D, 3.0E7D);
        double d4 = MathHelper.a(d2, -3.0E7D, 3.0E7D);

        this.lastX = d3;
        this.lastY = d1;
        this.lastZ = d4;
        this.setPosition(d3, d1, d4);
        this.yaw = f % 360.0F;
        this.pitch = MathHelper.a(f1, -90.0F, 90.0F) % 360.0F;
        this.lastYaw = this.yaw;
        this.lastPitch = this.pitch;
        world.getChunkAt((int) Math.floor(this.locX) >> 4, (int) Math.floor(this.locZ) >> 4); // CraftBukkit
    }

    public void setPositionRotation(BlockPosition blockposition, float f, float f1) {
        this.setPositionRotation((double) blockposition.getX() + 0.5D, (double) blockposition.getY(), (double) blockposition.getZ() + 0.5D, f, f1);
    }

    public void setPositionRotation(double d0, double d1, double d2, float f, float f1) {
        this.f(d0, d1, d2);
        this.yaw = f;
        this.pitch = f1;
        this.Z();
    }

    public void f(double d0, double d1, double d2) {
        this.setPositionRaw(d0, d1, d2);
        this.lastX = d0;
        this.lastY = d1;
        this.lastZ = d2;
        this.E = d0;
        this.F = d1;
        this.G = d2;
    }

    public float g(Entity entity) {
        float f = (float) (this.locX() - entity.locX());
        float f1 = (float) (this.locY() - entity.locY());
        float f2 = (float) (this.locZ() - entity.locZ());

        return MathHelper.c(f * f + f1 * f1 + f2 * f2);
    }

    public double g(double d0, double d1, double d2) {
        double d3 = this.locX() - d0;
        double d4 = this.locY() - d1;
        double d5 = this.locZ() - d2;

        return d3 * d3 + d4 * d4 + d5 * d5;
    }

    public double h(Entity entity) {
        return this.c(entity.getPositionVector());
    }

    public double c(Vec3D vec3d) {
        double d0 = this.locX() - vec3d.x;
        double d1 = this.locY() - vec3d.y;
        double d2 = this.locZ() - vec3d.z;

        return d0 * d0 + d1 * d1 + d2 * d2;
    }

    public void pickup(EntityHuman entityhuman) {}

    public void collide(Entity entity) {
        if (!this.isSameVehicle(entity)) {
            if (!entity.noclip && !this.noclip) {
                double d0 = entity.locX() - this.locX();
                double d1 = entity.locZ() - this.locZ();
                double d2 = MathHelper.a(d0, d1);

                if (d2 >= 0.009999999776482582D) {
                    d2 = (double) MathHelper.sqrt(d2);
                    d0 /= d2;
                    d1 /= d2;
                    double d3 = 1.0D / d2;

                    if (d3 > 1.0D) {
                        d3 = 1.0D;
                    }

                    d0 *= d3;
                    d1 *= d3;
                    d0 *= 0.05000000074505806D;
                    d1 *= 0.05000000074505806D;
                    d0 *= (double) (1.0F - this.J);
                    d1 *= (double) (1.0F - this.J);
                    if (!this.isVehicle()) {
                        this.h(-d0, 0.0D, -d1);
                    }

                    if (!entity.isVehicle()) {
                        entity.h(d0, 0.0D, d1);
                    }
                }

            }
        }
    }

    public void h(double d0, double d1, double d2) {
        this.setMot(this.getMot().add(d0, d1, d2));
        this.impulse = true;
    }

    protected void velocityChanged() {
        this.velocityChanged = true;
    }

    public boolean damageEntity(DamageSource damagesource, float f) {
        if (this.isInvulnerable(damagesource)) {
            return false;
        } else {
            this.velocityChanged();
            return false;
        }
    }

    public final Vec3D f(float f) {
        return this.c(this.g(f), this.h(f));
    }

    public float g(float f) {
        return f == 1.0F ? this.pitch : MathHelper.g(f, this.lastPitch, this.pitch);
    }

    public float h(float f) {
        return f == 1.0F ? this.yaw : MathHelper.g(f, this.lastYaw, this.yaw);
    }

    protected final Vec3D c(float f, float f1) {
        float f2 = f * 0.017453292F;
        float f3 = -f1 * 0.017453292F;
        float f4 = MathHelper.cos(f3);
        float f5 = MathHelper.sin(f3);
        float f6 = MathHelper.cos(f2);
        float f7 = MathHelper.sin(f2);

        return new Vec3D((double) (f5 * f6), (double) (-f7), (double) (f4 * f6));
    }

    public final Vec3D i(float f) {
        return this.d(this.g(f), this.h(f));
    }

    protected final Vec3D d(float f, float f1) {
        return this.c(f - 90.0F, f1);
    }

    public final Vec3D j(float f) {
        if (f == 1.0F) {
            return new Vec3D(this.locX(), this.getHeadY(), this.locZ());
        } else {
            double d0 = MathHelper.d((double) f, this.lastX, this.locX());
            double d1 = MathHelper.d((double) f, this.lastY, this.locY()) + (double) this.getHeadHeight();
            double d2 = MathHelper.d((double) f, this.lastZ, this.locZ());

            return new Vec3D(d0, d1, d2);
        }
    }

    public MovingObjectPosition a(double d0, float f, boolean flag) {
        Vec3D vec3d = this.j(f);
        Vec3D vec3d1 = this.f(f);
        Vec3D vec3d2 = vec3d.add(vec3d1.x * d0, vec3d1.y * d0, vec3d1.z * d0);

        return this.world.rayTrace(new RayTrace(vec3d, vec3d2, RayTrace.BlockCollisionOption.OUTLINE, flag ? RayTrace.FluidCollisionOption.ANY : RayTrace.FluidCollisionOption.NONE, this));
    }

    public boolean isInteractable() {
        return false;
    }

    public boolean isCollidable() {
        return false;
    }

    public void a(Entity entity, int i, DamageSource damagesource) {
        if (entity instanceof EntityPlayer) {
            CriterionTriggers.c.a((EntityPlayer) entity, this, damagesource);
        }

    }

    public boolean c(NBTTagCompound nbttagcompound) {
        String s = this.getSaveID();

        if (this.persist && !this.dead && s != null) { // CraftBukkit - persist flag
            nbttagcompound.setString("id", s);
            this.save(nbttagcompound);
            return true;
        } else {
            return false;
        }
    }

    public boolean d(NBTTagCompound nbttagcompound) {
        return this.isPassenger() ? false : this.c(nbttagcompound);
    }

    public NBTTagCompound save(NBTTagCompound nbttagcompound) {
        try {
            nbttagcompound.set("Pos", this.a(this.locX(), this.locY(), this.locZ()));
            Vec3D vec3d = this.getMot();

            nbttagcompound.set("Motion", this.a(vec3d.x, vec3d.y, vec3d.z));

            // CraftBukkit start - Checking for NaN pitch/yaw and resetting to zero
            // TODO: make sure this is the best way to address this.
            if (Float.isNaN(this.yaw)) {
                this.yaw = 0;
            }

            if (Float.isNaN(this.pitch)) {
                this.pitch = 0;
            }
            // CraftBukkit end

            nbttagcompound.set("Rotation", this.a(this.yaw, this.pitch));
            nbttagcompound.setFloat("FallDistance", this.fallDistance);
            nbttagcompound.setShort("Fire", (short) this.fireTicks);
            nbttagcompound.setShort("Air", (short) this.getAirTicks());
            nbttagcompound.setBoolean("OnGround", this.onGround);
            nbttagcompound.setInt("Dimension", this.dimension.getType().getDimensionID()); // CraftBukkit - preserve Vanilla compat
            nbttagcompound.setBoolean("Invulnerable", this.invulnerable);
            nbttagcompound.setInt("PortalCooldown", this.portalCooldown);
            nbttagcompound.a("UUID", this.getUniqueID());
            // CraftBukkit start
            // PAIL: Check above UUID reads 1.8 properly, ie: UUIDMost / UUIDLeast
            nbttagcompound.setLong("WorldUUIDLeast", ((WorldServer) this.world).getDataManager().getUUID().getLeastSignificantBits());
            nbttagcompound.setLong("WorldUUIDMost", ((WorldServer) this.world).getDataManager().getUUID().getMostSignificantBits());
            nbttagcompound.setInt("Bukkit.updateLevel", CURRENT_LEVEL);
            // CraftBukkit end
            IChatBaseComponent ichatbasecomponent = this.getCustomName();

            if (ichatbasecomponent != null) {
                nbttagcompound.setString("CustomName", IChatBaseComponent.ChatSerializer.a(ichatbasecomponent));
            }

            if (this.getCustomNameVisible()) {
                nbttagcompound.setBoolean("CustomNameVisible", this.getCustomNameVisible());
            }

            if (this.isSilent()) {
                nbttagcompound.setBoolean("Silent", this.isSilent());
            }

            if (this.isNoGravity()) {
                nbttagcompound.setBoolean("NoGravity", this.isNoGravity());
            }

            if (this.glowing) {
                nbttagcompound.setBoolean("Glowing", this.glowing);
            }

            NBTTagList nbttaglist;
            Iterator iterator;

            if (!this.aE.isEmpty()) {
                nbttaglist = new NBTTagList();
                iterator = this.aE.iterator();

                while (iterator.hasNext()) {
                    String s = (String) iterator.next();

                    nbttaglist.add(NBTTagString.a(s));
                }

                nbttagcompound.set("Tags", nbttaglist);
            }

            this.b(nbttagcompound);
            if (this.isVehicle()) {
                nbttaglist = new NBTTagList();
                iterator = this.getPassengers().iterator();

                while (iterator.hasNext()) {
                    Entity entity = (Entity) iterator.next();
                    NBTTagCompound nbttagcompound1 = new NBTTagCompound();

                    if (entity.c(nbttagcompound1)) {
                        nbttaglist.add(nbttagcompound1);
                    }
                }

                if (!nbttaglist.isEmpty()) {
                    nbttagcompound.set("Passengers", nbttaglist);
                }
            }

            // CraftBukkit start - stores eventually existing bukkit values
            if (this.bukkitEntity != null) {
                this.bukkitEntity.storeBukkitValues(nbttagcompound);
            }
            // CraftBukkit end
            return nbttagcompound;
        } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.a(throwable, "Saving entity NBT");
            CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Entity being saved");

            this.appendEntityCrashDetails(crashreportsystemdetails);
            throw new ReportedException(crashreport);
        }
    }

    public void f(NBTTagCompound nbttagcompound) {
        try {
            NBTTagList nbttaglist = nbttagcompound.getList("Pos", 6);
            NBTTagList nbttaglist1 = nbttagcompound.getList("Motion", 6);
            NBTTagList nbttaglist2 = nbttagcompound.getList("Rotation", 5);
            double d0 = nbttaglist1.h(0);
            double d1 = nbttaglist1.h(1);
            double d2 = nbttaglist1.h(2);

            this.setMot(Math.abs(d0) > 10.0D ? 0.0D : d0, Math.abs(d1) > 10.0D ? 0.0D : d1, Math.abs(d2) > 10.0D ? 0.0D : d2);
            this.f(nbttaglist.h(0), nbttaglist.h(1), nbttaglist.h(2));
            this.yaw = nbttaglist2.i(0);
            this.pitch = nbttaglist2.i(1);
            this.lastYaw = this.yaw;
            this.lastPitch = this.pitch;
            this.setHeadRotation(this.yaw);
            this.l(this.yaw);
            this.fallDistance = nbttagcompound.getFloat("FallDistance");
            this.fireTicks = nbttagcompound.getShort("Fire");
            this.setAirTicks(nbttagcompound.getShort("Air"));
            this.onGround = nbttagcompound.getBoolean("OnGround");
            if (nbttagcompound.hasKey("Dimension")) {
                // this.dimension = DimensionManager.a(nbttagcompound.getInt("Dimension")); // CraftBukkit - redundant
            }

            this.invulnerable = nbttagcompound.getBoolean("Invulnerable");
            this.portalCooldown = nbttagcompound.getInt("PortalCooldown");
            if (nbttagcompound.b("UUID")) {
                this.uniqueID = nbttagcompound.a("UUID");
                this.am = this.uniqueID.toString();
            }

            if (Double.isFinite(this.locX()) && Double.isFinite(this.locY()) && Double.isFinite(this.locZ())) {
                if (Double.isFinite((double) this.yaw) && Double.isFinite((double) this.pitch)) {
                    this.Z();
                    this.setYawPitch(this.yaw, this.pitch);
                    if (nbttagcompound.hasKeyOfType("CustomName", 8)) {
                        this.setCustomName(IChatBaseComponent.ChatSerializer.a(nbttagcompound.getString("CustomName")));
                    }

                    this.setCustomNameVisible(nbttagcompound.getBoolean("CustomNameVisible"));
                    this.setSilent(nbttagcompound.getBoolean("Silent"));
                    this.setNoGravity(nbttagcompound.getBoolean("NoGravity"));
                    this.h(nbttagcompound.getBoolean("Glowing"));
                    if (nbttagcompound.hasKeyOfType("Tags", 9)) {
                        this.aE.clear();
                        NBTTagList nbttaglist3 = nbttagcompound.getList("Tags", 8);
                        int i = Math.min(nbttaglist3.size(), 1024);

                        for (int j = 0; j < i; ++j) {
                            this.aE.add(nbttaglist3.getString(j));
                        }
                    }

                    this.a(nbttagcompound);
                    if (this.aM()) {
                        this.Z();
                    }

                } else {
                    throw new IllegalStateException("Entity has invalid rotation");
                }
            } else {
                throw new IllegalStateException("Entity has invalid position");
            }

            // CraftBukkit start
            if (this instanceof EntityLiving) {
                EntityLiving entity = (EntityLiving) this;

                // Reset the persistence for tamed animals
                if (entity instanceof EntityTameableAnimal && !isLevelAtLeast(nbttagcompound, 2) && !nbttagcompound.getBoolean("PersistenceRequired")) {
                    EntityInsentient entityinsentient = (EntityInsentient) entity;
                    entityinsentient.persistent = !entityinsentient.isTypeNotPersistent(0);
                }
            }
            // CraftBukkit end

            // CraftBukkit start - Reset world
            if (this instanceof EntityPlayer) {
                Server server = Bukkit.getServer();
                org.bukkit.World bworld = null;

                // TODO: Remove World related checks, replaced with WorldUID
                String worldName = nbttagcompound.getString("world");

                if (nbttagcompound.hasKey("WorldUUIDMost") && nbttagcompound.hasKey("WorldUUIDLeast")) {
                    UUID uid = new UUID(nbttagcompound.getLong("WorldUUIDMost"), nbttagcompound.getLong("WorldUUIDLeast"));
                    bworld = server.getWorld(uid);
                } else {
                    bworld = server.getWorld(worldName);
                }

                if (bworld == null) {
                    bworld = ((org.bukkit.craftbukkit.CraftServer) server).getServer().getWorldServer(DimensionManager.OVERWORLD).getWorld();
                }

                spawnIn(bworld == null ? null : ((CraftWorld) bworld).getHandle());
            }
            this.getBukkitEntity().readBukkitValues(nbttagcompound);
            // CraftBukkit end

        } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.a(throwable, "Loading entity NBT");
            CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Entity being loaded");

            this.appendEntityCrashDetails(crashreportsystemdetails);
            throw new ReportedException(crashreport);
        }
    }

    protected boolean aM() {
        return true;
    }

    @Nullable
    public final String getSaveID() {
        EntityTypes<?> entitytypes = this.getEntityType();
        MinecraftKey minecraftkey = EntityTypes.getName(entitytypes);

        return entitytypes.a() && minecraftkey != null ? minecraftkey.toString() : null;
    }

    protected abstract void a(NBTTagCompound nbttagcompound);

    protected abstract void b(NBTTagCompound nbttagcompound);

    protected NBTTagList a(double... adouble) {
        NBTTagList nbttaglist = new NBTTagList();
        double[] adouble1 = adouble;
        int i = adouble.length;

        for (int j = 0; j < i; ++j) {
            double d0 = adouble1[j];

            nbttaglist.add(NBTTagDouble.a(d0));
        }

        return nbttaglist;
    }

    protected NBTTagList a(float... afloat) {
        NBTTagList nbttaglist = new NBTTagList();
        float[] afloat1 = afloat;
        int i = afloat.length;

        for (int j = 0; j < i; ++j) {
            float f = afloat1[j];

            nbttaglist.add(NBTTagFloat.a(f));
        }

        return nbttaglist;
    }

    @Nullable
    public EntityItem a(IMaterial imaterial) {
        return this.a(imaterial, 0);
    }

    @Nullable
    public EntityItem a(IMaterial imaterial, int i) {
        return this.a(new ItemStack(imaterial), (float) i);
    }

    @Nullable
    public EntityItem a(ItemStack itemstack) {
        return this.a(itemstack, 0.0F);
    }

    @Nullable
    public EntityItem a(ItemStack itemstack, float f) {
        if (itemstack.isEmpty()) {
            return null;
        } else if (this.world.isClientSide) {
            return null;
        } else {
            // CraftBukkit start - Capture drops for death event
            if (this instanceof EntityLiving && !((EntityLiving) this).forceDrops) {
                ((EntityLiving) this).drops.add(org.bukkit.craftbukkit.inventory.CraftItemStack.asBukkitCopy(itemstack));
                return null;
            }
            // CraftBukkit end
            EntityItem entityitem = new EntityItem(this.world, this.locX(), this.locY() + (double) f, this.locZ(), itemstack);

            entityitem.defaultPickupDelay();
            // CraftBukkit start
            EntityDropItemEvent event = new EntityDropItemEvent(this.getBukkitEntity(), (org.bukkit.entity.Item) entityitem.getBukkitEntity());
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return null;
            }
            // CraftBukkit end
            this.world.addEntity(entityitem);
            return entityitem;
        }
    }

    public boolean isAlive() {
        return !this.dead;
    }

    public boolean inBlock() {
        if (this.noclip) {
            return false;
        } else {
            BlockPosition.PooledBlockPosition blockposition_pooledblockposition = BlockPosition.PooledBlockPosition.r();
            Throwable throwable = null;

            try {
                for (int i = 0; i < 8; ++i) {
                    int j = MathHelper.floor(this.locY() + (double) (((float) ((i >> 0) % 2) - 0.5F) * 0.1F) + (double) this.headHeight);
                    int k = MathHelper.floor(this.locX() + (double) (((float) ((i >> 1) % 2) - 0.5F) * this.size.width * 0.8F));
                    int l = MathHelper.floor(this.locZ() + (double) (((float) ((i >> 2) % 2) - 0.5F) * this.size.width * 0.8F));

                    if (blockposition_pooledblockposition.getX() != k || blockposition_pooledblockposition.getY() != j || blockposition_pooledblockposition.getZ() != l) {
                        blockposition_pooledblockposition.d(k, j, l);
                        if (this.world.getType(blockposition_pooledblockposition).m(this.world, blockposition_pooledblockposition)) {
                            boolean flag = true;

                            return flag;
                        }
                    }
                }

                return false;
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
    }

    public boolean b(EntityHuman entityhuman, EnumHand enumhand) {
        return false;
    }

    @Nullable
    public AxisAlignedBB j(Entity entity) {
        return null;
    }

    public void passengerTick() {
        this.setMot(Vec3D.a);
        this.tick();
        if (this.isPassenger()) {
            this.getVehicle().k(this);
        }
    }

    public void k(Entity entity) {
        this.a(entity, Entity::setPosition);
    }

    public void a(Entity entity, Entity.a entity_a) {
        if (this.w(entity)) {
            entity_a.accept(entity, this.locX(), this.locY() + this.aS() + entity.aR(), this.locZ());
        }
    }

    public double aR() {
        return 0.0D;
    }

    public double aS() {
        return (double) this.size.height * 0.75D;
    }

    public boolean startRiding(Entity entity) {
        return this.a(entity, false);
    }

    public boolean a(Entity entity, boolean flag) {
        for (Entity entity1 = entity; entity1.vehicle != null; entity1 = entity1.vehicle) {
            if (entity1.vehicle == this) {
                return false;
            }
        }

        if (!flag && (!this.n(entity) || !entity.q(this))) {
            return false;
        } else {
            if (this.isPassenger()) {
                this.stopRiding();
            }

            this.vehicle = entity;
            if (!this.vehicle.addPassenger(this)) this.vehicle = null; // CraftBukkit
            return true;
        }
    }

    protected boolean n(Entity entity) {
        return this.j <= 0;
    }

    protected boolean c(EntityPose entitypose) {
        return this.world.getCubes(this, this.d(entitypose));
    }

    public void ejectPassengers() {
        for (int i = this.passengers.size() - 1; i >= 0; --i) {
            ((Entity) this.passengers.get(i)).stopRiding();
        }

    }

    public void stopRiding() {
        if (this.vehicle != null) {
            Entity entity = this.vehicle;

            this.vehicle = null;
            if (!entity.removePassenger(this)) this.vehicle = entity; // CraftBukkit
        }

    }

    protected boolean addPassenger(Entity entity) { // CraftBukkit
        if (entity.getVehicle() != this) {
            throw new IllegalStateException("Use x.startRiding(y), not y.addPassenger(x)");
        } else {
            // CraftBukkit start
            com.google.common.base.Preconditions.checkState(!entity.passengers.contains(this), "Circular entity riding! %s %s", this, entity);

            CraftEntity craft = (CraftEntity) entity.getBukkitEntity().getVehicle();
            Entity orig = craft == null ? null : craft.getHandle();
            if (getBukkitEntity() instanceof Vehicle && entity.getBukkitEntity() instanceof LivingEntity) {
                VehicleEnterEvent event = new VehicleEnterEvent(
                        (Vehicle) getBukkitEntity(),
                         entity.getBukkitEntity()
                );
                // Suppress during worldgen
                if (this.valid) {
                    Bukkit.getPluginManager().callEvent(event);
                }
                CraftEntity craftn = (CraftEntity) entity.getBukkitEntity().getVehicle();
                Entity n = craftn == null ? null : craftn.getHandle();
                if (event.isCancelled() || n != orig) {
                    return false;
                }
            }
            // CraftBukkit end
            if (!this.world.isClientSide && entity instanceof EntityHuman && !(this.getRidingPassenger() instanceof EntityHuman)) {
                this.passengers.add(0, entity);
            } else {
                this.passengers.add(entity);
            }

        }
        return true; // CraftBukkit
    }

    protected boolean removePassenger(Entity entity) { // CraftBukkit
        if (entity.getVehicle() == this) {
            throw new IllegalStateException("Use x.stopRiding(y), not y.removePassenger(x)");
        } else {
            // CraftBukkit start
            CraftEntity craft = (CraftEntity) entity.getBukkitEntity().getVehicle();
            Entity orig = craft == null ? null : craft.getHandle();
            if (getBukkitEntity() instanceof Vehicle && entity.getBukkitEntity() instanceof LivingEntity) {
                VehicleExitEvent event = new VehicleExitEvent(
                        (Vehicle) getBukkitEntity(),
                        (LivingEntity) entity.getBukkitEntity()
                );
                Bukkit.getPluginManager().callEvent(event);
                CraftEntity craftn = (CraftEntity) entity.getBukkitEntity().getVehicle();
                Entity n = craftn == null ? null : craftn.getHandle();
                if (event.isCancelled() || n != orig) {
                    return false;
                }
            }
            // CraftBukkit end
            this.passengers.remove(entity);
            entity.j = 60;
        }
        return true; // CraftBukkit
    }

    protected boolean q(Entity entity) {
        return this.getPassengers().size() < 1;
    }

    public float aV() {
        return 0.0F;
    }

    public Vec3D getLookDirection() {
        return this.c(this.pitch, this.yaw);
    }

    public Vec2F aX() {
        return new Vec2F(this.pitch, this.yaw);
    }

    public void c(BlockPosition blockposition) {
        if (this.portalCooldown > 0) {
            this.portalCooldown = this.ba();
        } else {
            if (!this.world.isClientSide && !blockposition.equals(this.ai)) {
                this.ai = new BlockPosition(blockposition);
                BlockPortal blockportal = (BlockPortal) Blocks.NETHER_PORTAL;
                ShapeDetector.ShapeDetectorCollection shapedetector_shapedetectorcollection = BlockPortal.c((GeneratorAccess) this.world, this.ai);
                double d0 = shapedetector_shapedetectorcollection.getFacing().m() == EnumDirection.EnumAxis.X ? (double) shapedetector_shapedetectorcollection.a().getZ() : (double) shapedetector_shapedetectorcollection.a().getX();
                double d1 = Math.abs(MathHelper.c((shapedetector_shapedetectorcollection.getFacing().m() == EnumDirection.EnumAxis.X ? this.locZ() : this.locX()) - (double) (shapedetector_shapedetectorcollection.getFacing().f().d() == EnumDirection.EnumAxisDirection.NEGATIVE ? 1 : 0), d0, d0 - (double) shapedetector_shapedetectorcollection.d()));
                double d2 = MathHelper.c(this.locY() - 1.0D, (double) shapedetector_shapedetectorcollection.a().getY(), (double) (shapedetector_shapedetectorcollection.a().getY() - shapedetector_shapedetectorcollection.e()));

                this.aj = new Vec3D(d1, d2, 0.0D);
                this.ak = shapedetector_shapedetectorcollection.getFacing();
            }

            this.af = true;
        }
    }

    protected void doPortalTick() {
        if (this.world instanceof WorldServer) {
            int i = this.ab();

            if (this.af) {
                if ((true || this.world.getMinecraftServer().getAllowNether()) && !this.isPassenger() && this.ag++ >= i) { // CraftBukkit
                    this.world.getMethodProfiler().enter("portal");
                    this.ag = i;
                    this.portalCooldown = this.ba();
                    // CraftBukkit start
                    if (this instanceof EntityPlayer) {
                        ((EntityPlayer) this).a(this.world.worldProvider.getDimensionManager().getType() == DimensionManager.NETHER ? DimensionManager.OVERWORLD : DimensionManager.NETHER, PlayerTeleportEvent.TeleportCause.NETHER_PORTAL);
                    } else {
                        this.a(this.world.worldProvider.getDimensionManager().getType() == DimensionManager.NETHER ? DimensionManager.OVERWORLD : DimensionManager.NETHER);
                    }
                    // CraftBukkit end
                    this.world.getMethodProfiler().exit();
                }

                this.af = false;
            } else {
                if (this.ag > 0) {
                    this.ag -= 4;
                }

                if (this.ag < 0) {
                    this.ag = 0;
                }
            }

            this.E();
        }
    }

    public int ba() {
        return 300;
    }

    public Iterable<ItemStack> bc() {
        return Entity.c;
    }

    public Iterable<ItemStack> getArmorItems() {
        return Entity.c;
    }

    public Iterable<ItemStack> be() {
        return Iterables.concat(this.bc(), this.getArmorItems());
    }

    public void setEquipment(EnumItemSlot enumitemslot, ItemStack itemstack) {}

    public boolean isBurning() {
        boolean flag = this.world != null && this.world.isClientSide;

        return !this.isFireProof() && (this.fireTicks > 0 || flag && this.getFlag(0));
    }

    public boolean isPassenger() {
        return this.getVehicle() != null;
    }

    public boolean isVehicle() {
        return !this.getPassengers().isEmpty();
    }

    public boolean bi() {
        return true;
    }

    public void setSneaking(boolean flag) {
        this.setFlag(1, flag);
    }

    public boolean isSneaking() {
        return this.getFlag(1);
    }

    public boolean bk() {
        return this.isSneaking();
    }

    public boolean bl() {
        return this.isSneaking();
    }

    public boolean bm() {
        return this.isSneaking();
    }

    public boolean bn() {
        return this.isSneaking();
    }

    public boolean bo() {
        return this.getPose() == EntityPose.CROUCHING;
    }

    public boolean isSprinting() {
        return this.getFlag(3);
    }

    public void setSprinting(boolean flag) {
        this.setFlag(3, flag);
    }

    public boolean isSwimming() {
        return this.getFlag(4);
    }

    public boolean br() {
        return this.getPose() == EntityPose.SWIMMING;
    }

    public void setSwimming(boolean flag) {
        // CraftBukkit start
        if (this.isSwimming() != flag && this instanceof EntityLiving) {
            if (CraftEventFactory.callToggleSwimEvent((EntityLiving) this, flag).isCancelled()) {
                return;
            }
        }
        // CraftBukkit end
        this.setFlag(4, flag);
    }

    public boolean bt() {
        return this.glowing || this.world.isClientSide && this.getFlag(6);
    }

    public void h(boolean flag) {
        this.glowing = flag;
        if (!this.world.isClientSide) {
            this.setFlag(6, this.glowing);
        }

    }

    public boolean isInvisible() {
        return this.getFlag(5);
    }

    @Nullable
    public ScoreboardTeamBase getScoreboardTeam() {
        return this.world.getScoreboard().getPlayerTeam(this.getName());
    }

    public boolean r(Entity entity) {
        return this.a(entity.getScoreboardTeam());
    }

    public boolean a(ScoreboardTeamBase scoreboardteambase) {
        return this.getScoreboardTeam() != null ? this.getScoreboardTeam().isAlly(scoreboardteambase) : false;
    }

    public void setInvisible(boolean flag) {
        this.setFlag(5, flag);
    }

    public boolean getFlag(int i) {
        return ((Byte) this.datawatcher.get(Entity.T) & 1 << i) != 0;
    }

    public void setFlag(int i, boolean flag) {
        byte b0 = (Byte) this.datawatcher.get(Entity.T);

        if (flag) {
            this.datawatcher.set(Entity.T, (byte) (b0 | 1 << i));
        } else {
            this.datawatcher.set(Entity.T, (byte) (b0 & ~(1 << i)));
        }

    }

    public int bw() {
        return 300;
    }

    public int getAirTicks() {
        return (Integer) this.datawatcher.get(Entity.AIR_TICKS);
    }

    public void setAirTicks(int i) {
        // CraftBukkit start
        EntityAirChangeEvent event = new EntityAirChangeEvent(this.getBukkitEntity(), i);
        // Suppress during worldgen
        if (this.valid) {
            event.getEntity().getServer().getPluginManager().callEvent(event);
        }
        if (event.isCancelled()) {
            return;
        }
        this.datawatcher.set(Entity.AIR_TICKS, event.getAmount());
        // CraftBukkit end
    }

    public void onLightningStrike(EntityLightning entitylightning) {
        ++this.fireTicks;
        // CraftBukkit start
        final org.bukkit.entity.Entity thisBukkitEntity = this.getBukkitEntity();
        final org.bukkit.entity.Entity stormBukkitEntity = entitylightning.getBukkitEntity();
        final PluginManager pluginManager = Bukkit.getPluginManager();
        // CraftBukkit end

        if (this.fireTicks == 0) {
            // CraftBukkit start - Call a combust event when lightning strikes
            EntityCombustByEntityEvent entityCombustEvent = new EntityCombustByEntityEvent(stormBukkitEntity, thisBukkitEntity, 8);
            pluginManager.callEvent(entityCombustEvent);
            if (!entityCombustEvent.isCancelled()) {
                this.setOnFire(entityCombustEvent.getDuration(), false);
            }
            // CraftBukkit end
        }

        // CraftBukkit start
        if (thisBukkitEntity instanceof Hanging) {
            HangingBreakByEntityEvent hangingEvent = new HangingBreakByEntityEvent((Hanging) thisBukkitEntity, stormBukkitEntity);
            pluginManager.callEvent(hangingEvent);

            if (hangingEvent.isCancelled()) {
                return;
            }
        }

        if (this.isFireProof()) {
            return;
        }
        CraftEventFactory.entityDamage = entitylightning;
        if (!this.damageEntity(DamageSource.LIGHTNING, 5.0F)) {
            CraftEventFactory.entityDamage = null;
            return;
        }
        // CraftBukkit end
    }

    public void j(boolean flag) {
        Vec3D vec3d = this.getMot();
        double d0;

        if (flag) {
            d0 = Math.max(-0.9D, vec3d.y - 0.03D);
        } else {
            d0 = Math.min(1.8D, vec3d.y + 0.1D);
        }

        this.setMot(vec3d.x, d0, vec3d.z);
    }

    public void k(boolean flag) {
        Vec3D vec3d = this.getMot();
        double d0;

        if (flag) {
            d0 = Math.max(-0.3D, vec3d.y - 0.03D);
        } else {
            d0 = Math.min(0.7D, vec3d.y + 0.06D);
        }

        this.setMot(vec3d.x, d0, vec3d.z);
        this.fallDistance = 0.0F;
    }

    public void b(EntityLiving entityliving) {}

    protected void k(double d0, double d1, double d2) {
        BlockPosition blockposition = new BlockPosition(d0, d1, d2);
        Vec3D vec3d = new Vec3D(d0 - (double) blockposition.getX(), d1 - (double) blockposition.getY(), d2 - (double) blockposition.getZ());
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
        EnumDirection enumdirection = EnumDirection.UP;
        double d3 = Double.MAX_VALUE;
        EnumDirection[] aenumdirection = new EnumDirection[]{EnumDirection.NORTH, EnumDirection.SOUTH, EnumDirection.WEST, EnumDirection.EAST, EnumDirection.UP};
        int i = aenumdirection.length;

        for (int j = 0; j < i; ++j) {
            EnumDirection enumdirection1 = aenumdirection[j];

            blockposition_mutableblockposition.g(blockposition).c(enumdirection1);
            if (!this.world.getType(blockposition_mutableblockposition).p(this.world, blockposition_mutableblockposition)) {
                double d4 = vec3d.a(enumdirection1.m());
                double d5 = enumdirection1.d() == EnumDirection.EnumAxisDirection.POSITIVE ? 1.0D - d4 : d4;

                if (d5 < d3) {
                    d3 = d5;
                    enumdirection = enumdirection1;
                }
            }
        }

        float f = this.random.nextFloat() * 0.2F + 0.1F;
        float f1 = (float) enumdirection.d().a();
        Vec3D vec3d1 = this.getMot().a(0.75D);

        if (enumdirection.m() == EnumDirection.EnumAxis.X) {
            this.setMot((double) (f1 * f), vec3d1.y, vec3d1.z);
        } else if (enumdirection.m() == EnumDirection.EnumAxis.Y) {
            this.setMot(vec3d1.x, (double) (f1 * f), vec3d1.z);
        } else if (enumdirection.m() == EnumDirection.EnumAxis.Z) {
            this.setMot(vec3d1.x, vec3d1.y, (double) (f1 * f));
        }

    }

    public void a(IBlockData iblockdata, Vec3D vec3d) {
        this.fallDistance = 0.0F;
        this.y = vec3d;
    }

    private static void c(IChatBaseComponent ichatbasecomponent) {
        ichatbasecomponent.a((chatmodifier) -> {
            chatmodifier.setChatClickable((ChatClickable) null);
        }).getSiblings().forEach(Entity::c);
    }

    @Override
    public IChatBaseComponent getDisplayName() {
        IChatBaseComponent ichatbasecomponent = this.getCustomName();

        if (ichatbasecomponent != null) {
            IChatBaseComponent ichatbasecomponent1 = ichatbasecomponent.h();

            c(ichatbasecomponent1);
            return ichatbasecomponent1;
        } else {
            return this.by();
        }
    }

    protected IChatBaseComponent by() {
        return this.f.g();
    }

    public boolean s(Entity entity) {
        return this == entity;
    }

    public float getHeadRotation() {
        return 0.0F;
    }

    public void setHeadRotation(float f) {}

    public void l(float f) {}

    public boolean bA() {
        return true;
    }

    public boolean t(Entity entity) {
        return false;
    }

    public String toString() {
        return String.format(Locale.ROOT, "%s['%s'/%d, l='%s', x=%.2f, y=%.2f, z=%.2f]", this.getClass().getSimpleName(), this.getDisplayName().getText(), this.id, this.world == null ? "~NULL~" : this.world.getWorldData().getName(), this.locX(), this.locY(), this.locZ());
    }

    public boolean isInvulnerable(DamageSource damagesource) {
        return this.invulnerable && damagesource != DamageSource.OUT_OF_WORLD && !damagesource.v();
    }

    public boolean isInvulnerable() {
        return this.invulnerable;
    }

    public void setInvulnerable(boolean flag) {
        this.invulnerable = flag;
    }

    public void u(Entity entity) {
        this.setPositionRotation(entity.locX(), entity.locY(), entity.locZ(), entity.yaw, entity.pitch);
    }

    public void v(Entity entity) {
        NBTTagCompound nbttagcompound = entity.save(new NBTTagCompound());

        nbttagcompound.remove("Dimension");
        this.f(nbttagcompound);
        this.portalCooldown = entity.portalCooldown;
        this.ai = entity.ai;
        this.aj = entity.aj;
        this.ak = entity.ak;
    }

    @Nullable
    public Entity a(DimensionManager dimensionmanager) {
        // CraftBukkit start
        return teleportTo(dimensionmanager, null);
    }

    @Nullable
    public Entity teleportTo(DimensionManager dimensionmanager, BlockPosition location) {
        // CraftBukkit end
        if (!this.world.isClientSide && !this.dead) {
            this.world.getMethodProfiler().enter("changeDimension");
            MinecraftServer minecraftserver = this.getMinecraftServer();
            DimensionManager dimensionmanager1 = this.dimension;
            WorldServer worldserver = minecraftserver.getWorldServer(dimensionmanager1);
            WorldServer worldserver1 = minecraftserver.getWorldServer(dimensionmanager);
            // CraftBukkit start
            if (worldserver1 == null){
                return null;
            }

            // this.dimension = dimensionmanager;
            // this.decouple();
            // CraftBukkit end
            this.world.getMethodProfiler().enter("reposition");
            Vec3D vec3d = this.getMot();
            float f = 0.0F;
            BlockPosition blockposition = location; // CraftBukkit

        if (blockposition == null) { // CraftBukkit
            if (dimensionmanager1.getType() == DimensionManager.THE_END && dimensionmanager == DimensionManager.OVERWORLD) { // CraftBukkit
                // CraftBukkit start
                EntityPortalEvent event = CraftEventFactory.callEntityPortalEvent(this, worldserver1, worldserver1.getHighestBlockYAt(HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, worldserver1.getSpawn()), 0);
                if (event == null) {
                    return null;
                }
                worldserver1 = ((CraftWorld) event.getTo().getWorld()).getHandle();
                blockposition = new BlockPosition(event.getTo().getX(), event.getTo().getY(), event.getTo().getZ());
                // CraftBukkit end
            } else if (dimensionmanager.getType() == DimensionManager.THE_END) { // CraftBukkit
                // CraftBukkit start
                EntityPortalEvent event = CraftEventFactory.callEntityPortalEvent(this, worldserver1, worldserver1.getDimensionSpawn() != null ? worldserver1.getDimensionSpawn() : worldserver1.getSpawn(), 0);
                if (event == null) {
                    return null;
                }
                worldserver1 = ((CraftWorld) event.getTo().getWorld()).getHandle();
                blockposition = new BlockPosition(event.getTo().getX(), event.getTo().getY(), event.getTo().getZ());
                // CraftBukkit end
            } else {
                double d0 = this.locX();
                double d1 = this.locZ();
                double d2 = 8.0D;

                if (dimensionmanager1.getType() == DimensionManager.OVERWORLD && dimensionmanager.getType() == DimensionManager.NETHER) { // CraftBukkit
                    d0 /= 8.0D;
                    d1 /= 8.0D;
                } else if (dimensionmanager1.getType() == DimensionManager.NETHER && dimensionmanager.getType() == DimensionManager.OVERWORLD) { // CraftBukkit
                    d0 *= 8.0D;
                    d1 *= 8.0D;
                }

                double d3 = Math.min(-2.9999872E7D, worldserver1.getWorldBorder().c() + 16.0D);
                double d4 = Math.min(-2.9999872E7D, worldserver1.getWorldBorder().d() + 16.0D);
                double d5 = Math.min(2.9999872E7D, worldserver1.getWorldBorder().e() - 16.0D);
                double d6 = Math.min(2.9999872E7D, worldserver1.getWorldBorder().f() - 16.0D);

                d0 = MathHelper.a(d0, d3, d5);
                d1 = MathHelper.a(d1, d4, d6);
                Vec3D vec3d1 = this.getPortalOffset();

                blockposition = new BlockPosition(d0, this.locY(), d1);
                // CraftBukkit start
                EntityPortalEvent event = CraftEventFactory.callEntityPortalEvent(this, worldserver1, blockposition, 128);
                if (event == null) {
                    return null;
                }
                worldserver1 = ((CraftWorld) event.getTo().getWorld()).getHandle();
                blockposition = new BlockPosition(event.getTo().getX(), event.getTo().getY(), event.getTo().getZ());
                int searchRadius = event.getSearchRadius();
                // CraftBukkit end
                ShapeDetector.Shape shapedetector_shape = worldserver1.getTravelAgent().findPortal(blockposition, vec3d, this.getPortalDirection(), vec3d1.x, vec3d1.y, this instanceof EntityHuman, searchRadius); // CraftBukkit - search radius

                if (shapedetector_shape == null) {
                    return null;
                }

                blockposition = new BlockPosition(shapedetector_shape.position);
                vec3d = shapedetector_shape.velocity;
                f = (float) shapedetector_shape.yaw;
            }
        } // CraftBukkit

            // CraftBukkit start

            this.dimension = dimensionmanager;
            this.decouple();
            // CraftBukkit end

            this.world.getMethodProfiler().exitEnter("reloading");
            Entity entity = this.getEntityType().a((World) worldserver1);

            if (entity != null) {
                entity.v(this);
                entity.setPositionRotation(blockposition, entity.yaw + f, entity.pitch);
                entity.setMot(vec3d);
                worldserver1.addEntityTeleport(entity);
                // CraftBukkit start - Forward the CraftEntity to the new entity
                this.getBukkitEntity().setHandle(entity);
                entity.bukkitEntity = this.getBukkitEntity();

                if (this instanceof EntityInsentient) {
                    ((EntityInsentient)this).unleash(true, false); // Unleash to prevent duping of leads.
                }
                // CraftBukkit end
            }

            this.dead = true;
            this.world.getMethodProfiler().exit();
            worldserver.resetEmptyTime();
            worldserver1.resetEmptyTime();
            this.world.getMethodProfiler().exit();
            return entity;
        } else {
            return null;
        }
    }

    public boolean canPortal() {
        return true;
    }

    public float a(Explosion explosion, IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, Fluid fluid, float f) {
        return f;
    }

    public boolean a(Explosion explosion, IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, float f) {
        return true;
    }

    public int bD() {
        return 3;
    }

    public Vec3D getPortalOffset() {
        return this.aj;
    }

    public EnumDirection getPortalDirection() {
        return this.ak;
    }

    public boolean isIgnoreBlockTrigger() {
        return false;
    }

    public void appendEntityCrashDetails(CrashReportSystemDetails crashreportsystemdetails) {
        crashreportsystemdetails.a("Entity Type", () -> {
            return EntityTypes.getName(this.getEntityType()) + " (" + this.getClass().getCanonicalName() + ")";
        });
        crashreportsystemdetails.a("Entity ID", (Object) this.id);
        crashreportsystemdetails.a("Entity Name", () -> {
            return this.getDisplayName().getString();
        });
        crashreportsystemdetails.a("Entity's Exact location", (Object) String.format(Locale.ROOT, "%.2f, %.2f, %.2f", this.locX(), this.locY(), this.locZ()));
        crashreportsystemdetails.a("Entity's Block location", (Object) CrashReportSystemDetails.a(MathHelper.floor(this.locX()), MathHelper.floor(this.locY()), MathHelper.floor(this.locZ())));
        Vec3D vec3d = this.getMot();

        crashreportsystemdetails.a("Entity's Momentum", (Object) String.format(Locale.ROOT, "%.2f, %.2f, %.2f", vec3d.x, vec3d.y, vec3d.z));
        crashreportsystemdetails.a("Entity's Passengers", () -> {
            return this.getPassengers().toString();
        });
        crashreportsystemdetails.a("Entity's Vehicle", () -> {
            return this.getVehicle().toString();
        });
    }

    public void a(UUID uuid) {
        this.uniqueID = uuid;
        this.am = this.uniqueID.toString();
    }

    public UUID getUniqueID() {
        return this.uniqueID;
    }

    public String getUniqueIDString() {
        return this.am;
    }

    public String getName() {
        return this.am;
    }

    public boolean bM() {
        return true;
    }

    @Override
    public IChatBaseComponent getScoreboardDisplayName() {
        return ScoreboardTeam.a(this.getScoreboardTeam(), this.getDisplayName()).a((chatmodifier) -> {
            chatmodifier.setChatHoverable(this.bS()).setInsertion(this.getUniqueIDString());
        });
    }

    public void setCustomName(@Nullable IChatBaseComponent ichatbasecomponent) {
        this.datawatcher.set(Entity.az, Optional.ofNullable(ichatbasecomponent));
    }

    @Nullable
    @Override
    public IChatBaseComponent getCustomName() {
        return (IChatBaseComponent) ((Optional) this.datawatcher.get(Entity.az)).orElse((Object) null);
    }

    @Override
    public boolean hasCustomName() {
        return ((Optional) this.datawatcher.get(Entity.az)).isPresent();
    }

    public void setCustomNameVisible(boolean flag) {
        this.datawatcher.set(Entity.aA, flag);
    }

    public boolean getCustomNameVisible() {
        return (Boolean) this.datawatcher.get(Entity.aA);
    }

    public final void enderTeleportAndLoad(double d0, double d1, double d2) {
        if (this.world instanceof WorldServer) {
            ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(new BlockPosition(d0, d1, d2));

            ((WorldServer) this.world).getChunkProvider().addTicket(TicketType.POST_TELEPORT, chunkcoordintpair, 0, this.getId());
            this.world.getChunkAt(chunkcoordintpair.x, chunkcoordintpair.z);
            this.enderTeleportTo(d0, d1, d2);
        }
    }

    public void enderTeleportTo(double d0, double d1, double d2) {
        if (this.world instanceof WorldServer) {
            WorldServer worldserver = (WorldServer) this.world;

            this.setPositionRotation(d0, d1, d2, this.yaw, this.pitch);
            this.cg().forEach((entity) -> {
                worldserver.chunkCheck(entity);
                entity.aF = true;
                entity.a(Entity::teleportAndSync);
            });
        }
    }

    public void a(DataWatcherObject<?> datawatcherobject) {
        if (Entity.POSE.equals(datawatcherobject)) {
            this.updateSize();
        }

    }

    public void updateSize() {
        EntitySize entitysize = this.size;
        EntityPose entitypose = this.getPose();
        EntitySize entitysize1 = this.a(entitypose);

        this.size = entitysize1;
        this.headHeight = this.getHeadHeight(entitypose, entitysize1);
        if (entitysize1.width < entitysize.width) {
            double d0 = (double) entitysize1.width / 2.0D;

            this.a(new AxisAlignedBB(this.locX() - d0, this.locY(), this.locZ() - d0, this.locX() + d0, this.locY() + (double) entitysize1.height, this.locZ() + d0));
        } else {
            AxisAlignedBB axisalignedbb = this.getBoundingBox();

            this.a(new AxisAlignedBB(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ, axisalignedbb.minX + (double) entitysize1.width, axisalignedbb.minY + (double) entitysize1.height, axisalignedbb.minZ + (double) entitysize1.width));
            if (entitysize1.width > entitysize.width && !this.justCreated && !this.world.isClientSide) {
                float f = entitysize.width - entitysize1.width;

                this.move(EnumMoveType.SELF, new Vec3D((double) f, 0.0D, (double) f));
            }

        }
    }

    public EnumDirection getDirection() {
        return EnumDirection.fromAngle((double) this.yaw);
    }

    public EnumDirection getAdjustedDirection() {
        return this.getDirection();
    }

    protected ChatHoverable bS() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        MinecraftKey minecraftkey = EntityTypes.getName(this.getEntityType());

        nbttagcompound.setString("id", this.getUniqueIDString());
        if (minecraftkey != null) {
            nbttagcompound.setString("type", minecraftkey.toString());
        }

        nbttagcompound.setString("name", IChatBaseComponent.ChatSerializer.a(this.getDisplayName()));
        return new ChatHoverable(ChatHoverable.EnumHoverAction.SHOW_ENTITY, new ChatComponentText(nbttagcompound.toString()));
    }

    public boolean a(EntityPlayer entityplayer) {
        return true;
    }

    public AxisAlignedBB getBoundingBox() {
        return this.boundingBox;
    }

    protected AxisAlignedBB d(EntityPose entitypose) {
        EntitySize entitysize = this.a(entitypose);
        float f = entitysize.width / 2.0F;
        Vec3D vec3d = new Vec3D(this.locX() - (double) f, this.locY(), this.locZ() - (double) f);
        Vec3D vec3d1 = new Vec3D(this.locX() + (double) f, this.locY() + (double) entitysize.height, this.locZ() + (double) f);

        return new AxisAlignedBB(vec3d, vec3d1);
    }

    public void a(AxisAlignedBB axisalignedbb) {
        // CraftBukkit start - block invalid bounding boxes
        double minX = axisalignedbb.minX,
                minY = axisalignedbb.minY,
                minZ = axisalignedbb.minZ,
                maxX = axisalignedbb.maxX,
                maxY = axisalignedbb.maxY,
                maxZ = axisalignedbb.maxZ;
        double len = axisalignedbb.maxX - axisalignedbb.minX;
        if (len < 0) maxX = minX;
        if (len > 64) maxX = minX + 64.0;

        len = axisalignedbb.maxY - axisalignedbb.minY;
        if (len < 0) maxY = minY;
        if (len > 64) maxY = minY + 64.0;

        len = axisalignedbb.maxZ - axisalignedbb.minZ;
        if (len < 0) maxZ = minZ;
        if (len > 64) maxZ = minZ + 64.0;
        this.boundingBox = new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
        // CraftBukkit end
    }

    protected float getHeadHeight(EntityPose entitypose, EntitySize entitysize) {
        return entitysize.height * 0.85F;
    }

    public final float getHeadHeight() {
        return this.headHeight;
    }

    public boolean a_(int i, ItemStack itemstack) {
        return false;
    }

    @Override
    public void sendMessage(IChatBaseComponent ichatbasecomponent) {}

    public BlockPosition getChunkCoordinates() {
        return new BlockPosition(this);
    }

    public Vec3D bX() {
        return this.getPositionVector();
    }

    public World getWorld() {
        return this.world;
    }

    @Nullable
    public MinecraftServer getMinecraftServer() {
        return this.world.getMinecraftServer();
    }

    public EnumInteractionResult a(EntityHuman entityhuman, Vec3D vec3d, EnumHand enumhand) {
        return EnumInteractionResult.PASS;
    }

    public boolean ca() {
        return false;
    }

    protected void a(EntityLiving entityliving, Entity entity) {
        if (entity instanceof EntityLiving) {
            EnchantmentManager.a((EntityLiving) entity, (Entity) entityliving);
        }

        EnchantmentManager.b(entityliving, entity);
    }

    public void b(EntityPlayer entityplayer) {}

    public void c(EntityPlayer entityplayer) {}

    public float a(EnumBlockRotation enumblockrotation) {
        float f = MathHelper.g(this.yaw);

        switch (enumblockrotation) {
            case CLOCKWISE_180:
                return f + 180.0F;
            case COUNTERCLOCKWISE_90:
                return f + 270.0F;
            case CLOCKWISE_90:
                return f + 90.0F;
            default:
                return f;
        }
    }

    public float a(EnumBlockMirror enumblockmirror) {
        float f = MathHelper.g(this.yaw);

        switch (enumblockmirror) {
            case LEFT_RIGHT:
                return -f;
            case FRONT_BACK:
                return 180.0F - f;
            default:
                return f;
        }
    }

    public boolean cb() {
        return false;
    }

    public boolean cc() {
        boolean flag = this.aF;

        this.aF = false;
        return flag;
    }

    @Nullable
    public Entity getRidingPassenger() {
        return null;
    }

    public List<Entity> getPassengers() {
        return (List) (this.passengers.isEmpty() ? Collections.emptyList() : Lists.newArrayList(this.passengers));
    }

    public boolean w(Entity entity) {
        Iterator iterator = this.getPassengers().iterator();

        Entity entity1;

        do {
            if (!iterator.hasNext()) {
                return false;
            }

            entity1 = (Entity) iterator.next();
        } while (!entity1.equals(entity));

        return true;
    }

    public boolean a(Class<? extends Entity> oclass) {
        Iterator iterator = this.getPassengers().iterator();

        Entity entity;

        do {
            if (!iterator.hasNext()) {
                return false;
            }

            entity = (Entity) iterator.next();
        } while (!oclass.isAssignableFrom(entity.getClass()));

        return true;
    }

    public Collection<Entity> getAllPassengers() {
        Set<Entity> set = Sets.newHashSet();
        Iterator iterator = this.getPassengers().iterator();

        while (iterator.hasNext()) {
            Entity entity = (Entity) iterator.next();

            set.add(entity);
            entity.a(false, set);
        }

        return set;
    }

    public Stream<Entity> cg() {
        return Stream.concat(Stream.of(this), this.passengers.stream().flatMap(Entity::cg));
    }

    public boolean hasSinglePlayerPassenger() {
        Set<Entity> set = Sets.newHashSet();

        this.a(true, set);
        return set.size() == 1;
    }

    private void a(boolean flag, Set<Entity> set) {
        Entity entity;

        for (Iterator iterator = this.getPassengers().iterator(); iterator.hasNext(); entity.a(flag, set)) {
            entity = (Entity) iterator.next();
            if (!flag || EntityPlayer.class.isAssignableFrom(entity.getClass())) {
                set.add(entity);
            }
        }

    }

    public Entity getRootVehicle() {
        Entity entity;

        for (entity = this; entity.isPassenger(); entity = entity.getVehicle()) {
            ;
        }

        return entity;
    }

    public boolean isSameVehicle(Entity entity) {
        return this.getRootVehicle() == entity.getRootVehicle();
    }

    public boolean y(Entity entity) {
        Iterator iterator = this.getPassengers().iterator();

        Entity entity1;

        do {
            if (!iterator.hasNext()) {
                return false;
            }

            entity1 = (Entity) iterator.next();
            if (entity1.equals(entity)) {
                return true;
            }
        } while (!entity1.y(entity));

        return true;
    }

    public void a(Entity.a entity_a) {
        Iterator iterator = this.passengers.iterator();

        while (iterator.hasNext()) {
            Entity entity = (Entity) iterator.next();

            this.a(entity, entity_a);
        }

    }

    public boolean cj() {
        Entity entity = this.getRidingPassenger();

        return entity instanceof EntityHuman ? ((EntityHuman) entity).ec() : !this.world.isClientSide;
    }

    @Nullable
    public Entity getVehicle() {
        return this.vehicle;
    }

    public EnumPistonReaction getPushReaction() {
        return EnumPistonReaction.NORMAL;
    }

    public SoundCategory getSoundCategory() {
        return SoundCategory.NEUTRAL;
    }

    public int getMaxFireTicks() {
        return 1;
    }

    public CommandListenerWrapper getCommandListener() {
        return new CommandListenerWrapper(this, this.getPositionVector(), this.aX(), this.world instanceof WorldServer ? (WorldServer) this.world : null, this.y(), this.getDisplayName().getString(), this.getScoreboardDisplayName(), this.world.getMinecraftServer(), this);
    }

    protected int y() {
        return 0;
    }

    public boolean k(int i) {
        return this.y() >= i;
    }

    @Override
    public boolean shouldSendSuccess() {
        return this.world.getGameRules().getBoolean(GameRules.SEND_COMMAND_FEEDBACK);
    }

    @Override
    public boolean shouldSendFailure() {
        return true;
    }

    @Override
    public boolean shouldBroadcastCommands() {
        return true;
    }

    public void a(ArgumentAnchor.Anchor argumentanchor_anchor, Vec3D vec3d) {
        Vec3D vec3d1 = argumentanchor_anchor.a(this);
        double d0 = vec3d.x - vec3d1.x;
        double d1 = vec3d.y - vec3d1.y;
        double d2 = vec3d.z - vec3d1.z;
        double d3 = (double) MathHelper.sqrt(d0 * d0 + d2 * d2);

        this.pitch = MathHelper.g((float) (-(MathHelper.d(d1, d3) * 57.2957763671875D)));
        this.yaw = MathHelper.g((float) (MathHelper.d(d2, d0) * 57.2957763671875D) - 90.0F);
        this.setHeadRotation(this.yaw);
        this.lastPitch = this.pitch;
        this.lastYaw = this.yaw;
    }

    public boolean b(Tag<FluidType> tag) {
        AxisAlignedBB axisalignedbb = this.getBoundingBox().shrink(0.001D);
        int i = MathHelper.floor(axisalignedbb.minX);
        int j = MathHelper.f(axisalignedbb.maxX);
        int k = MathHelper.floor(axisalignedbb.minY);
        int l = MathHelper.f(axisalignedbb.maxY);
        int i1 = MathHelper.floor(axisalignedbb.minZ);
        int j1 = MathHelper.f(axisalignedbb.maxZ);

        if (!this.world.isAreaLoaded(i, k, i1, j, l, j1)) {
            return false;
        } else {
            double d0 = 0.0D;
            boolean flag = this.bM();
            boolean flag1 = false;
            Vec3D vec3d = Vec3D.a;
            int k1 = 0;
            BlockPosition.PooledBlockPosition blockposition_pooledblockposition = BlockPosition.PooledBlockPosition.r();
            Throwable throwable = null;

            try {
                for (int l1 = i; l1 < j; ++l1) {
                    for (int i2 = k; i2 < l; ++i2) {
                        for (int j2 = i1; j2 < j1; ++j2) {
                            blockposition_pooledblockposition.d(l1, i2, j2);
                            Fluid fluid = this.world.getFluid(blockposition_pooledblockposition);

                            if (fluid.a(tag)) {
                                double d1 = (double) ((float) i2 + fluid.getHeight(this.world, blockposition_pooledblockposition));

                                if (d1 >= axisalignedbb.minY) {
                                    flag1 = true;
                                    d0 = Math.max(d1 - axisalignedbb.minY, d0);
                                    if (flag) {
                                        Vec3D vec3d1 = fluid.c(this.world, blockposition_pooledblockposition);

                                        if (d0 < 0.4D) {
                                            vec3d1 = vec3d1.a(d0);
                                        }

                                        vec3d = vec3d.e(vec3d1);
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

            if (vec3d.f() > 0.0D) {
                if (k1 > 0) {
                    vec3d = vec3d.a(1.0D / (double) k1);
                }

                if (!(this instanceof EntityHuman)) {
                    vec3d = vec3d.d();
                }

                this.setMot(this.getMot().e(vec3d.a(0.014D)));
            }

            this.N = d0;
            return flag1;
        }
    }

    public double co() {
        return this.N;
    }

    public final float getWidth() {
        return this.size.width;
    }

    public final float getHeight() {
        return this.size.height;
    }

    public abstract Packet<?> L();

    public EntitySize a(EntityPose entitypose) {
        return this.f.k();
    }

    public Vec3D getPositionVector() {
        return new Vec3D(this.locX, this.locY, this.locZ);
    }

    public Vec3D getMot() {
        return this.mot;
    }

    public void setMot(Vec3D vec3d) {
        this.mot = vec3d;
    }

    public void setMot(double d0, double d1, double d2) {
        this.setMot(new Vec3D(d0, d1, d2));
    }

    public final double locX() {
        return this.locX;
    }

    public double c(double d0) {
        return this.locX + (double) this.getWidth() * d0;
    }

    public double d(double d0) {
        return this.c((2.0D * this.random.nextDouble() - 1.0D) * d0);
    }

    public final double locY() {
        return this.locY;
    }

    public double e(double d0) {
        return this.locY + (double) this.getHeight() * d0;
    }

    public double cv() {
        return this.e(this.random.nextDouble());
    }

    public double getHeadY() {
        return this.locY + (double) this.headHeight;
    }

    public final double locZ() {
        return this.locZ;
    }

    public double f(double d0) {
        return this.locZ + (double) this.getWidth() * d0;
    }

    public double g(double d0) {
        return this.f((2.0D * this.random.nextDouble() - 1.0D) * d0);
    }

    public void setPositionRaw(double d0, double d1, double d2) {
        this.locX = d0;
        this.locY = d1;
        this.locZ = d2;
    }

    public void checkDespawn() {}

    public void teleportAndSync(double d0, double d1, double d2) {
        this.setPositionRotation(d0, d1, d2, this.yaw, this.pitch);
    }

    @FunctionalInterface
    public interface a {

        void accept(Entity entity, double d0, double d1, double d2);
    }
}
