package net.luko.bombs.block.entity;

import net.luko.bombs.Bombs;
import net.luko.bombs.recipe.ModRecipeTypes;
import net.luko.bombs.screen.DemolitionTableMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DemolitionTableBlockEntity extends BlockEntity implements MenuProvider {
    private final ItemStackHandler itemHandler = new ItemStackHandler(4);

    private static final int BOMB_SLOT = 0;
    private static final int UPGRADE_SLOT = 1;
    private static final int CASING_SLOT = 2;
    private static final int OUTPUT_SLOT = 3;

    private int lastInputHash = -1;

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    public DemolitionTableBlockEntity(BlockPos pos, BlockState state){
        super(ModBlockEntities.DEMOLITION_TABLE_BE.get(), pos, state);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction side){
        if(capability == ForgeCapabilities.ITEM_HANDLER){
            return lazyItemHandler.cast();
        }
        return super.getCapability(capability, side);
    }

    @Override
    public void onLoad(){
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }

    @Override
    public void invalidateCaps(){
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    public void drops(){
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for(int i = 0; i < itemHandler.getSlots(); i++){
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }

        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    @Override
    public Component getDisplayName(){
        return Component.translatable("block.bombs.demolition_table");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player){
        return new DemolitionTableMenu(i, inventory, this);
    }

    @Override
    public void load(CompoundTag tag){
        super.load(tag);
        itemHandler.deserializeNBT(tag.getCompound("inventory"));
    }

    public void tick(Level level1, BlockPos pos, BlockState state1){
        if (level.isClientSide()) return;

        SimpleContainer container = getContainerFromHandler();
        int currentHash = getInputHash(container);

        if(currentHash != lastInputHash){
            lastInputHash = currentHash;

            if(itemHandler.getStackInSlot(CASING_SLOT).isEmpty()){
                tryApplyRecipe(ModRecipeTypes.DEMOLITION_MODIFIER_TYPE.get(), container);
            } else {
                tryApplyRecipe(ModRecipeTypes.DEMOLITION_UPGRADE_TYPE.get(), container);
            }
        }
    }

    private SimpleContainer getContainerFromHandler(){
        SimpleContainer container = new SimpleContainer(itemHandler.getSlots());
        for(int i = 0; i < itemHandler.getSlots(); i++){
            container.setItem(i, itemHandler.getStackInSlot(i));
        }
        return container;
    }

    private void tryApplyRecipe(RecipeType<?> type, SimpleContainer container){
        level.getRecipeManager().getRecipeFor((RecipeType<Recipe<Container>>) type, container, level)
                .ifPresentOrElse(recipe -> {
                    ItemStack result = recipe.assemble(container, level.registryAccess());
                    itemHandler.setStackInSlot(OUTPUT_SLOT, result);
                }, () -> {
                    // No valid recipe, clear output
                    itemHandler.setStackInSlot(OUTPUT_SLOT, ItemStack.EMPTY);
                });
    }

    private int getInputHash(SimpleContainer container){
        int hash = 1;
        for(int i = 0; i < container.getContainerSize(); i++){
            hash = 31 * hash + container.getItem(i).hashCode();
        }
        return hash;
    }
}
