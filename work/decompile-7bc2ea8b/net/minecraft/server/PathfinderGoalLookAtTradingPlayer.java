package net.minecraft.server;

public class PathfinderGoalLookAtTradingPlayer extends PathfinderGoalLookAtPlayer {

    private final EntityVillagerAbstract g;

    public PathfinderGoalLookAtTradingPlayer(EntityVillagerAbstract entityvillagerabstract) {
        super(entityvillagerabstract, EntityHuman.class, 8.0F);
        this.g = entityvillagerabstract;
    }

    @Override
    public boolean a() {
        if (this.g.eO()) {
            this.b = this.g.getTrader();
            return true;
        } else {
            return false;
        }
    }
}
