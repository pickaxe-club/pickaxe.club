package net.minecraft.server;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import javax.annotation.Nullable;

public class BehaviorBedJump extends Behavior<EntityInsentient> {

    private final float b;
    @Nullable
    private BlockPosition c;
    private int d;
    private int e;
    private int f;

    public BehaviorBedJump(float f) {
        super(ImmutableMap.of(MemoryModuleType.NEAREST_BED, MemoryStatus.VALUE_PRESENT, MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT));
        this.b = f;
    }

    protected boolean a(WorldServer worldserver, EntityInsentient entityinsentient) {
        return entityinsentient.isBaby() && this.b(worldserver, entityinsentient);
    }

    protected void a(WorldServer worldserver, EntityInsentient entityinsentient, long i) {
        super.a(worldserver, entityinsentient, i);
        this.a(entityinsentient).ifPresent((blockposition) -> {
            this.c = blockposition;
            this.d = 100;
            this.e = 3 + worldserver.random.nextInt(4);
            this.f = 0;
            this.a(entityinsentient, blockposition);
        });
    }

    protected void c(WorldServer worldserver, EntityInsentient entityinsentient, long i) {
        super.c(worldserver, entityinsentient, i);
        this.c = null;
        this.d = 0;
        this.e = 0;
        this.f = 0;
    }

    protected boolean b(WorldServer worldserver, EntityInsentient entityinsentient, long i) {
        return entityinsentient.isBaby() && this.c != null && this.a(worldserver, this.c) && !this.e(worldserver, entityinsentient) && !this.f(worldserver, entityinsentient);
    }

    @Override
    protected boolean a(long i) {
        return false;
    }

    protected void d(WorldServer worldserver, EntityInsentient entityinsentient, long i) {
        if (!this.c(worldserver, entityinsentient)) {
            --this.d;
        } else if (this.f > 0) {
            --this.f;
        } else {
            if (this.d(worldserver, entityinsentient)) {
                entityinsentient.getControllerJump().jump();
                --this.e;
                this.f = 5;
            }

        }
    }

    private void a(EntityInsentient entityinsentient, BlockPosition blockposition) {
        entityinsentient.getBehaviorController().setMemory(MemoryModuleType.WALK_TARGET, (Object) (new MemoryTarget(blockposition, this.b, 0)));
    }

    private boolean b(WorldServer worldserver, EntityInsentient entityinsentient) {
        return this.c(worldserver, entityinsentient) || this.a(entityinsentient).isPresent();
    }

    private boolean c(WorldServer worldserver, EntityInsentient entityinsentient) {
        BlockPosition blockposition = entityinsentient.getChunkCoordinates();
        BlockPosition blockposition1 = blockposition.down();

        return this.a(worldserver, blockposition) || this.a(worldserver, blockposition1);
    }

    private boolean d(WorldServer worldserver, EntityInsentient entityinsentient) {
        return this.a(worldserver, entityinsentient.getChunkCoordinates());
    }

    private boolean a(WorldServer worldserver, BlockPosition blockposition) {
        return worldserver.getType(blockposition).a((Tag) TagsBlock.BEDS);
    }

    private Optional<BlockPosition> a(EntityInsentient entityinsentient) {
        return entityinsentient.getBehaviorController().getMemory(MemoryModuleType.NEAREST_BED);
    }

    private boolean e(WorldServer worldserver, EntityInsentient entityinsentient) {
        return !this.c(worldserver, entityinsentient) && this.d <= 0;
    }

    private boolean f(WorldServer worldserver, EntityInsentient entityinsentient) {
        return this.c(worldserver, entityinsentient) && this.e <= 0;
    }
}
