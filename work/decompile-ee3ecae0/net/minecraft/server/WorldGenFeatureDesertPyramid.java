package net.minecraft.server;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class WorldGenFeatureDesertPyramid extends WorldGenFeatureRandomScattered<WorldGenFeatureEmptyConfiguration> {

    public WorldGenFeatureDesertPyramid(Function<Dynamic<?>, ? extends WorldGenFeatureEmptyConfiguration> function) {
        super(function);
    }

    @Override
    public String b() {
        return "Desert_Pyramid";
    }

    @Override
    public int c() {
        return 3;
    }

    @Override
    public StructureGenerator.a a() {
        return WorldGenFeatureDesertPyramid.a::new;
    }

    @Override
    protected int getSeed() {
        return 14357617;
    }

    public static class a extends StructureStart {

        public a(StructureGenerator<?> structuregenerator, int i, int j, StructureBoundingBox structureboundingbox, int k, long l) {
            super(structuregenerator, i, j, structureboundingbox, k, l);
        }

        @Override
        public void a(ChunkGenerator<?> chunkgenerator, DefinedStructureManager definedstructuremanager, int i, int j, BiomeBase biomebase) {
            WorldGenDesertPyramidPiece worldgendesertpyramidpiece = new WorldGenDesertPyramidPiece(this.d, i * 16, j * 16);

            this.b.add(worldgendesertpyramidpiece);
            this.b();
        }
    }
}
