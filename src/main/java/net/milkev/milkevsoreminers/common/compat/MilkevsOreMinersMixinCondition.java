package net.milkev.milkevsoreminers.common.compat;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.loader.api.FabricLoader;
import net.milkev.milkevsoreminers.common.MilkevsOreMiners;
import net.milkev.milkevsoreminers.common.ModConfig;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class MilkevsOreMinersMixinCondition implements IMixinConfigPlugin {

    ModConfig config;

    @Override
    public void onLoad(String mixinPackage) {
        AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
        config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {

        //System.out.println(mixinClassName);
        final String id = "net.milkev."+ MilkevsOreMiners.MOD_ID + ".common.mixins.";
        return switch (mixinClassName) {
            case "a" ->
                false;
            default ->
                    true;
        };
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
}
