package net.milkev.milkevsoreminers.common;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name="milkevsoreminers")
public class ModConfig implements ConfigData {
    @Comment("Amount of energy used by the Advanced Sifter per tick. Default: ?")
    int advancedSifterPowerUse = 50;
    @Comment("Amount of energy used by the Tier 1 Mining Rig per tick. Default: ?")
    int tier1MiningRigPowerUse = 500;
    @Comment("Amount of energy used by the Tier 2 Mining Rig per tick. Default: ?")
    int tier2MiningRigPowerUse = 1500;
    @Comment("Amount of energy used by the Tier 3 Mining Rig per tick. Default: ?")
    int tier3MiningRigPowerUse = 3000;
    @Comment("Amount of energy used by the Tier 4 Mining Rig per tick. Default: ?")
    int tier4MiningRigPowerUse = 9000;
}
