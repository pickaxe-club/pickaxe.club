package net.minecraft.server;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Supplier;

public final class WorldDimension {

    public static final MapCodec<WorldDimension> a = RecordCodecBuilder.mapCodec((instance) -> {
        return instance.group(DimensionManager.k.fieldOf("type").forGetter(WorldDimension::a), ChunkGenerator.a.fieldOf("generator").forGetter(WorldDimension::c)).apply(instance, instance.stable(WorldDimension::new));
    });
    public static final ResourceKey<WorldDimension> OVERWORLD = ResourceKey.a(IRegistry.af, new MinecraftKey("overworld"));
    public static final ResourceKey<WorldDimension> THE_NETHER = ResourceKey.a(IRegistry.af, new MinecraftKey("the_nether"));
    public static final ResourceKey<WorldDimension> THE_END = ResourceKey.a(IRegistry.af, new MinecraftKey("the_end"));
    private static final LinkedHashSet<ResourceKey<WorldDimension>> e = Sets.newLinkedHashSet(ImmutableList.of(WorldDimension.OVERWORLD, WorldDimension.THE_NETHER, WorldDimension.THE_END));
    private final Supplier<DimensionManager> f;
    private final ChunkGenerator g;

    public WorldDimension(Supplier<DimensionManager> supplier, ChunkGenerator chunkgenerator) {
        this.f = supplier;
        this.g = chunkgenerator;
    }

    public Supplier<DimensionManager> a() {
        return this.f;
    }

    public DimensionManager b() {
        return (DimensionManager) this.f.get();
    }

    public ChunkGenerator c() {
        return this.g;
    }

    public static RegistryMaterials<WorldDimension> a(RegistryMaterials<WorldDimension> registrymaterials) {
        RegistryMaterials<WorldDimension> registrymaterials1 = new RegistryMaterials<>(IRegistry.af, Lifecycle.experimental());
        Iterator iterator = WorldDimension.e.iterator();

        while (iterator.hasNext()) {
            ResourceKey<WorldDimension> resourcekey = (ResourceKey) iterator.next();
            WorldDimension worlddimension = (WorldDimension) registrymaterials.a(resourcekey);

            if (worlddimension != null) {
                registrymaterials1.a(resourcekey, (Object) worlddimension);
                if (registrymaterials.c(resourcekey)) {
                    registrymaterials1.d(resourcekey);
                }
            }
        }

        iterator = registrymaterials.c().iterator();

        while (iterator.hasNext()) {
            Entry<ResourceKey<WorldDimension>, WorldDimension> entry = (Entry) iterator.next();
            ResourceKey<WorldDimension> resourcekey1 = (ResourceKey) entry.getKey();

            if (!WorldDimension.e.contains(resourcekey1)) {
                registrymaterials1.a(resourcekey1, entry.getValue());
                if (registrymaterials.c(resourcekey1)) {
                    registrymaterials1.d(resourcekey1);
                }
            }
        }

        return registrymaterials1;
    }

    public static boolean a(long i, RegistryMaterials<WorldDimension> registrymaterials) {
        List<Entry<ResourceKey<WorldDimension>, WorldDimension>> list = Lists.newArrayList(registrymaterials.c());

        if (list.size() != WorldDimension.e.size()) {
            return false;
        } else {
            Entry<ResourceKey<WorldDimension>, WorldDimension> entry = (Entry) list.get(0);
            Entry<ResourceKey<WorldDimension>, WorldDimension> entry1 = (Entry) list.get(1);
            Entry<ResourceKey<WorldDimension>, WorldDimension> entry2 = (Entry) list.get(2);

            if (entry.getKey() == WorldDimension.OVERWORLD && entry1.getKey() == WorldDimension.THE_NETHER && entry2.getKey() == WorldDimension.THE_END) {
                if (((WorldDimension) entry.getValue()).b() != DimensionManager.OVERWORLD_IMPL && ((WorldDimension) entry.getValue()).b() != DimensionManager.j) {
                    return false;
                } else if (((WorldDimension) entry1.getValue()).b() != DimensionManager.THE_NETHER_IMPL) {
                    return false;
                } else if (((WorldDimension) entry2.getValue()).b() != DimensionManager.THE_END_IMPL) {
                    return false;
                } else if (((WorldDimension) entry1.getValue()).c() instanceof ChunkGeneratorAbstract && ((WorldDimension) entry2.getValue()).c() instanceof ChunkGeneratorAbstract) {
                    ChunkGeneratorAbstract chunkgeneratorabstract = (ChunkGeneratorAbstract) ((WorldDimension) entry1.getValue()).c();
                    ChunkGeneratorAbstract chunkgeneratorabstract1 = (ChunkGeneratorAbstract) ((WorldDimension) entry2.getValue()).c();

                    if (!chunkgeneratorabstract.a(i, GeneratorSettingBase.a.d)) {
                        return false;
                    } else if (!chunkgeneratorabstract1.a(i, GeneratorSettingBase.a.e)) {
                        return false;
                    } else if (!(chunkgeneratorabstract.getWorldChunkManager() instanceof WorldChunkManagerMultiNoise)) {
                        return false;
                    } else {
                        WorldChunkManagerMultiNoise worldchunkmanagermultinoise = (WorldChunkManagerMultiNoise) chunkgeneratorabstract.getWorldChunkManager();

                        if (!worldchunkmanagermultinoise.b(i)) {
                            return false;
                        } else if (!(chunkgeneratorabstract1.getWorldChunkManager() instanceof WorldChunkManagerTheEnd)) {
                            return false;
                        } else {
                            WorldChunkManagerTheEnd worldchunkmanagertheend = (WorldChunkManagerTheEnd) chunkgeneratorabstract1.getWorldChunkManager();

                            return worldchunkmanagertheend.b(i);
                        }
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
    }
}
