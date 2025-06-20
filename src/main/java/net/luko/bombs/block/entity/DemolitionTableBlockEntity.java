package net.luko.bombs.block.entity;

import net.luko.bombs.item.BombItem;
import net.luko.bombs.recipe.*;
import net.luko.bombs.screen.DemolitionTableMenu;
import net.luko.bombs.util.RecipeUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

public class DemolitionTableBlockEntity extends BlockEntity implements MenuProvider, WorldlyContainer {
    public final ItemStackHandler itemHandler = new ItemStackHandler(4){
        @Override
        protected void onContentsChanged(int slot){
            setChanged();
            if(!level.isClientSide()){
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };

    private static final int BOMB_SLOT = 0;
    private static final int UPGRADE_SLOT = 1;
    private static final int CASING_SLOT = 2;
    private static final int OUTPUT_SLOT = 3;

    private int lastInputHash = -1;

    public DemolitionTableBlockEntity(BlockPos pos, BlockState state){
        super(ModBlockEntities.DEMOLITION_TABLE_BE.get(), pos, state);
    }

    @Override
    public void onLoad(){
        super.onLoad();
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
    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player){
        return new DemolitionTableMenu(i, inventory, this);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket(){
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider){
        return saveWithoutMetadata(provider);
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider registries){
        super.saveAdditional(tag, registries);
        tag.put("inventory", itemHandler.serializeNBT(registries));
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries){
        super.loadAdditional(tag, registries);
        itemHandler.deserializeNBT(registries, tag.getCompound("inventory"));
    }

    public void tick(Level level1, BlockPos pos, BlockState state1){
        if (level.isClientSide()) return;

        SimpleContainer container = getContainerFromHandler();
        int currentHash = getInputHash(container);

        if(currentHash != lastInputHash){
            lastInputHash = currentHash;

            if(itemHandler.getStackInSlot(CASING_SLOT).isEmpty()){
                tryApplyRecipe(ModRecipeTypes.DEMOLITION_MODIFIER_TYPE.get(), getModifierRecipeInput());
            } else {
                tryApplyRecipe(ModRecipeTypes.DEMOLITION_UPGRADE_TYPE.get(), getUpgradeRecipeInput());
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

    private <T extends Recipe<I>, I extends RecipeInput> void tryApplyRecipe(RecipeType<T> type, I input){
        level.getRecipeManager().getRecipeFor(type, input, level).ifPresentOrElse(tRecipeHolder -> {
            ItemStack result = tRecipeHolder.value().assemble(input, level.registryAccess());
            itemHandler.setStackInSlot(OUTPUT_SLOT, result);
        }, () -> {
            itemHandler.setStackInSlot(OUTPUT_SLOT, ItemStack.EMPTY);
        });
    }

    private void consumeRecipeIngredients(){
        if(level == null || level.isClientSide()) return;

        if(itemHandler.getStackInSlot(CASING_SLOT).isEmpty()){
            // Modifier recipe input (no casing)
            level.getRecipeManager()
                    .getRecipeFor((ModRecipeTypes.DEMOLITION_MODIFIER_TYPE.get()), getModifierRecipeInput(), level)
                    .ifPresent(recipe -> {
                        itemHandler.getStackInSlot(BOMB_SLOT).shrink(1);
                        itemHandler.getStackInSlot(UPGRADE_SLOT).shrink(1);
                    });
        } else {
            //Upgrade recipe input (has casing)
            level.getRecipeManager()
                    .getRecipeFor((ModRecipeTypes.DEMOLITION_UPGRADE_TYPE.get()), getUpgradeRecipeInput(), level)
                    .ifPresent(recipe -> {
                        itemHandler.getStackInSlot(BOMB_SLOT).shrink(1);
                        itemHandler.getStackInSlot(UPGRADE_SLOT).shrink(1);
                        itemHandler.getStackInSlot(CASING_SLOT).shrink(1);
                    });
        }
    }

    private DemolitionModifierRecipeInput getModifierRecipeInput(){
        return new DemolitionModifierRecipeInput(
                itemHandler.getStackInSlot(BOMB_SLOT),
                itemHandler.getStackInSlot(UPGRADE_SLOT)
        );
    }

    private DemolitionUpgradeRecipeInput getUpgradeRecipeInput(){
        return new DemolitionUpgradeRecipeInput(
                itemHandler.getStackInSlot(BOMB_SLOT),
                itemHandler.getStackInSlot(UPGRADE_SLOT),
                itemHandler.getStackInSlot(CASING_SLOT)
        );
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
