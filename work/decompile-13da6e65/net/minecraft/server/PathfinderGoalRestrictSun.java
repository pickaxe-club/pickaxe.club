package net.minecraft.server;

public class PathfinderGoalRestrictSun extends PathfinderGoal {

    private final EntityCreature a;

    public PathfinderGoalRestrictSun(EntityCreature entitycreature) {
        this.a = entitycreature;
    }

    @Override
    public boolean a() {
        return this.a.world.isDay() && this.a.getEquipment(EnumItemSlot.HEAD).isEmpty() && PathfinderGoalUtil.a(this.a);
    }

    @Override
    public void c() {
        ((Navigation) this.a.getNavigation()).c(true);
    }

    @Override
    public void d() {
        if (PathfinderGoalUtil.a(this.a)) {
            ((Navigation) this.a.getNavigation()).c(false);
        }

    }
}
