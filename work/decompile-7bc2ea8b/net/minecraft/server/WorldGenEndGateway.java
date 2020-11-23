package net.minecraft.server;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.Random;

public class WorldGenEndGateway extends WorldGenerator<WorldGenEndGatewayConfiguration> {

    public WorldGenEndGateway(Codec<WorldGenEndGatewayConfiguration> codec) {
        super(codec);
    }

    public boolean a(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, Random random, BlockPosition blockposition, WorldGenEndGatewayConfiguration worldgenendgatewayconfiguration) {
        Iterator iterator = BlockPosition.a(blockposition.b(-1, -2, -1), blockposition.b(1, 2, 1)).iterator();

        while (iterator.hasNext()) {
            BlockPosition blockposition1 = (BlockPosition) iterator.next();
            boolean flag = blockposition1.getX() == blockposition.getX();
            boolean flag1 = blockposition1.getY() == blockposition.getY();
            boolean flag2 = blockposition1.getZ() == blockposition.getZ();
            boolean flag3 = Math.abs(blockposition1.getY() - blockposition.getY()) == 2;

            if (flag && flag1 && flag2) {
                BlockPosition blockposition2 = blockposition1.immutableCopy();

                this.a(generatoraccessseed, blockposition2, Blocks.END_GATEWAY.getBlockData());
                worldgenendgatewayconfiguration.b().ifPresent((blockposition3) -> {
                    TileEntity tileentity = generatoraccessseed.getTileEntity(blockposition2);

                    if (tileentity instanceof TileEntityEndGateway) {
                        TileEntityEndGateway tileentityendgateway = (TileEntityEndGateway) tileentity;

                        tileentityendgateway.a(blockposition3, worldgenendgatewayconfiguration.c());
                        tileentity.update();
                    }

                });
            } else if (flag1) {
                this.a(generatoraccessseed, blockposition1, Blocks.AIR.getBlockData());
            } else if (flag3 && flag && flag2) {
                this.a(generatoraccessseed, blockposition1, Blocks.BEDROCK.getBlockData());
            } else if ((flag || flag2) && !flag3) {
                this.a(generatoraccessseed, blockposition1, Blocks.BEDROCK.getBlockData());
            } else {
                this.a(generatoraccessseed, blockposition1, Blocks.AIR.getBlockData());
            }
        }

        return true;
    }
}
