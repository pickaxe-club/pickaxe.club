package net.minecraft.server;

import javax.annotation.Nullable;

public class EntityHorseDonkey extends EntityHorseChestedAbstract {

    public EntityHorseDonkey(EntityTypes<? extends EntityHorseDonkey> entitytypes, World world) {
        super(entitytypes, world);
    }

    @Override
    protected SoundEffect getSoundAmbient() {
        super.getSoundAmbient();
        return SoundEffects.ENTITY_DONKEY_AMBIENT;
    }

    @Override
    protected SoundEffect getSoundAngry() {
        super.getSoundAngry();
        return SoundEffects.ENTITY_DONKEY_ANGRY;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        super.getSoundDeath();
        return SoundEffects.ENTITY_DONKEY_DEATH;
    }

    @Nullable
    @Override
    protected SoundEffect fg() {
        return SoundEffects.ENTITY_DONKEY_EAT;
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        super.getSoundHurt(damagesource);
        return SoundEffects.ENTITY_DONKEY_HURT;
    }

    @Override
    public boolean mate(EntityAnimal entityanimal) {
        return entityanimal == this ? false : (!(entityanimal instanceof EntityHorseDonkey) && !(entityanimal instanceof EntityHorse) ? false : this.fo() && ((EntityHorseAbstract) entityanimal).fo());
    }

    @Override
    public EntityAgeable createChild(WorldServer worldserver, EntityAgeable entityageable) {
        EntityTypes<? extends EntityHorseAbstract> entitytypes = entityageable instanceof EntityHorse ? EntityTypes.MULE : EntityTypes.DONKEY;
        EntityHorseAbstract entityhorseabstract = (EntityHorseAbstract) entitytypes.a((World) worldserver);

        this.a(entityageable, entityhorseabstract);
        return entityhorseabstract;
    }
}
