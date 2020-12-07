package net.minecraft.server;

public interface WorldAccess extends GeneratorAccess {

    WorldServer getMinecraftWorld();

    default void addAllEntities(Entity entity) {
        entity.cp().forEach(this::addEntity);
    }
}
