package net.luko.bombs.block.entity;

import net.luko.bombs.data.modifiers.ModifierPriorityManager;
import net.luko.bombs.item.BombItem;
import net.luko.bombs.recipe.ModRecipeTypes;
import net.luko.bombs.screen.DemolitionTableMenu;
import net.luko.bombs.util.BombRecipeUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class DemolitionTableBlockEntity extends BlockEntity implements MenuProvider, WorldlyContainer {
    private final ItemStackHandler itemHandler = new ItemStackHandler(11);

    private static final int INPUT_SLOT = 0;
    private static final int UPGRADE_SLOT_1 = 1;
    private static final int UPGRADE_SLOT_2 = 2;
    private static final int UPGRADE_SLOT_3 = 3;
    private static final int MODIFIER_SLOT_1 = 4;
    private static final int MODIFIER_SLOT_2 = 5;
    private static final int MODIFIER_SLOT_3 = 6;
    private static final int MODIFIER_SLOT_4 = 7;
    private static final int MODIFIER_SLOT_5 = 8;
    private static final int MODIFIER_SLOT_6 = 9;
    private static final int OUTPUT_SLOT = 10;

    private int lastContainerHash = -1;

    private List<Integer> validRecipeSlots = new ArrayList<>();
    private List<Integer> invalidRecipeSlots = new ArrayList<>();

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

    @Override
    protected void saveAdditional(CompoundTag tag){
        super.saveAdditional(tag);
        tag.put("inventory", itemHandler.serializeNBT());
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(){
        CompoundTag tag = super.getUpdateTag();
        tag.put("inventory", itemHandler.serializeNBT());
        tag.putIntArray("invalidRecipeSlots", invalidRecipeSlots);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag){
        super.handleUpdateTag(tag);
        if (tag.contains("inventory")) itemHandler.deserializeNBT(tag.getCompound("inventory"));
        this.invalidRecipeSlots = Arrays.stream(tag.getIntArray("invalidRecipeSlots"))
                .boxed().collect(Collectors.toList());
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket(){
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet){
        handleUpdateTag(packet.getTag());
    }

    public void tick(Level level1, BlockPos pos, BlockState state1){
        if (level.isClientSide()) return;

        resetQuickCraftFlag();

        if(getContainerHash(getContainerFromHandler()) != lastContainerHash){
            updateHash();
            refreshOutput();
        }
    }

    private boolean quickCraftFlag = false;

    public void markQuickCraftFlag(){
        quickCraftFlag = true;
    }

    public void resetQuickCraftFlag(){
        quickCraftFlag = false;
    }

    public boolean getQuickCraftFlag() {
        return quickCraftFlag;
    }

    public SimpleContainer getContainerFromHandler(){
        SimpleContainer container = new SimpleContainer(itemHandler.getSlots());
        for(int i = 0; i < itemHandler.getSlots(); i++){
            container.setItem(i, itemHandler.getStackInSlot(i));
        }
        return container;
    }

    public void refreshOutput(){
        clearValidRecipeSlots();
        clearInvalidRecipeSlots();

        ItemStack result = fullAssemble();
        if(result.equals(itemHandler.getStackInSlot(INPUT_SLOT))) result = ItemStack.EMPTY;

        itemHandler.setStackInSlot(OUTPUT_SLOT, result);
        setChanged();
        if(!level.isClientSide())
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
    }

    public ItemStack fullAssemble(){
        SimpleContainer container = getContainerFromHandler();

        ItemStack result = container.getItem(INPUT_SLOT);

        for(int i = UPGRADE_SLOT_1; i < MODIFIER_SLOT_1; i++){
            if(!container.getItem(i).isEmpty()) {
                result = getUpgradeRecipeOutput(result, container.getItem(i), i);
            }
        }

        List<Integer> modifierCandidateSlots = getSortedModifierCandidateSlots(container);
        for(int i : modifierCandidateSlots){
            result = getModifierRecipeOutput(result, container.getItem(i), i);
        }

        return result;
    }

    private static class ModifierCandidate {
        final String modifier;
        final int slot;
        final int priority;
        final ItemStack ingredient;

        ModifierCandidate(String modifier, int slot, int priority, ItemStack ingredient){
            this.modifier = modifier;
            this.slot = slot;
            this.priority = priority;
            this.ingredient = ingredient;
        }
    }

    private List<Integer> getSortedModifierCandidateSlots(SimpleContainer container){
        ItemStack inputBomb = container.getItem(INPUT_SLOT);

        List<ModifierCandidate> candidates = new ArrayList<>();

        for(int i = MODIFIER_SLOT_1; i < OUTPUT_SLOT; i++){
            if(!container.getItem(i).isEmpty()) {
                ModifierCandidate possibleCandidate = checkModifierRecipe(inputBomb, container.getItem(i), i);
                if(possibleCandidate != null) candidates.add(possibleCandidate);
                else markInvalidSlot(i);
            }
        }

        candidates.sort(Comparator.comparingInt(a -> a.priority));

        return candidates.stream().map(c -> c.slot).toList();
    }

    private ItemStack getUpgradeRecipeOutput(ItemStack input, ItemStack ingredient, int slot){
        SimpleContainer isolatedContainer = new SimpleContainer(2);
        isolatedContainer.setItem(0, input);
        isolatedContainer.setItem(1, ingredient);

        AtomicReference<ItemStack> result = new AtomicReference<>();

        level.getRecipeManager().getRecipeFor(ModRecipeTypes.DEMOLITION_UPGRADE_TYPE.get(), isolatedContainer, level)
                .ifPresentOrElse(recipe -> {
                    markValidSlot(slot);
                    result.set(recipe.assemble(isolatedContainer, level.registryAccess()));

                }, () -> {
                    markInvalidSlot(slot);
                    result.set(input);
                });

        return result.get();
    }

    private ItemStack getModifierRecipeOutput(ItemStack input, ItemStack ingredient, int slot){
        SimpleContainer isolatedContainer = new SimpleContainer(2);
        isolatedContainer.setItem(0, input);
        isolatedContainer.setItem(1, ingredient);
        AtomicReference<ItemStack> result = new AtomicReference<>();

        level.getRecipeManager().getRecipeFor(ModRecipeTypes.DEMOLITION_MODIFIER_TYPE.get(), isolatedContainer, level)
                .ifPresentOrElse(recipe -> {
                    markValidSlot(slot);
                    result.set(recipe.assemble(isolatedContainer, level.registryAccess()));
                }, () -> {
                    markInvalidSlot(slot);
                    result.set(input);
                });

        return result.get();
    }

    private ModifierCandidate checkModifierRecipe(ItemStack input, ItemStack ingredient, int slot){
        SimpleContainer isolatedContainer = new SimpleContainer(2);
        isolatedContainer.setItem(0, input);
        isolatedContainer.setItem(1, ingredient);
        AtomicReference<ModifierCandidate> result = new AtomicReference<>();

        level.getRecipeManager().getRecipeFor(ModRecipeTypes.DEMOLITION_MODIFIER_TYPE.get(), isolatedContainer, level)
                .ifPresentOrElse(recipe -> {
                    String modifier = recipe.getModifierName();
                    result.set(new ModifierCandidate(
                            modifier, slot, ModifierPriorityManager.INSTANCE.getPriority(modifier), ingredient));
                }, () -> {
                    result.set(null);
                });

        return result.get();
    }

    private void markValidSlot(int slot){
        validRecipeSlots.add(slot);
        if(level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    private void markInvalidSlot(int slot){
        invalidRecipeSlots.add(slot);
        if(level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    private void clearValidRecipeSlots(){
        validRecipeSlots.clear();
        if(level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    private void clearInvalidRecipeSlots(){
        invalidRecipeSlots.clear();
        if(level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }

    }

    public List<Integer> getInvalidRecipeSlots(){
        return List.copyOf(invalidRecipeSlots);
    }

    public void consumeRecipeIngredients(int amount){
        itemHandler.getStackInSlot(INPUT_SLOT).shrink(amount);
        for(int slot : validRecipeSlots){
            itemHandler.getStackInSlot(slot).shrink(amount);
        }

        clearValidRecipeSlots();
        clearInvalidRecipeSlots();
    }

    public int smallestValidSlotItemCount(){
        int count = itemHandler.getStackInSlot(INPUT_SLOT).getCount();
        for(int slot : validRecipeSlots){
            if(itemHandler.getStackInSlot(slot).getCount() < count){
                count = itemHandler.getStackInSlot(slot).getCount();
            }
        }
        return count;
    }

    private int getContainerHash(SimpleContainer container){
        int hash = 1;
        for(int i = 0; i < OUTPUT_SLOT; i++){
            hash = 31 * hash + container.getItem(i).hashCode();
        }
        return hash;
    }

    public void updateHash(){
        lastContainerHash = getContainerHash(getContainerFromHandler());
    }

    @Override
    public int[] getSlotsForFace(Direction side){
        return switch (side) {
            case UP -> new int[]{INPUT_SLOT};
            case DOWN -> new int[]{OUTPUT_SLOT};
            default -> new int[]
                    {UPGRADE_SLOT_1, UPGRADE_SLOT_2, UPGRADE_SLOT_3,
                    MODIFIER_SLOT_1, MODIFIER_SLOT_2, MODIFIER_SLOT_3,
                    MODIFIER_SLOT_4, MODIFIER_SLOT_5, MODIFIER_SLOT_6};
        };
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack stack, @Nullable Direction direction){
        return switch (direction){
            case UP -> index == 0 && stack.getItem() instanceof BombItem;
            case DOWN -> false;
            default -> {
                if (index >= UPGRADE_SLOT_1 && index < MODIFIER_SLOT_1){
                    yield BombRecipeUtil.validUpgradeIngredient(level, stack);
                }
                if (index >= MODIFIER_SLOT_1 && index < OUTPUT_SLOT){
                    yield BombRecipeUtil.validModifierIngredient(level, stack);
                }
                yield false;
            }
        };
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction){
        return direction == Direction.DOWN && index == OUTPUT_SLOT;
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
            consumeRecipeIngredients(1);
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
