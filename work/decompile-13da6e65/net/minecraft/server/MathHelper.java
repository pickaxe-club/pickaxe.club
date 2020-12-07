package net.minecraft.server;

import java.util.Random;
import java.util.UUID;
import java.util.function.IntPredicate;

public class MathHelper {

    public static final float a = c(2.0F);
    private static final float[] b = (float[]) SystemUtils.a((Object) (new float[65536]), (afloat) -> {
        for (int i = 0; i < afloat.length; ++i) {
            afloat[i] = (float) Math.sin((double) i * 3.141592653589793D * 2.0D / 65536.0D);
        }

    });
    private static final Random c = new Random();
    private static final int[] d = new int[]{0, 1, 28, 2, 29, 14, 24, 3, 30, 22, 20, 15, 25, 17, 4, 8, 31, 27, 13, 23, 21, 19, 16, 7, 26, 12, 18, 6, 11, 5, 10, 9};
    private static final double e = Double.longBitsToDouble(4805340802404319232L);
    private static final double[] f = new double[257];
    private static final double[] g = new double[257];

    public static float sin(float f) {
        return MathHelper.b[(int) (f * 10430.378F) & '\uffff'];
    }

    public static float cos(float f) {
        return MathHelper.b[(int) (f * 10430.378F + 16384.0F) & '\uffff'];
    }

    public static float c(float f) {
        return (float) Math.sqrt((double) f);
    }

    public static float sqrt(double d0) {
        return (float) Math.sqrt(d0);
    }

    public static int d(float f) {
        int i = (int) f;

        return f < (float) i ? i - 1 : i;
    }

    public static int floor(double d0) {
        int i = (int) d0;

        return d0 < (double) i ? i - 1 : i;
    }

    public static long d(double d0) {
        long i = (long) d0;

        return d0 < (double) i ? i - 1L : i;
    }

    public static float e(float f) {
        return Math.abs(f);
    }

    public static int a(int i) {
        return Math.abs(i);
    }

    public static int f(float f) {
        int i = (int) f;

        return f > (float) i ? i + 1 : i;
    }

    public static int f(double d0) {
        int i = (int) d0;

        return d0 > (double) i ? i + 1 : i;
    }

    public static int clamp(int i, int j, int k) {
        return i < j ? j : (i > k ? k : i);
    }

    public static float a(float f, float f1, float f2) {
        return f < f1 ? f1 : (f > f2 ? f2 : f);
    }

    public static double a(double d0, double d1, double d2) {
        return d0 < d1 ? d1 : (d0 > d2 ? d2 : d0);
    }

    public static double b(double d0, double d1, double d2) {
        return d2 < 0.0D ? d0 : (d2 > 1.0D ? d1 : d(d2, d0, d1));
    }

    public static double a(double d0, double d1) {
        if (d0 < 0.0D) {
            d0 = -d0;
        }

        if (d1 < 0.0D) {
            d1 = -d1;
        }

        return d0 > d1 ? d0 : d1;
    }

    public static int a(int i, int j) {
        return Math.floorDiv(i, j);
    }

    public static int nextInt(Random random, int i, int j) {
        return i >= j ? i : random.nextInt(j - i + 1) + i;
    }

    public static float a(Random random, float f, float f1) {
        return f >= f1 ? f : random.nextFloat() * (f1 - f) + f;
    }

    public static double a(Random random, double d0, double d1) {
        return d0 >= d1 ? d0 : random.nextDouble() * (d1 - d0) + d0;
    }

    public static double a(long[] along) {
        long i = 0L;
        long[] along1 = along;
        int j = along.length;

        for (int k = 0; k < j; ++k) {
            long l = along1[k];

            i += l;
        }

        return (double) i / (double) along.length;
    }

    public static boolean b(double d0, double d1) {
        return Math.abs(d1 - d0) < 9.999999747378752E-6D;
    }

    public static int b(int i, int j) {
        return Math.floorMod(i, j);
    }

    public static float g(float f) {
        float f1 = f % 360.0F;

        if (f1 >= 180.0F) {
            f1 -= 360.0F;
        }

        if (f1 < -180.0F) {
            f1 += 360.0F;
        }

        return f1;
    }

    public static double g(double d0) {
        double d1 = d0 % 360.0D;

        if (d1 >= 180.0D) {
            d1 -= 360.0D;
        }

        if (d1 < -180.0D) {
            d1 += 360.0D;
        }

        return d1;
    }

    public static float c(float f, float f1) {
        return g(f1 - f);
    }

    public static float d(float f, float f1) {
        return e(c(f, f1));
    }

    public static float b(float f, float f1, float f2) {
        float f3 = c(f, f1);
        float f4 = a(f3, -f2, f2);

        return f1 - f4;
    }

    public static float c(float f, float f1, float f2) {
        f2 = e(f2);
        return f < f1 ? a(f + f2, f, f1) : a(f - f2, f1, f);
    }

    public static float d(float f, float f1, float f2) {
        float f3 = c(f, f1);

        return c(f, f + f3, f2);
    }

    public static int c(int i) {
        int j = i - 1;

        j |= j >> 1;
        j |= j >> 2;
        j |= j >> 4;
        j |= j >> 8;
        j |= j >> 16;
        return j + 1;
    }

    public static boolean d(int i) {
        return i != 0 && (i & i - 1) == 0;
    }

    public static int e(int i) {
        i = d(i) ? i : c(i);
        return MathHelper.d[(int) ((long) i * 125613361L >> 27) & 31];
    }

    public static int f(int i) {
        return e(i) - (d(i) ? 0 : 1);
    }

    public static int c(int i, int j) {
        if (j == 0) {
            return 0;
        } else if (i == 0) {
            return j;
        } else {
            if (i < 0) {
                j *= -1;
            }

            int k = i % j;

            return k == 0 ? i : i + j - k;
        }
    }

    public static float h(float f) {
        return f - (float) d(f);
    }

    public static double h(double d0) {
        return d0 - (double) d(d0);
    }

    public static long a(BaseBlockPosition baseblockposition) {
        return c(baseblockposition.getX(), baseblockposition.getY(), baseblockposition.getZ());
    }

    public static long c(int i, int j, int k) {
        long l = (long) (i * 3129871) ^ (long) k * 116129781L ^ (long) j;

        l = l * l * 42317861L + l * 11L;
        return l >> 16;
    }

    public static UUID a(Random random) {
        long i = random.nextLong() & -61441L | 16384L;
        long j = random.nextLong() & 4611686018427387903L | Long.MIN_VALUE;

        return new UUID(i, j);
    }

    public static UUID a() {
        return a(MathHelper.c);
    }

    public static double c(double d0, double d1, double d2) {
        return (d0 - d1) / (d2 - d1);
    }

    public static double d(double d0, double d1) {
        double d2 = d1 * d1 + d0 * d0;

        if (Double.isNaN(d2)) {
            return Double.NaN;
        } else {
            boolean flag = d0 < 0.0D;

            if (flag) {
                d0 = -d0;
            }

            boolean flag1 = d1 < 0.0D;

            if (flag1) {
                d1 = -d1;
            }

            boolean flag2 = d0 > d1;
            double d3;

            if (flag2) {
                d3 = d1;
                d1 = d0;
                d0 = d3;
            }

            d3 = i(d2);
            d1 *= d3;
            d0 *= d3;
            double d4 = MathHelper.e + d0;
            int i = (int) Double.doubleToRawLongBits(d4);
            double d5 = MathHelper.f[i];
            double d6 = MathHelper.g[i];
            double d7 = d4 - MathHelper.e;
            double d8 = d0 * d6 - d1 * d7;
            double d9 = (6.0D + d8 * d8) * d8 * 0.16666666666666666D;
            double d10 = d5 + d9;

            if (flag2) {
                d10 = 1.5707963267948966D - d10;
            }

            if (flag1) {
                d10 = 3.141592653589793D - d10;
            }

            if (flag) {
                d10 = -d10;
            }

            return d10;
        }
    }

    public static double i(double d0) {
        double d1 = 0.5D * d0;
        long i = Double.doubleToRawLongBits(d0);

        i = 6910469410427058090L - (i >> 1);
        d0 = Double.longBitsToDouble(i);
        d0 *= 1.5D - d1 * d0 * d0;
        return d0;
    }

    public static int f(float f, float f1, float f2) {
        int i = (int) (f * 6.0F) % 6;
        float f3 = f * 6.0F - (float) i;
        float f4 = f2 * (1.0F - f1);
        float f5 = f2 * (1.0F - f3 * f1);
        float f6 = f2 * (1.0F - (1.0F - f3) * f1);
        float f7;
        float f8;
        float f9;

        switch (i) {
            case 0:
                f7 = f2;
                f8 = f6;
                f9 = f4;
                break;
            case 1:
                f7 = f5;
                f8 = f2;
                f9 = f4;
                break;
            case 2:
                f7 = f4;
                f8 = f2;
                f9 = f6;
                break;
            case 3:
                f7 = f4;
                f8 = f5;
                f9 = f2;
                break;
            case 4:
                f7 = f6;
                f8 = f4;
                f9 = f2;
                break;
            case 5:
                f7 = f2;
                f8 = f4;
                f9 = f5;
                break;
            default:
                throw new RuntimeException("Something went wrong when converting from HSV to RGB. Input was " + f + ", " + f1 + ", " + f2);
        }

        int j = clamp((int) (f7 * 255.0F), 0, 255);
        int k = clamp((int) (f8 * 255.0F), 0, 255);
        int l = clamp((int) (f9 * 255.0F), 0, 255);

        return j << 16 | k << 8 | l;
    }

    public static int g(int i) {
        i ^= i >>> 16;
        i *= -2048144789;
        i ^= i >>> 13;
        i *= -1028477387;
        i ^= i >>> 16;
        return i;
    }

    public static int a(int i, int j, IntPredicate intpredicate) {
        int k = j - i;

        while (k > 0) {
            int l = k / 2;
            int i1 = i + l;

            if (intpredicate.test(i1)) {
                k = l;
            } else {
                i = i1 + 1;
                k -= l + 1;
            }
        }

        return i;
    }

    public static float g(float f, float f1, float f2) {
        return f1 + f * (f2 - f1);
    }

    public static double d(double d0, double d1, double d2) {
        return d1 + d0 * (d2 - d1);
    }

    public static double a(double d0, double d1, double d2, double d3, double d4, double d5) {
        return d(d1, d(d0, d2, d3), d(d0, d4, d5));
    }

    public static double a(double d0, double d1, double d2, double d3, double d4, double d5, double d6, double d7, double d8, double d9, double d10) {
        return d(d2, a(d0, d1, d3, d4, d5, d6), a(d0, d1, d7, d8, d9, d10));
    }

    public static double j(double d0) {
        return d0 * d0 * d0 * (d0 * (d0 * 6.0D - 15.0D) + 10.0D);
    }

    public static int k(double d0) {
        return d0 == 0.0D ? 0 : (d0 > 0.0D ? 1 : -1);
    }

    @Deprecated
    public static float j(float f, float f1, float f2) {
        float f3;

        for (f3 = f1 - f; f3 < -180.0F; f3 += 360.0F) {
            ;
        }

        while (f3 >= 180.0F) {
            f3 -= 360.0F;
        }

        return f + f2 * f3;
    }

    public static float k(float f) {
        return f * f;
    }

    static {
        for (int i = 0; i < 257; ++i) {
            double d0 = (double) i / 256.0D;
            double d1 = Math.asin(d0);

            MathHelper.g[i] = Math.cos(d1);
            MathHelper.f[i] = d1;
        }

    }
}
