package net.minecraft.server;

import java.util.Iterator;
import javax.annotation.Nullable;

public class InventoryCraftResult implements IInventory, RecipeHolder {

    private final NonNullList<ItemStack> items;
    @Nullable
    private IRecipe<?> b;

    public InventoryCraftResult() {
        this.items = NonNullList.a(1, ItemStack.b);
    }

    @Override
    public int getSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        Iterator iterator = this.items.iterator();

        ItemStack itemstack;

        do {
            if (!iterator.hasNext()) {
                return true;
            }

            itemstack = (ItemStack) iterator.next();
        } while (itemstack.isEmpty());

        return false;
    }

    @Override
    public ItemStack getItem(int i) {
        return (ItemStack) this.items.get(0);
    }

    @Override
    public ItemStack splitStack(int i, int j) {
        return ContainerUtil.a(this.items, 0);
    }

    @Override
    public ItemStack splitWithoutUpdate(int i) {
        return ContainerUtil.a(this.items, 0);
    }

    @Override
    public void setItem(int i, ItemStack itemstack) {
        this.items.set(0, itemstack);
    }

    @Override
    public void update() {}

    @Override
    public boolean a(EntityHuman entityhuman) {
        return true;
    }

    @Override
    public void clear() {
        this.items.clear();
    }

    @Override
    public void a(@Nullable IRecipe<?> irecipe) {
        this.b = irecipe;
    }

    @Nullable
    @Override
    public IRecipe<?> ak_() {
        return this.b;
    }
}
