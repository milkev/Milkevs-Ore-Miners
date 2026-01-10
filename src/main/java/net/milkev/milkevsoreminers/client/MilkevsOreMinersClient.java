package net.milkev.milkevsoreminers.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.milkev.milkevsoreminers.client.gui.AdvancedSifterScreen;
import net.milkev.milkevsoreminers.client.gui.BasicMiningRigScreen;
import net.milkev.milkevsoreminers.common.MilkevsOreMiners;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;

public class MilkevsOreMinersClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        HandledScreens.register(MilkevsOreMiners.ADVANCED_SIFTER_SCREEN_HANDLER, AdvancedSifterScreen::new);
        HandledScreens.register(MilkevsOreMiners.MINING_RIG.BASIC.SCREEN_HANDLER, BasicMiningRigScreen::new);

        BlockRenderLayerMap.INSTANCE.putBlock(MilkevsOreMiners.SIFTER_BLOCK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MilkevsOreMiners.MINING_RIG.BASIC.GLASS, RenderLayer.getTranslucent());
        //this will be needed for shaders in the future
        
    }
}
