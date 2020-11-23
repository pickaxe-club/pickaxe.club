package net.minecraft.server;

import com.google.common.collect.ComparisonChain;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MobEffect implements Comparable<MobEffect> {

    private static final Logger LOGGER = LogManager.getLogger();
    private final MobEffectList b;
    private int duration;
    private int amplification;
    private boolean splash;
    private boolean ambient;
    private boolean showParticles;
    private boolean showIcon;
    @Nullable
    private MobEffect hiddenEffect;

    public MobEffect(MobEffectList mobeffectlist) {
        this(mobeffectlist, 0, 0);
    }

    public MobEffect(MobEffectList mobeffectlist, int i) {
        this(mobeffectlist, i, 0);
    }

    public MobEffect(MobEffectList mobeffectlist, int i, int j) {
        this(mobeffectlist, i, j, false, true);
    }

    public MobEffect(MobEffectList mobeffectlist, int i, int j, boolean flag, boolean flag1) {
        this(mobeffectlist, i, j, flag, flag1, flag1);
    }

    public MobEffect(MobEffectList mobeffectlist, int i, int j, boolean flag, boolean flag1, boolean flag2) {
        this(mobeffectlist, i, j, flag, flag1, flag2, (MobEffect) null);
    }

    public MobEffect(MobEffectList mobeffectlist, int i, int j, boolean flag, boolean flag1, boolean flag2, @Nullable MobEffect mobeffect) {
        this.b = mobeffectlist;
        this.duration = i;
        this.amplification = j;
        this.ambient = flag;
        this.showParticles = flag1;
        this.showIcon = flag2;
        this.hiddenEffect = mobeffect;
    }

    public MobEffect(MobEffect mobeffect) {
        this.b = mobeffect.b;
        this.a(mobeffect);
    }

    void a(MobEffect mobeffect) {
        this.duration = mobeffect.duration;
        this.amplification = mobeffect.amplification;
        this.ambient = mobeffect.ambient;
        this.showParticles = mobeffect.showParticles;
        this.showIcon = mobeffect.showIcon;
    }

    public boolean b(MobEffect mobeffect) {
        if (this.b != mobeffect.b) {
            MobEffect.LOGGER.warn("This method should only be called for matching effects!");
        }

        boolean flag = false;

        if (mobeffect.amplification > this.amplification) {
            if (mobeffect.duration < this.duration) {
                MobEffect mobeffect1 = this.hiddenEffect;

                this.hiddenEffect = new MobEffect(this);
                this.hiddenEffect.hiddenEffect = mobeffect1;
            }

            this.amplification = mobeffect.amplification;
            this.duration = mobeffect.duration;
            flag = true;
        } else if (mobeffect.duration > this.duration) {
            if (mobeffect.amplification == this.amplification) {
                this.duration = mobeffect.duration;
                flag = true;
            } else if (this.hiddenEffect == null) {
                this.hiddenEffect = new MobEffect(mobeffect);
            } else {
                this.hiddenEffect.b(mobeffect);
            }
        }

        if (!mobeffect.ambient && this.ambient || flag) {
            this.ambient = mobeffect.ambient;
            flag = true;
        }

        if (mobeffect.showParticles != this.showParticles) {
            this.showParticles = mobeffect.showParticles;
            flag = true;
        }

        if (mobeffect.showIcon != this.showIcon) {
            this.showIcon = mobeffect.showIcon;
            flag = true;
        }

        return flag;
    }

    public MobEffectList getMobEffect() {
        return this.b;
    }

    public int getDuration() {
        return this.duration;
    }

    public int getAmplifier() {
        return this.amplification;
    }

    public boolean isAmbient() {
        return this.ambient;
    }

    public boolean isShowParticles() {
        return this.showParticles;
    }

    public boolean isShowIcon() {
        return this.showIcon;
    }

    public boolean tick(EntityLiving entityliving, Runnable runnable) {
        if (this.duration > 0) {
            if (this.b.a(this.duration, this.amplification)) {
                this.a(entityliving);
            }

            this.i();
            if (this.duration == 0 && this.hiddenEffect != null) {
                this.a(this.hiddenEffect);
                this.hiddenEffect = this.hiddenEffect.hiddenEffect;
                runnable.run();
            }
        }

        return this.duration > 0;
    }

    private int i() {
        if (this.hiddenEffect != null) {
            this.hiddenEffect.i();
        }

        return --this.duration;
    }

    public void a(EntityLiving entityliving) {
        if (this.duration > 0) {
            this.b.tick(entityliving, this.amplification);
        }

    }

    public String g() {
        return this.b.c();
    }

    public String toString() {
        String s;

        if (this.amplification > 0) {
            s = this.g() + " x " + (this.amplification + 1) + ", Duration: " + this.duration;
        } else {
            s = this.g() + ", Duration: " + this.duration;
        }

        if (this.splash) {
            s = s + ", Splash: true";
        }

        if (!this.showParticles) {
            s = s + ", Particles: false";
        }

        if (!this.showIcon) {
            s = s + ", Show Icon: false";
        }

        return s;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (!(object instanceof MobEffect)) {
            return false;
        } else {
            MobEffect mobeffect = (MobEffect) object;

            return this.duration == mobeffect.duration && this.amplification == mobeffect.amplification && this.splash == mobeffect.splash && this.ambient == mobeffect.ambient && this.b.equals(mobeffect.b);
        }
    }

    public int hashCode() {
        int i = this.b.hashCode();

        i = 31 * i + this.duration;
        i = 31 * i + this.amplification;
        i = 31 * i + (this.splash ? 1 : 0);
        i = 31 * i + (this.ambient ? 1 : 0);
        return i;
    }

    public NBTTagCompound a(NBTTagCompound nbttagcompound) {
        nbttagcompound.setByte("Id", (byte) MobEffectList.getId(this.getMobEffect()));
        this.c(nbttagcompound);
        return nbttagcompound;
    }

    private void c(NBTTagCompound nbttagcompound) {
        nbttagcompound.setByte("Amplifier", (byte) this.getAmplifier());
        nbttagcompound.setInt("Duration", this.getDuration());
        nbttagcompound.setBoolean("Ambient", this.isAmbient());
        nbttagcompound.setBoolean("ShowParticles", this.isShowParticles());
        nbttagcompound.setBoolean("ShowIcon", this.isShowIcon());
        if (this.hiddenEffect != null) {
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();

            this.hiddenEffect.a(nbttagcompound1);
            nbttagcompound.set("HiddenEffect", nbttagcompound1);
        }

    }

    public static MobEffect b(NBTTagCompound nbttagcompound) {
        byte b0 = nbttagcompound.getByte("Id");
        MobEffectList mobeffectlist = MobEffectList.fromId(b0);

        return mobeffectlist == null ? null : a(mobeffectlist, nbttagcompound);
    }

    private static MobEffect a(MobEffectList mobeffectlist, NBTTagCompound nbttagcompound) {
        byte b0 = nbttagcompound.getByte("Amplifier");
        int i = nbttagcompound.getInt("Duration");
        boolean flag = nbttagcompound.getBoolean("Ambient");
        boolean flag1 = true;

        if (nbttagcompound.hasKeyOfType("ShowParticles", 1)) {
            flag1 = nbttagcompound.getBoolean("ShowParticles");
        }

        boolean flag2 = flag1;

        if (nbttagcompound.hasKeyOfType("ShowIcon", 1)) {
            flag2 = nbttagcompound.getBoolean("ShowIcon");
        }

        MobEffect mobeffect = null;

        if (nbttagcompound.hasKeyOfType("HiddenEffect", 10)) {
            mobeffect = a(mobeffectlist, nbttagcompound.getCompound("HiddenEffect"));
        }

        return new MobEffect(mobeffectlist, i, b0 < 0 ? 0 : b0, flag, flag1, flag2, mobeffect);
    }

    public int compareTo(MobEffect mobeffect) {
        boolean flag = true;

        return (this.getDuration() <= 32147 || mobeffect.getDuration() <= 32147) && (!this.isAmbient() || !mobeffect.isAmbient()) ? ComparisonChain.start().compare(this.isAmbient(), mobeffect.isAmbient()).compare(this.getDuration(), mobeffect.getDuration()).compare(this.getMobEffect().getColor(), mobeffect.getMobEffect().getColor()).result() : ComparisonChain.start().compare(this.isAmbient(), mobeffect.isAmbient()).compare(this.getMobEffect().getColor(), mobeffect.getMobEffect().getColor()).result();
    }
}
