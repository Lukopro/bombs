package net.luko.bombs.screen;

import net.luko.bombs.block.ModBlocks;
import net.luko.bombs.block.entity.DemolitionTableBlockEntity;
import net.luko.bombs.item.BombItem;
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

public class DemolitionTableMenu extends AbstractContainerMenu {
    public final DemolitionTableBlockEntity blockEntity;
    private final Level level;

    public static final int DEMOLITION_TABLE_SLOT_0_X = 26;
    public static final int DEMOLITION_TABLE_SLOT_1_X = 52;
    public static final int DEMOLITION_TABLE_SLOT_2_X = 78;
    public static final int DEMOLITION_TABLE_SLOT_3_X = 134;
    public static final int DEMOLITION_TABLE_SLOTS_Y = 39;

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
            this.addSlot(new SlotItemHandler(iItemHandler, 0, DEMOLITION_TABLE_SLOT_0_X, DEMOLITION_TABLE_SLOTS_Y){
                @Override
                public boolean mayPlace(ItemStack stack){
                    if(stack.getItem() instanceof BombItem){
                        return true;
                    }
                    return false;
                }
            });
            this.addSlot(new SlotItemHandler(iItemHandler, 1, DEMOLITION_TABLE_SLOT_1_X, DEMOLITION_TABLE_SLOTS_Y));
            this.addSlot(new SlotItemHandler(iItemHandler, 2, DEMOLITION_TABLE_SLOT_2_X, DEMOLITION_TABLE_SLOTS_Y));
            this.addSlot(new SlotItemHandler(iItemHandler, 3, DEMOLITION_TABLE_SLOT_3_X, DEMOLITION_TABLE_SLOTS_Y){
                @Override
                public boolean mayPlace(ItemStack stack){
                    // Disallow placing items into output slot.
                    return false;
                }

                @Override
                public void onTake(Player player, ItemStack stack){
                    super.onTake(player, stack);

                    // Consume inputs from bomb, upgrade, and casing slots.
                    getSlot(TE_INVENTORY_FIRST_SLOT_INDEX + 0).remove(1);
                    getSlot(TE_INVENTORY_FIRST_SLOT_INDEX + 1).remove(1);
                    getSlot(TE_INVENTORY_FIRST_SLOT_INDEX + 2).remove(1);
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
    protected static final int TE_INVENTORY_SLOT_COUNT = 4;  // must be the number of slots you have!

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        Slot sourceSlot = slots.get(i);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;  //EMPTY_ITEM
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        // Check if the slot clicked is one of the vanilla container slots
        if (i < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            // This is a vanilla container slot so merge the stack into the tile inventory
            if (!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX, TE_INVENTORY_FIRST_SLOT_INDEX
                    + TE_INVENTORY_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;  // EMPTY_ITEM
            }
        } else if (i < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT) {
            // This is a TE slot so merge the stack into the players inventory
            if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            System.out.println("Invalid slotIndex:" + i);
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
                this.addSlot(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory inventory){
        for(int i = 0; i < 9; i++){
            this.addSlot(new Slot(inventory, i, 8 + i * 18, 142));
        }
    }
}
