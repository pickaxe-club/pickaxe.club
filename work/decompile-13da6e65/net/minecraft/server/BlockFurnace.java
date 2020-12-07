package net.minecraft.server;

public abstract class BlockFurnace extends BlockTileEntity {

    public static final BlockStateDirection FACING = BlockFacingHorizontal.FACING;
    public static final BlockStateBoolean LIT = BlockProperties.r;

    protected BlockFurnace(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.j((IBlockData) ((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockFurnace.FACING, EnumDirection.NORTH)).set(BlockFurnace.LIT, false));
    }

    @Override
    public EnumInteractionResult interact(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        if (world.isClientSide) {
            return EnumInteractionResult.SUCCESS;
        } else {
            this.a(world, blockposition, entityhuman);
            return EnumInteractionResult.CONSUME;
        }
    }

    protected abstract void a(World world, BlockPosition blockposition, EntityHuman entityhuman);

    @Override
    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        return (IBlockData) this.getBlockData().set(BlockFurnace.FACING, blockactioncontext.f().opposite());
    }

    @Override
    public void postPlace(World world, BlockPosition blockposition, IBlockData iblockdata, EntityLiving entityliving, ItemStack itemstack) {
        if (itemstack.hasName()) {
            TileEntity tileentity = world.getTileEntity(blockposition);

            if (tileentity instanceof TileEntityFurnace) {
                ((TileEntityFurnace) tileentity).setCustomName(itemstack.getName());
            }
        }

    }

    @Override
    public void remove(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (!iblockdata.a(iblockdata1.getBlock())) {
            TileEntity tileentity = world.getTileEntity(blockposition);

            if (tileentity instanceof TileEntityFurnace) {
                InventoryUtils.dropInventory(world, blockposition, (TileEntityFurnace) tileentity);
                ((TileEntityFurnace) tileentity).a(world, Vec3D.a((BaseBlockPosition) blockposition));
                world.updateAdjacentComparators(blockposition, this);
            }

            super.remove(iblockdata, world, blockposition, iblockdata1, flag);
        }
    }

    @Override
    public boolean isComplexRedstone(IBlockData iblockdata) {
        return true;
    }

    @Override
    public int a(IBlockData iblockdata, World world, BlockPosition blockposition) {
        return Container.a(world.getTileEntity(blockposition));
    }

    @Override
    public EnumRenderType b(IBlockData iblockdata) {
        return EnumRenderType.MODEL;
    }

    @Override
    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return (IBlockData) iblockdata.set(BlockFurnace.FACING, enumblockrotation.a((EnumDirection) iblockdata.get(BlockFurnace.FACING)));
    }

    @Override
    public IBlockData a(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        return iblockdata.a(enumblockmirror.a((EnumDirection) iblockdata.get(BlockFurnace.FACING)));
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockFurnace.FACING, BlockFurnace.LIT);
    }
}
