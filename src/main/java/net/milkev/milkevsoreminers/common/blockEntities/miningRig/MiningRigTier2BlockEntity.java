package net.milkev.milkevsoreminers.common.blockEntities.miningRig;

import net.milkev.milkevsoreminers.common.MilkevsOreMiners;
import net.milkev.milkevsoreminers.common.recipes.MilkevsSingleRecipeInput;
import net.milkev.milkevsoreminers.common.recipes.miningRig.MiningRigTier2Recipe;
import net.milkev.milkevsoreminers.common.util.MilkevsAugmentedEnergyStorage;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class MiningRigTier2BlockEntity extends MiningRigBaseBlockEntity {
    
    public MilkevsAugmentedEnergyStorage energyStorage = new MilkevsAugmentedEnergyStorage(50000000, 50000000, 0) {
        @Override
        protected void onFinalCommit() {
            markDirty();
        }
        @Override
        public boolean supportsExtraction() {
            return false;
        }
    };
    
    List<Item> output;
    
    public MiningRigTier2BlockEntity(BlockPos blockPos, BlockState blockState) {
        super(null, blockPos, blockState);
    }

    @Override
    public void cacheRecipe(World world) {
        if(this.output == null) {
            //MiningRigTier2Recipe recipe = world.getRecipeManager().getFirstMatch(MilkevsOreMiners.MINING_RIG_TIER_2_RECIPE_TYPE, new MilkevsSingleRecipeInput.Single(ItemStack.EMPTY), world).get().value();
            //this.recipe = ;
            //this.powerUsageSpeed = this.recipe.getPowerUsageSpeed();
        }
    }

    @Override
    public List<Item> getRecipeOutput() {
        return this.output;
    }

    @Override
    public long getPowerCost() {
        return 1;/*this.recipe.getPowerCost();*/
    }

    @Override
    public long getPowerUsageSpeed() {
        return 0;
    }

    @Override
    public float getRecipeChance() {
        return 0;
    }

    @Override
    public float getRecipeRolls() {
        return 0;
    }

    @Override
    public MilkevsAugmentedEnergyStorage getEnergyStorage() {
        return this.energyStorage;
    }

    @Override
    protected Block[][][][] getStructureMatrixList() {
        Block[] wall = new Block[]{Blocks.WHITE_CONCRETE}; //thse blocks are generic wall blocks that make up the majority of the structure
        Block[] upgradeSlot = new Block[]{Blocks.ORANGE_CONCRETE}; //these are slots for upgrades. this tier does not have any upgrade slots.
        Block[] controller = new Block[]{null}; //this is the controller block, aka this block
        Block[] io = new Block[]{Blocks.BARREL, Blocks.CHEST}; //these blocks are IO blocks, either storage or power. recommend having atleast 1 of each, but technically not required
        Block[] glass = new Block[]{Blocks.TINTED_GLASS}; //these blocks are the view winddows
        Block[] requiredEmpty = new Block[]{Blocks.STRUCTURE_VOID}; //these blocks MUST be air
        Block[] beaminizer = new Block[]{Blocks.GRAY_CONCRETE}; //the block that "creates" the beam
        Block[] oOS = new Block[]{Blocks.AIR}; //out Of Structure
        return new Block[][][][] {
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
                        {oOS, upgradeSlot, oOS, oOS}
                },
                {//y5
                        {oOS, wall, oOS, oOS},
                        {glass, requiredEmpty, glass, oOS},
                        {wall, wall, wall, oOS},
                        {oOS, upgradeSlot, oOS, oOS}
                },
                {//y6
                        {oOS, wall, oOS, oOS},
                        {glass, requiredEmpty, glass, oOS},
                        {wall, wall, wall, oOS},
                        {oOS, upgradeSlot, oOS, oOS}
                },
                {//y7
                        {oOS, wall, oOS, oOS},
                        {glass, requiredEmpty, glass, oOS},
                        {wall, wall, wall, oOS},
                        {oOS, upgradeSlot, oOS, oOS}
                },
                {//y8
                        {oOS, wall, oOS, oOS},
                        {wall, beaminizer, wall, oOS},
                        {wall, wall, wall, oOS},
                        {oOS, wall, oOS, oOS}
                },
                {//y9
                        {oOS, wall, oOS, oOS},
                        {wall, wall, wall, oOS},
                        {wall, wall, wall, oOS},
                        {oOS, wall, oOS, oOS}
                },
                {//y10
                        {oOS, oOS, oOS, oOS},
                        {oOS, wall, oOS, oOS},
                        {oOS, wall, oOS, oOS},
                        {oOS, wall, oOS, oOS}
                }
                
        };
    }
}
