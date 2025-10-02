package net.milkev.milkevsoreminers.common.blockEntities.miningRig;

import net.milkev.milkevsoreminers.common.MilkevsOreMiners;
import net.milkev.milkevsoreminers.common.recipes.MilkevsSingleRecipeInput;
import net.milkev.milkevsoreminers.common.recipes.RecipeUtils;
import net.milkev.milkevsoreminers.common.recipes.miningRig.BasicMiningRigRecipe;
import net.milkev.milkevsoreminers.common.util.MilkevsAugmentedEnergyStorage;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;

public class BasicMiningRigBlockEntity extends MiningRigBaseBlockEntity{
    
    public MilkevsAugmentedEnergyStorage energyStorage = new MilkevsAugmentedEnergyStorage(MilkevsOreMiners.PowerCapacity.get("MRbasic"), MilkevsOreMiners.PowerCapacity.get("1"), 0) {
        @Override
        protected void onFinalCommit() {
            markDirty();
        }
    };
    
    List<Item> output;
    float chance;
    float rolls;
    long powerCost;
    long basePowerConsumption;
    
    public BasicMiningRigBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(MilkevsOreMiners.MINING_RIG.BASIC.BLOCK_ENTITY, blockPos, blockState);
    }

    @Override
    public void cacheRecipe(World world) {
        if(this.output == null) {
            Optional<RecipeEntry<BasicMiningRigRecipe>> match = world.getRecipeManager().getFirstMatch(MilkevsOreMiners.MINING_RIG.BASIC.RECIPE_TYPE, new MilkevsSingleRecipeInput.Single(Items.ACACIA_BOAT.getDefaultStack()), world);
            if(match.isPresent()) {
                BasicMiningRigRecipe recipe = match.get().value();
                this.output = RecipeUtils.generateItemList(recipe.output(), world);
                this.chance = recipe.chance();
                this.rolls = recipe.rolls();
                this.powerCost = recipe.powerCost();
                this.basePowerConsumption = recipe.basePowerConsumption();
            }
        }
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
        return basePowerConsumption;
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
    public MilkevsAugmentedEnergyStorage getEnergyStorage() {
        return this.energyStorage;
    }

    @Override
    protected Block[][][][] getStructureMatrixList() {
        Block[] wall = new Block[]{MilkevsOreMiners.MINING_RIG.BASIC.WALL}; //thse blocks are generic wall blocks that make up the majority of the structure
        Block[] upgradeSlot = new Block[]{Blocks.ORANGE_CONCRETE}; //these are slots for upgrades. this tier does not have any upgrade slots.
        Block[] controller = new Block[]{MilkevsOreMiners.MINING_RIG.BASIC.CONTROLLER}; //this is the controller block, aka this block
        Block[] io = new Block[]{Blocks.BARREL, Blocks.CHEST}; //these blocks are IO blocks, either storage or power. to be replaced with having 1 of each required
        Block[] glass = new Block[]{MilkevsOreMiners.MINING_RIG.BASIC.GLASS}; //these blocks are the view windows
        Block[] requiredEmpty = new Block[]{Blocks.STRUCTURE_VOID}; //these blocks MUST be air
        Block[] beaminizer = new Block[]{Blocks.GRAY_CONCRETE}; //the block that "creates" the beam
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
