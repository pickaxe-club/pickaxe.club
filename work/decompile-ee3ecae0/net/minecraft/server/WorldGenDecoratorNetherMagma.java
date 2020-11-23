package net.minecraft.server;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class WorldGenDecoratorNetherMagma extends WorldGenDecorator<WorldGenDecoratorFrequencyConfiguration> {

    public WorldGenDecoratorNetherMagma(Function<Dynamic<?>, ? extends WorldGenDecoratorFrequencyConfiguration> function) {
        super(function);
    }

    public Stream<BlockPosition> a(GeneratorAccess generatoraccess, ChunkGenerator<? extends GeneratorSettingsDefault> chunkgenerator, Random random, WorldGenDecoratorFrequencyConfiguration worldgendecoratorfrequencyconfiguration, BlockPosition blockposition) {
        int i = generatoraccess.getSeaLevel() / 2 + 1;

        return IntStream.range(0, worldgendecoratorfrequencyconfiguration.a).mapToObj((j) -> {
            int k = random.nextInt(16) + blockposition.getX();
            int l = random.nextInt(16) + blockposition.getZ();
            int i1 = i - 5 + random.nextInt(10);

            return new BlockPosition(k, i1, l);
        });
    }
}
