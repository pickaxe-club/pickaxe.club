package net.minecraft.server;

import java.util.Iterator;
import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;

public class MobSpawnerTrader {

    private final Random a = new Random();
    private final WorldServer b;
    private int c;
    private int d;
    private int e;

    public MobSpawnerTrader(WorldServer worldserver) {
        this.b = worldserver;
        this.c = 1200;
        WorldData worlddata = worldserver.getWorldData();

        this.d = worlddata.Q();
        this.e = worlddata.R();
        if (this.d == 0 && this.e == 0) {
            this.d = 24000;
            worlddata.j(this.d);
            this.e = 25;
            worlddata.k(this.e);
        }

    }

    public void a() {
        if (this.b.getGameRules().getBoolean(GameRules.DO_TRADER_SPAWNING)) {
            if (--this.c <= 0) {
                this.c = 1200;
                WorldData worlddata = this.b.getWorldData();

                this.d -= 1200;
                worlddata.j(this.d);
                if (this.d <= 0) {
                    this.d = 24000;
                    if (this.b.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING)) {
                        int i = this.e;

                        this.e = MathHelper.clamp(this.e + 25, 25, 75);
                        worlddata.k(this.e);
                        if (this.a.nextInt(100) <= i) {
                            if (this.b()) {
                                this.e = 25;
                            }

                        }
                    }
                }
            }
        }
    }

    private boolean b() {
        EntityPlayer entityplayer = this.b.k();

        if (entityplayer == null) {
            return true;
        } else if (this.a.nextInt(10) != 0) {
            return false;
        } else {
            BlockPosition blockposition = entityplayer.getChunkCoordinates();
            boolean flag = true;
            VillagePlace villageplace = this.b.B();
            Optional<BlockPosition> optional = villageplace.b(VillagePlaceType.r.c(), (blockposition1) -> {
                return true;
            }, blockposition, 48, VillagePlace.Occupancy.ANY);
            BlockPosition blockposition1 = (BlockPosition) optional.orElse(blockposition);
            BlockPosition blockposition2 = this.a(blockposition1, 48);

            if (blockposition2 != null && this.a(blockposition2)) {
                if (this.b.getBiome(blockposition2) == Biomes.THE_VOID) {
                    return false;
                }

                EntityVillagerTrader entityvillagertrader = (EntityVillagerTrader) EntityTypes.WANDERING_TRADER.spawnCreature(this.b, (NBTTagCompound) null, (IChatBaseComponent) null, (EntityHuman) null, blockposition2, EnumMobSpawn.EVENT, false, false, org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.NATURAL); // CraftBukkit

                if (entityvillagertrader != null) {
                    for (int i = 0; i < 2; ++i) {
                        this.a(entityvillagertrader, 4);
                    }

                    this.b.getWorldData().a(entityvillagertrader.getUniqueID());
                    entityvillagertrader.u(48000);
                    entityvillagertrader.g(blockposition1);
                    entityvillagertrader.a(blockposition1, 16);
                    return true;
                }
            }

            return false;
        }
    }

    private void a(EntityVillagerTrader entityvillagertrader, int i) {
        BlockPosition blockposition = this.a(new BlockPosition(entityvillagertrader), i);

        if (blockposition != null) {
            EntityLlamaTrader entityllamatrader = (EntityLlamaTrader) EntityTypes.TRADER_LLAMA.spawnCreature(this.b, (NBTTagCompound) null, (IChatBaseComponent) null, (EntityHuman) null, blockposition, EnumMobSpawn.EVENT, false, false, org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.NATURAL); // CraftBukkit

            if (entityllamatrader != null) {
                entityllamatrader.setLeashHolder(entityvillagertrader, true);
            }
        }
    }

    @Nullable
    private BlockPosition a(BlockPosition blockposition, int i) {
        BlockPosition blockposition1 = null;

        for (int j = 0; j < 10; ++j) {
            int k = blockposition.getX() + this.a.nextInt(i * 2) - i;
            int l = blockposition.getZ() + this.a.nextInt(i * 2) - i;
            int i1 = this.b.a(HeightMap.Type.WORLD_SURFACE, k, l);
            BlockPosition blockposition2 = new BlockPosition(k, i1, l);

            if (SpawnerCreature.a(EntityPositionTypes.Surface.ON_GROUND, (IWorldReader) this.b, blockposition2, EntityTypes.WANDERING_TRADER)) {
                blockposition1 = blockposition2;
                break;
            }
        }

        return blockposition1;
    }

    private boolean a(BlockPosition blockposition) {
        Iterator iterator = BlockPosition.a(blockposition, blockposition.b(1, 2, 1)).iterator();

        BlockPosition blockposition1;

        do {
            if (!iterator.hasNext()) {
                return true;
            }

            blockposition1 = (BlockPosition) iterator.next();
        } while (this.b.getType(blockposition1).getCollisionShape(this.b, blockposition1).isEmpty());

        return false;
    }
}
