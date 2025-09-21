package net.milkev.milkevsoreminers.common.blocks;

import com.mojang.serialization.MapCodec;
import net.milkev.milkevsoreminers.common.MilkevsOreMiners;
import net.milkev.milkevsoreminers.common.blockEntities.AdvancedSifterBlockEntity;
import net.milkev.milkevsoreminers.common.blockEntities.SifterBlockEntity;
import net.milkev.milkevsoreminers.common.recipes.AdvancedSifterRecipe;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class AdvancedSifterBlock extends BlockWithEntity implements BlockEntityProvider {
    public AdvancedSifterBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return null;
    }

    @Override
    public ActionResult onUse(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, BlockHitResult blockHitResult) {
        if(world.isClient) {return ActionResult.SUCCESS;}
        AdvancedSifterBlockEntity advancedSifterBlockEntity = (AdvancedSifterBlockEntity) world.getBlockEntity(blockPos);

        return advancedSifterBlockEntity.interact(blockState, world, blockPos, playerEntity, playerEntity.getActiveHand(), blockHitResult);
    }

    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return (world1, pos, state1, blockEntity) -> {
            if (blockEntity instanceof BlockEntityTicker) {
                ((BlockEntityTicker) blockEntity).tick(world1, pos, state1, blockEntity);
            }
        };
    }

    @Override
    protected void onStateReplaced(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
        AdvancedSifterBlockEntity blockEntity = (AdvancedSifterBlockEntity) world.getBlockEntity(blockPos);
        System.out.println(blockEntity.getItems());
        blockEntity.getItems().iterator().forEachRemaining(stack -> Block.dropStack(world, blockPos, stack));
        super.onStateReplaced(blockState, world, blockPos, blockState2, bl);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new AdvancedSifterBlockEntity(blockPos, blockState);
    }
}
