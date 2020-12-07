package net.minecraft.server;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;

public class WorldGenDecoratorHeightmapSpreadDouble<DC extends WorldGenFeatureDecoratorConfiguration> extends WorldGenDecoratorHeightAbstract<DC> {

    public WorldGenDecoratorHeightmapSpreadDouble(Codec<DC> codec) {
        super(codec);
    }

    @Override
    protected HeightMap.Type a(DC dc) {
        return HeightMap.Type.MOTION_BLOCKING;
    }

    @Override
    public Stream<BlockPosition> a(WorldGenDecoratorContext worldgendecoratorcontext, Random random, DC dc, BlockPosition blockposition) {
        int i = blockposition.getX();
        int j = blockposition.getZ();
        int k = worldgendecoratorcontext.a(this.a(dc), i, j);

        return k == 0 ? Stream.of() : Stream.of(new BlockPosition(i, random.nextInt(k * 2), j));
    }
}
