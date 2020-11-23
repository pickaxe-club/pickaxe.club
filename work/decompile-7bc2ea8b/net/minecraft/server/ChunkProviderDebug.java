package net.minecraft.server;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ChunkProviderDebug extends ChunkGenerator {

    public static final ChunkProviderDebug d = new ChunkProviderDebug();
    public static final Codec<ChunkProviderDebug> e = MapCodec.of(Encoder.empty(), Decoder.unit(() -> {
        return ChunkProviderDebug.d;
    })).stable().codec();
    private static final List<IBlockData> h = (List) StreamSupport.stream(IRegistry.BLOCK.spliterator(), false).flatMap((block) -> {
        return block.getStates().a().stream();
    }).collect(Collectors.toList());
    private static final int i = MathHelper.f(MathHelper.c((float) ChunkProviderDebug.h.size()));
    private static final int j = MathHelper.f((float) ChunkProviderDebug.h.size() / (float) ChunkProviderDebug.i);
    protected static final IBlockData f = Blocks.AIR.getBlockData();
    protected static final IBlockData g = Blocks.BARRIER.getBlockData();

    private ChunkProviderDebug() {
        super(new WorldChunkManagerHell(Biomes.PLAINS), new StructureSettings(false));
    }

    @Override
    protected Codec<? extends ChunkGenerator> a() {
        return ChunkProviderDebug.e;
    }

    @Override
    public void buildBase(RegionLimitedWorldAccess regionlimitedworldaccess, IChunkAccess ichunkaccess) {}

    @Override
    public void doCarving(long i, BiomeManager biomemanager, IChunkAccess ichunkaccess, WorldGenStage.Features worldgenstage_features) {}

    @Override
    public void addDecorations(RegionLimitedWorldAccess regionlimitedworldaccess, StructureManager structuremanager) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
        int i = regionlimitedworldaccess.a();
        int j = regionlimitedworldaccess.b();

        for (int k = 0; k < 16; ++k) {
            for (int l = 0; l < 16; ++l) {
                int i1 = (i << 4) + k;
                int j1 = (j << 4) + l;

                regionlimitedworldaccess.setTypeAndData(blockposition_mutableblockposition.d(i1, 60, j1), ChunkProviderDebug.g, 2);
                IBlockData iblockdata = b(i1, j1);

                if (iblockdata != null) {
                    regionlimitedworldaccess.setTypeAndData(blockposition_mutableblockposition.d(i1, 70, j1), iblockdata, 2);
                }
            }
        }

    }

    @Override
    public void buildNoise(GeneratorAccess generatoraccess, StructureManager structuremanager, IChunkAccess ichunkaccess) {}

    @Override
    public int getBaseHeight(int i, int j, HeightMap.Type heightmap_type) {
        return 0;
    }

    @Override
    public IBlockAccess a(int i, int j) {
        return new BlockColumn(new IBlockData[0]);
    }

    public static IBlockData b(int i, int j) {
        IBlockData iblockdata = ChunkProviderDebug.f;

        if (i > 0 && j > 0 && i % 2 != 0 && j % 2 != 0) {
            i /= 2;
            j /= 2;
            if (i <= ChunkProviderDebug.i && j <= ChunkProviderDebug.j) {
                int k = MathHelper.a(i * ChunkProviderDebug.i + j);

                if (k < ChunkProviderDebug.h.size()) {
                    iblockdata = (IBlockData) ChunkProviderDebug.h.get(k);
                }
            }
        }

        return iblockdata;
    }
}
