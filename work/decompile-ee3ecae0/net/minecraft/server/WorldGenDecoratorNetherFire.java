package net.minecraft.server;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;

public class WorldGenDecoratorNetherFire extends WorldGenDecoratorFeatureSimple<WorldGenDecoratorFrequencyConfiguration> {

    public WorldGenDecoratorNetherFire(Function<Dynamic<?>, ? extends WorldGenDecoratorFrequencyConfiguration> function) {
        super(function);
    }

    public Stream<BlockPosition> a(Random random, WorldGenDecoratorFrequencyConfiguration worldgendecoratorfrequencyconfiguration, BlockPosition blockposition) {
        List<BlockPosition> list = Lists.newArrayList();

        for (int i = 0; i < random.nextInt(random.nextInt(worldgendecoratorfrequencyconfiguration.a) + 1) + 1; ++i) {
            int j = random.nextInt(16) + blockposition.getX();
            int k = random.nextInt(16) + blockposition.getZ();
            int l = random.nextInt(120) + 4;

            list.add(new BlockPosition(j, l, k));
        }

        return list.stream();
    }
}
