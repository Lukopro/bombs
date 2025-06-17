package net.luko.bombs.block.entity;

import net.luko.bombs.item.BombItem;
import net.luko.bombs.recipe.DemolitionModifierRecipe;
import net.luko.bombs.recipe.DemolitionUpgradeRecipe;
import net.luko.bombs.recipe.ModRecipeTypes;
import net.luko.bombs.screen.DemolitionTableMenu;
import net.luko.bombs.util.RecipeUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.*;
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
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DemolitionTableBlockEntity extends BlockEntity implements MenuProvider, WorldlyContainer {
    private final ItemStackHandler itemHandler = new ItemStackHandler(4);

    private static final int BOMB_SLOT = 0;
    private static final int UPGRADE_SLOT = 1;
    private static final int CASING_SLOT = 2;
    private static final int OUTPUT_SLOT = 3;

    private int lastInputHash = -1;

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    private final LazyOptional<IItemHandlerModifiable>[] sidedHandlers = SidedInvWrapper.create(this, Direction.values());

    public DemolitionTableBlockEntity(BlockPos pos, BlockState state){
        super(ModBlockEntities.DEMOLITION_TABLE_BE.get(), pos, state);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction side){
        if(capability == ForgeCapabilities.ITEM_HANDLER){
            if(side == null){
                return lazyItemHandler.cast();
            }
            return sidedHandlers[side.ordinal()].cast();
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
        for(LazyOptional<IItemHandlerModifiable> handler : sidedHandlers){
            handler.invalidate();
        }
    }

    public void drops(){
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for(int i = 0; i < itemHandler.getSlots() - 1; i++){
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

    private void consumeRecipeIngredients(){
        SimpleContainer container = getContainerFromHandler();
        RecipeType<?> type = itemHandler.getStackInSlot(CASING_SLOT).isEmpty()
                ? ModRecipeTypes.DEMOLITION_MODIFIER_TYPE.get()
                : ModRecipeTypes.DEMOLITION_UPGRADE_TYPE.get();

        level.getRecipeManager().getRecipeFor((RecipeType<Recipe<Container>>) type, container, level)
                .ifPresent(recipe -> {
                    itemHandler.getStackInSlot(BOMB_SLOT).shrink(1);
                    itemHandler.getStackInSlot(UPGRADE_SLOT).shrink(1);
                    if(recipe instanceof DemolitionUpgradeRecipe){
                        itemHandler.getStackInSlot(CASING_SLOT).shrink(1);
                    }
                });
    }



    private int getInputHash(SimpleContainer container){
        int hash = 1;
        for(int i = 0; i < container.getContainerSize(); i++){
            hash = 31 * hash + container.getItem(i).hashCode();
        }
        return hash;
    }

    @Override
    public int[] getSlotsForFace(Direction side){
        return switch (side) {
            case UP -> new int[]{BOMB_SLOT};
            case DOWN -> new int[]{OUTPUT_SLOT};
            default -> new int[]{UPGRADE_SLOT, CASING_SLOT};
        };
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack stack, @Nullable Direction direction){
        return switch (direction){
            case UP -> index == 0 && stack.getItem() instanceof BombItem;
            case DOWN -> false;
            default -> {
                if (index == 1){
                    yield RecipeUtil.validUpgradeIngredient(level, stack);
                }
                if (index == 2){
                    yield RecipeUtil.validCasingIngredient(level, stack);
                }
                yield false;
            }
        };
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction){
        return direction == Direction.DOWN && index == 3;
    }

    @Override
    public int getContainerSize() {
        return itemHandler.getSlots();
    }

    @Override
    public boolean isEmpty() {
        for(int i = 0; i < itemHandler.getSlots(); i++){
            if(!itemHandler.getStackInSlot(i).isEmpty()){
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int pSlot) {
        return itemHandler.getStackInSlot(pSlot);
    }

    @Override
    public ItemStack removeItem(int pSlot, int pAmount) {
        ItemStack stack = itemHandler.getStackInSlot(pSlot);
        if(stack.isEmpty()) return ItemStack.EMPTY;

        ItemStack result = stack.split(pAmount);
        if(!result.isEmpty()){
            setChanged();
        }

        if(pSlot == OUTPUT_SLOT){
            consumeRecipeIngredients();
        }
        return result;
    }

    @Override
    public ItemStack removeItemNoUpdate(int pSlot) {
        ItemStack stack = itemHandler.getStackInSlot(pSlot);
        itemHandler.setStackInSlot(pSlot, ItemStack.EMPTY);
        return stack;
    }

    @Override
    public void setItem(int pSlot, ItemStack pStack) {
        itemHandler.setStackInSlot(pSlot, pStack);
        if(pStack.getCount() > getMaxStackSize()){
            pStack.setCount(getMaxStackSize());
        }
        setChanged();
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return pPlayer.distanceToSqr(worldPosition.getX() + 0.5,
                                    worldPosition.getY() + 0.5,
                                    worldPosition.getZ() + 0.5) <= 64;
    }

    @Override
    public void clearContent() {
        for(int i = 0; i < itemHandler.getSlots(); i++){
            itemHandler.setStackInSlot(i, ItemStack.EMPTY);
        }
    }
}
