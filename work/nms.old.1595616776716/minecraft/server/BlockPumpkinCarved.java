package net.minecraft.server;

import java.util.Iterator;
import java.util.function.Predicate;
import javax.annotation.Nullable;

// CraftBukkit start
import org.bukkit.craftbukkit.util.BlockStateListPopulator;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
// CraftBukkit end

public class BlockPumpkinCarved extends BlockFacingHorizontal {

    public static final BlockStateDirection a = BlockFacingHorizontal.FACING;
    @Nullable
    private ShapeDetector b;
    @Nullable
    private ShapeDetector c;
    @Nullable
    private ShapeDetector d;
    @Nullable
    private ShapeDetector e;
    private static final Predicate<IBlockData> f = (iblockdata) -> {
        return iblockdata != null && (iblockdata.getBlock() == Blocks.CARVED_PUMPKIN || iblockdata.getBlock() == Blocks.JACK_O_LANTERN);
    };

    protected BlockPumpkinCarved(Block.Info block_info) {
        super(block_info);
        this.p((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockPumpkinCarved.a, EnumDirection.NORTH));
    }

    @Override
    public void onPlace(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (iblockdata1.getBlock() != iblockdata.getBlock()) {
            this.a(world, blockposition);
        }
    }

    public boolean a(IWorldReader iworldreader, BlockPosition blockposition) {
        return this.c().a(iworldreader, blockposition) != null || this.e().a(iworldreader, blockposition) != null;
    }

    private void a(World world, BlockPosition blockposition) {
        ShapeDetector.ShapeDetectorCollection shapedetector_shapedetectorcollection = this.getSnowmanShape().a(world, blockposition);
        int i;
        Iterator iterator;
        EntityPlayer entityplayer;
        int j;

        BlockStateListPopulator blockList = new BlockStateListPopulator(world); // CraftBukkit - Use BlockStateListPopulator
        if (shapedetector_shapedetectorcollection != null) {
            for (i = 0; i < this.getSnowmanShape().b(); ++i) {
                ShapeDetectorBlock shapedetectorblock = shapedetector_shapedetectorcollection.a(0, i, 0);

                blockList.setTypeAndData(shapedetectorblock.getPosition(), Blocks.AIR.getBlockData(), 2); // CraftBukkit
                // world.triggerEffect(2001, shapedetectorblock.getPosition(), Block.getCombinedId(shapedetectorblock.a())); // CraftBukkit
            }

            EntitySnowman entitysnowman = (EntitySnowman) EntityTypes.SNOW_GOLEM.a(world);
            BlockPosition blockposition1 = shapedetector_shapedetectorcollection.a(0, 2, 0).getPosition();

            entitysnowman.setPositionRotation((double) blockposition1.getX() + 0.5D, (double) blockposition1.getY() + 0.05D, (double) blockposition1.getZ() + 0.5D, 0.0F, 0.0F);
            // CraftBukkit start
            if (!world.addEntity(entitysnowman, SpawnReason.BUILD_SNOWMAN)) {
                return;
            }
            for (BlockPosition pos : blockList.getBlocks()) {
                world.triggerEffect(2001, pos, Block.getCombinedId(world.getType(pos)));
            }
            blockList.updateList();
            // CraftBukkit end
            iterator = world.a(EntityPlayer.class, entitysnowman.getBoundingBox().g(5.0D)).iterator();

            while (iterator.hasNext()) {
                entityplayer = (EntityPlayer) iterator.next();
                CriterionTriggers.n.a(entityplayer, (Entity) entitysnowman);
            }

            for (j = 0; j < this.getSnowmanShape().b(); ++j) {
                ShapeDetectorBlock shapedetectorblock1 = shapedetector_shapedetectorcollection.a(0, j, 0);

                world.update(shapedetectorblock1.getPosition(), Blocks.AIR);
            }
        } else {
            shapedetector_shapedetectorcollection = this.getIronGolemShape().a(world, blockposition);
            if (shapedetector_shapedetectorcollection != null) {
                for (i = 0; i < this.getIronGolemShape().c(); ++i) {
                    for (int k = 0; k < this.getIronGolemShape().b(); ++k) {
                        ShapeDetectorBlock shapedetectorblock2 = shapedetector_shapedetectorcollection.a(i, k, 0);

                        blockList.setTypeAndData(shapedetectorblock2.getPosition(), Blocks.AIR.getBlockData(), 2); // CraftBukkit
                        // world.triggerEffect(2001, shapedetectorblock2.getPosition(), Block.getCombinedId(shapedetectorblock2.a())); // CraftBukkit
                    }
                }

                BlockPosition blockposition2 = shapedetector_shapedetectorcollection.a(1, 2, 0).getPosition();
                EntityIronGolem entityirongolem = (EntityIronGolem) EntityTypes.IRON_GOLEM.a(world);

                entityirongolem.setPlayerCreated(true);
                entityirongolem.setPositionRotation((double) blockposition2.getX() + 0.5D, (double) blockposition2.getY() + 0.05D, (double) blockposition2.getZ() + 0.5D, 0.0F, 0.0F);
                // CraftBukkit start
                if (!world.addEntity(entityirongolem, SpawnReason.BUILD_IRONGOLEM)) {
                    return;
                }
                for (BlockPosition pos : blockList.getBlocks()) {
                    world.triggerEffect(2001, pos, Block.getCombinedId(world.getType(pos)));
                }
                blockList.updateList();
                // CraftBukkit end
                iterator = world.a(EntityPlayer.class, entityirongolem.getBoundingBox().g(5.0D)).iterator();

                while (iterator.hasNext()) {
                    entityplayer = (EntityPlayer) iterator.next();
                    CriterionTriggers.n.a(entityplayer, (Entity) entityirongolem);
                }

                for (j = 0; j < this.getIronGolemShape().c(); ++j) {
                    for (int l = 0; l < this.getIronGolemShape().b(); ++l) {
                        ShapeDetectorBlock shapedetectorblock3 = shapedetector_shapedetectorcollection.a(j, l, 0);

                        world.update(shapedetectorblock3.getPosition(), Blocks.AIR);
                    }
                }
            }
        }

    }

    @Override
    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        return (IBlockData) this.getBlockData().set(BlockPumpkinCarved.a, blockactioncontext.f().opposite());
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockPumpkinCarved.a);
    }

    private ShapeDetector c() {
        if (this.b == null) {
            this.b = ShapeDetectorBuilder.a().a(" ", "#", "#").a('#', ShapeDetectorBlock.a(BlockStatePredicate.a(Blocks.SNOW_BLOCK))).b();
        }

        return this.b;
    }

    private ShapeDetector getSnowmanShape() {
        if (this.c == null) {
            this.c = ShapeDetectorBuilder.a().a("^", "#", "#").a('^', ShapeDetectorBlock.a(BlockPumpkinCarved.f)).a('#', ShapeDetectorBlock.a(BlockStatePredicate.a(Blocks.SNOW_BLOCK))).b();
        }

        return this.c;
    }

    private ShapeDetector e() {
        if (this.d == null) {
            this.d = ShapeDetectorBuilder.a().a("~ ~", "###", "~#~").a('#', ShapeDetectorBlock.a(BlockStatePredicate.a(Blocks.IRON_BLOCK))).a('~', ShapeDetectorBlock.a(MaterialPredicate.a(Material.AIR))).b();
        }

        return this.d;
    }

    private ShapeDetector getIronGolemShape() {
        if (this.e == null) {
            this.e = ShapeDetectorBuilder.a().a("~^~", "###", "~#~").a('^', ShapeDetectorBlock.a(BlockPumpkinCarved.f)).a('#', ShapeDetectorBlock.a(BlockStatePredicate.a(Blocks.IRON_BLOCK))).a('~', ShapeDetectorBlock.a(MaterialPredicate.a(Material.AIR))).b();
        }

        return this.e;
    }

    @Override
    public boolean a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EntityTypes<?> entitytypes) {
        return true;
    }
}
