package net.milkev.milkevsoreminers.common.blocks;

import com.mojang.serialization.MapCodec;
import net.milkev.milkevsoreminers.common.MilkevsOreMiners;
import net.milkev.milkevsoreminers.common.blockEntities.SifterBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class SifterBlock extends BlockWithEntity implements BlockEntityProvider {
    public SifterBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return null;
    }
    
    @Override
    public ActionResult onUse(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, BlockHitResult blockHitResult) {
        if(world.isClient) {return ActionResult.SUCCESS;}
        SifterBlockEntity sifterBlockEntity = (SifterBlockEntity) world.getBlockEntity(blockPos);
        return sifterBlockEntity.interact(playerEntity.getStackInHand(playerEntity.getActiveHand()), playerEntity);
    }

    @Override
    protected void onStateReplaced(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
        SifterBlockEntity sifterBlockEntity = (SifterBlockEntity) world.getBlockEntity(blockPos);
        System.out.println(sifterBlockEntity.getItems());
        sifterBlockEntity.getItems().iterator().forEachRemaining(stack -> Block.dropStack(world, blockPos, stack));
        super.onStateReplaced(blockState, world, blockPos, blockState2, bl);
    }
    
    /*
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
        return VoxelShapes.union(
                VoxelShapes.cuboid(0f, 0.5f, 0f, 1f, 0.5f, 1.0f),
                VoxelShapes.cuboid(0, 0, 0, 0.1f, 0.5f, 0.1f)
        );
    }
     */

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new SifterBlockEntity(blockPos, blockState);
    }
}
