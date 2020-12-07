package net.minecraft.server;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class WorldGenDecoratorCountNoise extends WorldGenDecorator<WorldGenFeatureDecoratorNoiseConfiguration> {

    public WorldGenDecoratorCountNoise(Codec<WorldGenFeatureDecoratorNoiseConfiguration> codec) {
        super(codec);
    }

    public Stream<BlockPosition> a(WorldGenDecoratorContext worldgendecoratorcontext, Random random, WorldGenFeatureDecoratorNoiseConfiguration worldgenfeaturedecoratornoiseconfiguration, BlockPosition blockposition) {
        double d0 = BiomeBase.f.a((double) blockposition.getX() / 200.0D, (double) blockposition.getZ() / 200.0D, false);
        int i = d0 < worldgenfeaturedecoratornoiseconfiguration.c ? worldgenfeaturedecoratornoiseconfiguration.d : worldgenfeaturedecoratornoiseconfiguration.e;

        return IntStream.range(0, i).mapToObj((j) -> {
            return blockposition;
        });
    }
}
