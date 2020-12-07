package net.minecraft.server;

import java.util.List;
import javax.annotation.Nullable;

public class ContainerSmithing extends ContainerAnvilAbstract {

    private final World g;
    @Nullable
    private RecipeSmithing h;
    private final List<RecipeSmithing> i;

    public ContainerSmithing(int i, PlayerInventory playerinventory) {
        this(i, playerinventory, ContainerAccess.a);
    }

    public ContainerSmithing(int i, PlayerInventory playerinventory, ContainerAccess containeraccess) {
        super(Containers.SMITHING, i, playerinventory, containeraccess);
        this.g = playerinventory.player.world;
        this.i = this.g.getCraftingManager().a(Recipes.SMITHING);
    }

    @Override
    protected boolean a(IBlockData iblockdata) {
        return iblockdata.a(Blocks.SMITHING_TABLE);
    }

    @Override
    protected boolean b(EntityHuman entityhuman, boolean flag) {
        return this.h != null && this.h.a(this.repairInventory, this.g);
    }

    @Override
    protected ItemStack a(EntityHuman entityhuman, ItemStack itemstack) {
        itemstack.a(entityhuman.world, entityhuman, itemstack.getCount());
        this.resultInventory.b(entityhuman);
        this.d(0);
        this.d(1);
        this.containerAccess.a((world, blockposition) -> {
            world.triggerEffect(1044, blockposition, 0);
        });
        return itemstack;
    }

    private void d(int i) {
        ItemStack itemstack = this.repairInventory.getItem(i);

        itemstack.subtract(1);
        this.repairInventory.setItem(i, itemstack);
    }

    @Override
    public void e() {
        List<RecipeSmithing> list = this.g.getCraftingManager().b(Recipes.SMITHING, this.repairInventory, this.g);

        if (list.isEmpty()) {
            this.resultInventory.setItem(0, ItemStack.b);
        } else {
            this.h = (RecipeSmithing) list.get(0);
            ItemStack itemstack = this.h.a(this.repairInventory);

            this.resultInventory.a((IRecipe) this.h);
            this.resultInventory.setItem(0, itemstack);
        }

    }

    @Override
    protected boolean a(ItemStack itemstack) {
        return this.i.stream().anyMatch((recipesmithing) -> {
            return recipesmithing.a(itemstack);
        });
    }

    @Override
    public boolean a(ItemStack itemstack, Slot slot) {
        return slot.inventory != this.resultInventory && super.a(itemstack, slot);
    }
}
