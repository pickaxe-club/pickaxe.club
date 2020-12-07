package net.minecraft.server;

public class EntitySnowball extends EntityProjectileThrowable {

    public EntitySnowball(EntityTypes<? extends EntitySnowball> entitytypes, World world) {
        super(entitytypes, world);
    }

    public EntitySnowball(World world, EntityLiving entityliving) {
        super(EntityTypes.SNOWBALL, entityliving, world);
    }

    public EntitySnowball(World world, double d0, double d1, double d2) {
        super(EntityTypes.SNOWBALL, d0, d1, d2, world);
    }

    @Override
    protected Item getDefaultItem() {
        return Items.SNOWBALL;
    }

    @Override
    protected void a(MovingObjectPositionEntity movingobjectpositionentity) {
        super.a(movingobjectpositionentity);
        Entity entity = movingobjectpositionentity.getEntity();
        int i = entity instanceof EntityBlaze ? 3 : 0;

        entity.damageEntity(DamageSource.projectile(this, this.getShooter()), (float) i);
    }

    @Override
    protected void a(MovingObjectPosition movingobjectposition) {
        super.a(movingobjectposition);
        if (!this.world.isClientSide) {
            this.world.broadcastEntityEffect(this, (byte) 3);
            this.die();
        }

    }
}
