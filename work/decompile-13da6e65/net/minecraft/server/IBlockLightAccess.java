package net.minecraft.server;

public interface IBlockLightAccess extends IBlockAccess {

    LightEngine e();

    default int getBrightness(EnumSkyBlock enumskyblock, BlockPosition blockposition) {
        return this.e().a(enumskyblock).b(blockposition);
    }

    default int getLightLevel(BlockPosition blockposition, int i) {
        return this.e().b(blockposition, i);
    }

    default boolean e(BlockPosition blockposition) {
        return this.getBrightness(EnumSkyBlock.SKY, blockposition) >= this.K();
    }
}
