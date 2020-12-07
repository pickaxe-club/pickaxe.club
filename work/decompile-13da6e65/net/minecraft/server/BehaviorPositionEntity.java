package net.minecraft.server;

import java.util.List;
import java.util.Optional;

public class BehaviorPositionEntity implements BehaviorPosition {

    private final Entity a;
    private final boolean b;

    public BehaviorPositionEntity(Entity entity, boolean flag) {
        this.a = entity;
        this.b = flag;
    }

    @Override
    public Vec3D a() {
        return this.b ? this.a.getPositionVector().add(0.0D, (double) this.a.getHeadHeight(), 0.0D) : this.a.getPositionVector();
    }

    @Override
    public BlockPosition b() {
        return this.a.getChunkCoordinates();
    }

    @Override
    public boolean a(EntityLiving entityliving) {
        if (!(this.a instanceof EntityLiving)) {
            return true;
        } else {
            Optional<List<EntityLiving>> optional = entityliving.getBehaviorController().getMemory(MemoryModuleType.VISIBLE_MOBS);

            return this.a.isAlive() && optional.isPresent() && ((List) optional.get()).contains(this.a);
        }
    }

    public String toString() {
        return "EntityTracker for " + this.a;
    }
}
