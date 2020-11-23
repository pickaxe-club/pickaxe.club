package net.minecraft.server;

public class ItemEgg extends Item {

    public ItemEgg(Item.Info item_info) {
        super(item_info);
    }

    @Override
    public InteractionResultWrapper<ItemStack> a(World world, EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);

        // world.playSound((EntityHuman) null, entityhuman.locX(), entityhuman.locY(), entityhuman.locZ(), SoundEffects.ENTITY_EGG_THROW, SoundCategory.PLAYERS, 0.5F, 0.4F / (ItemEgg.i.nextFloat() * 0.4F + 0.8F)); // CraftBukkit - moved down
        if (!world.isClientSide) {
            EntityEgg entityegg = new EntityEgg(world, entityhuman);

            entityegg.setItem(itemstack);
            entityegg.a(entityhuman, entityhuman.pitch, entityhuman.yaw, 0.0F, 1.5F, 1.0F);
            // CraftBukkit start
            if (!world.addEntity(entityegg)) {
                if (entityhuman instanceof EntityPlayer) {
                    ((EntityPlayer) entityhuman).getBukkitEntity().updateInventory();
                }
                return InteractionResultWrapper.fail(itemstack);
            }
            // CraftBukkit end
        }
        world.playSound((EntityHuman) null, entityhuman.locX(), entityhuman.locY(), entityhuman.locZ(), SoundEffects.ENTITY_EGG_THROW, SoundCategory.PLAYERS, 0.5F, 0.4F / (ItemEgg.i.nextFloat() * 0.4F + 0.8F)); // CraftBukkit - from above

        entityhuman.b(StatisticList.ITEM_USED.b(this));
        if (!entityhuman.abilities.canInstantlyBuild) {
            itemstack.subtract(1);
        }

        return InteractionResultWrapper.success(itemstack);
    }
}
