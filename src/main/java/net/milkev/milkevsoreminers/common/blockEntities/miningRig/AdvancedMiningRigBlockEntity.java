package net.milkev.milkevsoreminers.common.blockEntities.miningRig;

import net.milkev.milkevsoreminers.common.MilkevsOreMiners;
import net.milkev.milkevsoreminers.common.recipes.MilkevsSingleRecipeInput;
import net.milkev.milkevsoreminers.common.recipes.RecipeUtils;
import net.milkev.milkevsoreminers.common.recipes.miningRig.AdvancedMiningRigRecipe;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class AdvancedMiningRigBlockEntity extends MiningRigBaseBlockEntity {
    
    List<Item> output;
    float chance;
    float rolls;
    long powerCost;
    long basePowerConsumption;
    
    public AdvancedMiningRigBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(MilkevsOreMiners.MINING_RIG.ADVANCED.BLOCK_ENTITY, blockPos, blockState);
        this.energyStorage.setCapacity(MilkevsOreMiners.PowerCapacity.get(MilkevsOreMiners.MINING_RIG.ADVANCED.ID));
    }

    @Override
    public void cacheRecipe(World world) {
        Optional<RecipeEntry<AdvancedMiningRigRecipe>> matches = world.getRecipeManager().getFirstMatch(MilkevsOreMiners.MINING_RIG.ADVANCED.RECIPE_TYPE, new MilkevsSingleRecipeInput.Single(Items.CHEST.getDefaultStack()), world);
        if(matches.isPresent()) {
            AdvancedMiningRigRecipe entry = matches.get().value();
            output = RecipeUtils.generateItemList(entry.output(), world);
            chance = entry.chance();
            rolls = entry.rolls();
            powerCost = entry.powerCost();
            basePowerConsumption = entry.basePowerConsumption();
        }
    }

    @Override
    public void decacheRecipe() {
        this.output = List.of();
        this.chance = 0;
        this.rolls = 0;
        this.powerCost = 0;
        this.basePowerConsumption = 0;
    }

    @Override
    public List<Item> getRecipeOutput() {
        return this.output;
    }

    @Override
    public long getPowerCost() {
        return this.powerCost;
    }

    @Override
    public long getPowerUsageSpeed() {
        return this.basePowerConsumption;
    }

    @Override
    public float getRecipeChance() {
        return this.chance;
    }

    @Override
    public float getRecipeRolls() {
        return this.rolls;
    }

    @Override
    public List<Block> validItemIOBlocks() {
        return List.of(MilkevsOreMiners.MINING_RIG.ADVANCED.ITEM_STORAGE);
    }

    @Override
    public List<Block> validEnergyIOBlocks() {
        return List.of();
    }

    @Override
    public List<Block> validLaserBlocks() {
        return List.of(Blocks.GRAY_CONCRETE);
    }

    private static final Text displayName = MilkevsOreMiners.makeTranslation("container.advanced_sifter");
    
    @Override
    public Text getDisplayName() {
        return displayName;
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        //return new BasicMiningRigSceenHandler(syncId, playerInventory, this);
        return null;
    }

    @Override
    protected Block[][][][] getStructureMatrixList() {
        //TODO 
        //make actual structure matrix for advanced mining rig (currently is just copy of basic)
        Block[] wall = new Block[]{MilkevsOreMiners.MINING_RIG.ADVANCED.WALL}; //these blocks are generic wall blocks that make up the majority of the structure
        Block[] upgradeSlot = new Block[]{Blocks.ORANGE_CONCRETE}; //these are slots for upgrades. this tier has 2? slots
        Block[] controller = new Block[]{MilkevsOreMiners.MINING_RIG.ADVANCED.CONTROLLER}; //this is the controller block, aka this block
        Block[] io = validItemIOBlocks().toArray(new Block[0]); //these blocks are IO blocks, either storage or power.
        Block[] glass = new Block[]{MilkevsOreMiners.MINING_RIG.ADVANCED.GLASS}; //these blocks are the view windows
        Block[] beaminizer = validLaserBlocks().toArray(new Block[0]); //the block that "creates" the beam
        Block[] requiredEmpty = new Block[]{Blocks.STRUCTURE_VOID}; //these blocks MUST be air
        Block[] oOS = new Block[]{Blocks.AIR}; //we dont care what these blocks are
        return new Block[][][][]{
                {//y1
                        {wall, wall, wall, oOS},
                        {wall, requiredEmpty, wall, oOS},
                        {wall, wall, wall, oOS},
                        {wall, wall, wall, oOS}
                },
                {//y2
                        {wall, wall, wall, oOS},
                        {wall, requiredEmpty, wall, oOS},
                        {wall, wall, wall, oOS},
                        {io, controller, io, oOS}
                },
                {//y3
                        {oOS, wall, oOS, oOS},
                        {glass, requiredEmpty, glass, oOS},
                        {wall, wall, wall, oOS},
                        {wall, wall, wall, oOS}
                },
                {//y4
                        {oOS, wall, oOS, oOS},
                        {glass, requiredEmpty, glass, oOS},
                        {wall, wall, wall, oOS},
                        {oOS, wall, oOS, oOS}
                },
                {//y5
                        {oOS, wall, oOS, oOS},
                        {glass, requiredEmpty, glass, oOS},
                        {wall, wall, wall, oOS},
                        {oOS, wall, oOS, oOS}
                },
                {//y6
                        {oOS, wall, oOS, oOS},
                        {wall, beaminizer, wall, oOS},
                        {wall, wall, wall, oOS},
                        {oOS, wall, oOS, oOS}
                },
                {//y7
                        {oOS, wall, oOS, oOS},
                        {wall, wall, wall, oOS},
                        {wall, wall, wall, oOS},
                        {oOS, wall, oOS, oOS}
                },
                {//y8
                        {oOS, oOS, oOS, oOS},
                        {oOS, wall, oOS, oOS},
                        {oOS, wall, oOS, oOS},
                        {oOS, wall, oOS, oOS}
                }
        };
    }
}
