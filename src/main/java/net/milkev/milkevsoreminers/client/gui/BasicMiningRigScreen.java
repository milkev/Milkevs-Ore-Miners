package net.milkev.milkevsoreminers.client.gui;

import net.milkev.milkevsoreminers.common.MilkevsOreMiners;
import net.milkev.milkevsoreminers.common.blockEntities.miningRig.BasicMiningRigBlockEntity;
import net.milkev.milkevsoreminers.common.gui.BasicMiningRigSceenHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class BasicMiningRigScreen extends HandledScreen<BasicMiningRigSceenHandler> {
    private static final Identifier TEXTURE = MilkevsOreMiners.id("textures/gui/mining_rig_basic.png");
    
    public BasicMiningRigScreen(BasicMiningRigSceenHandler screenHandler, PlayerInventory playerInventory, Text text) {
        super(screenHandler, playerInventory, text);
        this.backgroundHeight += 4;
        this.playerInventoryTitleY += 4;
    }
    
    @Override
    protected void init() {
        super.init();
        
    }

    @Override
    protected void drawBackground(DrawContext drawContext, float f, int i, int j) {
        drawContext.drawTexture(TEXTURE, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight);
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
        //draw power bar
        BasicMiningRigBlockEntity blockEntity = this.handler.getBlockEntity();
        /*float percent = (float) blockEntity.energyStorage.getAmount() / blockEntity.energyStorage.getCapacity();
        int pixels = 52 - Math.round(percent * 52);
        context.drawTexture(TEXTURE, this.x + 8, this.y + 18 + pixels, 177, 32 + pixels, 16, 52 - pixels);
        //draw progress
        pixels = 23 - Math.round(blockEntity.getProgress() * 23);
        context.drawTexture(TEXTURE, this.x + 66, this.y + 35, 176, 14, 23 - pixels, 16);*/
    }
}
