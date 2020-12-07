package net.minecraft.server;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;

public class WorldGenSurfaceMesa extends WorldGenSurface<WorldGenSurfaceConfigurationBase> {

    private static final IBlockData K = Blocks.WHITE_TERRACOTTA.getBlockData();
    private static final IBlockData L = Blocks.ORANGE_TERRACOTTA.getBlockData();
    private static final IBlockData M = Blocks.TERRACOTTA.getBlockData();
    private static final IBlockData N = Blocks.YELLOW_TERRACOTTA.getBlockData();
    private static final IBlockData O = Blocks.BROWN_TERRACOTTA.getBlockData();
    private static final IBlockData P = Blocks.RED_TERRACOTTA.getBlockData();
    private static final IBlockData Q = Blocks.LIGHT_GRAY_TERRACOTTA.getBlockData();
    protected IBlockData[] a;
    protected long b;
    protected NoiseGenerator3 c;
    protected NoiseGenerator3 d;
    protected NoiseGenerator3 e;

    public WorldGenSurfaceMesa(Codec<WorldGenSurfaceConfigurationBase> codec) {
        super(codec);
    }

    public void a(Random random, IChunkAccess ichunkaccess, BiomeBase biomebase, int i, int j, int k, double d0, IBlockData iblockdata, IBlockData iblockdata1, int l, long i1, WorldGenSurfaceConfigurationBase worldgensurfaceconfigurationbase) {
        int j1 = i & 15;
        int k1 = j & 15;
        IBlockData iblockdata2 = WorldGenSurfaceMesa.K;
        WorldGenSurfaceConfiguration worldgensurfaceconfiguration = biomebase.e().e();
        IBlockData iblockdata3 = worldgensurfaceconfiguration.b();
        IBlockData iblockdata4 = worldgensurfaceconfiguration.a();
        IBlockData iblockdata5 = iblockdata3;
        int l1 = (int) (d0 / 3.0D + 3.0D + random.nextDouble() * 0.25D);
        boolean flag = Math.cos(d0 / 3.0D * 3.141592653589793D) > 0.0D;
        int i2 = -1;
        boolean flag1 = false;
        int j2 = 0;
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

        for (int k2 = k; k2 >= 0; --k2) {
            if (j2 < 15) {
                blockposition_mutableblockposition.d(j1, k2, k1);
                IBlockData iblockdata6 = ichunkaccess.getType(blockposition_mutableblockposition);

                if (iblockdata6.isAir()) {
                    i2 = -1;
                } else if (iblockdata6.a(iblockdata.getBlock())) {
                    if (i2 == -1) {
                        flag1 = false;
                        if (l1 <= 0) {
                            iblockdata2 = Blocks.AIR.getBlockData();
                            iblockdata5 = iblockdata;
                        } else if (k2 >= l - 4 && k2 <= l + 1) {
                            iblockdata2 = WorldGenSurfaceMesa.K;
                            iblockdata5 = iblockdata3;
                        }

                        if (k2 < l && (iblockdata2 == null || iblockdata2.isAir())) {
                            iblockdata2 = iblockdata1;
                        }

                        i2 = l1 + Math.max(0, k2 - l);
                        if (k2 >= l - 1) {
                            if (k2 > l + 3 + l1) {
                                IBlockData iblockdata7;

                                if (k2 >= 64 && k2 <= 127) {
                                    if (flag) {
                                        iblockdata7 = WorldGenSurfaceMesa.M;
                                    } else {
                                        iblockdata7 = this.a(i, k2, j);
                                    }
                                } else {
                                    iblockdata7 = WorldGenSurfaceMesa.L;
                                }

                                ichunkaccess.setType(blockposition_mutableblockposition, iblockdata7, false);
                            } else {
                                ichunkaccess.setType(blockposition_mutableblockposition, iblockdata4, false);
                                flag1 = true;
                            }
                        } else {
                            ichunkaccess.setType(blockposition_mutableblockposition, iblockdata5, false);
                            Block block = iblockdata5.getBlock();

                            if (block == Blocks.WHITE_TERRACOTTA || block == Blocks.ORANGE_TERRACOTTA || block == Blocks.MAGENTA_TERRACOTTA || block == Blocks.LIGHT_BLUE_TERRACOTTA || block == Blocks.YELLOW_TERRACOTTA || block == Blocks.LIME_TERRACOTTA || block == Blocks.PINK_TERRACOTTA || block == Blocks.GRAY_TERRACOTTA || block == Blocks.LIGHT_GRAY_TERRACOTTA || block == Blocks.CYAN_TERRACOTTA || block == Blocks.PURPLE_TERRACOTTA || block == Blocks.BLUE_TERRACOTTA || block == Blocks.BROWN_TERRACOTTA || block == Blocks.GREEN_TERRACOTTA || block == Blocks.RED_TERRACOTTA || block == Blocks.BLACK_TERRACOTTA) {
                                ichunkaccess.setType(blockposition_mutableblockposition, WorldGenSurfaceMesa.L, false);
                            }
                        }
                    } else if (i2 > 0) {
                        --i2;
                        if (flag1) {
                            ichunkaccess.setType(blockposition_mutableblockposition, WorldGenSurfaceMesa.L, false);
                        } else {
                            ichunkaccess.setType(blockposition_mutableblockposition, this.a(i, k2, j), false);
                        }
                    }

                    ++j2;
                }
            }
        }

    }

    @Override
    public void a(long i) {
        if (this.b != i || this.a == null) {
            this.b(i);
        }

        if (this.b != i || this.c == null || this.d == null) {
            SeededRandom seededrandom = new SeededRandom(i);

            this.c = new NoiseGenerator3(seededrandom, IntStream.rangeClosed(-3, 0));
            this.d = new NoiseGenerator3(seededrandom, ImmutableList.of(0));
        }

        this.b = i;
    }

    protected void b(long i) {
        this.a = new IBlockData[64];
        Arrays.fill(this.a, WorldGenSurfaceMesa.M);
        SeededRandom seededrandom = new SeededRandom(i);

        this.e = new NoiseGenerator3(seededrandom, ImmutableList.of(0));

        int j;

        for (j = 0; j < 64; ++j) {
            j += seededrandom.nextInt(5) + 1;
            if (j < 64) {
                this.a[j] = WorldGenSurfaceMesa.L;
            }
        }

        j = seededrandom.nextInt(4) + 2;

        int k;
        int l;
        int i1;
        int j1;

        for (j1 = 0; j1 < j; ++j1) {
            k = seededrandom.nextInt(3) + 1;
            l = seededrandom.nextInt(64);

            for (i1 = 0; l + i1 < 64 && i1 < k; ++i1) {
                this.a[l + i1] = WorldGenSurfaceMesa.N;
            }
        }

        j1 = seededrandom.nextInt(4) + 2;

        int k1;

        for (k = 0; k < j1; ++k) {
            l = seededrandom.nextInt(3) + 2;
            i1 = seededrandom.nextInt(64);

            for (k1 = 0; i1 + k1 < 64 && k1 < l; ++k1) {
                this.a[i1 + k1] = WorldGenSurfaceMesa.O;
            }
        }

        k = seededrandom.nextInt(4) + 2;

        for (l = 0; l < k; ++l) {
            i1 = seededrandom.nextInt(3) + 1;
            k1 = seededrandom.nextInt(64);

            for (int l1 = 0; k1 + l1 < 64 && l1 < i1; ++l1) {
                this.a[k1 + l1] = WorldGenSurfaceMesa.P;
            }
        }

        l = seededrandom.nextInt(3) + 3;
        i1 = 0;

        for (k1 = 0; k1 < l; ++k1) {
            boolean flag = true;

            i1 += seededrandom.nextInt(16) + 4;

            for (int i2 = 0; i1 + i2 < 64 && i2 < 1; ++i2) {
                this.a[i1 + i2] = WorldGenSurfaceMesa.K;
                if (i1 + i2 > 1 && seededrandom.nextBoolean()) {
                    this.a[i1 + i2 - 1] = WorldGenSurfaceMesa.Q;
                }

                if (i1 + i2 < 63 && seededrandom.nextBoolean()) {
                    this.a[i1 + i2 + 1] = WorldGenSurfaceMesa.Q;
                }
            }
        }

    }

    protected IBlockData a(int i, int j, int k) {
        int l = (int) Math.round(this.e.a((double) i / 512.0D, (double) k / 512.0D, false) * 2.0D);

        return this.a[(j + l + 64) % 64];
    }
}
