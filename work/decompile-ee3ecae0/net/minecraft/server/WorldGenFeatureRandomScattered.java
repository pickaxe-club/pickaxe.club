package net.minecraft.server;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;

public abstract class WorldGenFeatureRandomScattered<C extends WorldGenFeatureConfiguration> extends StructureGenerator<C> {

    public WorldGenFeatureRandomScattered(Function<Dynamic<?>, ? extends C> function) {
        super(function);
    }

    @Override
    protected ChunkCoordIntPair a(ChunkGenerator<?> chunkgenerator, Random random, int i, int j, int k, int l) {
        int i1 = this.a(chunkgenerator);
        int j1 = this.b(chunkgenerator);
        int k1 = i + i1 * k;
        int l1 = j + i1 * l;
        int i2 = k1 < 0 ? k1 - i1 + 1 : k1;
        int j2 = l1 < 0 ? l1 - i1 + 1 : l1;
        int k2 = i2 / i1;
        int l2 = j2 / i1;

        ((SeededRandom) random).a(chunkgenerator.getSeed(), k2, l2, this.getSeed());
        k2 *= i1;
        l2 *= i1;
        k2 += random.nextInt(i1 - j1);
        l2 += random.nextInt(i1 - j1);
        return new ChunkCoordIntPair(k2, l2);
    }

    @Override
    public boolean a(BiomeManager biomemanager, ChunkGenerator<?> chunkgenerator, Random random, int i, int j, BiomeBase biomebase) {
        ChunkCoordIntPair chunkcoordintpair = this.a(chunkgenerator, random, i, j, 0, 0);

        return i == chunkcoordintpair.x && j == chunkcoordintpair.z && chunkgenerator.canSpawnStructure(biomebase, this);
    }

    protected int a(ChunkGenerator<?> chunkgenerator) {
        return chunkgenerator.getSettings().h();
    }

    protected int b(ChunkGenerator<?> chunkgenerator) {
        return chunkgenerator.getSettings().i();
    }

    protected abstract int getSeed();
}
