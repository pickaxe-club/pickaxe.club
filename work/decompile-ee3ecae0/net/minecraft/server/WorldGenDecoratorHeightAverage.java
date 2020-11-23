package net.minecraft.server;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class WorldGenDecoratorHeightAverage extends WorldGenDecoratorFeatureSimple<WorldGenDecoratorHeightAverageConfiguration> {

    public WorldGenDecoratorHeightAverage(Function<Dynamic<?>, ? extends WorldGenDecoratorHeightAverageConfiguration> function) {
        super(function);
    }

    public Stream<BlockPosition> a(Random random, WorldGenDecoratorHeightAverageConfiguration worldgendecoratorheightaverageconfiguration, BlockPosition blockposition) {
        int i = worldgendecoratorheightaverageconfiguration.a;
        int j = worldgendecoratorheightaverageconfiguration.b;
        int k = worldgendecoratorheightaverageconfiguration.c;

        return IntStream.range(0, i).mapToObj((l) -> {
            int i1 = random.nextInt(16) + blockposition.getX();
            int j1 = random.nextInt(16) + blockposition.getZ();
            int k1 = random.nextInt(k) + random.nextInt(k) - k + j;

            return new BlockPosition(i1, k1, j1);
        });
    }
}
