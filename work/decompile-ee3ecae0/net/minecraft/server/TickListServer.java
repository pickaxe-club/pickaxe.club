package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public class TickListServer<T> implements TickList<T> {

    protected final Predicate<T> a;
    private final Function<T, MinecraftKey> b;
    private final Function<MinecraftKey, T> c;
    private final Set<NextTickListEntry<T>> nextTickListHash = Sets.newHashSet();
    private final TreeSet<NextTickListEntry<T>> nextTickList = Sets.newTreeSet(NextTickListEntry.a());
    private final WorldServer f;
    private final Queue<NextTickListEntry<T>> g = Queues.newArrayDeque();
    private final List<NextTickListEntry<T>> h = Lists.newArrayList();
    private final Consumer<NextTickListEntry<T>> i;

    public TickListServer(WorldServer worldserver, Predicate<T> predicate, Function<T, MinecraftKey> function, Function<MinecraftKey, T> function1, Consumer<NextTickListEntry<T>> consumer) {
        this.a = predicate;
        this.b = function;
        this.c = function1;
        this.f = worldserver;
        this.i = consumer;
    }

    public void b() {
        int i = this.nextTickList.size();

        if (i != this.nextTickListHash.size()) {
            throw new IllegalStateException("TickNextTick list out of synch");
        } else {
            if (i > 65536) {
                i = 65536;
            }

            ChunkProviderServer chunkproviderserver = this.f.getChunkProvider();
            Iterator<NextTickListEntry<T>> iterator = this.nextTickList.iterator();

            this.f.getMethodProfiler().enter("cleaning");

            NextTickListEntry nextticklistentry;

            while (i > 0 && iterator.hasNext()) {
                nextticklistentry = (NextTickListEntry) iterator.next();
                if (nextticklistentry.b > this.f.getTime()) {
                    break;
                }

                if (chunkproviderserver.a(nextticklistentry.a)) {
                    iterator.remove();
                    this.nextTickListHash.remove(nextticklistentry);
                    this.g.add(nextticklistentry);
                    --i;
                }
            }

            this.f.getMethodProfiler().exitEnter("ticking");

            while ((nextticklistentry = (NextTickListEntry) this.g.poll()) != null) {
                if (chunkproviderserver.a(nextticklistentry.a)) {
                    try {
                        this.h.add(nextticklistentry);
                        this.i.accept(nextticklistentry);
                    } catch (Throwable throwable) {
                        CrashReport crashreport = CrashReport.a(throwable, "Exception while ticking");
                        CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Block being ticked");

                        CrashReportSystemDetails.a(crashreportsystemdetails, nextticklistentry.a, (IBlockData) null);
                        throw new ReportedException(crashreport);
                    }
                } else {
                    this.a(nextticklistentry.a, nextticklistentry.b(), 0);
                }
            }

            this.f.getMethodProfiler().exit();
            this.h.clear();
            this.g.clear();
        }
    }

    @Override
    public boolean b(BlockPosition blockposition, T t0) {
        return this.g.contains(new NextTickListEntry<>(blockposition, t0));
    }

    @Override
    public void a(Stream<NextTickListEntry<T>> stream) {
        stream.forEach(this::a);
    }

    public List<NextTickListEntry<T>> a(ChunkCoordIntPair chunkcoordintpair, boolean flag, boolean flag1) {
        int i = (chunkcoordintpair.x << 4) - 2;
        int j = i + 16 + 2;
        int k = (chunkcoordintpair.z << 4) - 2;
        int l = k + 16 + 2;

        return this.a(new StructureBoundingBox(i, 0, k, j, 256, l), flag, flag1);
    }

    public List<NextTickListEntry<T>> a(StructureBoundingBox structureboundingbox, boolean flag, boolean flag1) {
        List<NextTickListEntry<T>> list = this.a((List) null, this.nextTickList, structureboundingbox, flag);

        if (flag && list != null) {
            this.nextTickListHash.removeAll(list);
        }

        list = this.a(list, this.g, structureboundingbox, flag);
        if (!flag1) {
            list = this.a(list, this.h, structureboundingbox, flag);
        }

        return list == null ? Collections.emptyList() : list;
    }

    @Nullable
    private List<NextTickListEntry<T>> a(@Nullable List<NextTickListEntry<T>> list, Collection<NextTickListEntry<T>> collection, StructureBoundingBox structureboundingbox, boolean flag) {
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            NextTickListEntry<T> nextticklistentry = (NextTickListEntry) iterator.next();
            BlockPosition blockposition = nextticklistentry.a;

            if (blockposition.getX() >= structureboundingbox.a && blockposition.getX() < structureboundingbox.d && blockposition.getZ() >= structureboundingbox.c && blockposition.getZ() < structureboundingbox.f) {
                if (flag) {
                    iterator.remove();
                }

                if (list == null) {
                    list = Lists.newArrayList();
                }

                ((List) list).add(nextticklistentry);
            }
        }

        return (List) list;
    }

    public void a(StructureBoundingBox structureboundingbox, BlockPosition blockposition) {
        List<NextTickListEntry<T>> list = this.a(structureboundingbox, false, false);
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            NextTickListEntry<T> nextticklistentry = (NextTickListEntry) iterator.next();

            if (structureboundingbox.b((BaseBlockPosition) nextticklistentry.a)) {
                BlockPosition blockposition1 = nextticklistentry.a.a((BaseBlockPosition) blockposition);
                T t0 = nextticklistentry.b();

                this.a(new NextTickListEntry<>(blockposition1, t0, nextticklistentry.b, nextticklistentry.c));
            }
        }

    }

    public NBTTagList a(ChunkCoordIntPair chunkcoordintpair) {
        List<NextTickListEntry<T>> list = this.a(chunkcoordintpair, false, true);

        return a(this.b, list, this.f.getTime());
    }

    public static <T> NBTTagList a(Function<T, MinecraftKey> function, Iterable<NextTickListEntry<T>> iterable, long i) {
        NBTTagList nbttaglist = new NBTTagList();
        Iterator iterator = iterable.iterator();

        while (iterator.hasNext()) {
            NextTickListEntry<T> nextticklistentry = (NextTickListEntry) iterator.next();
            NBTTagCompound nbttagcompound = new NBTTagCompound();

            nbttagcompound.setString("i", ((MinecraftKey) function.apply(nextticklistentry.b())).toString());
            nbttagcompound.setInt("x", nextticklistentry.a.getX());
            nbttagcompound.setInt("y", nextticklistentry.a.getY());
            nbttagcompound.setInt("z", nextticklistentry.a.getZ());
            nbttagcompound.setInt("t", (int) (nextticklistentry.b - i));
            nbttagcompound.setInt("p", nextticklistentry.c.a());
            nbttaglist.add(nbttagcompound);
        }

        return nbttaglist;
    }

    @Override
    public boolean a(BlockPosition blockposition, T t0) {
        return this.nextTickListHash.contains(new NextTickListEntry<>(blockposition, t0));
    }

    @Override
    public void a(BlockPosition blockposition, T t0, int i, TickListPriority ticklistpriority) {
        if (!this.a.test(t0)) {
            this.a(new NextTickListEntry<>(blockposition, t0, (long) i + this.f.getTime(), ticklistpriority));
        }

    }

    private void a(NextTickListEntry<T> nextticklistentry) {
        if (!this.nextTickListHash.contains(nextticklistentry)) {
            this.nextTickListHash.add(nextticklistentry);
            this.nextTickList.add(nextticklistentry);
        }

    }

    public int a() {
        return this.nextTickListHash.size();
    }
}
