package net.minecraft.server;

public class ItemLingeringPotion extends ItemPotionThrowable {

    public ItemLingeringPotion(Item.Info item_info) {
        super(item_info);
    }

    @Override
    public InteractionResultWrapper<ItemStack> a(World world, EntityHuman entityhuman, EnumHand enumhand) {
        world.playSound((EntityHuman) null, entityhuman.locX(), entityhuman.locY(), entityhuman.locZ(), SoundEffects.ENTITY_LINGERING_POTION_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (ItemLingeringPotion.RANDOM.nextFloat() * 0.4F + 0.8F));
        return super.a(world, entityhuman, enumhand);
    }
}
