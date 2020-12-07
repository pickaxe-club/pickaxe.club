package net.minecraft.server;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import javax.annotation.Nullable;

public class MobEffectList {

    private final Map<AttributeBase, AttributeModifier> a = Maps.newHashMap();
    private final MobEffectInfo b;
    private final int c;
    @Nullable
    private String d;

    @Nullable
    public static MobEffectList fromId(int i) {
        return (MobEffectList) IRegistry.MOB_EFFECT.fromId(i);
    }

    public static int getId(MobEffectList mobeffectlist) {
        return IRegistry.MOB_EFFECT.a((Object) mobeffectlist);
    }

    protected MobEffectList(MobEffectInfo mobeffectinfo, int i) {
        this.b = mobeffectinfo;
        this.c = i;
    }

    public void tick(EntityLiving entityliving, int i) {
        if (this == MobEffects.REGENERATION) {
            if (entityliving.getHealth() < entityliving.getMaxHealth()) {
                entityliving.heal(1.0F);
            }
        } else if (this == MobEffects.POISON) {
            if (entityliving.getHealth() > 1.0F) {
                entityliving.damageEntity(DamageSource.MAGIC, 1.0F);
            }
        } else if (this == MobEffects.WITHER) {
            entityliving.damageEntity(DamageSource.WITHER, 1.0F);
        } else if (this == MobEffects.HUNGER && entityliving instanceof EntityHuman) {
            ((EntityHuman) entityliving).applyExhaustion(0.005F * (float) (i + 1));
        } else if (this == MobEffects.SATURATION && entityliving instanceof EntityHuman) {
            if (!entityliving.world.isClientSide) {
                ((EntityHuman) entityliving).getFoodData().eat(i + 1, 1.0F);
            }
        } else if ((this != MobEffects.HEAL || entityliving.dj()) && (this != MobEffects.HARM || !entityliving.dj())) {
            if (this == MobEffects.HARM && !entityliving.dj() || this == MobEffects.HEAL && entityliving.dj()) {
                entityliving.damageEntity(DamageSource.MAGIC, (float) (6 << i));
            }
        } else {
            entityliving.heal((float) Math.max(4 << i, 0));
        }

    }

    public void applyInstantEffect(@Nullable Entity entity, @Nullable Entity entity1, EntityLiving entityliving, int i, double d0) {
        int j;

        if ((this != MobEffects.HEAL || entityliving.dj()) && (this != MobEffects.HARM || !entityliving.dj())) {
            if ((this != MobEffects.HARM || entityliving.dj()) && (this != MobEffects.HEAL || !entityliving.dj())) {
                this.tick(entityliving, i);
            } else {
                j = (int) (d0 * (double) (6 << i) + 0.5D);
                if (entity == null) {
                    entityliving.damageEntity(DamageSource.MAGIC, (float) j);
                } else {
                    entityliving.damageEntity(DamageSource.c(entity, entity1), (float) j);
                }
            }
        } else {
            j = (int) (d0 * (double) (4 << i) + 0.5D);
            entityliving.heal((float) j);
        }

    }

    public boolean a(int i, int j) {
        int k;

        if (this == MobEffects.REGENERATION) {
            k = 50 >> j;
            return k > 0 ? i % k == 0 : true;
        } else if (this == MobEffects.POISON) {
            k = 25 >> j;
            return k > 0 ? i % k == 0 : true;
        } else if (this == MobEffects.WITHER) {
            k = 40 >> j;
            return k > 0 ? i % k == 0 : true;
        } else {
            return this == MobEffects.HUNGER;
        }
    }

    public boolean isInstant() {
        return false;
    }

    protected String b() {
        if (this.d == null) {
            this.d = SystemUtils.a("effect", IRegistry.MOB_EFFECT.getKey(this));
        }

        return this.d;
    }

    public String c() {
        return this.b();
    }

    public IChatBaseComponent d() {
        return new ChatMessage(this.c());
    }

    public int getColor() {
        return this.c;
    }

    public MobEffectList a(AttributeBase attributebase, String s, double d0, AttributeModifier.Operation attributemodifier_operation) {
        AttributeModifier attributemodifier = new AttributeModifier(UUID.fromString(s), this::c, d0, attributemodifier_operation);

        this.a.put(attributebase, attributemodifier);
        return this;
    }

    public void a(EntityLiving entityliving, AttributeMapBase attributemapbase, int i) {
        Iterator iterator = this.a.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry<AttributeBase, AttributeModifier> entry = (Entry) iterator.next();
            AttributeModifiable attributemodifiable = attributemapbase.a((AttributeBase) entry.getKey());

            if (attributemodifiable != null) {
                attributemodifiable.removeModifier((AttributeModifier) entry.getValue());
            }
        }

    }

    public void b(EntityLiving entityliving, AttributeMapBase attributemapbase, int i) {
        Iterator iterator = this.a.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry<AttributeBase, AttributeModifier> entry = (Entry) iterator.next();
            AttributeModifiable attributemodifiable = attributemapbase.a((AttributeBase) entry.getKey());

            if (attributemodifiable != null) {
                AttributeModifier attributemodifier = (AttributeModifier) entry.getValue();

                attributemodifiable.removeModifier(attributemodifier);
                attributemodifiable.addModifier(new AttributeModifier(attributemodifier.getUniqueId(), this.c() + " " + i, this.a(i, attributemodifier), attributemodifier.getOperation()));
            }
        }

    }

    public double a(int i, AttributeModifier attributemodifier) {
        return attributemodifier.getAmount() * (double) (i + 1);
    }
}
