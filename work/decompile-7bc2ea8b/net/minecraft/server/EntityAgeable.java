package net.minecraft.server;

import javax.annotation.Nullable;

public abstract class EntityAgeable extends EntityCreature {

    private static final DataWatcherObject<Boolean> bv = DataWatcher.a(EntityAgeable.class, DataWatcherRegistry.i);
    protected int b;
    protected int c;
    protected int d;

    protected EntityAgeable(EntityTypes<? extends EntityAgeable> entitytypes, World world) {
        super(entitytypes, world);
    }

    @Override
    public GroupDataEntity prepare(GeneratorAccess generatoraccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        if (groupdataentity == null) {
            groupdataentity = new EntityAgeable.a();
        }

        EntityAgeable.a entityageable_a = (EntityAgeable.a) groupdataentity;

        if (entityageable_a.c() && entityageable_a.a() > 0 && this.random.nextFloat() <= entityageable_a.d()) {
            this.setAgeRaw(-24000);
        }

        entityageable_a.b();
        return super.prepare(generatoraccess, difficultydamagescaler, enummobspawn, (GroupDataEntity) groupdataentity, nbttagcompound);
    }

    @Nullable
    public abstract EntityAgeable createChild(EntityAgeable entityageable);

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.datawatcher.register(EntityAgeable.bv, false);
    }

    public boolean canBreed() {
        return false;
    }

    public int getAge() {
        return this.world.isClientSide ? ((Boolean) this.datawatcher.get(EntityAgeable.bv) ? -1 : 1) : this.b;
    }

    public void setAge(int i, boolean flag) {
        int j = this.getAge();
        int k = j;

        j += i * 20;
        if (j > 0) {
            j = 0;
        }

        int l = j - k;

        this.setAgeRaw(j);
        if (flag) {
            this.c += l;
            if (this.d == 0) {
                this.d = 40;
            }
        }

        if (this.getAge() == 0) {
            this.setAgeRaw(this.c);
        }

    }

    public void setAge(int i) {
        this.setAge(i, false);
    }

    public void setAgeRaw(int i) {
        int j = this.b;

        this.b = i;
        if (j < 0 && i >= 0 || j >= 0 && i < 0) {
            this.datawatcher.set(EntityAgeable.bv, i < 0);
            this.m();
        }

    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);
        nbttagcompound.setInt("Age", this.getAge());
        nbttagcompound.setInt("ForcedAge", this.c);
    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        this.setAgeRaw(nbttagcompound.getInt("Age"));
        this.c = nbttagcompound.getInt("ForcedAge");
    }

    @Override
    public void a(DataWatcherObject<?> datawatcherobject) {
        if (EntityAgeable.bv.equals(datawatcherobject)) {
            this.updateSize();
        }

        super.a(datawatcherobject);
    }

    @Override
    public void movementTick() {
        super.movementTick();
        if (this.world.isClientSide) {
            if (this.d > 0) {
                if (this.d % 4 == 0) {
                    this.world.addParticle(Particles.HAPPY_VILLAGER, this.d(1.0D), this.cE() + 0.5D, this.g(1.0D), 0.0D, 0.0D, 0.0D);
                }

                --this.d;
            }
        } else if (this.isAlive()) {
            int i = this.getAge();

            if (i < 0) {
                ++i;
                this.setAgeRaw(i);
            } else if (i > 0) {
                --i;
                this.setAgeRaw(i);
            }
        }

    }

    protected void m() {}

    @Override
    public boolean isBaby() {
        return this.getAge() < 0;
    }

    @Override
    public void a(boolean flag) {
        this.setAgeRaw(flag ? -24000 : 0);
    }

    public static class a implements GroupDataEntity {

        private int a;
        private boolean b = true;
        private float c = 0.05F;

        public a() {}

        public int a() {
            return this.a;
        }

        public void b() {
            ++this.a;
        }

        public boolean c() {
            return this.b;
        }

        public void a(boolean flag) {
            this.b = flag;
        }

        public float d() {
            return this.c;
        }

        public void a(float f) {
            this.c = f;
        }
    }
}
