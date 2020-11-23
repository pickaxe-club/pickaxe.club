package net.minecraft.server;

import java.util.Map.Entry;
// CraftBukkit start
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;
// CraftBukkit end

public class EntityExperienceOrb extends Entity {

    public int b;
    public int c;
    public int d;
    private int e;
    public int value;
    private EntityHuman targetPlayer;
    private int targetTime;

    public EntityExperienceOrb(World world, double d0, double d1, double d2, int i) {
        this(EntityTypes.EXPERIENCE_ORB, world);
        this.setPosition(d0, d1, d2);
        this.yaw = (float) (this.random.nextDouble() * 360.0D);
        this.setMot((this.random.nextDouble() * 0.20000000298023224D - 0.10000000149011612D) * 2.0D, this.random.nextDouble() * 0.2D * 2.0D, (this.random.nextDouble() * 0.20000000298023224D - 0.10000000149011612D) * 2.0D);
        this.value = i;
    }

    public EntityExperienceOrb(EntityTypes<? extends EntityExperienceOrb> entitytypes, World world) {
        super(entitytypes, world);
        this.e = 5;
    }

    @Override
    protected boolean playStepSound() {
        return false;
    }

    @Override
    protected void initDatawatcher() {}

    @Override
    public void tick() {
        super.tick();
        EntityHuman prevTarget = this.targetPlayer;// CraftBukkit - store old target
        if (this.d > 0) {
            --this.d;
        }

        this.lastX = this.locX();
        this.lastY = this.locY();
        this.lastZ = this.locZ();
        if (this.a(TagsFluid.WATER)) {
            this.k();
        } else if (!this.isNoGravity()) {
            this.setMot(this.getMot().add(0.0D, -0.03D, 0.0D));
        }

        if (this.world.getFluid(new BlockPosition(this)).a(TagsFluid.LAVA)) {
            this.setMot((double) ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F), 0.20000000298023224D, (double) ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F));
            this.a(SoundEffects.ENTITY_GENERIC_BURN, 0.4F, 2.0F + this.random.nextFloat() * 0.4F);
        }

        if (!this.world.a(this.getBoundingBox())) {
            this.k(this.locX(), (this.getBoundingBox().minY + this.getBoundingBox().maxY) / 2.0D, this.locZ());
        }

        double d0 = 8.0D;

        if (this.targetTime < this.b - 20 + this.getId() % 100) {
            if (this.targetPlayer == null || this.targetPlayer.h((Entity) this) > 64.0D) {
                this.targetPlayer = this.world.findNearbyPlayer(this, 8.0D);
            }

            this.targetTime = this.b;
        }

        if (this.targetPlayer != null && this.targetPlayer.isSpectator()) {
            this.targetPlayer = null;
        }

        // CraftBukkit start
        boolean cancelled = false;
        if (this.targetPlayer != prevTarget) {
            EntityTargetLivingEntityEvent event = CraftEventFactory.callEntityTargetLivingEvent(this, targetPlayer, (targetPlayer != null) ? EntityTargetEvent.TargetReason.CLOSEST_PLAYER : EntityTargetEvent.TargetReason.FORGOT_TARGET);
            EntityLiving target = (event.getTarget() == null) ? null : ((org.bukkit.craftbukkit.entity.CraftLivingEntity) event.getTarget()).getHandle();
            cancelled = event.isCancelled();

            if (cancelled) {
                targetPlayer = prevTarget;
            } else {
                targetPlayer = (target instanceof EntityHuman) ? (EntityHuman) target : null;
            }
        }

        if (this.targetPlayer != null && !cancelled) {
            // CraftBukkit end
            Vec3D vec3d = new Vec3D(this.targetPlayer.locX() - this.locX(), this.targetPlayer.locY() + (double) this.targetPlayer.getHeadHeight() / 2.0D - this.locY(), this.targetPlayer.locZ() - this.locZ());
            double d1 = vec3d.g();

            if (d1 < 64.0D) {
                double d2 = 1.0D - Math.sqrt(d1) / 8.0D;

                this.setMot(this.getMot().e(vec3d.d().a(d2 * d2 * 0.1D)));
            }
        }

        this.move(EnumMoveType.SELF, this.getMot());
        float f = 0.98F;

        if (this.onGround) {
            f = this.world.getType(new BlockPosition(this.locX(), this.locY() - 1.0D, this.locZ())).getBlock().l() * 0.98F;
        }

        this.setMot(this.getMot().d((double) f, 0.98D, (double) f));
        if (this.onGround) {
            this.setMot(this.getMot().d(1.0D, -0.9D, 1.0D));
        }

        ++this.b;
        ++this.c;
        if (this.c >= 6000) {
            this.die();
        }

    }

    private void k() {
        Vec3D vec3d = this.getMot();

        this.setMot(vec3d.x * 0.9900000095367432D, Math.min(vec3d.y + 5.000000237487257E-4D, 0.05999999865889549D), vec3d.z * 0.9900000095367432D);
    }

    @Override
    protected void aD() {}

    @Override
    protected void burn(float i) { // CraftBukkit - int -> float
        this.damageEntity(DamageSource.FIRE, (float) i);
    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        if (this.isInvulnerable(damagesource)) {
            return false;
        } else {
            this.velocityChanged();
            this.e = (int) ((float) this.e - f);
            if (this.e <= 0) {
                this.die();
            }

            return false;
        }
    }

    @Override
    public void b(NBTTagCompound nbttagcompound) {
        nbttagcompound.setShort("Health", (short) this.e);
        nbttagcompound.setShort("Age", (short) this.c);
        nbttagcompound.setShort("Value", (short) this.value);
    }

    @Override
    public void a(NBTTagCompound nbttagcompound) {
        this.e = nbttagcompound.getShort("Health");
        this.c = nbttagcompound.getShort("Age");
        this.value = nbttagcompound.getShort("Value");
    }

    @Override
    public void pickup(EntityHuman entityhuman) {
        if (!this.world.isClientSide) {
            if (this.d == 0 && entityhuman.bC == 0) {
                entityhuman.bC = 2;
                entityhuman.receive(this, 1);
                Entry<EnumItemSlot, ItemStack> entry = EnchantmentManager.b(Enchantments.MENDING, (EntityLiving) entityhuman);

                if (entry != null) {
                    ItemStack itemstack = (ItemStack) entry.getValue();

                    if (!itemstack.isEmpty() && itemstack.f()) {
                        int i = Math.min(this.c(this.value), itemstack.getDamage());

                        // CraftBukkit start
                        org.bukkit.event.player.PlayerItemMendEvent event = CraftEventFactory.callPlayerItemMendEvent(entityhuman, this, itemstack, i);
                        i = event.getRepairAmount();
                        if (!event.isCancelled()) {
                            this.value -= this.b(i);
                            itemstack.setDamage(itemstack.getDamage() - i);
                        }
                        // CraftBukkit end
                    }
                }

                if (this.value > 0) {
                    entityhuman.giveExp(CraftEventFactory.callPlayerExpChangeEvent(entityhuman, this.value).getAmount()); // CraftBukkit - this.value -> event.getAmount()
                }

                this.die();
            }

        }
    }

    private int b(int i) {
        return i / 2;
    }

    private int c(int i) {
        return i * 2;
    }

    public int f() {
        return this.value;
    }

    public static int getOrbValue(int i) {
        // CraftBukkit start
        if (i > 162670129) return i - 100000;
        if (i > 81335063) return 81335063;
        if (i > 40667527) return 40667527;
        if (i > 20333759) return 20333759;
        if (i > 10166857) return 10166857;
        if (i > 5083423) return 5083423;
        if (i > 2541701) return 2541701;
        if (i > 1270849) return 1270849;
        if (i > 635413) return 635413;
        if (i > 317701) return 317701;
        if (i > 158849) return 158849;
        if (i > 79423) return 79423;
        if (i > 39709) return 39709;
        if (i > 19853) return 19853;
        if (i > 9923) return 9923;
        if (i > 4957) return 4957;
        // CraftBukkit end
        return i >= 2477 ? 2477 : (i >= 1237 ? 1237 : (i >= 617 ? 617 : (i >= 307 ? 307 : (i >= 149 ? 149 : (i >= 73 ? 73 : (i >= 37 ? 37 : (i >= 17 ? 17 : (i >= 7 ? 7 : (i >= 3 ? 3 : 1)))))))));
    }

    @Override
    public boolean bA() {
        return false;
    }

    @Override
    public Packet<?> L() {
        return new PacketPlayOutSpawnEntityExperienceOrb(this);
    }
}
