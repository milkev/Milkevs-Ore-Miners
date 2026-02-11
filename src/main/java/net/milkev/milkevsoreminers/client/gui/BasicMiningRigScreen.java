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
        //draw power bar
        float percent = (float) blockEntity.energyStorage.getAmount() / blockEntity.energyStorage.getCapacity();
        int pixels = (int) (52 - Math.floor(percent * 52));
        context.drawTexture(TEXTURE, this.x + 14, this.y + 18 + pixels, 177, 32 + pixels, 16, 52 - pixels);
        //draw progress
        pixels = (int) (47 - Math.floor(blockEntity.getProgress() * 47));
        context.drawTexture(TEXTURE, this.x + 83, this.y + 24 + pixels, 176, 85 + pixels, 10, 48 - pixels);
    }
}
