package net.minecraft.server;

import javax.annotation.Nullable;

public interface IWorldWriter {

    boolean setTypeAndData(BlockPosition blockposition, IBlockData iblockdata, int i);

    boolean a(BlockPosition blockposition, boolean flag);

    default boolean b(BlockPosition blockposition, boolean flag) {
        return this.a(blockposition, flag, (Entity) null);
    }

    boolean a(BlockPosition blockposition, boolean flag, @Nullable Entity entity);

    default boolean addEntity(Entity entity) {
        return false;
    }

    // CraftBukkit start
    default boolean addEntity(Entity entity, org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason reason) {
        return false;
    }
    // CraftBukkit end
}
