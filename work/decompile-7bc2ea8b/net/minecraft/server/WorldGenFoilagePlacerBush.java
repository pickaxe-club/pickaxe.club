package net.minecraft.server;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import java.util.Set;

public class WorldGenFoilagePlacerBush extends WorldGenFoilagePlacerBlob {

    public static final Codec<WorldGenFoilagePlacerBush> c = RecordCodecBuilder.create((instance) -> {
        return a(instance).apply(instance, WorldGenFoilagePlacerBush::new);
    });

    public WorldGenFoilagePlacerBush(int i, int j, int k, int l, int i1) {
        super(i, j, k, l, i1, WorldGenFoilagePlacers.e);
    }

    @Override
    protected WorldGenFoilagePlacers<?> a() {
        return WorldGenFoilagePlacers.e;
    }

    @Override
    protected void a(VirtualLevelWritable virtuallevelwritable, Random random, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration, int i, WorldGenFoilagePlacer.b worldgenfoilageplacer_b, int j, int k, Set<BlockPosition> set, int l, StructureBoundingBox structureboundingbox) {
        for (int i1 = l; i1 >= l - j; --i1) {
            int j1 = k + worldgenfoilageplacer_b.b() - 1 - i1;

            this.a(virtuallevelwritable, random, worldgenfeaturetreeconfiguration, worldgenfoilageplacer_b.a(), j1, set, i1, worldgenfoilageplacer_b.c(), structureboundingbox);
        }

    }

    @Override
    protected boolean a(Random random, int i, int j, int k, int l, boolean flag) {
        return i == l && k == l && random.nextInt(2) == 0;
    }
}
