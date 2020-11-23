package net.minecraft.server;

// CraftBukkit start
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.block.BlockDispenseEvent;
// CraftBukkit end

public class DispenseBehaviorBoat extends DispenseBehaviorItem {

    private final DispenseBehaviorItem b = new DispenseBehaviorItem();
    private final EntityBoat.EnumBoatType c;

    public DispenseBehaviorBoat(EntityBoat.EnumBoatType entityboat_enumboattype) {
        this.c = entityboat_enumboattype;
    }

    @Override
    public ItemStack a(ISourceBlock isourceblock, ItemStack itemstack) {
        EnumDirection enumdirection = (EnumDirection) isourceblock.getBlockData().get(BlockDispenser.FACING);
        World world = isourceblock.getWorld();
        double d0 = isourceblock.getX() + (double) ((float) enumdirection.getAdjacentX() * 1.125F);
        double d1 = isourceblock.getY() + (double) ((float) enumdirection.getAdjacentY() * 1.125F);
        double d2 = isourceblock.getZ() + (double) ((float) enumdirection.getAdjacentZ() * 1.125F);
        BlockPosition blockposition = isourceblock.getBlockPosition().shift(enumdirection);
        double d3;

        if (world.getFluid(blockposition).a(TagsFluid.WATER)) {
            d3 = 1.0D;
        } else {
            if (!world.getType(blockposition).isAir() || !world.getFluid(blockposition.down()).a(TagsFluid.WATER)) {
                return this.b.dispense(isourceblock, itemstack);
            }

            d3 = 0.0D;
        }

        // EntityBoat entityboat = new EntityBoat(world, d0, d1 + d3, d2);
        // CraftBukkit start
        ItemStack itemstack1 = itemstack.cloneAndSubtract(1);
        org.bukkit.block.Block block = world.getWorld().getBlockAt(isourceblock.getBlockPosition().getX(), isourceblock.getBlockPosition().getY(), isourceblock.getBlockPosition().getZ());
        CraftItemStack craftItem = CraftItemStack.asCraftMirror(itemstack1);

        BlockDispenseEvent event = new BlockDispenseEvent(block, craftItem.clone(), new org.bukkit.util.Vector(d0, d1 + d3, d2));
        if (!BlockDispenser.eventFired) {
            world.getServer().getPluginManager().callEvent(event);
        }

        if (event.isCancelled()) {
            itemstack.add(1);
            return itemstack;
        }

        if (!event.getItem().equals(craftItem)) {
            itemstack.add(1);
            // Chain to handler for new item
            ItemStack eventStack = CraftItemStack.asNMSCopy(event.getItem());
            IDispenseBehavior idispensebehavior = (IDispenseBehavior) BlockDispenser.REGISTRY.get(eventStack.getItem());
            if (idispensebehavior != IDispenseBehavior.NONE && idispensebehavior != this) {
                idispensebehavior.dispense(isourceblock, eventStack);
                return itemstack;
            }
        }

        EntityBoat entityboat = new EntityBoat(world, event.getVelocity().getX(), event.getVelocity().getY(), event.getVelocity().getZ());
        // CraftBukkit end

        entityboat.setType(this.c);
        entityboat.yaw = enumdirection.n();
        if (!world.addEntity(entityboat)) itemstack.add(1); // CraftBukkit
        // itemstack.subtract(1); // CraftBukkit - handled during event processing
        return itemstack;
    }

    @Override
    protected void a(ISourceBlock isourceblock) {
        isourceblock.getWorld().triggerEffect(1000, isourceblock.getBlockPosition(), 0);
    }
}
