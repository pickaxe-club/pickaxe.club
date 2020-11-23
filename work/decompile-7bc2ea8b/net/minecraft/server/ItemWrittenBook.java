package net.minecraft.server;

import javax.annotation.Nullable;

public class ItemWrittenBook extends Item {

    public ItemWrittenBook(Item.Info item_info) {
        super(item_info);
    }

    public static boolean a(@Nullable NBTTagCompound nbttagcompound) {
        if (!ItemBookAndQuill.a(nbttagcompound)) {
            return false;
        } else if (!nbttagcompound.hasKeyOfType("title", 8)) {
            return false;
        } else {
            String s = nbttagcompound.getString("title");

            return s.length() > 32 ? false : nbttagcompound.hasKeyOfType("author", 8);
        }
    }

    public static int d(ItemStack itemstack) {
        return itemstack.getTag().getInt("generation");
    }

    public static int g(ItemStack itemstack) {
        NBTTagCompound nbttagcompound = itemstack.getTag();

        return nbttagcompound != null ? nbttagcompound.getList("pages", 8).size() : 0;
    }

    @Override
    public IChatBaseComponent h(ItemStack itemstack) {
        if (itemstack.hasTag()) {
            NBTTagCompound nbttagcompound = itemstack.getTag();
            String s = nbttagcompound.getString("title");

            if (!UtilColor.b(s)) {
                return new ChatComponentText(s);
            }
        }

        return super.h(itemstack);
    }

    @Override
    public EnumInteractionResult a(ItemActionContext itemactioncontext) {
        World world = itemactioncontext.getWorld();
        BlockPosition blockposition = itemactioncontext.getClickPosition();
        IBlockData iblockdata = world.getType(blockposition);

        return iblockdata.a(Blocks.LECTERN) ? (BlockLectern.a(world, blockposition, iblockdata, itemactioncontext.getItemStack()) ? EnumInteractionResult.a(world.isClientSide) : EnumInteractionResult.PASS) : EnumInteractionResult.PASS;
    }

    @Override
    public InteractionResultWrapper<ItemStack> a(World world, EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);

        entityhuman.openBook(itemstack, enumhand);
        entityhuman.b(StatisticList.ITEM_USED.b(this));
        return InteractionResultWrapper.a(itemstack, world.s_());
    }

    public static boolean a(ItemStack itemstack, @Nullable CommandListenerWrapper commandlistenerwrapper, @Nullable EntityHuman entityhuman) {
        NBTTagCompound nbttagcompound = itemstack.getTag();

        if (nbttagcompound != null && !nbttagcompound.getBoolean("resolved")) {
            nbttagcompound.setBoolean("resolved", true);
            if (!a(nbttagcompound)) {
                return false;
            } else {
                NBTTagList nbttaglist = nbttagcompound.getList("pages", 8);

                for (int i = 0; i < nbttaglist.size(); ++i) {
                    String s = nbttaglist.getString(i);

                    Object object;

                    try {
                        IChatMutableComponent ichatmutablecomponent = IChatBaseComponent.ChatSerializer.b(s);

                        object = ChatComponentUtils.filterForDisplay(commandlistenerwrapper, ichatmutablecomponent, entityhuman, 0);
                    } catch (Exception exception) {
                        object = new ChatComponentText(s);
                    }

                    nbttaglist.set(i, (NBTBase) NBTTagString.a(IChatBaseComponent.ChatSerializer.a((IChatBaseComponent) object)));
                }

                nbttagcompound.set("pages", nbttaglist);
                return true;
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean e(ItemStack itemstack) {
        return true;
    }
}
