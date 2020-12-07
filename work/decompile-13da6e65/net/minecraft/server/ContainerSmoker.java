package net.minecraft.server;

public class ContainerSmoker extends ContainerFurnace {

    public ContainerSmoker(int i, PlayerInventory playerinventory) {
        super(Containers.SMOKER, Recipes.SMOKING, RecipeBookType.SMOKER, i, playerinventory);
    }

    public ContainerSmoker(int i, PlayerInventory playerinventory, IInventory iinventory, IContainerProperties icontainerproperties) {
        super(Containers.SMOKER, Recipes.SMOKING, RecipeBookType.SMOKER, i, playerinventory, iinventory, icontainerproperties);
    }
}
