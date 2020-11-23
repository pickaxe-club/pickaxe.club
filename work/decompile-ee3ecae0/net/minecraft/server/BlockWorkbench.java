package net.minecraft.server;

public class BlockWorkbench extends Block {

    private static final IChatBaseComponent a = new ChatMessage("container.crafting", new Object[0]);

    protected BlockWorkbench(Block.Info block_info) {
        super(block_info);
    }

    @Override
    public EnumInteractionResult interact(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        if (world.isClientSide) {
            return EnumInteractionResult.SUCCESS;
        } else {
            entityhuman.openContainer(iblockdata.b(world, blockposition));
            entityhuman.a(StatisticList.INTERACT_WITH_CRAFTING_TABLE);
            return EnumInteractionResult.SUCCESS;
        }
    }

    @Override
    public ITileInventory getInventory(IBlockData iblockdata, World world, BlockPosition blockposition) {
        return new TileInventory((i, playerinventory, entityhuman) -> {
            return new ContainerWorkbench(i, playerinventory, ContainerAccess.at(world, blockposition));
        }, BlockWorkbench.a);
    }
}
