package net.minecraft.server;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;

public class WorldGenNether extends StructureGenerator<WorldGenFeatureEmptyConfiguration> {

    private static final List<BiomeBase.BiomeMeta> u = Lists.newArrayList(new BiomeBase.BiomeMeta[]{new BiomeBase.BiomeMeta(EntityTypes.BLAZE, 10, 2, 3), new BiomeBase.BiomeMeta(EntityTypes.ZOMBIFIED_PIGLIN, 5, 4, 4), new BiomeBase.BiomeMeta(EntityTypes.WITHER_SKELETON, 8, 5, 5), new BiomeBase.BiomeMeta(EntityTypes.SKELETON, 2, 5, 5), new BiomeBase.BiomeMeta(EntityTypes.MAGMA_CUBE, 3, 4, 4)});

    public WorldGenNether(Codec<WorldGenFeatureEmptyConfiguration> codec) {
        super(codec);
    }

    protected boolean a(ChunkGenerator chunkgenerator, WorldChunkManager worldchunkmanager, long i, SeededRandom seededrandom, int j, int k, BiomeBase biomebase, ChunkCoordIntPair chunkcoordintpair, WorldGenFeatureEmptyConfiguration worldgenfeatureemptyconfiguration) {
        return seededrandom.nextInt(5) < 2;
    }

    @Override
    public StructureGenerator.a<WorldGenFeatureEmptyConfiguration> a() {
        return WorldGenNether.a::new;
    }

    @Override
    public List<BiomeBase.BiomeMeta> c() {
        return WorldGenNether.u;
    }

    public static class a extends StructureStart<WorldGenFeatureEmptyConfiguration> {

        public a(StructureGenerator<WorldGenFeatureEmptyConfiguration> structuregenerator, int i, int j, StructureBoundingBox structureboundingbox, int k, long l) {
            super(structuregenerator, i, j, structureboundingbox, k, l);
        }

        public void a(ChunkGenerator chunkgenerator, DefinedStructureManager definedstructuremanager, int i, int j, BiomeBase biomebase, WorldGenFeatureEmptyConfiguration worldgenfeatureemptyconfiguration) {
            WorldGenNetherPieces.WorldGenNetherPiece15 worldgennetherpieces_worldgennetherpiece15 = new WorldGenNetherPieces.WorldGenNetherPiece15(this.d, (i << 4) + 2, (j << 4) + 2);

            this.b.add(worldgennetherpieces_worldgennetherpiece15);
            worldgennetherpieces_worldgennetherpiece15.a((StructurePiece) worldgennetherpieces_worldgennetherpiece15, this.b, (Random) this.d);
            List list = worldgennetherpieces_worldgennetherpiece15.d;

            while (!list.isEmpty()) {
                int k = this.d.nextInt(list.size());
                StructurePiece structurepiece = (StructurePiece) list.remove(k);

                structurepiece.a((StructurePiece) worldgennetherpieces_worldgennetherpiece15, this.b, (Random) this.d);
            }

            this.b();
            this.a(this.d, 48, 70);
        }
    }
}
