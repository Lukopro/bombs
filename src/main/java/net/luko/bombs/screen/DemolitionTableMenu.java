package net.luko.bombs.screen;

import net.luko.bombs.block.ModBlocks;
import net.luko.bombs.block.entity.DemolitionTableBlockEntity;
import net.luko.bombs.item.bomb.BombItem;
import net.luko.bombs.util.BombRecipeUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.SlotItemHandler;

import java.util.Map;

public class DemolitionTableMenu extends AbstractContainerMenu {
    public final DemolitionTableBlockEntity blockEntity;
    private final Level level;
    public static final Map<Integer, Integer> SLOT_X_POSITIONS = Map.ofEntries(
            Map.entry(0, 19),
            Map.entry(1, 63),
            Map.entry(2, 63),
            Map.entry(3, 63),
            Map.entry(4, 99),
            Map.entry(5, 99),
            Map.entry(6, 99),
            Map.entry(7, 117),
            Map.entry(8, 117),
            Map.entry(9, 117),
            Map.entry(10, 161)
    );

    public static final Map<Integer, Integer> SLOT_Y_POSITIONS = Map.ofEntries(
            Map.entry(0, 42),
            Map.entry(1, 24),
            Map.entry(2, 42),
            Map.entry(3, 60),
            Map.entry(4, 24),
            Map.entry(5, 42),
            Map.entry(6, 60),
            Map.entry(7, 24),
            Map.entry(8, 42),
            Map.entry(9, 60),
            Map.entry(10, 42)
    );

    public DemolitionTableMenu(int containerId, Inventory inventory, FriendlyByteBuf extraData){
        this(containerId, inventory, inventory.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public DemolitionTableMenu(int containerId, Inventory inventory, BlockEntity entity){
        super(ModMenuTypes.DEMOLITION_TABLE_MENU.get(), containerId);
        checkContainerSize(inventory, 4);
        blockEntity = (DemolitionTableBlockEntity) entity;
        this.level = inventory.player.level();

        addPlayerHotbar(inventory);
        addPlayerInventory(inventory);

        this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(iItemHandler -> {
            this.addSlot(new SlotItemHandler(iItemHandler, 0, 19, 42){
                @Override
                public boolean mayPlace(ItemStack stack){
                    if(stack.getItem() instanceof BombItem){
                        return true;
                    }
                    return false;
                }
            });

            for(int i = 1; i < 4; i++){
                this.addSlot(new SlotItemHandler(iItemHandler, i, SLOT_X_POSITIONS.get(i), SLOT_Y_POSITIONS.get(i)){
                    @Override
                    public boolean mayPlace(ItemStack stack){
                        return BombRecipeUtil.validUpgradeIngredient(level, stack);
                    }
                });
            }

            for(int i = 4; i < 10; i++){
                this.addSlot(new SlotItemHandler(iItemHandler, i, SLOT_X_POSITIONS.get(i), SLOT_Y_POSITIONS.get(i)));
            }

            this.addSlot(new SlotItemHandler(iItemHandler, 10, 161, 42){
                @Override
                public boolean mayPlace(ItemStack stack){
                    return false;
                }

                @Override
                public void onTake(Player player, ItemStack stack){
                    super.onTake(player, stack);

                    ((DemolitionTableBlockEntity) entity).consumeRecipeIngredients(1);
                    ((DemolitionTableBlockEntity) entity).refreshOutput();
                }
            });
        });
    }

    // CREDIT GOES TO: diesieben07 | https://github.com/diesieben07/SevenCommons
    protected static final int HOTBAR_SLOT_COUNT = 9;
    protected static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    protected static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    protected static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    protected static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    protected static final int VANILLA_FIRST_SLOT_INDEX = 0;
    protected static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;
    protected static final int TE_INVENTORY_SLOT_COUNT = 11;  // must be the number of slots you have!

    @Override
    public ItemStack quickMoveStack(Player player, int slot) {
        Slot sourceSlot = slots.get(slot);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;  //EMPTY_ITEM
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        // Handle shift-clicking the OUTPUT slot (i = 10)
        if(slot == TE_INVENTORY_FIRST_SLOT_INDEX + 10){
            if(blockEntity.getQuickCraftFlag()) return ItemStack.EMPTY;
            blockEntity.markQuickCraftFlag();

            int craftCount = blockEntity.smallestValidSlotItemCount();
            ItemStack crafted = blockEntity.getItem(10);

            crafted.setCount(craftCount);
            if(!player.getInventory().add(crafted)) return ItemStack.EMPTY;

            blockEntity.consumeRecipeIngredients(craftCount);
            blockEntity.refreshOutput();

            return copyOfSourceStack;
        }

        // Check if the slot clicked is one of the vanilla container slots
        if (slot < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            // This is a vanilla container slot so merge the stack into the tile inventory
            if (!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX, TE_INVENTORY_FIRST_SLOT_INDEX
                    + TE_INVENTORY_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;  // EMPTY_ITEM
            }
        } else if (slot < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT) {
            // This is a TE slot so merge the stack into the players inventory
            if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            System.out.println("Invalid slotIndex:" + slot);
            return ItemStack.EMPTY;
        }
        // If stack size == 0 (the entire stack was moved) set slot contents to null
        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }
        sourceSlot.onTake(player, sourceStack);
        return copyOfSourceStack;
    }

    @Override
    public boolean stillValid(Player player){
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                player, ModBlocks.DEMOLITION_TABLE.get());
    }

    private void addPlayerInventory(Inventory inventory){
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 9; j++){
                this.addSlot(new Slot(inventory, j + i * 9 + 9, 18 + j * 18, 104 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory inventory){
        for(int i = 0; i < 9; i++){
            this.addSlot(new Slot(inventory, i, 18 + i * 18, 162));
        }
    }
}
