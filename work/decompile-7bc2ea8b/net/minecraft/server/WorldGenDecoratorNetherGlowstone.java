package net.minecraft.server;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class WorldGenDecoratorNetherGlowstone extends WorldGenDecoratorFeatureSimple<WorldGenDecoratorFrequencyConfiguration> {

    public WorldGenDecoratorNetherGlowstone(Codec<WorldGenDecoratorFrequencyConfiguration> codec) {
        super(codec);
    }

    public Stream<BlockPosition> a(Random random, WorldGenDecoratorFrequencyConfiguration worldgendecoratorfrequencyconfiguration, BlockPosition blockposition) {
        return IntStream.range(0, random.nextInt(random.nextInt(worldgendecoratorfrequencyconfiguration.b) + 1)).mapToObj((i) -> {
            int j = random.nextInt(16) + blockposition.getX();
            int k = random.nextInt(16) + blockposition.getZ();
            int l = random.nextInt(120) + 4;

            return new BlockPosition(j, l, k);
        });
    }
}
