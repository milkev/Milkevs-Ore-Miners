package net.milkev.milkevsoreminers.common.blockEntities.miningRig;

import net.milkev.milkevsoreminers.common.recipes.miningRig.MiningRigTier1Recipe;
import net.milkev.milkevsoreminers.common.util.MilkevsAugmentedEnergyStorage;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MiningRigTier1BlockEntity extends MiningRigBaseBlockEntity{
    
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
    
    MiningRigTier1Recipe recipe;
    
    public MiningRigTier1BlockEntity(BlockPos blockPos, BlockState blockState) {
        super(null, blockPos, blockState);
    }

    @Override
    public void cacheRecipe(World world) {
        if(this.recipe == null) {
            //this.recipe = world.getRecipeManager().getFirstMatch(MiningRigTier1Recipe, new MyRecipeInput.Single(ItemStack.EMPTY), world).get();
            this.powerUsageSpeed = this.recipe.getPowerUsageSpeed();
        }
    }

    @Override
    public ItemStack getRecipeOutput() {
        return this.recipe.getOutput();
    }

    @Override
    public int getRecipePowerCost() {
        return this.recipe.getPowerCost();
    }

    @Override
    public MilkevsAugmentedEnergyStorage getEnergyStorage() {
        return this.energyStorage;
    }

    @Override
    public void usePower() {
        if(this.energyStorage.hasEnoughEnergy(getPowerUsageSpeed())) {
            this.energyStorage.consumeEnergy(getPowerUsageSpeed());
            this.powerUsage -= getPowerUsageSpeed();
        }
    }

    @Override
    protected Block[][][][] getStructureMatrixList() {
        Block[] wall = new Block[]{Blocks.WHITE_CONCRETE}; //thse blocks are generic wall blocks that make up the majority of the structure
        Block[] upgradeSlot = new Block[]{Blocks.ORANGE_CONCRETE}; //these are slots for upgrades. this tier does not have any upgrade slots.
        Block[] controller = new Block[]{}; //this is the controller block, aka this block
        Block[] io = new Block[]{Blocks.BARREL, Blocks.CHEST}; //these blocks are IO blocks, either storage or power. recommend having atleast 1 of each, but technically not required
        Block[] glass = new Block[]{Blocks.TINTED_GLASS}; //these blocks are the view winddows
        Block[] requiredEmpty = new Block[]{Blocks.STRUCTURE_VOID}; //these blocks MUST be air
        Block[] beaminizer = new Block[]{Blocks.GRAY_CONCRETE}; //the block that "creates" the beam
        Block[] oOS = new Block[]{Blocks.AIR}; //out Of Structure
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
