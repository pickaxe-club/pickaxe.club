package net.minecraft.server;

public interface IRecipe<C extends IInventory> {

    boolean a(C c0, World world);

    ItemStack a(C c0);

    ItemStack getResult();

    default NonNullList<ItemStack> b(C c0) {
        NonNullList<ItemStack> nonnulllist = NonNullList.a(c0.getSize(), ItemStack.a);

        for (int i = 0; i < nonnulllist.size(); ++i) {
            Item item = c0.getItem(i).getItem();

            if (item.q()) {
                nonnulllist.set(i, new ItemStack(item.p()));
            }
        }

        return nonnulllist;
    }

    default NonNullList<RecipeItemStack> a() {
        return NonNullList.a();
    }

    default boolean isComplex() {
        return false;
    }

    MinecraftKey getKey();

    RecipeSerializer<?> getRecipeSerializer();

    Recipes<?> g();

    org.bukkit.inventory.Recipe toBukkitRecipe(); // CraftBukkit
}
