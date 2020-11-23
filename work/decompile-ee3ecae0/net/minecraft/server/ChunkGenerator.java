package net.minecraft.server;

import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map.Entry;
import javax.annotation.Nullable;

public abstract class ChunkGenerator<C extends GeneratorSettingsDefault> {

    protected final GeneratorAccess a;
    protected final long seed;
    protected final WorldChunkManager c;
    protected final C settings;

    public ChunkGenerator(GeneratorAccess generatoraccess, WorldChunkManager worldchunkmanager, C c0) {
        this.a = generatoraccess;
        this.seed = generatoraccess.getSeed();
        this.c = worldchunkmanager;
        this.settings = c0;
    }

    public void createBiomes(IChunkAccess ichunkaccess) {
        ChunkCoordIntPair chunkcoordintpair = ichunkaccess.getPos();

        ((ProtoChunk) ichunkaccess).a(new BiomeStorage(chunkcoordintpair, this.c));
    }

    protected BiomeBase getBiome(BiomeManager biomemanager, BlockPosition blockposition) {
        return biomemanager.a(blockposition);
    }

    public void doCarving(BiomeManager biomemanager, IChunkAccess ichunkaccess, WorldGenStage.Features worldgenstage_features) {
        SeededRandom seededrandom = new SeededRandom();
        boolean flag = true;
        ChunkCoordIntPair chunkcoordintpair = ichunkaccess.getPos();
        int i = chunkcoordintpair.x;
        int j = chunkcoordintpair.z;
        BiomeBase biomebase = this.getBiome(biomemanager, chunkcoordintpair.l());
        BitSet bitset = ichunkaccess.a(worldgenstage_features);

        for (int k = i - 8; k <= i + 8; ++k) {
            for (int l = j - 8; l <= j + 8; ++l) {
                List<WorldGenCarverWrapper<?>> list = biomebase.a(worldgenstage_features);
                ListIterator listiterator = list.listIterator();

                while (listiterator.hasNext()) {
                    int i1 = listiterator.nextIndex();
                    WorldGenCarverWrapper<?> worldgencarverwrapper = (WorldGenCarverWrapper) listiterator.next();

                    seededrandom.c(this.seed + (long) i1, k, l);
                    if (worldgencarverwrapper.a(seededrandom, k, l)) {
                        worldgencarverwrapper.a(ichunkaccess, (blockposition) -> {
                            return this.getBiome(biomemanager, blockposition);
                        }, seededrandom, this.getSeaLevel(), k, l, i, j, bitset);
                    }
                }
            }
        }

    }

    @Nullable
    public BlockPosition findNearestMapFeature(World world, String s, BlockPosition blockposition, int i, boolean flag) {
        StructureGenerator<?> structuregenerator = (StructureGenerator) WorldGenerator.ao.get(s.toLowerCase(Locale.ROOT));

        return structuregenerator != null ? structuregenerator.getNearestGeneratedFeature(world, this, blockposition, i, flag) : null;
    }

    public void addDecorations(RegionLimitedWorldAccess regionlimitedworldaccess) {
        int i = regionlimitedworldaccess.a();
        int j = regionlimitedworldaccess.b();
        int k = i * 16;
        int l = j * 16;
        BlockPosition blockposition = new BlockPosition(k, 0, l);
        BiomeBase biomebase = this.getBiome(regionlimitedworldaccess.d(), blockposition.b(8, 8, 8));
        SeededRandom seededrandom = new SeededRandom();
        long i1 = seededrandom.a(regionlimitedworldaccess.getSeed(), k, l);
        WorldGenStage.Decoration[] aworldgenstage_decoration = WorldGenStage.Decoration.values();
        int j1 = aworldgenstage_decoration.length;

        for (int k1 = 0; k1 < j1; ++k1) {
            WorldGenStage.Decoration worldgenstage_decoration = aworldgenstage_decoration[k1];

            try {
                biomebase.a(worldgenstage_decoration, this, regionlimitedworldaccess, i1, seededrandom, blockposition);
            } catch (Exception exception) {
                CrashReport crashreport = CrashReport.a(exception, "Biome decoration");

                crashreport.a("Generation").a("CenterX", (Object) i).a("CenterZ", (Object) j).a("Step", (Object) worldgenstage_decoration).a("Seed", (Object) i1).a("Biome", (Object) IRegistry.BIOME.getKey(biomebase));
                throw new ReportedException(crashreport);
            }
        }

    }

    public abstract void buildBase(RegionLimitedWorldAccess regionlimitedworldaccess, IChunkAccess ichunkaccess);

    public void addMobs(RegionLimitedWorldAccess regionlimitedworldaccess) {}

    public C getSettings() {
        return this.settings;
    }

    public abstract int getSpawnHeight();

    public void doMobSpawning(WorldServer worldserver, boolean flag, boolean flag1) {}

    public boolean canSpawnStructure(BiomeBase biomebase, StructureGenerator<? extends WorldGenFeatureConfiguration> structuregenerator) {
        return biomebase.a(structuregenerator);
    }

    @Nullable
    public <C extends WorldGenFeatureConfiguration> C getFeatureConfiguration(BiomeBase biomebase, StructureGenerator<C> structuregenerator) {
        return biomebase.b(structuregenerator);
    }

    public WorldChunkManager getWorldChunkManager() {
        return this.c;
    }

    public long getSeed() {
        return this.seed;
    }

    public int getGenerationDepth() {
        return 256;
    }

    public List<BiomeBase.BiomeMeta> getMobsFor(EnumCreatureType enumcreaturetype, BlockPosition blockposition) {
        return this.a.getBiome(blockposition).getMobs(enumcreaturetype);
    }

    public void createStructures(BiomeManager biomemanager, IChunkAccess ichunkaccess, ChunkGenerator<?> chunkgenerator, DefinedStructureManager definedstructuremanager) {
        Iterator iterator = WorldGenerator.ao.values().iterator();

        while (iterator.hasNext()) {
            StructureGenerator<?> structuregenerator = (StructureGenerator) iterator.next();

            if (chunkgenerator.getWorldChunkManager().a(structuregenerator)) {
                StructureStart structurestart = ichunkaccess.a(structuregenerator.b());
                int i = structurestart != null ? structurestart.j() : 0;
                SeededRandom seededrandom = new SeededRandom();
                ChunkCoordIntPair chunkcoordintpair = ichunkaccess.getPos();
                StructureStart structurestart1 = StructureStart.a;
                BiomeBase biomebase = biomemanager.a(new BlockPosition(chunkcoordintpair.d() + 9, 0, chunkcoordintpair.e() + 9));

                if (structuregenerator.a(biomemanager, chunkgenerator, seededrandom, chunkcoordintpair.x, chunkcoordintpair.z, biomebase)) {
                    StructureStart structurestart2 = structuregenerator.a().create(structuregenerator, chunkcoordintpair.x, chunkcoordintpair.z, StructureBoundingBox.a(), i, chunkgenerator.getSeed());

                    structurestart2.a(this, definedstructuremanager, chunkcoordintpair.x, chunkcoordintpair.z, biomebase);
                    structurestart1 = structurestart2.e() ? structurestart2 : StructureStart.a;
                }

                ichunkaccess.a(structuregenerator.b(), structurestart1);
            }
        }

    }

    public void storeStructures(GeneratorAccess generatoraccess, IChunkAccess ichunkaccess) {
        boolean flag = true;
        int i = ichunkaccess.getPos().x;
        int j = ichunkaccess.getPos().z;
        int k = i << 4;
        int l = j << 4;

        for (int i1 = i - 8; i1 <= i + 8; ++i1) {
            for (int j1 = j - 8; j1 <= j + 8; ++j1) {
                long k1 = ChunkCoordIntPair.pair(i1, j1);
                Iterator iterator = generatoraccess.getChunkAt(i1, j1).h().entrySet().iterator();

                while (iterator.hasNext()) {
                    Entry<String, StructureStart> entry = (Entry) iterator.next();
                    StructureStart structurestart = (StructureStart) entry.getValue();

                    if (structurestart != StructureStart.a && structurestart.c().a(k, l, k + 15, l + 15)) {
                        ichunkaccess.a((String) entry.getKey(), k1);
                        PacketDebug.a(generatoraccess, structurestart);
                    }
                }
            }
        }

    }

    public abstract void buildNoise(GeneratorAccess generatoraccess, IChunkAccess ichunkaccess);

    public int getSeaLevel() {
        return 63;
    }

    public abstract int getBaseHeight(int i, int j, HeightMap.Type heightmap_type);

    public int b(int i, int j, HeightMap.Type heightmap_type) {
        return this.getBaseHeight(i, j, heightmap_type);
    }

    public int c(int i, int j, HeightMap.Type heightmap_type) {
        return this.getBaseHeight(i, j, heightmap_type) - 1;
    }
}
