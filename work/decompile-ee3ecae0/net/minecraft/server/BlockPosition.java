package net.minecraft.server;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.Spliterator.OfInt;
import java.util.Spliterators.AbstractSpliterator;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.concurrent.Immutable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Immutable
public class BlockPosition extends BaseBlockPosition implements MinecraftSerializable {

    private static final Logger LOGGER = LogManager.getLogger();
    public static final BlockPosition ZERO = new BlockPosition(0, 0, 0);
    private static final int c = 1 + MathHelper.e(MathHelper.c(30000000));
    private static final int d = BlockPosition.c;
    private static final int f = 64 - BlockPosition.c - BlockPosition.d;
    private static final long g = (1L << BlockPosition.c) - 1L;
    private static final long h = (1L << BlockPosition.f) - 1L;
    private static final long i = (1L << BlockPosition.d) - 1L;
    private static final int j = BlockPosition.f;
    private static final int k = BlockPosition.f + BlockPosition.d;

    public BlockPosition(int i, int j, int k) {
        super(i, j, k);
    }

    public BlockPosition(double d0, double d1, double d2) {
        super(d0, d1, d2);
    }

    public BlockPosition(Entity entity) {
        this(entity.locX(), entity.locY(), entity.locZ());
    }

    public BlockPosition(Vec3D vec3d) {
        this(vec3d.x, vec3d.y, vec3d.z);
    }

    public BlockPosition(IPosition iposition) {
        this(iposition.getX(), iposition.getY(), iposition.getZ());
    }

    public BlockPosition(BaseBlockPosition baseblockposition) {
        this(baseblockposition.getX(), baseblockposition.getY(), baseblockposition.getZ());
    }

    public static <T> BlockPosition a(Dynamic<T> dynamic) {
        OfInt ofint = dynamic.asIntStream().spliterator();
        int[] aint = new int[3];

        if (ofint.tryAdvance((i) -> {
            aint[0] = i;
        }) && ofint.tryAdvance((i) -> {
            aint[1] = i;
        })) {
            ofint.tryAdvance((i) -> {
                aint[2] = i;
            });
        }

        return new BlockPosition(aint[0], aint[1], aint[2]);
    }

    @Override
    public <T> T a(DynamicOps<T> dynamicops) {
        return dynamicops.createIntList(IntStream.of(new int[]{this.getX(), this.getY(), this.getZ()}));
    }

    public static long a(long i, EnumDirection enumdirection) {
        return a(i, enumdirection.getAdjacentX(), enumdirection.getAdjacentY(), enumdirection.getAdjacentZ());
    }

    public static long a(long i, int j, int k, int l) {
        return a(b(i) + j, c(i) + k, d(i) + l);
    }

    public static int b(long i) {
        return (int) (i << 64 - BlockPosition.k - BlockPosition.c >> 64 - BlockPosition.c);
    }

    public static int c(long i) {
        return (int) (i << 64 - BlockPosition.f >> 64 - BlockPosition.f);
    }

    public static int d(long i) {
        return (int) (i << 64 - BlockPosition.j - BlockPosition.d >> 64 - BlockPosition.d);
    }

    public static BlockPosition fromLong(long i) {
        return new BlockPosition(b(i), c(i), d(i));
    }

    public static long a(int i, int j, int k) {
        long l = 0L;

        l |= ((long) i & BlockPosition.g) << BlockPosition.k;
        l |= ((long) j & BlockPosition.h) << 0;
        l |= ((long) k & BlockPosition.i) << BlockPosition.j;
        return l;
    }

    public static long f(long i) {
        return i & -16L;
    }

    public long asLong() {
        return a(this.getX(), this.getY(), this.getZ());
    }

    public BlockPosition a(double d0, double d1, double d2) {
        return d0 == 0.0D && d1 == 0.0D && d2 == 0.0D ? this : new BlockPosition((double) this.getX() + d0, (double) this.getY() + d1, (double) this.getZ() + d2);
    }

    public BlockPosition b(int i, int j, int k) {
        return i == 0 && j == 0 && k == 0 ? this : new BlockPosition(this.getX() + i, this.getY() + j, this.getZ() + k);
    }

    public BlockPosition a(BaseBlockPosition baseblockposition) {
        return this.b(baseblockposition.getX(), baseblockposition.getY(), baseblockposition.getZ());
    }

    public BlockPosition b(BaseBlockPosition baseblockposition) {
        return this.b(-baseblockposition.getX(), -baseblockposition.getY(), -baseblockposition.getZ());
    }

    public BlockPosition up() {
        return this.shift(EnumDirection.UP);
    }

    public BlockPosition up(int i) {
        return this.shift(EnumDirection.UP, i);
    }

    @Override
    public BlockPosition down() {
        return this.shift(EnumDirection.DOWN);
    }

    @Override
    public BlockPosition down(int i) {
        return this.shift(EnumDirection.DOWN, i);
    }

    public BlockPosition north() {
        return this.shift(EnumDirection.NORTH);
    }

    public BlockPosition north(int i) {
        return this.shift(EnumDirection.NORTH, i);
    }

    public BlockPosition south() {
        return this.shift(EnumDirection.SOUTH);
    }

    public BlockPosition south(int i) {
        return this.shift(EnumDirection.SOUTH, i);
    }

    public BlockPosition west() {
        return this.shift(EnumDirection.WEST);
    }

    public BlockPosition west(int i) {
        return this.shift(EnumDirection.WEST, i);
    }

    public BlockPosition east() {
        return this.shift(EnumDirection.EAST);
    }

    public BlockPosition east(int i) {
        return this.shift(EnumDirection.EAST, i);
    }

    public BlockPosition shift(EnumDirection enumdirection) {
        return new BlockPosition(this.getX() + enumdirection.getAdjacentX(), this.getY() + enumdirection.getAdjacentY(), this.getZ() + enumdirection.getAdjacentZ());
    }

    @Override
    public BlockPosition shift(EnumDirection enumdirection, int i) {
        return i == 0 ? this : new BlockPosition(this.getX() + enumdirection.getAdjacentX() * i, this.getY() + enumdirection.getAdjacentY() * i, this.getZ() + enumdirection.getAdjacentZ() * i);
    }

    public BlockPosition a(EnumBlockRotation enumblockrotation) {
        switch (enumblockrotation) {
            case NONE:
            default:
                return this;
            case CLOCKWISE_90:
                return new BlockPosition(-this.getZ(), this.getY(), this.getX());
            case CLOCKWISE_180:
                return new BlockPosition(-this.getX(), this.getY(), -this.getZ());
            case COUNTERCLOCKWISE_90:
                return new BlockPosition(this.getZ(), this.getY(), -this.getX());
        }
    }

    @Override
    public BlockPosition d(BaseBlockPosition baseblockposition) {
        return new BlockPosition(this.getY() * baseblockposition.getZ() - this.getZ() * baseblockposition.getY(), this.getZ() * baseblockposition.getX() - this.getX() * baseblockposition.getZ(), this.getX() * baseblockposition.getY() - this.getY() * baseblockposition.getX());
    }

    public BlockPosition immutableCopy() {
        return this;
    }

    public static Iterable<BlockPosition> a(BlockPosition blockposition, BlockPosition blockposition1) {
        return b(Math.min(blockposition.getX(), blockposition1.getX()), Math.min(blockposition.getY(), blockposition1.getY()), Math.min(blockposition.getZ(), blockposition1.getZ()), Math.max(blockposition.getX(), blockposition1.getX()), Math.max(blockposition.getY(), blockposition1.getY()), Math.max(blockposition.getZ(), blockposition1.getZ()));
    }

    public static Stream<BlockPosition> b(BlockPosition blockposition, BlockPosition blockposition1) {
        return a(Math.min(blockposition.getX(), blockposition1.getX()), Math.min(blockposition.getY(), blockposition1.getY()), Math.min(blockposition.getZ(), blockposition1.getZ()), Math.max(blockposition.getX(), blockposition1.getX()), Math.max(blockposition.getY(), blockposition1.getY()), Math.max(blockposition.getZ(), blockposition1.getZ()));
    }

    public static Stream<BlockPosition> a(StructureBoundingBox structureboundingbox) {
        return a(Math.min(structureboundingbox.a, structureboundingbox.d), Math.min(structureboundingbox.b, structureboundingbox.e), Math.min(structureboundingbox.c, structureboundingbox.f), Math.max(structureboundingbox.a, structureboundingbox.d), Math.max(structureboundingbox.b, structureboundingbox.e), Math.max(structureboundingbox.c, structureboundingbox.f));
    }

    public static Stream<BlockPosition> a(final int i, final int j, final int k, final int l, final int i1, final int j1) {
        return StreamSupport.stream(new AbstractSpliterator<BlockPosition>((long) ((l - i + 1) * (i1 - j + 1) * (j1 - k + 1)), 64) {
            final CursorPosition a = new CursorPosition(i, j, k, l, i1, j1);
            final BlockPosition.MutableBlockPosition b = new BlockPosition.MutableBlockPosition();

            public boolean tryAdvance(Consumer<? super BlockPosition> consumer) {
                if (this.a.a()) {
                    consumer.accept(this.b.d(this.a.b(), this.a.c(), this.a.d()));
                    return true;
                } else {
                    return false;
                }
            }
        }, false);
    }

    public static Iterable<BlockPosition> b(int i, int j, int k, int l, int i1, int j1) {
        return () -> {
            return new AbstractIterator<BlockPosition>() {
                final CursorPosition a = new CursorPosition(i, j, k, l, i1, j1);
                final BlockPosition.MutableBlockPosition b = new BlockPosition.MutableBlockPosition();

                protected BlockPosition computeNext() {
                    return (BlockPosition) (this.a.a() ? this.b.d(this.a.b(), this.a.c(), this.a.d()) : (BlockPosition) this.endOfData());
                }
            };
        };
    }

    public static final class PooledBlockPosition extends BlockPosition.MutableBlockPosition implements AutoCloseable {

        private boolean f;
        private static final List<BlockPosition.PooledBlockPosition> g = Lists.newArrayList();

        private PooledBlockPosition(int i, int j, int k) {
            super(i, j, k);
        }

        public static BlockPosition.PooledBlockPosition r() {
            return f(0, 0, 0);
        }

        public static BlockPosition.PooledBlockPosition b(Entity entity) {
            return d(entity.locX(), entity.locY(), entity.locZ());
        }

        public static BlockPosition.PooledBlockPosition d(double d0, double d1, double d2) {
            return f(MathHelper.floor(d0), MathHelper.floor(d1), MathHelper.floor(d2));
        }

        public static BlockPosition.PooledBlockPosition f(int i, int j, int k) {
            synchronized (BlockPosition.PooledBlockPosition.g) {
                if (!BlockPosition.PooledBlockPosition.g.isEmpty()) {
                    BlockPosition.PooledBlockPosition blockposition_pooledblockposition = (BlockPosition.PooledBlockPosition) BlockPosition.PooledBlockPosition.g.remove(BlockPosition.PooledBlockPosition.g.size() - 1);

                    if (blockposition_pooledblockposition != null && blockposition_pooledblockposition.f) {
                        blockposition_pooledblockposition.f = false;
                        blockposition_pooledblockposition.d(i, j, k);
                        return blockposition_pooledblockposition;
                    }
                }
            }

            return new BlockPosition.PooledBlockPosition(i, j, k);
        }

        @Override
        public BlockPosition.PooledBlockPosition d(int i, int j, int k) {
            return (BlockPosition.PooledBlockPosition) super.d(i, j, k);
        }

        @Override
        public BlockPosition.PooledBlockPosition a(Entity entity) {
            return (BlockPosition.PooledBlockPosition) super.a(entity);
        }

        @Override
        public BlockPosition.PooledBlockPosition c(double d0, double d1, double d2) {
            return (BlockPosition.PooledBlockPosition) super.c(d0, d1, d2);
        }

        @Override
        public BlockPosition.PooledBlockPosition g(BaseBlockPosition baseblockposition) {
            return (BlockPosition.PooledBlockPosition) super.g(baseblockposition);
        }

        @Override
        public BlockPosition.PooledBlockPosition c(EnumDirection enumdirection) {
            return (BlockPosition.PooledBlockPosition) super.c(enumdirection);
        }

        @Override
        public BlockPosition.PooledBlockPosition c(EnumDirection enumdirection, int i) {
            return (BlockPosition.PooledBlockPosition) super.c(enumdirection, i);
        }

        @Override
        public BlockPosition.PooledBlockPosition e(int i, int j, int k) {
            return (BlockPosition.PooledBlockPosition) super.e(i, j, k);
        }

        public void close() {
            synchronized (BlockPosition.PooledBlockPosition.g) {
                if (BlockPosition.PooledBlockPosition.g.size() < 100) {
                    BlockPosition.PooledBlockPosition.g.add(this);
                }

                this.f = true;
            }
        }
    }

    public static class MutableBlockPosition extends BlockPosition {

        protected int b;
        protected int c;
        protected int d;

        public MutableBlockPosition() {
            this(0, 0, 0);
        }

        public MutableBlockPosition(BlockPosition blockposition) {
            this(blockposition.getX(), blockposition.getY(), blockposition.getZ());
        }

        public MutableBlockPosition(int i, int j, int k) {
            super(0, 0, 0);
            this.b = i;
            this.c = j;
            this.d = k;
        }

        public MutableBlockPosition(double d0, double d1, double d2) {
            this(MathHelper.floor(d0), MathHelper.floor(d1), MathHelper.floor(d2));
        }

        public MutableBlockPosition(Entity entity) {
            this(entity.locX(), entity.locY(), entity.locZ());
        }

        @Override
        public BlockPosition a(double d0, double d1, double d2) {
            return super.a(d0, d1, d2).immutableCopy();
        }

        @Override
        public BlockPosition b(int i, int j, int k) {
            return super.b(i, j, k).immutableCopy();
        }

        @Override
        public BlockPosition shift(EnumDirection enumdirection, int i) {
            return super.shift(enumdirection, i).immutableCopy();
        }

        @Override
        public BlockPosition a(EnumBlockRotation enumblockrotation) {
            return super.a(enumblockrotation).immutableCopy();
        }

        @Override
        public int getX() {
            return this.b;
        }

        @Override
        public int getY() {
            return this.c;
        }

        @Override
        public int getZ() {
            return this.d;
        }

        public BlockPosition.MutableBlockPosition d(int i, int j, int k) {
            this.b = i;
            this.c = j;
            this.d = k;
            return this;
        }

        public BlockPosition.MutableBlockPosition a(Entity entity) {
            return this.c(entity.locX(), entity.locY(), entity.locZ());
        }

        public BlockPosition.MutableBlockPosition c(double d0, double d1, double d2) {
            return this.d(MathHelper.floor(d0), MathHelper.floor(d1), MathHelper.floor(d2));
        }

        public BlockPosition.MutableBlockPosition g(BaseBlockPosition baseblockposition) {
            return this.d(baseblockposition.getX(), baseblockposition.getY(), baseblockposition.getZ());
        }

        public BlockPosition.MutableBlockPosition g(long i) {
            return this.d(b(i), c(i), d(i));
        }

        public BlockPosition.MutableBlockPosition a(EnumAxisCycle enumaxiscycle, int i, int j, int k) {
            return this.d(enumaxiscycle.a(i, j, k, EnumDirection.EnumAxis.X), enumaxiscycle.a(i, j, k, EnumDirection.EnumAxis.Y), enumaxiscycle.a(i, j, k, EnumDirection.EnumAxis.Z));
        }

        public BlockPosition.MutableBlockPosition c(EnumDirection enumdirection) {
            return this.c(enumdirection, 1);
        }

        public BlockPosition.MutableBlockPosition c(EnumDirection enumdirection, int i) {
            return this.d(this.b + enumdirection.getAdjacentX() * i, this.c + enumdirection.getAdjacentY() * i, this.d + enumdirection.getAdjacentZ() * i);
        }

        public BlockPosition.MutableBlockPosition e(int i, int j, int k) {
            return this.d(this.b + i, this.c + j, this.d + k);
        }

        public void o(int i) {
            this.b = i;
        }

        public void p(int i) {
            this.c = i;
        }

        public void q(int i) {
            this.d = i;
        }

        @Override
        public BlockPosition immutableCopy() {
            return new BlockPosition(this);
        }
    }
}
