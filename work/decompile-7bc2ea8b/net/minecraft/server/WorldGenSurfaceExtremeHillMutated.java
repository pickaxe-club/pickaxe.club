package net.minecraft.server;

import com.mojang.serialization.Codec;
import java.util.Random;

public class WorldGenSurfaceExtremeHillMutated extends WorldGenSurface<WorldGenSurfaceConfigurationBase> {

    public WorldGenSurfaceExtremeHillMutated(Codec<WorldGenSurfaceConfigurationBase> codec) {
        super(codec);
    }

    public void a(Random random, IChunkAccess ichunkaccess, BiomeBase biomebase, int i, int j, int k, double d0, IBlockData iblockdata, IBlockData iblockdata1, int l, long i1, WorldGenSurfaceConfigurationBase worldgensurfaceconfigurationbase) {
        if (d0 >= -1.0D && d0 <= 2.0D) {
            if (d0 > 1.0D) {
                WorldGenSurface.S.a(random, ichunkaccess, biomebase, i, j, k, d0, iblockdata, iblockdata1, l, i1, WorldGenSurface.F);
            } else {
                WorldGenSurface.S.a(random, ichunkaccess, biomebase, i, j, k, d0, iblockdata, iblockdata1, l, i1, WorldGenSurface.D);
            }
        } else {
            WorldGenSurface.S.a(random, ichunkaccess, biomebase, i, j, k, d0, iblockdata, iblockdata1, l, i1, WorldGenSurface.C);
        }

    }
}
