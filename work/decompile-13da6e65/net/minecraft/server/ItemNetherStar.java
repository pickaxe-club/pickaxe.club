package net.minecraft.server;

public class ItemNetherStar extends Item {

    public ItemNetherStar(Item.Info item_info) {
        super(item_info);
    }

    @Override
    public boolean e(ItemStack itemstack) {
        return true;
    }
}
