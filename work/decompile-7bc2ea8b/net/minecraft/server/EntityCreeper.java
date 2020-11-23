package net.minecraft.server;

import java.util.Collection;
import java.util.Iterator;

public class EntityCreeper extends EntityMonster {

    private static final DataWatcherObject<Integer> b = DataWatcher.a(EntityCreeper.class, DataWatcherRegistry.b);
    private static final DataWatcherObject<Boolean> POWERED = DataWatcher.a(EntityCreeper.class, DataWatcherRegistry.i);
    private static final DataWatcherObject<Boolean> d = DataWatcher.a(EntityCreeper.class, DataWatcherRegistry.i);
    private int bv;
    private int fuseTicks;
    public int maxFuseTicks = 30;
    public int explosionRadius = 3;
    private int bz;

    public EntityCreeper(EntityTypes<? extends EntityCreeper> entitytypes, World world) {
        super(entitytypes, world);
    }

    @Override
    protected void initPathfinder() {
        this.goalSelector.a(1, new PathfinderGoalFloat(this));
        this.goalSelector.a(2, new PathfinderGoalSwell(this));
        this.goalSelector.a(3, new PathfinderGoalAvoidTarget<>(this, EntityOcelot.class, 6.0F, 1.0D, 1.2D));
        this.goalSelector.a(3, new PathfinderGoalAvoidTarget<>(this, EntityCat.class, 6.0F, 1.0D, 1.2D));
        this.goalSelector.a(4, new PathfinderGoalMeleeAttack(this, 1.0D, false));
        this.goalSelector.a(5, new PathfinderGoalRandomStrollLand(this, 0.8D));
        this.goalSelector.a(6, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.goalSelector.a(6, new PathfinderGoalRandomLookaround(this));
        this.targetSelector.a(1, new PathfinderGoalNearestAttackableTarget<>(this, EntityHuman.class, true));
        this.targetSelector.a(2, new PathfinderGoalHurtByTarget(this, new Class[0]));
    }

    public static AttributeProvider.Builder m() {
        return EntityMonster.eS().a(GenericAttributes.MOVEMENT_SPEED, 0.25D);
    }

    @Override
    public int bL() {
        return this.getGoalTarget() == null ? 3 : 3 + (int) (this.getHealth() - 1.0F);
    }

    @Override
    public boolean b(float f, float f1) {
        boolean flag = super.b(f, f1);

        this.fuseTicks = (int) ((float) this.fuseTicks + f * 1.5F);
        if (this.fuseTicks > this.maxFuseTicks - 5) {
            this.fuseTicks = this.maxFuseTicks - 5;
        }

        return flag;
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.datawatcher.register(EntityCreeper.b, -1);
        this.datawatcher.register(EntityCreeper.POWERED, false);
        this.datawatcher.register(EntityCreeper.d, false);
    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);
        if ((Boolean) this.datawatcher.get(EntityCreeper.POWERED)) {
            nbttagcompound.setBoolean("powered", true);
        }

        nbttagcompound.setShort("Fuse", (short) this.maxFuseTicks);
        nbttagcompound.setByte("ExplosionRadius", (byte) this.explosionRadius);
        nbttagcompound.setBoolean("ignited", this.isIgnited());
    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        this.datawatcher.set(EntityCreeper.POWERED, nbttagcompound.getBoolean("powered"));
        if (nbttagcompound.hasKeyOfType("Fuse", 99)) {
            this.maxFuseTicks = nbttagcompound.getShort("Fuse");
        }

        if (nbttagcompound.hasKeyOfType("ExplosionRadius", 99)) {
            this.explosionRadius = nbttagcompound.getByte("ExplosionRadius");
        }

        if (nbttagcompound.getBoolean("ignited")) {
            this.ignite();
        }

    }

    @Override
    public void tick() {
        if (this.isAlive()) {
            this.bv = this.fuseTicks;
            if (this.isIgnited()) {
                this.a(1);
            }

            int i = this.eL();

            if (i > 0 && this.fuseTicks == 0) {
                this.playSound(SoundEffects.ENTITY_CREEPER_PRIMED, 1.0F, 0.5F);
            }

            this.fuseTicks += i;
            if (this.fuseTicks < 0) {
                this.fuseTicks = 0;
            }

            if (this.fuseTicks >= this.maxFuseTicks) {
                this.fuseTicks = this.maxFuseTicks;
                this.explode();
            }
        }

        super.tick();
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.ENTITY_CREEPER_HURT;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.ENTITY_CREEPER_DEATH;
    }

    @Override
    protected void dropDeathLoot(DamageSource damagesource, int i, boolean flag) {
        super.dropDeathLoot(damagesource, i, flag);
        Entity entity = damagesource.getEntity();

        if (entity != this && entity instanceof EntityCreeper) {
            EntityCreeper entitycreeper = (EntityCreeper) entity;

            if (entitycreeper.canCauseHeadDrop()) {
                entitycreeper.setCausedHeadDrop();
                this.a((IMaterial) Items.CREEPER_HEAD);
            }
        }

    }

    @Override
    public boolean attackEntity(Entity entity) {
        return true;
    }

    public boolean isPowered() {
        return (Boolean) this.datawatcher.get(EntityCreeper.POWERED);
    }

    public int eL() {
        return (Integer) this.datawatcher.get(EntityCreeper.b);
    }

    public void a(int i) {
        this.datawatcher.set(EntityCreeper.b, i);
    }

    @Override
    public void onLightningStrike(EntityLightning entitylightning) {
        super.onLightningStrike(entitylightning);
        this.datawatcher.set(EntityCreeper.POWERED, true);
    }

    @Override
    protected EnumInteractionResult b(EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);

        if (itemstack.getItem() == Items.FLINT_AND_STEEL) {
            this.world.playSound(entityhuman, this.locX(), this.locY(), this.locZ(), SoundEffects.ITEM_FLINTANDSTEEL_USE, this.getSoundCategory(), 1.0F, this.random.nextFloat() * 0.4F + 0.8F);
            if (!this.world.isClientSide) {
                this.ignite();
                itemstack.damage(1, entityhuman, (entityhuman1) -> {
                    entityhuman1.broadcastItemBreak(enumhand);
                });
            }

            return EnumInteractionResult.a(this.world.isClientSide);
        } else {
            return super.b(entityhuman, enumhand);
        }
    }

    public void explode() {
        if (!this.world.isClientSide) {
            Explosion.Effect explosion_effect = this.world.getGameRules().getBoolean(GameRules.MOB_GRIEFING) ? Explosion.Effect.DESTROY : Explosion.Effect.NONE;
            float f = this.isPowered() ? 2.0F : 1.0F;

            this.killed = true;
            this.world.explode(this, this.locX(), this.locY(), this.locZ(), (float) this.explosionRadius * f, explosion_effect);
            this.die();
            this.createEffectCloud();
        }

    }

    private void createEffectCloud() {
        Collection<MobEffect> collection = this.getEffects();

        if (!collection.isEmpty()) {
            EntityAreaEffectCloud entityareaeffectcloud = new EntityAreaEffectCloud(this.world, this.locX(), this.locY(), this.locZ());

            entityareaeffectcloud.setRadius(2.5F);
            entityareaeffectcloud.setRadiusOnUse(-0.5F);
            entityareaeffectcloud.setWaitTime(10);
            entityareaeffectcloud.setDuration(entityareaeffectcloud.getDuration() / 2);
            entityareaeffectcloud.setRadiusPerTick(-entityareaeffectcloud.getRadius() / (float) entityareaeffectcloud.getDuration());
            Iterator iterator = collection.iterator();

            while (iterator.hasNext()) {
                MobEffect mobeffect = (MobEffect) iterator.next();

                entityareaeffectcloud.addEffect(new MobEffect(mobeffect));
            }

            this.world.addEntity(entityareaeffectcloud);
        }

    }

    public boolean isIgnited() {
        return (Boolean) this.datawatcher.get(EntityCreeper.d);
    }

    public void ignite() {
        this.datawatcher.set(EntityCreeper.d, true);
    }

    public boolean canCauseHeadDrop() {
        return this.isPowered() && this.bz < 1;
    }

    public void setCausedHeadDrop() {
        ++this.bz;
    }
}
