package net.minecraft.server;

import com.google.common.collect.Lists;
import java.util.List;

public class RecipeArmorDye extends IRecipeComplex {

    public RecipeArmorDye(MinecraftKey minecraftkey) {
        super(minecraftkey);
    }

    public boolean a(InventoryCrafting inventorycrafting, World world) {
        ItemStack itemstack = ItemStack.b;
        List<ItemStack> list = Lists.newArrayList();

        for (int i = 0; i < inventorycrafting.getSize(); ++i) {
            ItemStack itemstack1 = inventorycrafting.getItem(i);

            if (!itemstack1.isEmpty()) {
                if (itemstack1.getItem() instanceof IDyeable) {
                    if (!itemstack.isEmpty()) {
                        return false;
                    }

                    itemstack = itemstack1;
                } else {
                    if (!(itemstack1.getItem() instanceof ItemDye)) {
                        return false;
                    }

                    list.add(itemstack1);
                }
            }
        }

        return !itemstack.isEmpty() && !list.isEmpty();
    }

    public ItemStack a(InventoryCrafting inventorycrafting) {
        List<ItemDye> list = Lists.newArrayList();
        ItemStack itemstack = ItemStack.b;

        for (int i = 0; i < inventorycrafting.getSize(); ++i) {
            ItemStack itemstack1 = inventorycrafting.getItem(i);

            if (!itemstack1.isEmpty()) {
                Item item = itemstack1.getItem();

                if (item instanceof IDyeable) {
                    if (!itemstack.isEmpty()) {
                        return ItemStack.b;
                    }

                    itemstack = itemstack1.cloneItemStack();
                } else {
                    if (!(item instanceof ItemDye)) {
                        return ItemStack.b;
                    }

                    list.add((ItemDye) item);
                }
            }
        }

        if (!itemstack.isEmpty() && !list.isEmpty()) {
            return IDyeable.a(itemstack, list);
        } else {
            return ItemStack.b;
        }
    }

    @Override
    public RecipeSerializer<?> getRecipeSerializer() {
        return RecipeSerializer.c;
    }
}
