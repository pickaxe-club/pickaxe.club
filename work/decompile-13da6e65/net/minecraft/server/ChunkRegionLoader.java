package net.minecraft.server;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.shorts.ShortList;
import it.unimi.dsi.fastutil.shorts.ShortListIterator;
import java.util.Arrays;
import java.util.BitSet;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Function;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkRegionLoader {

    private static final Logger LOGGER = LogManager.getLogger();

    public static ProtoChunk loadChunk(WorldServer worldserver, DefinedStructureManager definedstructuremanager, VillagePlace villageplace, ChunkCoordIntPair chunkcoordintpair, NBTTagCompound nbttagcompound) {
        ChunkGenerator chunkgenerator = worldserver.getChunkProvider().getChunkGenerator();
        WorldChunkManager worldchunkmanager = chunkgenerator.getWorldChunkManager();
        NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("Level");
        ChunkCoordIntPair chunkcoordintpair1 = new ChunkCoordIntPair(nbttagcompound1.getInt("xPos"), nbttagcompound1.getInt("zPos"));

        if (!Objects.equals(chunkcoordintpair, chunkcoordintpair1)) {
            ChunkRegionLoader.LOGGER.error("Chunk file at {} is in the wrong location; relocating. (Expected {}, got {})", chunkcoordintpair, chunkcoordintpair, chunkcoordintpair1);
        }

        BiomeStorage biomestorage = new BiomeStorage(worldserver.r().b(IRegistry.ay), chunkcoordintpair, worldchunkmanager, nbttagcompound1.hasKeyOfType("Biomes", 11) ? nbttagcompound1.getIntArray("Biomes") : null);
        ChunkConverter chunkconverter = nbttagcompound1.hasKeyOfType("UpgradeData", 10) ? new ChunkConverter(nbttagcompound1.getCompound("UpgradeData")) : ChunkConverter.a;
        ProtoChunkTickList<Block> protochunkticklist = new ProtoChunkTickList<>((block) -> {
            return block == null || block.getBlockData().isAir();
        }, chunkcoordintpair, nbttagcompound1.getList("ToBeTicked", 9));
        ProtoChunkTickList<FluidType> protochunkticklist1 = new ProtoChunkTickList<>((fluidtype) -> {
            return fluidtype == null || fluidtype == FluidTypes.EMPTY;
        }, chunkcoordintpair, nbttagcompound1.getList("LiquidsToBeTicked", 9));
        boolean flag = nbttagcompound1.getBoolean("isLightOn");
        NBTTagList nbttaglist = nbttagcompound1.getList("Sections", 10);
        boolean flag1 = true;
        ChunkSection[] achunksection = new ChunkSection[16];
        boolean flag2 = worldserver.getDimensionManager().hasSkyLight();
        ChunkProviderServer chunkproviderserver = worldserver.getChunkProvider();
        LightEngine lightengine = chunkproviderserver.getLightEngine();

        if (flag) {
            lightengine.b(chunkcoordintpair, true);
        }

        for (int i = 0; i < nbttaglist.size(); ++i) {
            NBTTagCompound nbttagcompound2 = nbttaglist.getCompound(i);
            byte b0 = nbttagcompound2.getByte("Y");

            if (nbttagcompound2.hasKeyOfType("Palette", 9) && nbttagcompound2.hasKeyOfType("BlockStates", 12)) {
                ChunkSection chunksection = new ChunkSection(b0 << 4);

                chunksection.getBlocks().a(nbttagcompound2.getList("Palette", 10), nbttagcompound2.getLongArray("BlockStates"));
                chunksection.recalcBlockCounts();
                if (!chunksection.c()) {
                    achunksection[b0] = chunksection;
                }

                villageplace.a(chunkcoordintpair, chunksection);
            }

            if (flag) {
                if (nbttagcompound2.hasKeyOfType("BlockLight", 7)) {
                    lightengine.a(EnumSkyBlock.BLOCK, SectionPosition.a(chunkcoordintpair, b0), new NibbleArray(nbttagcompound2.getByteArray("BlockLight")), true);
                }

                if (flag2 && nbttagcompound2.hasKeyOfType("SkyLight", 7)) {
                    lightengine.a(EnumSkyBlock.SKY, SectionPosition.a(chunkcoordintpair, b0), new NibbleArray(nbttagcompound2.getByteArray("SkyLight")), true);
                }
            }
        }

        long j = nbttagcompound1.getLong("InhabitedTime");
        ChunkStatus.Type chunkstatus_type = a(nbttagcompound);
        Object object;

        if (chunkstatus_type == ChunkStatus.Type.LEVELCHUNK) {
            NBTTagList nbttaglist1;
            Function function;
            RegistryBlocks registryblocks;
            Object object1;

            if (nbttagcompound1.hasKeyOfType("TileTicks", 9)) {
                nbttaglist1 = nbttagcompound1.getList("TileTicks", 10);
                function = IRegistry.BLOCK::getKey;
                registryblocks = IRegistry.BLOCK;
                registryblocks.getClass();
                object1 = TickListChunk.a(nbttaglist1, function, registryblocks::get);
            } else {
                object1 = protochunkticklist;
            }

            Object object2;

            if (nbttagcompound1.hasKeyOfType("LiquidTicks", 9)) {
                nbttaglist1 = nbttagcompound1.getList("LiquidTicks", 10);
                function = IRegistry.FLUID::getKey;
                registryblocks = IRegistry.FLUID;
                registryblocks.getClass();
                object2 = TickListChunk.a(nbttaglist1, function, registryblocks::get);
            } else {
                object2 = protochunkticklist1;
            }

            object = new Chunk(worldserver.getMinecraftWorld(), chunkcoordintpair, biomestorage, chunkconverter, (TickList) object1, (TickList) object2, j, achunksection, (chunk) -> {
                loadEntities(nbttagcompound1, chunk);
            });
        } else {
            ProtoChunk protochunk = new ProtoChunk(chunkcoordintpair, chunkconverter, achunksection, protochunkticklist, protochunkticklist1);

            protochunk.a(biomestorage);
            object = protochunk;
            protochunk.setInhabitedTime(j);
            protochunk.a(ChunkStatus.a(nbttagcompound1.getString("Status")));
            if (protochunk.getChunkStatus().b(ChunkStatus.FEATURES)) {
                protochunk.a(lightengine);
            }

            if (!flag && protochunk.getChunkStatus().b(ChunkStatus.LIGHT)) {
                Iterator iterator = BlockPosition.b(chunkcoordintpair.d(), 0, chunkcoordintpair.e(), chunkcoordintpair.f(), 255, chunkcoordintpair.g()).iterator();

                while (iterator.hasNext()) {
                    BlockPosition blockposition = (BlockPosition) iterator.next();

                    if (((IChunkAccess) object).getType(blockposition).f() != 0) {
                        protochunk.k(blockposition);
                    }
                }
            }
        }

        ((IChunkAccess) object).b(flag);
        NBTTagCompound nbttagcompound3 = nbttagcompound1.getCompound("Heightmaps");
        EnumSet<HeightMap.Type> enumset = EnumSet.noneOf(HeightMap.Type.class);
        Iterator iterator1 = ((IChunkAccess) object).getChunkStatus().h().iterator();

        while (iterator1.hasNext()) {
            HeightMap.Type heightmap_type = (HeightMap.Type) iterator1.next();
            String s = heightmap_type.b();

            if (nbttagcompound3.hasKeyOfType(s, 12)) {
                ((IChunkAccess) object).a(heightmap_type, nbttagcompound3.getLongArray(s));
            } else {
                enumset.add(heightmap_type);
            }
        }

        HeightMap.a((IChunkAccess) object, enumset);
        NBTTagCompound nbttagcompound4 = nbttagcompound1.getCompound("Structures");

        ((IChunkAccess) object).a(a(definedstructuremanager, nbttagcompound4, worldserver.getSeed()));
        ((IChunkAccess) object).b(a(chunkcoordintpair, nbttagcompound4));
        if (nbttagcompound1.getBoolean("shouldSave")) {
            ((IChunkAccess) object).setNeedsSaving(true);
        }

        NBTTagList nbttaglist2 = nbttagcompound1.getList("PostProcessing", 9);

        NBTTagList nbttaglist3;
        int k;

        for (int l = 0; l < nbttaglist2.size(); ++l) {
            nbttaglist3 = nbttaglist2.b(l);

            for (k = 0; k < nbttaglist3.size(); ++k) {
                ((IChunkAccess) object).a(nbttaglist3.d(k), l);
            }
        }

        if (chunkstatus_type == ChunkStatus.Type.LEVELCHUNK) {
            return new ProtoChunkExtension((Chunk) object);
        } else {
            ProtoChunk protochunk1 = (ProtoChunk) object;

            nbttaglist3 = nbttagcompound1.getList("Entities", 10);

            for (k = 0; k < nbttaglist3.size(); ++k) {
                protochunk1.b(nbttaglist3.getCompound(k));
            }

            NBTTagList nbttaglist4 = nbttagcompound1.getList("TileEntities", 10);

            NBTTagCompound nbttagcompound5;

            for (int i1 = 0; i1 < nbttaglist4.size(); ++i1) {
                nbttagcompound5 = nbttaglist4.getCompound(i1);
                ((IChunkAccess) object).a(nbttagcompound5);
            }

            NBTTagList nbttaglist5 = nbttagcompound1.getList("Lights", 9);

            for (int j1 = 0; j1 < nbttaglist5.size(); ++j1) {
                NBTTagList nbttaglist6 = nbttaglist5.b(j1);

                for (int k1 = 0; k1 < nbttaglist6.size(); ++k1) {
                    protochunk1.b(nbttaglist6.d(k1), j1);
                }
            }

            nbttagcompound5 = nbttagcompound1.getCompound("CarvingMasks");
            Iterator iterator2 = nbttagcompound5.getKeys().iterator();

            while (iterator2.hasNext()) {
                String s1 = (String) iterator2.next();
                WorldGenStage.Features worldgenstage_features = WorldGenStage.Features.valueOf(s1);

                protochunk1.a(worldgenstage_features, BitSet.valueOf(nbttagcompound5.getByteArray(s1)));
            }

            return protochunk1;
        }
    }

    public static NBTTagCompound saveChunk(WorldServer worldserver, IChunkAccess ichunkaccess) {
        ChunkCoordIntPair chunkcoordintpair = ichunkaccess.getPos();
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        NBTTagCompound nbttagcompound1 = new NBTTagCompound();

        nbttagcompound.setInt("DataVersion", SharedConstants.getGameVersion().getWorldVersion());
        nbttagcompound.set("Level", nbttagcompound1);
        nbttagcompound1.setInt("xPos", chunkcoordintpair.x);
        nbttagcompound1.setInt("zPos", chunkcoordintpair.z);
        nbttagcompound1.setLong("LastUpdate", worldserver.getTime());
        nbttagcompound1.setLong("InhabitedTime", ichunkaccess.getInhabitedTime());
        nbttagcompound1.setString("Status", ichunkaccess.getChunkStatus().d());
        ChunkConverter chunkconverter = ichunkaccess.p();

        if (!chunkconverter.a()) {
            nbttagcompound1.set("UpgradeData", chunkconverter.b());
        }

        ChunkSection[] achunksection = ichunkaccess.getSections();
        NBTTagList nbttaglist = new NBTTagList();
        LightEngineThreaded lightenginethreaded = worldserver.getChunkProvider().getLightEngine();
        boolean flag = ichunkaccess.r();

        NBTTagCompound nbttagcompound2;

        for (int i = -1; i < 17; ++i) {
            ChunkSection chunksection = (ChunkSection) Arrays.stream(achunksection).filter((chunksection1) -> {
                return chunksection1 != null && chunksection1.getYPosition() >> 4 == i;
            }).findFirst().orElse(Chunk.a);
            NibbleArray nibblearray = lightenginethreaded.a(EnumSkyBlock.BLOCK).a(SectionPosition.a(chunkcoordintpair, i));
            NibbleArray nibblearray1 = lightenginethreaded.a(EnumSkyBlock.SKY).a(SectionPosition.a(chunkcoordintpair, i));

            if (chunksection != Chunk.a || nibblearray != null || nibblearray1 != null) {
                nbttagcompound2 = new NBTTagCompound();
                nbttagcompound2.setByte("Y", (byte) (i & 255));
                if (chunksection != Chunk.a) {
                    chunksection.getBlocks().a(nbttagcompound2, "Palette", "BlockStates");
                }

                if (nibblearray != null && !nibblearray.c()) {
                    nbttagcompound2.setByteArray("BlockLight", nibblearray.asBytes());
                }

                if (nibblearray1 != null && !nibblearray1.c()) {
                    nbttagcompound2.setByteArray("SkyLight", nibblearray1.asBytes());
                }

                nbttaglist.add(nbttagcompound2);
            }
        }

        nbttagcompound1.set("Sections", nbttaglist);
        if (flag) {
            nbttagcompound1.setBoolean("isLightOn", true);
        }

        BiomeStorage biomestorage = ichunkaccess.getBiomeIndex();

        if (biomestorage != null) {
            nbttagcompound1.setIntArray("Biomes", biomestorage.a());
        }

        NBTTagList nbttaglist1 = new NBTTagList();
        Iterator iterator = ichunkaccess.c().iterator();

        NBTTagCompound nbttagcompound3;

        while (iterator.hasNext()) {
            BlockPosition blockposition = (BlockPosition) iterator.next();

            nbttagcompound3 = ichunkaccess.j(blockposition);
            if (nbttagcompound3 != null) {
                nbttaglist1.add(nbttagcompound3);
            }
        }

        nbttagcompound1.set("TileEntities", nbttaglist1);
        NBTTagList nbttaglist2 = new NBTTagList();

        if (ichunkaccess.getChunkStatus().getType() == ChunkStatus.Type.LEVELCHUNK) {
            Chunk chunk = (Chunk) ichunkaccess;

            chunk.d(false);

            for (int j = 0; j < chunk.getEntitySlices().length; ++j) {
                Iterator iterator1 = chunk.getEntitySlices()[j].iterator();

                while (iterator1.hasNext()) {
                    Entity entity = (Entity) iterator1.next();
                    NBTTagCompound nbttagcompound4 = new NBTTagCompound();

                    if (entity.d(nbttagcompound4)) {
                        chunk.d(true);
                        nbttaglist2.add(nbttagcompound4);
                    }
                }
            }
        } else {
            ProtoChunk protochunk = (ProtoChunk) ichunkaccess;

            nbttaglist2.addAll(protochunk.y());
            nbttagcompound1.set("Lights", a(protochunk.w()));
            nbttagcompound3 = new NBTTagCompound();
            WorldGenStage.Features[] aworldgenstage_features = WorldGenStage.Features.values();
            int k = aworldgenstage_features.length;

            for (int l = 0; l < k; ++l) {
                WorldGenStage.Features worldgenstage_features = aworldgenstage_features[l];
                BitSet bitset = protochunk.a(worldgenstage_features);

                if (bitset != null) {
                    nbttagcompound3.setByteArray(worldgenstage_features.toString(), bitset.toByteArray());
                }
            }

            nbttagcompound1.set("CarvingMasks", nbttagcompound3);
        }

        nbttagcompound1.set("Entities", nbttaglist2);
        TickList<Block> ticklist = ichunkaccess.n();

        if (ticklist instanceof ProtoChunkTickList) {
            nbttagcompound1.set("ToBeTicked", ((ProtoChunkTickList) ticklist).b());
        } else if (ticklist instanceof TickListChunk) {
            nbttagcompound1.set("TileTicks", ((TickListChunk) ticklist).b());
        } else {
            nbttagcompound1.set("TileTicks", worldserver.getBlockTickList().a(chunkcoordintpair));
        }

        TickList<FluidType> ticklist1 = ichunkaccess.o();

        if (ticklist1 instanceof ProtoChunkTickList) {
            nbttagcompound1.set("LiquidsToBeTicked", ((ProtoChunkTickList) ticklist1).b());
        } else if (ticklist1 instanceof TickListChunk) {
            nbttagcompound1.set("LiquidTicks", ((TickListChunk) ticklist1).b());
        } else {
            nbttagcompound1.set("LiquidTicks", worldserver.getFluidTickList().a(chunkcoordintpair));
        }

        nbttagcompound1.set("PostProcessing", a(ichunkaccess.l()));
        nbttagcompound2 = new NBTTagCompound();
        Iterator iterator2 = ichunkaccess.f().iterator();

        while (iterator2.hasNext()) {
            Entry<HeightMap.Type, HeightMap> entry = (Entry) iterator2.next();

            if (ichunkaccess.getChunkStatus().h().contains(entry.getKey())) {
                nbttagcompound2.set(((HeightMap.Type) entry.getKey()).b(), new NBTTagLongArray(((HeightMap) entry.getValue()).a()));
            }
        }

        nbttagcompound1.set("Heightmaps", nbttagcompound2);
        nbttagcompound1.set("Structures", a(chunkcoordintpair, ichunkaccess.h(), ichunkaccess.v()));
        return nbttagcompound;
    }

    public static ChunkStatus.Type a(@Nullable NBTTagCompound nbttagcompound) {
        if (nbttagcompound != null) {
            ChunkStatus chunkstatus = ChunkStatus.a(nbttagcompound.getCompound("Level").getString("Status"));

            if (chunkstatus != null) {
                return chunkstatus.getType();
            }
        }

        return ChunkStatus.Type.PROTOCHUNK;
    }

    private static void loadEntities(NBTTagCompound nbttagcompound, Chunk chunk) {
        NBTTagList nbttaglist = nbttagcompound.getList("Entities", 10);
        World world = chunk.getWorld();

        for (int i = 0; i < nbttaglist.size(); ++i) {
            NBTTagCompound nbttagcompound1 = nbttaglist.getCompound(i);

            EntityTypes.a(nbttagcompound1, world, (entity) -> {
                chunk.a(entity);
                return entity;
            });
            chunk.d(true);
        }

        NBTTagList nbttaglist1 = nbttagcompound.getList("TileEntities", 10);

        for (int j = 0; j < nbttaglist1.size(); ++j) {
            NBTTagCompound nbttagcompound2 = nbttaglist1.getCompound(j);
            boolean flag = nbttagcompound2.getBoolean("keepPacked");

            if (flag) {
                chunk.a(nbttagcompound2);
            } else {
                BlockPosition blockposition = new BlockPosition(nbttagcompound2.getInt("x"), nbttagcompound2.getInt("y"), nbttagcompound2.getInt("z"));
                TileEntity tileentity = TileEntity.create(chunk.getType(blockposition), nbttagcompound2);

                if (tileentity != null) {
                    chunk.a(tileentity);
                }
            }
        }

    }

    private static NBTTagCompound a(ChunkCoordIntPair chunkcoordintpair, Map<StructureGenerator<?>, StructureStart<?>> map, Map<StructureGenerator<?>, LongSet> map1) {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        NBTTagCompound nbttagcompound1 = new NBTTagCompound();
        Iterator iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry<StructureGenerator<?>, StructureStart<?>> entry = (Entry) iterator.next();

            nbttagcompound1.set(((StructureGenerator) entry.getKey()).i(), ((StructureStart) entry.getValue()).a(chunkcoordintpair.x, chunkcoordintpair.z));
        }

        nbttagcompound.set("Starts", nbttagcompound1);
        NBTTagCompound nbttagcompound2 = new NBTTagCompound();
        Iterator iterator1 = map1.entrySet().iterator();

        while (iterator1.hasNext()) {
            Entry<StructureGenerator<?>, LongSet> entry1 = (Entry) iterator1.next();

            nbttagcompound2.set(((StructureGenerator) entry1.getKey()).i(), new NBTTagLongArray((LongSet) entry1.getValue()));
        }

        nbttagcompound.set("References", nbttagcompound2);
        return nbttagcompound;
    }

    private static Map<StructureGenerator<?>, StructureStart<?>> a(DefinedStructureManager definedstructuremanager, NBTTagCompound nbttagcompound, long i) {
        Map<StructureGenerator<?>, StructureStart<?>> map = Maps.newHashMap();
        NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("Starts");
        Iterator iterator = nbttagcompound1.getKeys().iterator();

        while (iterator.hasNext()) {
            String s = (String) iterator.next();
            String s1 = s.toLowerCase(Locale.ROOT);
            StructureGenerator<?> structuregenerator = (StructureGenerator) StructureGenerator.a.get(s1);

            if (structuregenerator == null) {
                ChunkRegionLoader.LOGGER.error("Unknown structure start: {}", s1);
            } else {
                StructureStart<?> structurestart = StructureGenerator.a(definedstructuremanager, nbttagcompound1.getCompound(s), i);

                if (structurestart != null) {
                    map.put(structuregenerator, structurestart);
                }
            }
        }

        return map;
    }

    private static Map<StructureGenerator<?>, LongSet> a(ChunkCoordIntPair chunkcoordintpair, NBTTagCompound nbttagcompound) {
        Map<StructureGenerator<?>, LongSet> map = Maps.newHashMap();
        NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("References");
        Iterator iterator = nbttagcompound1.getKeys().iterator();

        while (iterator.hasNext()) {
            String s = (String) iterator.next();

            map.put(StructureGenerator.a.get(s.toLowerCase(Locale.ROOT)), new LongOpenHashSet(Arrays.stream(nbttagcompound1.getLongArray(s)).filter((i) -> {
                ChunkCoordIntPair chunkcoordintpair1 = new ChunkCoordIntPair(i);

                if (chunkcoordintpair1.a(chunkcoordintpair) > 8) {
                    ChunkRegionLoader.LOGGER.warn("Found invalid structure reference [ {} @ {} ] for chunk {}.", s, chunkcoordintpair1, chunkcoordintpair);
                    return false;
                } else {
                    return true;
                }
            }).toArray()));
        }

        return map;
    }

    public static NBTTagList a(ShortList[] ashortlist) {
        NBTTagList nbttaglist = new NBTTagList();
        ShortList[] ashortlist1 = ashortlist;
        int i = ashortlist.length;

        for (int j = 0; j < i; ++j) {
            ShortList shortlist = ashortlist1[j];
            NBTTagList nbttaglist1 = new NBTTagList();

            if (shortlist != null) {
                ShortListIterator shortlistiterator = shortlist.iterator();

                while (shortlistiterator.hasNext()) {
                    Short oshort = (Short) shortlistiterator.next();

                    nbttaglist1.add(NBTTagShort.a(oshort));
                }
            }

            nbttaglist.add(nbttaglist1);
        }

        return nbttaglist;
    }
}
