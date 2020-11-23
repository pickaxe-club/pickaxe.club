package net.minecraft.server;

public class EntityEnderSignal extends Entity {

    private static final DataWatcherObject<ItemStack> b = DataWatcher.a(EntityEnderSignal.class, DataWatcherRegistry.g);
    public double targetX;
    public double targetY;
    public double targetZ;
    public int despawnTimer;
    public boolean shouldDropItem;

    public EntityEnderSignal(EntityTypes<? extends EntityEnderSignal> entitytypes, World world) {
        super(entitytypes, world);
    }

    public EntityEnderSignal(World world, double d0, double d1, double d2) {
        this(EntityTypes.EYE_OF_ENDER, world);
        this.despawnTimer = 0;
        this.setPosition(d0, d1, d2);
    }

    public void b(ItemStack itemstack) {
        if (itemstack.getItem() != Items.ENDER_EYE || itemstack.hasTag()) {
            this.getDataWatcher().set(EntityEnderSignal.b, SystemUtils.a((Object) itemstack.cloneItemStack(), (itemstack1) -> {
                itemstack1.setCount(1);
            }));
        }

    }

    private ItemStack i() {
        return (ItemStack) this.getDataWatcher().get(EntityEnderSignal.b);
    }

    public ItemStack f() {
        ItemStack itemstack = this.i();

        return itemstack.isEmpty() ? new ItemStack(Items.ENDER_EYE) : itemstack;
    }

    @Override
    protected void initDatawatcher() {
        this.getDataWatcher().register(EntityEnderSignal.b, ItemStack.a);
    }

    public void a(BlockPosition blockposition) {
        double d0 = (double) blockposition.getX();
        int i = blockposition.getY();
        double d1 = (double) blockposition.getZ();
        double d2 = d0 - this.locX();
        double d3 = d1 - this.locZ();
        float f = MathHelper.sqrt(d2 * d2 + d3 * d3);

        if (f > 12.0F) {
            this.targetX = this.locX() + d2 / (double) f * 12.0D;
            this.targetZ = this.locZ() + d3 / (double) f * 12.0D;
            this.targetY = this.locY() + 8.0D;
        } else {
            this.targetX = d0;
            this.targetY = (double) i;
            this.targetZ = d1;
        }

        this.despawnTimer = 0;
        this.shouldDropItem = this.random.nextInt(5) > 0;
    }

    @Override
    public void tick() {
        super.tick();
        Vec3D vec3d = this.getMot();
        double d0 = this.locX() + vec3d.x;
        double d1 = this.locY() + vec3d.y;
        double d2 = this.locZ() + vec3d.z;
        float f = MathHelper.sqrt(b(vec3d));

        this.yaw = (float) (MathHelper.d(vec3d.x, vec3d.z) * 57.2957763671875D);

        for (this.pitch = (float) (MathHelper.d(vec3d.y, (double) f) * 57.2957763671875D); this.pitch - this.lastPitch < -180.0F; this.lastPitch -= 360.0F) {
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
        if (!this.world.isClientSide) {
            double d3 = this.targetX - d0;
            double d4 = this.targetZ - d2;
            float f1 = (float) Math.sqrt(d3 * d3 + d4 * d4);
            float f2 = (float) MathHelper.d(d4, d3);
            double d5 = MathHelper.d(0.0025D, (double) f, (double) f1);
            double d6 = vec3d.y;

            if (f1 < 1.0F) {
                d5 *= 0.8D;
                d6 *= 0.8D;
            }

            int i = this.locY() < this.targetY ? 1 : -1;

            vec3d = new Vec3D(Math.cos((double) f2) * d5, d6 + ((double) i - d6) * 0.014999999664723873D, Math.sin((double) f2) * d5);
            this.setMot(vec3d);
        }

        float f3 = 0.25F;

        if (this.isInWater()) {
            for (int j = 0; j < 4; ++j) {
                this.world.addParticle(Particles.BUBBLE, d0 - vec3d.x * 0.25D, d1 - vec3d.y * 0.25D, d2 - vec3d.z * 0.25D, vec3d.x, vec3d.y, vec3d.z);
            }
        } else {
            this.world.addParticle(Particles.PORTAL, d0 - vec3d.x * 0.25D + this.random.nextDouble() * 0.6D - 0.3D, d1 - vec3d.y * 0.25D - 0.5D, d2 - vec3d.z * 0.25D + this.random.nextDouble() * 0.6D - 0.3D, vec3d.x, vec3d.y, vec3d.z);
        }

        if (!this.world.isClientSide) {
            this.setPosition(d0, d1, d2);
            ++this.despawnTimer;
            if (this.despawnTimer > 80 && !this.world.isClientSide) {
                this.a(SoundEffects.ENTITY_ENDER_EYE_DEATH, 1.0F, 1.0F);
                this.die();
                if (this.shouldDropItem) {
                    this.world.addEntity(new EntityItem(this.world, this.locX(), this.locY(), this.locZ(), this.f()));
                } else {
                    this.world.triggerEffect(2003, new BlockPosition(this), 0);
                }
            }
        } else {
            this.setPositionRaw(d0, d1, d2);
        }

    }

    @Override
    public void b(NBTTagCompound nbttagcompound) {
        ItemStack itemstack = this.i();

        if (!itemstack.isEmpty()) {
            nbttagcompound.set("Item", itemstack.save(new NBTTagCompound()));
        }

    }

    @Override
    public void a(NBTTagCompound nbttagcompound) {
        ItemStack itemstack = ItemStack.a(nbttagcompound.getCompound("Item"));

        this.b(itemstack);
    }

    @Override
    public float aI() {
        return 1.0F;
    }

    @Override
    public boolean bA() {
        return false;
    }

    @Override
    public Packet<?> L() {
        return new PacketPlayOutSpawnEntity(this);
    }
}
