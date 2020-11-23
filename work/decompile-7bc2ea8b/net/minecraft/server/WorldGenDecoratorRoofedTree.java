package net.minecraft.server;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class WorldGenDecoratorRoofedTree extends WorldGenDecorator<WorldGenFeatureEmptyConfiguration2> {

    public WorldGenDecoratorRoofedTree(Codec<WorldGenFeatureEmptyConfiguration2> codec) {
        super(codec);
    }

    public Stream<BlockPosition> a(GeneratorAccess generatoraccess, ChunkGenerator chunkgenerator, Random random, WorldGenFeatureEmptyConfiguration2 worldgenfeatureemptyconfiguration2, BlockPosition blockposition) {
        return IntStream.range(0, 16).mapToObj((i) -> {
            int j = i / 4;
            int k = i % 4;
            int l = j * 4 + 1 + random.nextInt(3) + blockposition.getX();
            int i1 = k * 4 + 1 + random.nextInt(3) + blockposition.getZ();
            int j1 = generatoraccess.a(HeightMap.Type.MOTION_BLOCKING, l, i1);

            return new BlockPosition(l, j1, i1);
        });
    }
}
