package net.minecraft.server;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;

public class WorldGenDecoratorIceburg extends WorldGenDecoratorFeatureSimple<WorldGenFeatureEmptyConfiguration2> {

    public WorldGenDecoratorIceburg(Codec<WorldGenFeatureEmptyConfiguration2> codec) {
        super(codec);
    }

    public Stream<BlockPosition> a(Random random, WorldGenFeatureEmptyConfiguration2 worldgenfeatureemptyconfiguration2, BlockPosition blockposition) {
        int i = random.nextInt(8) + 4 + blockposition.getX();
        int j = random.nextInt(8) + 4 + blockposition.getZ();

        return Stream.of(new BlockPosition(i, blockposition.getY(), j));
    }
}
