package net.minecraft.server;

import com.mojang.serialization.Codec;
import java.util.Random;

public class WorldGenEndIsland extends WorldGenerator<WorldGenFeatureEmptyConfiguration> {

    public WorldGenEndIsland(Codec<WorldGenFeatureEmptyConfiguration> codec) {
        super(codec);
    }

    public boolean a(GeneratorAccessSeed generatoraccessseed, ChunkGenerator chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureEmptyConfiguration worldgenfeatureemptyconfiguration) {
        float f = (float) (random.nextInt(3) + 4);

        for (int i = 0; f > 0.5F; --i) {
            for (int j = MathHelper.d(-f); j <= MathHelper.f(f); ++j) {
                for (int k = MathHelper.d(-f); k <= MathHelper.f(f); ++k) {
                    if ((float) (j * j + k * k) <= (f + 1.0F) * (f + 1.0F)) {
                        this.a(generatoraccessseed, blockposition.b(j, i, k), Blocks.END_STONE.getBlockData());
                    }
                }
            }

            f = (float) ((double) f - ((double) random.nextInt(2) + 0.5D));
        }

        return true;
    }
}
