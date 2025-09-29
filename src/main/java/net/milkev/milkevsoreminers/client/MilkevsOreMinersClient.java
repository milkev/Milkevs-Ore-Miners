package net.milkev.milkevsoreminers.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.milkev.milkevsoreminers.common.MilkevsOreMiners;
import net.minecraft.client.render.RenderLayer;

public class MilkevsOreMinersClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        BlockRenderLayerMap.INSTANCE.putBlock(MilkevsOreMiners.SIFTER_BLOCK, RenderLayer.getCutout());
        //this will be needed for shaders in the future
        
    }
}
