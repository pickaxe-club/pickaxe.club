package net.minecraft.server;

public class ItemGoldenAppleEnchanted extends Item {

    public ItemGoldenAppleEnchanted(Item.Info item_info) {
        super(item_info);
    }

    @Override
    public boolean e(ItemStack itemstack) {
        return true;
    }
}
