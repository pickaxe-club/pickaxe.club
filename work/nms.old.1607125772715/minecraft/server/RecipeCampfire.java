package net.minecraft.server;

// CraftBukkit start
import org.bukkit.craftbukkit.inventory.CraftCampfireRecipe;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.inventory.CraftRecipe;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.inventory.Recipe;
// CraftBukkit end

public class RecipeCampfire extends RecipeCooking {

    public RecipeCampfire(MinecraftKey minecraftkey, String s, RecipeItemStack recipeitemstack, ItemStack itemstack, float f, int i) {
        super(Recipes.CAMPFIRE_COOKING, minecraftkey, s, recipeitemstack, itemstack, f, i);
    }

    @Override
    public RecipeSerializer<?> getRecipeSerializer() {
        return RecipeSerializer.s;
    }

    // CraftBukkit start
    @Override
    public Recipe toBukkitRecipe() {
        CraftItemStack result = CraftItemStack.asCraftMirror(this.result);

        CraftCampfireRecipe recipe = new CraftCampfireRecipe(CraftNamespacedKey.fromMinecraft(this.key), result, CraftRecipe.toBukkit(this.ingredient), this.experience, this.cookingTime);
        recipe.setGroup(this.group);

        return recipe;
    }
    // CraftBukkit end
}
