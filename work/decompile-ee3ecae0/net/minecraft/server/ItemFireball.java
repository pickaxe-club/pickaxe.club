package net.minecraft.server;

public class ItemFireball extends Item {

    public ItemFireball(Item.Info item_info) {
        super(item_info);
    }

    @Override
    public EnumInteractionResult a(ItemActionContext itemactioncontext) {
        World world = itemactioncontext.getWorld();
        BlockPosition blockposition = itemactioncontext.getClickPosition();
        IBlockData iblockdata = world.getType(blockposition);
        boolean flag = false;

        if (iblockdata.getBlock() == Blocks.CAMPFIRE) {
            if (!(Boolean) iblockdata.get(BlockCampfire.b) && !(Boolean) iblockdata.get(BlockCampfire.d)) {
                this.a(world, blockposition);
                world.setTypeUpdate(blockposition, (IBlockData) iblockdata.set(BlockCampfire.b, true));
                flag = true;
            }
        } else {
            blockposition = blockposition.shift(itemactioncontext.getClickedFace());
            if (world.getType(blockposition).isAir()) {
                this.a(world, blockposition);
                world.setTypeUpdate(blockposition, ((BlockFire) Blocks.FIRE).a((IBlockAccess) world, blockposition));
                flag = true;
            }
        }

        if (flag) {
            itemactioncontext.getItemStack().subtract(1);
            return EnumInteractionResult.SUCCESS;
        } else {
            return EnumInteractionResult.FAIL;
        }
    }

    private void a(World world, BlockPosition blockposition) {
        world.playSound((EntityHuman) null, blockposition, SoundEffects.ITEM_FIRECHARGE_USE, SoundCategory.BLOCKS, 1.0F, (ItemFireball.i.nextFloat() - ItemFireball.i.nextFloat()) * 0.2F + 1.0F);
    }
}
