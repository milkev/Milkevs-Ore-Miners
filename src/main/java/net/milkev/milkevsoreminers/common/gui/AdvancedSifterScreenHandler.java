package net.milkev.milkevsoreminers.common.gui;

import net.milkev.milkevsoreminers.common.MilkevsOreMiners;
import net.milkev.milkevsoreminers.common.blockEntities.AdvancedSifterBlockEntity;
import net.milkev.milkevsoreminers.common.util.BlockPosPayload;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;

public class AdvancedSifterScreenHandler extends ScreenHandler {
    
    private final AdvancedSifterBlockEntity blockEntity;
    private final ScreenHandlerContext context;
    
    //client
    public AdvancedSifterScreenHandler(int syncId, PlayerInventory playerInventory, BlockPosPayload payload) {
        this(syncId, playerInventory, (AdvancedSifterBlockEntity) playerInventory.player.getWorld().getBlockEntity(payload.blockPos()));
    }
    
    //Main/Server
    public AdvancedSifterScreenHandler(int syncId, PlayerInventory playerInventory, AdvancedSifterBlockEntity advancedSifterBlockEntity) {
        super(MilkevsOreMiners.ADVANCED_SIFTER_SCREEN_HANDLER, syncId);
        blockEntity = advancedSifterBlockEntity;
        context = ScreenHandlerContext.create(advancedSifterBlockEntity.getWorld(), advancedSifterBlockEntity.getPos());
        playerSlots(playerInventory);
        //Input Slot
        addSlot(new Slot(this.blockEntity.getInventory(), 0, 44, 36));
        //Output Slots
        for(int i = 0; i < 12; i++) {
            addSlot(new Slot(this.blockEntity.getInventory(), 1 + i, 98 + (i%4) * 18, 18 + ((int) Math.floor(((double)i) / 3.99)) * 18));
        }
    }
    
    private void playerSlots(PlayerInventory playerInventory) {
        //hotbar
        for(int i = 0; i < 9; i++) {
            addSlot(new Slot(playerInventory, i, 8 + i * 18, 146));
        }
        //main inventory
        for(int i = 0; i < 9; i++) {
            for(int y = 0; y < 3; y++) {
                addSlot(new Slot(playerInventory, 9 + i + y * 9, 8 + i * 18, 88 + y * 18));
            }
        }
    }

    @Override
    public ItemStack quickMove(PlayerEntity playerEntity, int i) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canUse(PlayerEntity playerEntity) {
        return canUse(context, playerEntity, MilkevsOreMiners.ADVANCED_SIFTER_BLOCK);
    }

    public AdvancedSifterBlockEntity getBlockEntity() {
        return blockEntity;
    }
}
