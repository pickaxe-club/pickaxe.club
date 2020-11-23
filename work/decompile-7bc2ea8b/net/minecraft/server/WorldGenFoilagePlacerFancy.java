package net.minecraft.server;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import java.util.Set;

public class WorldGenFoilagePlacerFancy extends WorldGenFoilagePlacerBlob {

    public static final Codec<WorldGenFoilagePlacerFancy> c = RecordCodecBuilder.create((instance) -> {
        return a(instance).apply(instance, WorldGenFoilagePlacerFancy::new);
    });

    public WorldGenFoilagePlacerFancy(int i, int j, int k, int l, int i1) {
        super(i, j, k, l, i1, WorldGenFoilagePlacers.f);
    }

    @Override
    protected void a(VirtualLevelWritable virtuallevelwritable, Random random, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration, int i, WorldGenFoilagePlacer.b worldgenfoilageplacer_b, int j, int k, Set<BlockPosition> set, int l, StructureBoundingBox structureboundingbox) {
        for (int i1 = l; i1 >= l - j; --i1) {
            int j1 = k + (i1 != l && i1 != l - j ? 1 : 0);

            this.a(virtuallevelwritable, random, worldgenfeaturetreeconfiguration, worldgenfoilageplacer_b.a(), j1, set, i1, worldgenfoilageplacer_b.c(), structureboundingbox);
        }

    }

    @Override
    protected boolean a(Random random, int i, int j, int k, int l, boolean flag) {
        return MathHelper.k((float) i + 0.5F) + MathHelper.k((float) k + 0.5F) > (float) (l * l);
    }
}
