package net.minecraft.server;

import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;

public abstract class EntityTameableAnimal extends EntityAnimal {

    protected static final DataWatcherObject<Byte> bv = DataWatcher.a(EntityTameableAnimal.class, DataWatcherRegistry.a);
    protected static final DataWatcherObject<Optional<UUID>> bw = DataWatcher.a(EntityTameableAnimal.class, DataWatcherRegistry.o);
    private boolean willSit;

    protected EntityTameableAnimal(EntityTypes<? extends EntityTameableAnimal> entitytypes, World world) {
        super(entitytypes, world);
        this.eM();
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.datawatcher.register(EntityTameableAnimal.bv, (byte) 0);
        this.datawatcher.register(EntityTameableAnimal.bw, Optional.empty());
    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);
        if (this.getOwnerUUID() != null) {
            nbttagcompound.a("Owner", this.getOwnerUUID());
        }

        nbttagcompound.setBoolean("Sitting", this.willSit);
    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        UUID uuid;

        if (nbttagcompound.b("Owner")) {
            uuid = nbttagcompound.a("Owner");
        } else {
            String s = nbttagcompound.getString("Owner");

            uuid = NameReferencingFileConverter.a(this.getMinecraftServer(), s);
        }

        if (uuid != null) {
            try {
                this.setOwnerUUID(uuid);
                this.setTamed(true);
            } catch (Throwable throwable) {
                this.setTamed(false);
            }
        }

        this.willSit = nbttagcompound.getBoolean("Sitting");
        this.setSitting(this.willSit);
    }

    @Override
    public boolean a(EntityHuman entityhuman) {
        return !this.isLeashed();
    }

    public boolean isTamed() {
        return ((Byte) this.datawatcher.get(EntityTameableAnimal.bv) & 4) != 0;
    }

    public void setTamed(boolean flag) {
        byte b0 = (Byte) this.datawatcher.get(EntityTameableAnimal.bv);

        if (flag) {
            this.datawatcher.set(EntityTameableAnimal.bv, (byte) (b0 | 4));
        } else {
            this.datawatcher.set(EntityTameableAnimal.bv, (byte) (b0 & -5));
        }

        this.eM();
    }

    protected void eM() {}

    public boolean isSitting() {
        return ((Byte) this.datawatcher.get(EntityTameableAnimal.bv) & 1) != 0;
    }

    public void setSitting(boolean flag) {
        byte b0 = (Byte) this.datawatcher.get(EntityTameableAnimal.bv);

        if (flag) {
            this.datawatcher.set(EntityTameableAnimal.bv, (byte) (b0 | 1));
        } else {
            this.datawatcher.set(EntityTameableAnimal.bv, (byte) (b0 & -2));
        }

    }

    @Nullable
    public UUID getOwnerUUID() {
        return (UUID) ((Optional) this.datawatcher.get(EntityTameableAnimal.bw)).orElse((Object) null);
    }

    public void setOwnerUUID(@Nullable UUID uuid) {
        this.datawatcher.set(EntityTameableAnimal.bw, Optional.ofNullable(uuid));
    }

    public void tame(EntityHuman entityhuman) {
        this.setTamed(true);
        this.setOwnerUUID(entityhuman.getUniqueID());
        if (entityhuman instanceof EntityPlayer) {
            CriterionTriggers.x.a((EntityPlayer) entityhuman, (EntityAnimal) this);
        }

    }

    @Nullable
    public EntityLiving getOwner() {
        try {
            UUID uuid = this.getOwnerUUID();

            return uuid == null ? null : this.world.b(uuid);
        } catch (IllegalArgumentException illegalargumentexception) {
            return null;
        }
    }

    @Override
    public boolean d(EntityLiving entityliving) {
        return this.j(entityliving) ? false : super.d(entityliving);
    }

    public boolean j(EntityLiving entityliving) {
        return entityliving == this.getOwner();
    }

    public boolean a(EntityLiving entityliving, EntityLiving entityliving1) {
        return true;
    }

    @Override
    public ScoreboardTeamBase getScoreboardTeam() {
        if (this.isTamed()) {
            EntityLiving entityliving = this.getOwner();

            if (entityliving != null) {
                return entityliving.getScoreboardTeam();
            }
        }

        return super.getScoreboardTeam();
    }

    @Override
    public boolean r(Entity entity) {
        if (this.isTamed()) {
            EntityLiving entityliving = this.getOwner();

            if (entity == entityliving) {
                return true;
            }

            if (entityliving != null) {
                return entityliving.r(entity);
            }
        }

        return super.r(entity);
    }

    @Override
    public void die(DamageSource damagesource) {
        if (!this.world.isClientSide && this.world.getGameRules().getBoolean(GameRules.SHOW_DEATH_MESSAGES) && this.getOwner() instanceof EntityPlayer) {
            this.getOwner().sendMessage(this.getCombatTracker().getDeathMessage(), SystemUtils.b);
        }

        super.die(damagesource);
    }

    public boolean isWillSit() {
        return this.willSit;
    }

    public void setWillSit(boolean flag) {
        this.willSit = flag;
    }
}
