package net.minecraft.server;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;

public class WorldGenStronghold extends StructureGenerator<WorldGenFeatureEmptyConfiguration> {

    public WorldGenStronghold(Codec<WorldGenFeatureEmptyConfiguration> codec) {
        super(codec);
    }

    @Override
    public StructureGenerator.a<WorldGenFeatureEmptyConfiguration> a() {
        return WorldGenStronghold.a::new;
    }

    protected boolean a(ChunkGenerator chunkgenerator, WorldChunkManager worldchunkmanager, long i, SeededRandom seededrandom, int j, int k, BiomeBase biomebase, ChunkCoordIntPair chunkcoordintpair, WorldGenFeatureEmptyConfiguration worldgenfeatureemptyconfiguration) {
        return chunkgenerator.a(new ChunkCoordIntPair(j, k));
    }

    public static class a extends StructureStart<WorldGenFeatureEmptyConfiguration> {

        private final long e;

        public a(StructureGenerator<WorldGenFeatureEmptyConfiguration> structuregenerator, int i, int j, StructureBoundingBox structureboundingbox, int k, long l) {
            super(structuregenerator, i, j, structureboundingbox, k, l);
            this.e = l;
        }

        public void a(ChunkGenerator chunkgenerator, DefinedStructureManager definedstructuremanager, int i, int j, BiomeBase biomebase, WorldGenFeatureEmptyConfiguration worldgenfeatureemptyconfiguration) {
            int k = 0;

            WorldGenStrongholdPieces.WorldGenStrongholdStart worldgenstrongholdpieces_worldgenstrongholdstart;

            do {
                this.b.clear();
                this.c = StructureBoundingBox.a();
                this.d.c(this.e + (long) (k++), i, j);
                WorldGenStrongholdPieces.a();
                worldgenstrongholdpieces_worldgenstrongholdstart = new WorldGenStrongholdPieces.WorldGenStrongholdStart(this.d, (i << 4) + 2, (j << 4) + 2);
                this.b.add(worldgenstrongholdpieces_worldgenstrongholdstart);
                worldgenstrongholdpieces_worldgenstrongholdstart.a((StructurePiece) worldgenstrongholdpieces_worldgenstrongholdstart, this.b, (Random) this.d);
                List list = worldgenstrongholdpieces_worldgenstrongholdstart.c;

                while (!list.isEmpty()) {
                    int l = this.d.nextInt(list.size());
                    StructurePiece structurepiece = (StructurePiece) list.remove(l);

                    structurepiece.a((StructurePiece) worldgenstrongholdpieces_worldgenstrongholdstart, this.b, (Random) this.d);
                }

                this.b();
                this.a(chunkgenerator.getSeaLevel(), this.d, 10);
            } while (this.b.isEmpty() || worldgenstrongholdpieces_worldgenstrongholdstart.b == null);

        }
    }
}
