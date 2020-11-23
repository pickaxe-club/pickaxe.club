package net.minecraft.server;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// CraftBukkit start
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerVelocityEvent;
// CraftBukkit end

public class EntityTrackerEntry {

    private static final Logger LOGGER = LogManager.getLogger();
    private final WorldServer b;
    private final Entity tracker;
    private final int d;
    private final boolean e;
    private final Consumer<Packet<?>> f;
    private long xLoc;
    private long yLoc;
    private long zLoc;
    private int yRot;
    private int xRot;
    private int headYaw;
    private Vec3D m;
    private int tickCounter;
    private int o;
    private List<Entity> p;
    private boolean q;
    private boolean r;
    // CraftBukkit start
    private final Set<EntityPlayer> trackedPlayers;

    public EntityTrackerEntry(WorldServer worldserver, Entity entity, int i, boolean flag, Consumer<Packet<?>> consumer, Set<EntityPlayer> trackedPlayers) {
        this.trackedPlayers = trackedPlayers;
        // CraftBukkit end
        this.m = Vec3D.a;
        this.p = Collections.emptyList();
        this.b = worldserver;
        this.f = consumer;
        this.tracker = entity;
        this.d = i;
        this.e = flag;
        this.d();
        this.yRot = MathHelper.d(entity.yaw * 256.0F / 360.0F);
        this.xRot = MathHelper.d(entity.pitch * 256.0F / 360.0F);
        this.headYaw = MathHelper.d(entity.getHeadRotation() * 256.0F / 360.0F);
        this.r = entity.onGround;
    }

    public void a() {
        List<Entity> list = this.tracker.getPassengers();

        if (!list.equals(this.p)) {
            this.p = list;
            this.broadcastIncludingSelf(new PacketPlayOutMount(this.tracker)); // CraftBukkit
        }

        // PAIL : rename
        if (this.tracker instanceof EntityItemFrame /*&& this.tickCounter % 10 == 0*/) { // CraftBukkit - Moved below, should always enter this block
            EntityItemFrame entityitemframe = (EntityItemFrame) this.tracker;
            ItemStack itemstack = entityitemframe.getItem();

            if (this.tickCounter % 10 == 0 && itemstack.getItem() instanceof ItemWorldMap) { // CraftBukkit - Moved this.tickCounter % 10 logic here so item frames do not enter the other blocks
                WorldMap worldmap = ItemWorldMap.getSavedMap(itemstack, this.b);
                Iterator iterator = this.trackedPlayers.iterator(); // CraftBukkit

                while (iterator.hasNext()) {
                    EntityPlayer entityplayer = (EntityPlayer) iterator.next();

                    worldmap.a((EntityHuman) entityplayer, itemstack);
                    Packet<?> packet = ((ItemWorldMap) itemstack.getItem()).a(itemstack, (World) this.b, (EntityHuman) entityplayer);

                    if (packet != null) {
                        entityplayer.playerConnection.sendPacket(packet);
                    }
                }
            }

            this.c();
        }

        if (this.tickCounter % this.d == 0 || this.tracker.impulse || this.tracker.getDataWatcher().a()) {
            int i;
            int j;

            if (this.tracker.isPassenger()) {
                i = MathHelper.d(this.tracker.yaw * 256.0F / 360.0F);
                j = MathHelper.d(this.tracker.pitch * 256.0F / 360.0F);
                boolean flag = Math.abs(i - this.yRot) >= 1 || Math.abs(j - this.xRot) >= 1;

                if (flag) {
                    this.f.accept(new PacketPlayOutEntity.PacketPlayOutEntityLook(this.tracker.getId(), (byte) i, (byte) j, this.tracker.onGround));
                    this.yRot = i;
                    this.xRot = j;
                }

                this.d();
                this.c();
                this.q = true;
            } else {
                ++this.o;
                i = MathHelper.d(this.tracker.yaw * 256.0F / 360.0F);
                j = MathHelper.d(this.tracker.pitch * 256.0F / 360.0F);
                Vec3D vec3d = this.tracker.getPositionVector().d(PacketPlayOutEntity.a(this.xLoc, this.yLoc, this.zLoc));
                boolean flag1 = vec3d.g() >= 7.62939453125E-6D;
                Packet<?> packet1 = null;
                boolean flag2 = flag1 || this.tickCounter % 60 == 0;
                boolean flag3 = Math.abs(i - this.yRot) >= 1 || Math.abs(j - this.xRot) >= 1;

                // CraftBukkit start - Code moved from below
                if (flag2) {
                    this.d();
                }

                if (flag3) {
                    this.yRot = i;
                    this.xRot = j;
                }
                // CraftBukkit end

                if (this.tickCounter > 0 || this.tracker instanceof EntityArrow) {
                    long k = PacketPlayOutEntity.a(vec3d.x);
                    long l = PacketPlayOutEntity.a(vec3d.y);
                    long i1 = PacketPlayOutEntity.a(vec3d.z);
                    boolean flag4 = k < -32768L || k > 32767L || l < -32768L || l > 32767L || i1 < -32768L || i1 > 32767L;

                    if (!flag4 && this.o <= 400 && !this.q && this.r == this.tracker.onGround) {
                        if ((!flag2 || !flag3) && !(this.tracker instanceof EntityArrow)) {
                            if (flag2) {
                                packet1 = new PacketPlayOutEntity.PacketPlayOutRelEntityMove(this.tracker.getId(), (short) ((int) k), (short) ((int) l), (short) ((int) i1), this.tracker.onGround);
                            } else if (flag3) {
                                packet1 = new PacketPlayOutEntity.PacketPlayOutEntityLook(this.tracker.getId(), (byte) i, (byte) j, this.tracker.onGround);
                            }
                        } else {
                            packet1 = new PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook(this.tracker.getId(), (short) ((int) k), (short) ((int) l), (short) ((int) i1), (byte) i, (byte) j, this.tracker.onGround);
                        }
                    } else {
                        this.r = this.tracker.onGround;
                        this.o = 0;
                        packet1 = new PacketPlayOutEntityTeleport(this.tracker);
                    }
                }

                if ((this.e || this.tracker.impulse || this.tracker instanceof EntityLiving && ((EntityLiving) this.tracker).isGliding()) && this.tickCounter > 0) {
                    Vec3D vec3d1 = this.tracker.getMot();
                    double d0 = vec3d1.distanceSquared(this.m);

                    if (d0 > 1.0E-7D || d0 > 0.0D && vec3d1.g() == 0.0D) {
                        this.m = vec3d1;
                        this.f.accept(new PacketPlayOutEntityVelocity(this.tracker.getId(), this.m));
                    }
                }

                if (packet1 != null) {
                    this.f.accept(packet1);
                }

                this.c();
                /* CraftBukkit start - Code moved up
                if (flag2) {
                    this.d();
                }

                if (flag3) {
                    this.yRot = i;
                    this.xRot = j;
                }
                // CraftBukkit end */

                this.q = false;
            }

            i = MathHelper.d(this.tracker.getHeadRotation() * 256.0F / 360.0F);
            if (Math.abs(i - this.headYaw) >= 1) {
                this.f.accept(new PacketPlayOutEntityHeadRotation(this.tracker, (byte) i));
                this.headYaw = i;
            }

            this.tracker.impulse = false;
        }

        ++this.tickCounter;
        if (this.tracker.velocityChanged) {
            // CraftBukkit start - Create PlayerVelocity event
            boolean cancelled = false;

            if (this.tracker instanceof EntityPlayer) {
                Player player = (Player) this.tracker.getBukkitEntity();
                org.bukkit.util.Vector velocity = player.getVelocity();

                PlayerVelocityEvent event = new PlayerVelocityEvent(player, velocity.clone());
                this.tracker.world.getServer().getPluginManager().callEvent(event);

                if (event.isCancelled()) {
                    cancelled = true;
                } else if (!velocity.equals(event.getVelocity())) {
                    player.setVelocity(event.getVelocity());
                }
            }

            if (!cancelled) {
                this.broadcastIncludingSelf(new PacketPlayOutEntityVelocity(this.tracker));
            }
            // CraftBukkit end
            this.tracker.velocityChanged = false;
        }

    }

    public void a(EntityPlayer entityplayer) {
        this.tracker.c(entityplayer);
        entityplayer.c(this.tracker);
    }

    public void b(EntityPlayer entityplayer) {
        PlayerConnection playerconnection = entityplayer.playerConnection;

        entityplayer.playerConnection.getClass();
        this.a(playerconnection::sendPacket, entityplayer); // CraftBukkit - add player
        this.tracker.b(entityplayer);
        entityplayer.d(this.tracker);
    }

    public void a(Consumer<Packet<?>> consumer, EntityPlayer entityplayer) { // CraftBukkit - add player
        if (this.tracker.dead) {
            // CraftBukkit start - Remove useless error spam, just return
            // EntityTrackerEntry.LOGGER.warn("Fetching packet for removed entity " + this.tracker);
            return;
            // CraftBukkit end
        }

        Packet<?> packet = this.tracker.L();

        this.headYaw = MathHelper.d(this.tracker.getHeadRotation() * 256.0F / 360.0F);
        consumer.accept(packet);
        if (!this.tracker.getDataWatcher().d()) {
            consumer.accept(new PacketPlayOutEntityMetadata(this.tracker.getId(), this.tracker.getDataWatcher(), true));
        }

        boolean flag = this.e;

        if (this.tracker instanceof EntityLiving) {
            AttributeMapServer attributemapserver = (AttributeMapServer) ((EntityLiving) this.tracker).getAttributeMap();
            Collection<AttributeInstance> collection = attributemapserver.c();

            // CraftBukkit start - If sending own attributes send scaled health instead of current maximum health
            if (this.tracker.getId() == entityplayer.getId()) {
                ((EntityPlayer) this.tracker).getBukkitEntity().injectScaledMaxHealth(collection, false);
            }
            // CraftBukkit end

            if (!collection.isEmpty()) {
                consumer.accept(new PacketPlayOutUpdateAttributes(this.tracker.getId(), collection));
            }

            if (((EntityLiving) this.tracker).isGliding()) {
                flag = true;
            }
        }

        this.m = this.tracker.getMot();
        if (flag && !(packet instanceof PacketPlayOutSpawnEntityLiving)) {
            consumer.accept(new PacketPlayOutEntityVelocity(this.tracker.getId(), this.m));
        }

        if (this.tracker instanceof EntityLiving) {
            EnumItemSlot[] aenumitemslot = EnumItemSlot.values();
            int i = aenumitemslot.length;

            for (int j = 0; j < i; ++j) {
                EnumItemSlot enumitemslot = aenumitemslot[j];
                ItemStack itemstack = ((EntityLiving) this.tracker).getEquipment(enumitemslot);

                if (!itemstack.isEmpty()) {
                    consumer.accept(new PacketPlayOutEntityEquipment(this.tracker.getId(), enumitemslot, itemstack));
                }
            }
        }

        // CraftBukkit start - Fix for nonsensical head yaw
        this.headYaw = MathHelper.d(this.tracker.getHeadRotation() * 256.0F / 360.0F);
        consumer.accept(new PacketPlayOutEntityHeadRotation(this.tracker, (byte) headYaw));
        // CraftBukkit end

        if (this.tracker instanceof EntityLiving) {
            EntityLiving entityliving = (EntityLiving) this.tracker;
            Iterator iterator = entityliving.getEffects().iterator();

            while (iterator.hasNext()) {
                MobEffect mobeffect = (MobEffect) iterator.next();

                consumer.accept(new PacketPlayOutEntityEffect(this.tracker.getId(), mobeffect));
            }
        }

        if (!this.tracker.getPassengers().isEmpty()) {
            consumer.accept(new PacketPlayOutMount(this.tracker));
        }

        if (this.tracker.isPassenger()) {
            consumer.accept(new PacketPlayOutMount(this.tracker.getVehicle()));
        }

        if (this.tracker instanceof EntityInsentient) {
            EntityInsentient entityinsentient = (EntityInsentient) this.tracker;

            if (entityinsentient.isLeashed()) {
                consumer.accept(new PacketPlayOutAttachEntity(entityinsentient, entityinsentient.getLeashHolder()));
            }
        }

    }

    private void c() {
        DataWatcher datawatcher = this.tracker.getDataWatcher();

        if (datawatcher.a()) {
            this.broadcastIncludingSelf(new PacketPlayOutEntityMetadata(this.tracker.getId(), datawatcher, false));
        }

        if (this.tracker instanceof EntityLiving) {
            AttributeMapServer attributemapserver = (AttributeMapServer) ((EntityLiving) this.tracker).getAttributeMap();
            Set<AttributeInstance> set = attributemapserver.getAttributes();

            if (!set.isEmpty()) {
                // CraftBukkit start - Send scaled max health
                if (this.tracker instanceof EntityPlayer) {
                    ((EntityPlayer) this.tracker).getBukkitEntity().injectScaledMaxHealth(set, false);
                }
                // CraftBukkit end
                this.broadcastIncludingSelf(new PacketPlayOutUpdateAttributes(this.tracker.getId(), set));
            }

            set.clear();
        }

    }

    private void d() {
        this.xLoc = PacketPlayOutEntity.a(this.tracker.locX());
        this.yLoc = PacketPlayOutEntity.a(this.tracker.locY());
        this.zLoc = PacketPlayOutEntity.a(this.tracker.locZ());
    }

    public Vec3D b() {
        return PacketPlayOutEntity.a(this.xLoc, this.yLoc, this.zLoc);
    }

    private void broadcastIncludingSelf(Packet<?> packet) {
        this.f.accept(packet);
        if (this.tracker instanceof EntityPlayer) {
            ((EntityPlayer) this.tracker).playerConnection.sendPacket(packet);
        }

    }
}
