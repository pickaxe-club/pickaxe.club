package net.minecraft.server;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.WeakHashMap;

import org.bukkit.event.block.BlockRedstoneEvent; // CraftBukkit

public class BlockRedstoneTorch extends BlockTorch {

    public static final BlockStateBoolean LIT = BlockProperties.r;
    private static final Map<IBlockAccess, List<BlockRedstoneTorch.RedstoneUpdateInfo>> b = new WeakHashMap();

    protected BlockRedstoneTorch(Block.Info block_info) {
        super(block_info);
        this.p((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockRedstoneTorch.LIT, true));
    }

    @Override
    public int a(IWorldReader iworldreader) {
        return 2;
    }

    @Override
    public void onPlace(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        EnumDirection[] aenumdirection = EnumDirection.values();
        int i = aenumdirection.length;

        for (int j = 0; j < i; ++j) {
            EnumDirection enumdirection = aenumdirection[j];

            world.applyPhysics(blockposition.shift(enumdirection), this);
        }

    }

    @Override
    public void remove(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (!flag) {
            EnumDirection[] aenumdirection = EnumDirection.values();
            int i = aenumdirection.length;

            for (int j = 0; j < i; ++j) {
                EnumDirection enumdirection = aenumdirection[j];

                world.applyPhysics(blockposition.shift(enumdirection), this);
            }

        }
    }

    @Override
    public int a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return (Boolean) iblockdata.get(BlockRedstoneTorch.LIT) && EnumDirection.UP != enumdirection ? 15 : 0;
    }

    protected boolean a(World world, BlockPosition blockposition, IBlockData iblockdata) {
        return world.isBlockFacePowered(blockposition.down(), EnumDirection.DOWN);
    }

    @Override
    public void tick(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, Random random) {
        a(iblockdata, worldserver, blockposition, random, this.a((World) worldserver, blockposition, iblockdata));
    }

    public static void a(IBlockData iblockdata, World world, BlockPosition blockposition, Random random, boolean flag) {
        List list = (List) BlockRedstoneTorch.b.get(world);

        while (list != null && !list.isEmpty() && world.getTime() - ((BlockRedstoneTorch.RedstoneUpdateInfo) list.get(0)).b > 60L) {
            list.remove(0);
        }

        // CraftBukkit start
        org.bukkit.plugin.PluginManager manager = world.getServer().getPluginManager();
        org.bukkit.block.Block block = world.getWorld().getBlockAt(blockposition.getX(), blockposition.getY(), blockposition.getZ());
        int oldCurrent = ((Boolean) iblockdata.get(BlockRedstoneTorch.LIT)).booleanValue() ? 15 : 0;

        BlockRedstoneEvent event = new BlockRedstoneEvent(block, oldCurrent, oldCurrent);
        // CraftBukkit end
        if ((Boolean) iblockdata.get(BlockRedstoneTorch.LIT)) {
            if (flag) {
                // CraftBukkit start
                if (oldCurrent != 0) {
                    event.setNewCurrent(0);
                    manager.callEvent(event);
                    if (event.getNewCurrent() != 0) {
                        return;
                    }
                }
                // CraftBukkit end
                world.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockRedstoneTorch.LIT, false), 3);
                if (a(world, blockposition, true)) {
                    world.triggerEffect(1502, blockposition, 0);
                    world.getBlockTickList().a(blockposition, world.getType(blockposition).getBlock(), 160);
                }
            }
        } else if (!flag && !a(world, blockposition, false)) {
            // CraftBukkit start
            if (oldCurrent != 15) {
                event.setNewCurrent(15);
                manager.callEvent(event);
                if (event.getNewCurrent() != 15) {
                    return;
                }
            }
            // CraftBukkit end
            world.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockRedstoneTorch.LIT, true), 3);
        }

    }

    @Override
    public void doPhysics(IBlockData iblockdata, World world, BlockPosition blockposition, Block block, BlockPosition blockposition1, boolean flag) {
        if ((Boolean) iblockdata.get(BlockRedstoneTorch.LIT) == this.a(world, blockposition, iblockdata) && !world.getBlockTickList().b(blockposition, this)) {
            world.getBlockTickList().a(blockposition, this, this.a((IWorldReader) world));
        }

    }

    @Override
    public int b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return enumdirection == EnumDirection.DOWN ? iblockdata.b(iblockaccess, blockposition, enumdirection) : 0;
    }

    @Override
    public boolean isPowerSource(IBlockData iblockdata) {
        return true;
    }

    @Override
    public int a(IBlockData iblockdata) {
        return (Boolean) iblockdata.get(BlockRedstoneTorch.LIT) ? super.a(iblockdata) : 0;
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockRedstoneTorch.LIT);
    }

    private static boolean a(World world, BlockPosition blockposition, boolean flag) {
        List<BlockRedstoneTorch.RedstoneUpdateInfo> list = (List) BlockRedstoneTorch.b.computeIfAbsent(world, (iblockaccess) -> {
            return Lists.newArrayList();
        });

        if (flag) {
            list.add(new BlockRedstoneTorch.RedstoneUpdateInfo(blockposition.immutableCopy(), world.getTime()));
        }

        int i = 0;

        for (int j = 0; j < list.size(); ++j) {
            BlockRedstoneTorch.RedstoneUpdateInfo blockredstonetorch_redstoneupdateinfo = (BlockRedstoneTorch.RedstoneUpdateInfo) list.get(j);

            if (blockredstonetorch_redstoneupdateinfo.a.equals(blockposition)) {
                ++i;
                if (i >= 8) {
                    return true;
                }
            }
        }

        return false;
    }

    public static class RedstoneUpdateInfo {

        private final BlockPosition a;
        private final long b;

        public RedstoneUpdateInfo(BlockPosition blockposition, long i) {
            this.a = blockposition;
            this.b = i;
        }
    }
}
