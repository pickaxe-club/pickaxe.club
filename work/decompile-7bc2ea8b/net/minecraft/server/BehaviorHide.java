package net.minecraft.server;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;

public class BehaviorHide extends Behavior<EntityLiving> {

    private final int b;
    private final int c;
    private int d;

    public BehaviorHide(int i, int j) {
        super(ImmutableMap.of(MemoryModuleType.HIDING_PLACE, MemoryStatus.VALUE_PRESENT, MemoryModuleType.HEARD_BELL_TIME, MemoryStatus.VALUE_PRESENT));
        this.c = i * 20;
        this.d = 0;
        this.b = j;
    }

    @Override
    protected void a(WorldServer worldserver, EntityLiving entityliving, long i) {
        BehaviorController<?> behaviorcontroller = entityliving.getBehaviorController();
        Optional<Long> optional = behaviorcontroller.getMemory(MemoryModuleType.HEARD_BELL_TIME);
        boolean flag = (Long) optional.get() + 300L <= i;

        if (this.d <= this.c && !flag) {
            BlockPosition blockposition = ((GlobalPos) behaviorcontroller.getMemory(MemoryModuleType.HIDING_PLACE).get()).getBlockPosition();

            if (blockposition.a((BaseBlockPosition) entityliving.getChunkCoordinates(), (double) this.b)) {
                ++this.d;
            }

        } else {
            behaviorcontroller.removeMemory(MemoryModuleType.HEARD_BELL_TIME);
            behaviorcontroller.removeMemory(MemoryModuleType.HIDING_PLACE);
            behaviorcontroller.a(worldserver.getDayTime(), worldserver.getTime());
            this.d = 0;
        }
    }
}
