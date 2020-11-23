package net.minecraft.server;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class WorldGenFeatureTreeBeehive extends WorldGenFeatureTree {

    public static final Codec<WorldGenFeatureTreeBeehive> a = Codec.FLOAT.fieldOf("probability").xmap(WorldGenFeatureTreeBeehive::new, (worldgenfeaturetreebeehive) -> {
        return worldgenfeaturetreebeehive.b;
    }).codec();
    private final float b;

    public WorldGenFeatureTreeBeehive(float f) {
        this.b = f;
    }

    @Override
    protected WorldGenFeatureTrees<?> a() {
        return WorldGenFeatureTrees.d;
    }

    @Override
    public void a(GeneratorAccess generatoraccess, Random random, List<BlockPosition> list, List<BlockPosition> list1, Set<BlockPosition> set, StructureBoundingBox structureboundingbox) {
        if (random.nextFloat() < this.b) {
            EnumDirection enumdirection = BlockBeehive.a(random);
            int i = !list1.isEmpty() ? Math.max(((BlockPosition) list1.get(0)).getY() - 1, ((BlockPosition) list.get(0)).getY()) : Math.min(((BlockPosition) list.get(0)).getY() + 1 + random.nextInt(3), ((BlockPosition) list.get(list.size() - 1)).getY());
            List<BlockPosition> list2 = (List) list.stream().filter((blockposition) -> {
                return blockposition.getY() == i;
            }).collect(Collectors.toList());

            if (!list2.isEmpty()) {
                BlockPosition blockposition = (BlockPosition) list2.get(random.nextInt(list2.size()));
                BlockPosition blockposition1 = blockposition.shift(enumdirection);

                if (WorldGenerator.b(generatoraccess, blockposition1) && WorldGenerator.b(generatoraccess, blockposition1.shift(EnumDirection.SOUTH))) {
                    IBlockData iblockdata = (IBlockData) Blocks.BEE_NEST.getBlockData().set(BlockBeehive.a, EnumDirection.SOUTH);

                    this.a(generatoraccess, blockposition1, iblockdata, set, structureboundingbox);
                    TileEntity tileentity = generatoraccess.getTileEntity(blockposition1);

                    if (tileentity instanceof TileEntityBeehive) {
                        TileEntityBeehive tileentitybeehive = (TileEntityBeehive) tileentity;
                        int j = 2 + random.nextInt(2);

                        for (int k = 0; k < j; ++k) {
                            EntityBee entitybee = new EntityBee(EntityTypes.BEE, generatoraccess.getMinecraftWorld());

                            tileentitybeehive.a(entitybee, false, random.nextInt(599));
                        }
                    }

                }
            }
        }
    }
}
