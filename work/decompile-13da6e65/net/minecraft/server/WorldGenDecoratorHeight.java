package net.minecraft.server;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;

public abstract class WorldGenDecoratorHeight<DC extends WorldGenFeatureDecoratorConfiguration> extends WorldGenDecoratorHeightAbstract<DC> {

    public WorldGenDecoratorHeight(Codec<DC> codec) {
        super(codec);
    }

    @Override
    public Stream<BlockPosition> a(WorldGenDecoratorContext worldgendecoratorcontext, Random random, DC dc, BlockPosition blockposition) {
        int i = blockposition.getX();
        int j = blockposition.getZ();
        int k = worldgendecoratorcontext.a(this.a(dc), i, j);

        return k > 0 ? Stream.of(new BlockPosition(i, k, j)) : Stream.of();
    }
}
