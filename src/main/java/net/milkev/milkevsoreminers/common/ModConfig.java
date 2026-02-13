package net.milkev.milkevsoreminers.common;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name="milkevsoreminers")
public class ModConfig implements ConfigData {
    @Comment("How many ticks between checks to see if a valid structure is still valid. Set to 0 or lower to disable. May cause issues if disabled! Default: 20 (1 second)")
    int validStructureCheckTimer = 20;
    @Comment("Energy capacity of the advanced sifter. Default: ?")
    long advancedSifterPowerCapacity = 10000;
    @Comment("Energy capacity of the basic mining rig. Default: ?")
    long basicMiningRigPowerCapacity = 50000;
    @Comment("Energy capacity of the advanced mining rig. Default: ?")
    long advancedMiningRigPowerCapacity = 250000;
    @Comment("Energy capacity of the elite mining rig. Default: ?")
    long eliteMiningRigPowerCapacity = 1000000;
    @Comment("Energy capacity of the ultimate mining rig. Default: ?")
    long ultimateMiningRigPowerCapacity = 25000000;
}
