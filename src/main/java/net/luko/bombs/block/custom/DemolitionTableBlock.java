package net.luko.bombs.block.custom;

import com.mojang.serialization.MapCodec;
import net.luko.bombs.block.entity.DemolitionTableBlockEntity;
import net.luko.bombs.block.entity.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class DemolitionTableBlock extends BaseEntityBlock {
    public static final MapCodec<DemolitionTableBlock> CODEC = simpleCodec(DemolitionTableBlock::new);

    public DemolitionTableBlock(BlockBehaviour.Properties properties){
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public RenderShape getRenderShape(BlockState state){
        return RenderShape.MODEL;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston){
        if(state.getBlock() != newState.getBlock()){
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if(blockEntity instanceof DemolitionTableBlockEntity){
                ((DemolitionTableBlockEntity) blockEntity).drops();
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit){
        InteractionResult result = handleUse(level, pos, player);
        return ItemInteractionResult.sidedSuccess(result == InteractionResult.SUCCESS || result == InteractionResult.CONSUME);
    }

    @Override
    protected InteractionResult useWithoutItem( BlockState state, Level level, BlockPos pos, Player player,  BlockHitResult hit){
        return handleUse(level, pos, player);
    }

    public InteractionResult handleUse(Level level, BlockPos pos, Player player){
        if(!level.isClientSide()){
            BlockEntity entity = level.getBlockEntity(pos);
            if(entity instanceof DemolitionTableBlockEntity blockEntity){
                ((ServerPlayer) player).openMenu(new SimpleMenuProvider(blockEntity, Component.literal("Demolition Table")), pos);
            } else {
                throw new IllegalStateException("Container provider is missing.");
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state){
        return new DemolitionTableBlockEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType){
        if(level.isClientSide()){
            return null;
        }
        return createTickerHelper(blockEntityType, ModBlockEntities.DEMOLITION_TABLE_BE.get(),
                (level1, pos, state1, blockEntity) -> blockEntity.tick(level1, pos, state1));
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter worldIn, @NotNull BlockPos pos, @NotNull CollisionContext context){
        return Shapes.or(
                Block.box(0, 12, 0, 16, 16, 16),
                Block.box(0, 0, 0, 3, 12, 3),
                Block.box(13, 0, 0, 16, 12, 3),
                Block.box(13, 0, 13, 16, 12, 16),
                Block.box(0, 0, 13, 3, 12, 16),
                Block.box(0, 2, 0, 16, 4, 16)
        );
    }

    @Override
    public @NotNull VoxelShape getVisualShape(@NotNull BlockState state, @NotNull BlockGetter worldIn, @NotNull BlockPos pos, @NotNull CollisionContext context){
        return Shapes.or(
                Block.box(0, 12, 0, 16, 16, 16),
                Block.box(0, 0, 0, 3, 12, 3),
                Block.box(13, 0, 0, 16, 12, 3),
                Block.box(13, 0, 13, 16, 12, 16),
                Block.box(0, 0, 13, 3, 12, 16),
                Block.box(0, 2, 0, 16, 4, 16)
        );
    }

}
