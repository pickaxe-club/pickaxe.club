package net.minecraft.server;

import javax.annotation.Nullable;

public abstract class BlockBannerAbstract extends BlockTileEntity {

    private final EnumColor a;

    protected BlockBannerAbstract(EnumColor enumcolor, BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.a = enumcolor;
    }

    @Override
    public boolean ak_() {
        return true;
    }

    @Override
    public TileEntity createTile(IBlockAccess iblockaccess) {
        return new TileEntityBanner(this.a);
    }

    @Override
    public void postPlace(World world, BlockPosition blockposition, IBlockData iblockdata, @Nullable EntityLiving entityliving, ItemStack itemstack) {
        if (itemstack.hasName()) {
            TileEntity tileentity = world.getTileEntity(blockposition);

            if (tileentity instanceof TileEntityBanner) {
                ((TileEntityBanner) tileentity).a(itemstack.getName());
            }
        }

    }

    public EnumColor getColor() {
        return this.a;
    }
}
