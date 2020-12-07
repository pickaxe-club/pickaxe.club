package net.minecraft.server;

public class BlockStoneButton extends BlockButtonAbstract {

    protected BlockStoneButton(BlockBase.Info blockbase_info) {
        super(false, blockbase_info);
    }

    @Override
    protected SoundEffect a(boolean flag) {
        return flag ? SoundEffects.BLOCK_STONE_BUTTON_CLICK_ON : SoundEffects.BLOCK_STONE_BUTTON_CLICK_OFF;
    }
}
