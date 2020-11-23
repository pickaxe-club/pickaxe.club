package net.minecraft.server;

import java.util.Iterator;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason; // CraftBukkit

public abstract class EntityHorseAbstract extends EntityAnimal implements IInventoryListener, IJumpable {

    private static final Predicate<EntityLiving> bF = (entityliving) -> {
        return entityliving instanceof EntityHorseAbstract && ((EntityHorseAbstract) entityliving).hasReproduced();
    };
    private static final PathfinderTargetCondition bG = (new PathfinderTargetCondition()).a(16.0D).a().b().c().a(EntityHorseAbstract.bF);
    public static final IAttribute attributeJumpStrength = (new AttributeRanged((IAttribute) null, "horse.jumpStrength", 0.7D, 0.0D, 2.0D)).a("Jump Strength").a(true);
    private static final DataWatcherObject<Byte> bH = DataWatcher.a(EntityHorseAbstract.class, DataWatcherRegistry.a);
    private static final DataWatcherObject<Optional<UUID>> bI = DataWatcher.a(EntityHorseAbstract.class, DataWatcherRegistry.o);
    private int bJ;
    private int bK;
    private int bL;
    public int bx;
    public int by;
    protected boolean bz;
    public InventorySubcontainer inventoryChest;
    protected int bB;
    protected float jumpPower;
    private boolean canSlide;
    private float bN;
    private float bO;
    private float bP;
    private float bQ;
    private float bR;
    private float bS;
    protected boolean bD = true;
    protected int bE;
    public int maxDomestication = 100; // CraftBukkit - store max domestication value

    protected EntityHorseAbstract(EntityTypes<? extends EntityHorseAbstract> entitytypes, World world) {
        super(entitytypes, world);
        this.H = 1.0F;
        this.loadChest();
    }

    @Override
    protected void initPathfinder() {
        this.goalSelector.a(1, new PathfinderGoalPanic(this, 1.2D));
        this.goalSelector.a(1, new PathfinderGoalTame(this, 1.2D));
        this.goalSelector.a(2, new PathfinderGoalBreed(this, 1.0D, EntityHorseAbstract.class));
        this.goalSelector.a(4, new PathfinderGoalFollowParent(this, 1.0D));
        this.goalSelector.a(6, new PathfinderGoalRandomStrollLand(this, 0.7D));
        this.goalSelector.a(7, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 6.0F));
        this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
        this.ez();
    }

    protected void ez() {
        this.goalSelector.a(0, new PathfinderGoalFloat(this));
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.datawatcher.register(EntityHorseAbstract.bH, (byte) 0);
        this.datawatcher.register(EntityHorseAbstract.bI, Optional.empty());
    }

    protected boolean t(int i) {
        return ((Byte) this.datawatcher.get(EntityHorseAbstract.bH) & i) != 0;
    }

    protected void d(int i, boolean flag) {
        byte b0 = (Byte) this.datawatcher.get(EntityHorseAbstract.bH);

        if (flag) {
            this.datawatcher.set(EntityHorseAbstract.bH, (byte) (b0 | i));
        } else {
            this.datawatcher.set(EntityHorseAbstract.bH, (byte) (b0 & ~i));
        }

    }

    public boolean isTamed() {
        return this.t(2);
    }

    @Nullable
    public UUID getOwnerUUID() {
        return (UUID) ((Optional) this.datawatcher.get(EntityHorseAbstract.bI)).orElse((Object) null);
    }

    public void setOwnerUUID(@Nullable UUID uuid) {
        this.datawatcher.set(EntityHorseAbstract.bI, Optional.ofNullable(uuid));
    }

    public boolean eC() {
        return this.bz;
    }

    public void setTamed(boolean flag) {
        this.d(2, flag);
    }

    public void t(boolean flag) {
        this.bz = flag;
    }

    @Override
    public boolean a(EntityHuman entityhuman) {
        return super.a(entityhuman) && this.getMonsterType() != EnumMonsterType.UNDEAD;
    }

    @Override
    protected void u(float f) {
        if (f > 6.0F && this.eD()) {
            this.w(false);
        }

    }

    public boolean eD() {
        return this.t(16);
    }

    public boolean eE() {
        return this.t(32);
    }

    public boolean hasReproduced() {
        return this.t(8);
    }

    public void u(boolean flag) {
        this.d(8, flag);
    }

    public void v(boolean flag) {
        this.d(4, flag);
    }

    public int getTemper() {
        return this.bB;
    }

    public void setTemper(int i) {
        this.bB = i;
    }

    public int v(int i) {
        int j = MathHelper.clamp(this.getTemper() + i, 0, this.getMaxDomestication());

        this.setTemper(j);
        return j;
    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        Entity entity = damagesource.getEntity();

        return this.isVehicle() && entity != null && this.y(entity) ? false : super.damageEntity(damagesource, f);
    }

    @Override
    public boolean isCollidable() {
        return !this.isVehicle();
    }

    private void eq() {
        this.eu();
        if (!this.isSilent()) {
            this.world.playSound((EntityHuman) null, this.locX(), this.locY(), this.locZ(), SoundEffects.ENTITY_HORSE_EAT, this.getSoundCategory(), 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
        }

    }

    @Override
    public boolean b(float f, float f1) {
        if (f > 1.0F) {
            this.a(SoundEffects.ENTITY_HORSE_LAND, 0.4F, 1.0F);
        }

        int i = this.e(f, f1);

        if (i <= 0) {
            return false;
        } else {
            this.damageEntity(DamageSource.FALL, (float) i);
            if (this.isVehicle()) {
                Iterator iterator = this.getAllPassengers().iterator();

                while (iterator.hasNext()) {
                    Entity entity = (Entity) iterator.next();

                    entity.damageEntity(DamageSource.FALL, (float) i);
                }
            }

            this.cZ();
            return true;
        }
    }

    @Override
    protected int e(float f, float f1) {
        return MathHelper.f((f * 0.5F - 3.0F) * f1);
    }

    protected int getChestSlots() {
        return 2;
    }

    public void loadChest() {
        InventorySubcontainer inventorysubcontainer = this.inventoryChest;

        this.inventoryChest = new InventorySubcontainer(this.getChestSlots(), (org.bukkit.entity.AbstractHorse) this.getBukkitEntity()); // CraftBukkit
        if (inventorysubcontainer != null) {
            inventorysubcontainer.b((IInventoryListener) this);
            int i = Math.min(inventorysubcontainer.getSize(), this.inventoryChest.getSize());

            for (int j = 0; j < i; ++j) {
                ItemStack itemstack = inventorysubcontainer.getItem(j);

                if (!itemstack.isEmpty()) {
                    this.inventoryChest.setItem(j, itemstack.cloneItemStack());
                }
            }
        }

        this.inventoryChest.a((IInventoryListener) this);
        this.eI();
    }

    protected void eI() {
        if (!this.world.isClientSide) {
            this.v(!this.inventoryChest.getItem(0).isEmpty() && this.eK());
        }
    }

    @Override
    public void a(IInventory iinventory) {
        boolean flag = this.eL();

        this.eI();
        if (this.ticksLived > 20 && !flag && this.eL()) {
            this.a(SoundEffects.ENTITY_HORSE_SADDLE, 0.5F, 1.0F);
        }

    }

    public double getJumpStrength() {
        return this.getAttributeInstance(EntityHorseAbstract.attributeJumpStrength).getValue();
    }

    @Nullable
    @Override
    protected SoundEffect getSoundDeath() {
        return null;
    }

    @Nullable
    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        if (this.random.nextInt(3) == 0) {
            this.eW();
        }

        return null;
    }

    @Nullable
    @Override
    protected SoundEffect getSoundAmbient() {
        if (this.random.nextInt(10) == 0 && !this.isFrozen()) {
            this.eW();
        }

        return null;
    }

    public boolean eK() {
        return true;
    }

    public boolean eL() {
        return this.t(4);
    }

    @Nullable
    protected SoundEffect getSoundAngry() {
        this.eW();
        return null;
    }

    @Override
    protected void a(BlockPosition blockposition, IBlockData iblockdata) {
        if (!iblockdata.getMaterial().isLiquid()) {
            IBlockData iblockdata1 = this.world.getType(blockposition.up());
            SoundEffectType soundeffecttype = iblockdata.r();

            if (iblockdata1.getBlock() == Blocks.SNOW) {
                soundeffecttype = iblockdata1.r();
            }

            if (this.isVehicle() && this.bD) {
                ++this.bE;
                if (this.bE > 5 && this.bE % 3 == 0) {
                    this.a(soundeffecttype);
                } else if (this.bE <= 5) {
                    this.a(SoundEffects.ENTITY_HORSE_STEP_WOOD, soundeffecttype.a() * 0.15F, soundeffecttype.b());
                }
            } else if (soundeffecttype == SoundEffectType.a) {
                this.a(SoundEffects.ENTITY_HORSE_STEP_WOOD, soundeffecttype.a() * 0.15F, soundeffecttype.b());
            } else {
                this.a(SoundEffects.ENTITY_HORSE_STEP, soundeffecttype.a() * 0.15F, soundeffecttype.b());
            }

        }
    }

    protected void a(SoundEffectType soundeffecttype) {
        this.a(SoundEffects.ENTITY_HORSE_GALLOP, soundeffecttype.a() * 0.15F, soundeffecttype.b());
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeMap().b(EntityHorseAbstract.attributeJumpStrength);
        this.getAttributeInstance(GenericAttributes.MAX_HEALTH).setValue(53.0D);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.22499999403953552D);
    }

    @Override
    public int getMaxSpawnGroup() {
        return 6;
    }

    public int getMaxDomestication() {
        return this.maxDomestication; // CraftBukkit - return stored max domestication instead of 100
    }

    @Override
    protected float getSoundVolume() {
        return 0.8F;
    }

    @Override
    public int A() {
        return 400;
    }

    public void e(EntityHuman entityhuman) {
        if (!this.world.isClientSide && (!this.isVehicle() || this.w(entityhuman)) && this.isTamed()) {
            entityhuman.openHorseInventory(this, this.inventoryChest);
        }

    }

    protected boolean b(EntityHuman entityhuman, ItemStack itemstack) {
        boolean flag = false;
        float f = 0.0F;
        short short0 = 0;
        byte b0 = 0;
        Item item = itemstack.getItem();

        if (item == Items.WHEAT) {
            f = 2.0F;
            short0 = 20;
            b0 = 3;
        } else if (item == Items.SUGAR) {
            f = 1.0F;
            short0 = 30;
            b0 = 3;
        } else if (item == Blocks.HAY_BLOCK.getItem()) {
            f = 20.0F;
            short0 = 180;
        } else if (item == Items.APPLE) {
            f = 3.0F;
            short0 = 60;
            b0 = 3;
        } else if (item == Items.GOLDEN_CARROT) {
            f = 4.0F;
            short0 = 60;
            b0 = 5;
            if (this.isTamed() && this.getAge() == 0 && !this.isInLove()) {
                flag = true;
                this.f(entityhuman);
            }
        } else if (item == Items.GOLDEN_APPLE || item == Items.ENCHANTED_GOLDEN_APPLE) {
            f = 10.0F;
            short0 = 240;
            b0 = 10;
            if (this.isTamed() && this.getAge() == 0 && !this.isInLove()) {
                flag = true;
                this.f(entityhuman);
            }
        }

        if (this.getHealth() < this.getMaxHealth() && f > 0.0F) {
            this.heal(f, RegainReason.EATING); // CraftBukkit
            flag = true;
        }

        if (this.isBaby() && short0 > 0) {
            this.world.addParticle(Particles.HAPPY_VILLAGER, this.d(1.0D), this.cv() + 0.5D, this.g(1.0D), 0.0D, 0.0D, 0.0D);
            if (!this.world.isClientSide) {
                this.setAge(short0);
            }

            flag = true;
        }

        if (b0 > 0 && (flag || !this.isTamed()) && this.getTemper() < this.getMaxDomestication()) {
            flag = true;
            if (!this.world.isClientSide) {
                this.v(b0);
            }
        }

        if (flag) {
            this.eq();
        }

        return flag;
    }

    protected void g(EntityHuman entityhuman) {
        this.w(false);
        this.setStanding(false);
        if (!this.world.isClientSide) {
            entityhuman.yaw = this.yaw;
            entityhuman.pitch = this.pitch;
            entityhuman.startRiding(this);
        }

    }

    @Override
    protected boolean isFrozen() {
        return super.isFrozen() && this.isVehicle() && this.eL() || this.eD() || this.eE();
    }

    @Override
    public boolean i(ItemStack itemstack) {
        return false;
    }

    private void et() {
        this.bx = 1;
    }

    @Override
    protected void dropInventory() {
        super.dropInventory();
        if (this.inventoryChest != null) {
            for (int i = 0; i < this.inventoryChest.getSize(); ++i) {
                ItemStack itemstack = this.inventoryChest.getItem(i);

                if (!itemstack.isEmpty() && !EnchantmentManager.shouldNotDrop(itemstack)) {
                    this.a(itemstack);
                }
            }

        }
    }

    @Override
    public void movementTick() {
        if (this.random.nextInt(200) == 0) {
            this.et();
        }

        super.movementTick();
        if (!this.world.isClientSide && this.isAlive()) {
            if (this.random.nextInt(900) == 0 && this.deathTicks == 0) {
                this.heal(1.0F, RegainReason.REGEN); // CraftBukkit
            }

            if (this.eO()) {
                if (!this.eD() && !this.isVehicle() && this.random.nextInt(300) == 0 && this.world.getType((new BlockPosition(this)).down()).getBlock() == Blocks.GRASS_BLOCK) {
                    this.w(true);
                }

                if (this.eD() && ++this.bJ > 50) {
                    this.bJ = 0;
                    this.w(false);
                }
            }

            this.eN();
        }
    }

    protected void eN() {
        if (this.hasReproduced() && this.isBaby() && !this.eD()) {
            EntityLiving entityliving = this.world.a(EntityHorseAbstract.class, EntityHorseAbstract.bG, this, this.locX(), this.locY(), this.locZ(), this.getBoundingBox().g(16.0D));

            if (entityliving != null && this.h((Entity) entityliving) > 4.0D) {
                this.navigation.a((Entity) entityliving, 0);
            }
        }

    }

    public boolean eO() {
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.bK > 0 && ++this.bK > 30) {
            this.bK = 0;
            this.d(64, false);
        }

        if ((this.cj() || this.doAITick()) && this.bL > 0 && ++this.bL > 20) {
            this.bL = 0;
            this.setStanding(false);
        }

        if (this.bx > 0 && ++this.bx > 8) {
            this.bx = 0;
        }

        if (this.by > 0) {
            ++this.by;
            if (this.by > 300) {
                this.by = 0;
            }
        }

        this.bO = this.bN;
        if (this.eD()) {
            this.bN += (1.0F - this.bN) * 0.4F + 0.05F;
            if (this.bN > 1.0F) {
                this.bN = 1.0F;
            }
        } else {
            this.bN += (0.0F - this.bN) * 0.4F - 0.05F;
            if (this.bN < 0.0F) {
                this.bN = 0.0F;
            }
        }

        this.bQ = this.bP;
        if (this.eE()) {
            this.bN = 0.0F;
            this.bO = this.bN;
            this.bP += (1.0F - this.bP) * 0.4F + 0.05F;
            if (this.bP > 1.0F) {
                this.bP = 1.0F;
            }
        } else {
            this.canSlide = false;
            this.bP += (0.8F * this.bP * this.bP * this.bP - this.bP) * 0.6F - 0.05F;
            if (this.bP < 0.0F) {
                this.bP = 0.0F;
            }
        }

        this.bS = this.bR;
        if (this.t(64)) {
            this.bR += (1.0F - this.bR) * 0.7F + 0.05F;
            if (this.bR > 1.0F) {
                this.bR = 1.0F;
            }
        } else {
            this.bR += (0.0F - this.bR) * 0.7F - 0.05F;
            if (this.bR < 0.0F) {
                this.bR = 0.0F;
            }
        }

    }

    private void eu() {
        if (!this.world.isClientSide) {
            this.bK = 1;
            this.d(64, true);
        }

    }

    public void w(boolean flag) {
        this.d(16, flag);
    }

    public void setStanding(boolean flag) {
        if (flag) {
            this.w(false);
        }

        this.d(32, flag);
    }

    private void eW() {
        if (this.cj() || this.doAITick()) {
            this.bL = 1;
            this.setStanding(true);
        }

    }

    public void eP() {
        this.eW();
        SoundEffect soundeffect = this.getSoundAngry();

        if (soundeffect != null) {
            this.a(soundeffect, this.getSoundVolume(), this.dn());
        }

    }

    public boolean h(EntityHuman entityhuman) {
        this.setOwnerUUID(entityhuman.getUniqueID());
        this.setTamed(true);
        if (entityhuman instanceof EntityPlayer) {
            CriterionTriggers.x.a((EntityPlayer) entityhuman, (EntityAnimal) this);
        }

        this.world.broadcastEntityEffect(this, (byte) 7);
        return true;
    }

    @Override
    public void e(Vec3D vec3d) {
        if (this.isAlive()) {
            if (this.isVehicle() && this.dY() && this.eL()) {
                EntityLiving entityliving = (EntityLiving) this.getRidingPassenger();

                this.yaw = entityliving.yaw;
                this.lastYaw = this.yaw;
                this.pitch = entityliving.pitch * 0.5F;
                this.setYawPitch(this.yaw, this.pitch);
                this.aI = this.yaw;
                this.aK = this.aI;
                float f = entityliving.aZ * 0.5F;
                float f1 = entityliving.bb;

                if (f1 <= 0.0F) {
                    f1 *= 0.25F;
                    this.bE = 0;
                }

                if (this.onGround && this.jumpPower == 0.0F && this.eE() && !this.canSlide) {
                    f = 0.0F;
                    f1 = 0.0F;
                }

                double d0;
                double d1;

                if (this.jumpPower > 0.0F && !this.eC() && this.onGround) {
                    d0 = this.getJumpStrength() * (double) this.jumpPower * (double) this.ah();
                    if (this.hasEffect(MobEffects.JUMP)) {
                        d1 = d0 + (double) ((float) (this.getEffect(MobEffects.JUMP).getAmplifier() + 1) * 0.1F);
                    } else {
                        d1 = d0;
                    }

                    Vec3D vec3d1 = this.getMot();

                    this.setMot(vec3d1.x, d1, vec3d1.z);
                    this.t(true);
                    this.impulse = true;
                    if (f1 > 0.0F) {
                        float f2 = MathHelper.sin(this.yaw * 0.017453292F);
                        float f3 = MathHelper.cos(this.yaw * 0.017453292F);

                        this.setMot(this.getMot().add((double) (-0.4F * f2 * this.jumpPower), 0.0D, (double) (0.4F * f3 * this.jumpPower)));
                        this.eQ();
                    }

                    this.jumpPower = 0.0F;
                }

                this.aM = this.dt() * 0.1F;
                if (this.cj()) {
                    this.o((float) this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).getValue());
                    super.e(new Vec3D((double) f, vec3d.y, (double) f1));
                } else if (entityliving instanceof EntityHuman) {
                    this.setMot(Vec3D.a);
                }

                if (this.onGround) {
                    this.jumpPower = 0.0F;
                    this.t(false);
                }

                this.aC = this.aD;
                d0 = this.locX() - this.lastX;
                d1 = this.locZ() - this.lastZ;
                float f4 = MathHelper.sqrt(d0 * d0 + d1 * d1) * 4.0F;

                if (f4 > 1.0F) {
                    f4 = 1.0F;
                }

                this.aD += (f4 - this.aD) * 0.4F;
                this.aE += this.aD;
            } else {
                this.aM = 0.02F;
                super.e(vec3d);
            }
        }
    }

    protected void eQ() {
        this.a(SoundEffects.ENTITY_HORSE_JUMP, 0.4F, 1.0F);
    }

    @Override
    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setBoolean("EatingHaystack", this.eD());
        nbttagcompound.setBoolean("Bred", this.hasReproduced());
        nbttagcompound.setInt("Temper", this.getTemper());
        nbttagcompound.setBoolean("Tame", this.isTamed());
        if (this.getOwnerUUID() != null) {
            nbttagcompound.setString("OwnerUUID", this.getOwnerUUID().toString());
        }
        nbttagcompound.setInt("Bukkit.MaxDomestication", this.maxDomestication); // CraftBukkit

        if (!this.inventoryChest.getItem(0).isEmpty()) {
            nbttagcompound.set("SaddleItem", this.inventoryChest.getItem(0).save(new NBTTagCompound()));
        }

    }

    @Override
    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.w(nbttagcompound.getBoolean("EatingHaystack"));
        this.u(nbttagcompound.getBoolean("Bred"));
        this.setTemper(nbttagcompound.getInt("Temper"));
        this.setTamed(nbttagcompound.getBoolean("Tame"));
        String s;

        if (nbttagcompound.hasKeyOfType("OwnerUUID", 8)) {
            s = nbttagcompound.getString("OwnerUUID");
        } else {
            String s1 = nbttagcompound.getString("Owner");

            s = NameReferencingFileConverter.a(this.getMinecraftServer(), s1);
        }

        if (!s.isEmpty()) {
            this.setOwnerUUID(UUID.fromString(s));
        }
        // CraftBukkit start
        if (nbttagcompound.hasKey("Bukkit.MaxDomestication")) {
            this.maxDomestication = nbttagcompound.getInt("Bukkit.MaxDomestication");
        }
        // CraftBukkit end

        AttributeInstance attributeinstance = this.getAttributeMap().a("Speed");

        if (attributeinstance != null) {
            this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(attributeinstance.getBaseValue() * 0.25D);
        }

        if (nbttagcompound.hasKeyOfType("SaddleItem", 10)) {
            ItemStack itemstack = ItemStack.a(nbttagcompound.getCompound("SaddleItem"));

            if (itemstack.getItem() == Items.SADDLE) {
                this.inventoryChest.setItem(0, itemstack);
            }
        }

        this.eI();
    }

    @Override
    public boolean mate(EntityAnimal entityanimal) {
        return false;
    }

    protected boolean eR() {
        return !this.isVehicle() && !this.isPassenger() && this.isTamed() && !this.isBaby() && this.getHealth() >= this.getMaxHealth() && this.isInLove();
    }

    @Nullable
    @Override
    public EntityAgeable createChild(EntityAgeable entityageable) {
        return null;
    }

    protected void a(EntityAgeable entityageable, EntityHorseAbstract entityhorseabstract) {
        double d0 = this.getAttributeInstance(GenericAttributes.MAX_HEALTH).getBaseValue() + entityageable.getAttributeInstance(GenericAttributes.MAX_HEALTH).getBaseValue() + (double) this.eS();

        entityhorseabstract.getAttributeInstance(GenericAttributes.MAX_HEALTH).setValue(d0 / 3.0D);
        double d1 = this.getAttributeInstance(EntityHorseAbstract.attributeJumpStrength).getBaseValue() + entityageable.getAttributeInstance(EntityHorseAbstract.attributeJumpStrength).getBaseValue() + this.eT();

        entityhorseabstract.getAttributeInstance(EntityHorseAbstract.attributeJumpStrength).setValue(d1 / 3.0D);
        double d2 = this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).getBaseValue() + entityageable.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).getBaseValue() + this.eU();

        entityhorseabstract.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(d2 / 3.0D);
    }

    @Override
    public boolean dY() {
        return this.getRidingPassenger() instanceof EntityLiving;
    }

    @Override
    public boolean G_() {
        return this.eL();
    }

    @Override
    public void b(int i) {
        // CraftBukkit start
        float power;
        if (i >= 90) {
            power = 1.0F;
        } else {
            power = 0.4F + 0.4F * (float) i / 90.0F;
        }
        org.bukkit.event.entity.HorseJumpEvent event = org.bukkit.craftbukkit.event.CraftEventFactory.callHorseJumpEvent(this, power);
        if (event.isCancelled()) {
            return;
        }
        // CraftBukkit end
        this.canSlide = true;
        this.eW();
    }

    @Override
    public void c() {}

    @Override
    public void k(Entity entity) {
        super.k(entity);
        if (entity instanceof EntityInsentient) {
            EntityInsentient entityinsentient = (EntityInsentient) entity;

            this.aI = entityinsentient.aI;
        }

        if (this.bQ > 0.0F) {
            float f = MathHelper.sin(this.aI * 0.017453292F);
            float f1 = MathHelper.cos(this.aI * 0.017453292F);
            float f2 = 0.7F * this.bQ;
            float f3 = 0.15F * this.bQ;

            entity.setPosition(this.locX() + (double) (f2 * f), this.locY() + this.aS() + entity.aR() + (double) f3, this.locZ() - (double) (f2 * f1));
            if (entity instanceof EntityLiving) {
                ((EntityLiving) entity).aI = this.aI;
            }
        }

    }

    protected float eS() {
        return 15.0F + (float) this.random.nextInt(8) + (float) this.random.nextInt(9);
    }

    protected double eT() {
        return 0.4000000059604645D + this.random.nextDouble() * 0.2D + this.random.nextDouble() * 0.2D + this.random.nextDouble() * 0.2D;
    }

    protected double eU() {
        return (0.44999998807907104D + this.random.nextDouble() * 0.3D + this.random.nextDouble() * 0.3D + this.random.nextDouble() * 0.3D) * 0.25D;
    }

    @Override
    public boolean isClimbing() {
        return false;
    }

    @Override
    protected float b(EntityPose entitypose, EntitySize entitysize) {
        return entitysize.height * 0.95F;
    }

    public boolean eV() {
        return false;
    }

    public boolean j(ItemStack itemstack) {
        return false;
    }

    @Override
    public boolean a_(int i, ItemStack itemstack) {
        int j = i - 400;

        if (j >= 0 && j < 2 && j < this.inventoryChest.getSize()) {
            if (j == 0 && itemstack.getItem() != Items.SADDLE) {
                return false;
            } else if (j == 1 && (!this.eV() || !this.j(itemstack))) {
                return false;
            } else {
                this.inventoryChest.setItem(j, itemstack);
                this.eI();
                return true;
            }
        } else {
            int k = i - 500 + 2;

            if (k >= 2 && k < this.inventoryChest.getSize()) {
                this.inventoryChest.setItem(k, itemstack);
                return true;
            } else {
                return false;
            }
        }
    }

    @Nullable
    @Override
    public Entity getRidingPassenger() {
        return this.getPassengers().isEmpty() ? null : (Entity) this.getPassengers().get(0);
    }

    @Nullable
    @Override
    public GroupDataEntity prepare(GeneratorAccess generatoraccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        if (groupdataentity == null) {
            groupdataentity = new EntityAgeable.a();
            ((EntityAgeable.a) groupdataentity).a(0.2F);
        }

        return super.prepare(generatoraccess, difficultydamagescaler, enummobspawn, (GroupDataEntity) groupdataentity, nbttagcompound);
    }
}
