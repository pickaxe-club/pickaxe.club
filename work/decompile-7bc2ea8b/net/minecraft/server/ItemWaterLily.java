package net.minecraft.server;

public class ItemWaterLily extends ItemBlock {

    public ItemWaterLily(Block block, Item.Info item_info) {
        super(block, item_info);
    }

    @Override
    public EnumInteractionResult a(ItemActionContext itemactioncontext) {
        return EnumInteractionResult.PASS;
    }

    @Override
    public InteractionResultWrapper<ItemStack> a(World world, EntityHuman entityhuman, EnumHand enumhand) {
        MovingObjectPositionBlock movingobjectpositionblock = a(world, entityhuman, RayTrace.FluidCollisionOption.SOURCE_ONLY);
        MovingObjectPositionBlock movingobjectpositionblock1 = movingobjectpositionblock.a(movingobjectpositionblock.getBlockPosition().up());
        EnumInteractionResult enuminteractionresult = super.a(new ItemActionContext(entityhuman, enumhand, movingobjectpositionblock1));

        return new InteractionResultWrapper<>(enuminteractionresult, entityhuman.b(enumhand));
    }
}
