package net.milkev.milkevsoreminers.common;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name="milkevsoreminers")
public class ModConfig implements ConfigData {
    @Comment("Maximum amount of energy that the Advanced Sifter can hold. Default: 50000. Does not work at the moment.")
    int advancedSifterMaxEnergy = 50000;
}
