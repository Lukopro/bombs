package net.luko.bombs.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.luko.bombs.Bombs;
import net.luko.bombs.block.entity.DemolitionTableBlockEntity;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class DemolitionTableScreen extends AbstractContainerScreen<DemolitionTableMenu> {
    // Gui texture when the BlockEntity is empty
    private static final ResourceLocation EMPTY_TEXTURE =
            new ResourceLocation(Bombs.MODID, "textures/gui/demolition_table_gui_empty.png");

    // Gui texture when the BlockEntity has just a bomb
    private static final ResourceLocation BOMB_TEXTURE =
            new ResourceLocation(Bombs.MODID, "textures/gui/demolition_table_gui_bomb.png");

    // Gui texture when the BlockEntity has a bomb, upgrade, no casing
    private static final ResourceLocation MODIFIER_TEXTURE =
            new ResourceLocation(Bombs.MODID, "textures/gui/demolition_table_gui_modifier.png");

    // Gui texture when the BlockEntity has just a bomb, upgrade, and casing
    private static final ResourceLocation UPGRADE_TEXTURE =
            new ResourceLocation(Bombs.MODID, "textures/gui/demolition_table_gui_upgrade.png");

    public DemolitionTableScreen(DemolitionTableMenu menu, Inventory inventory, Component title){
        super(menu, inventory, title);
    }

    @Override
    protected void init(){
        super.init();
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1){
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, getTexture());
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(getTexture(), x, y, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick){
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void containerTick(){
        super.containerTick();
    }

    private ResourceLocation getTexture(){
        boolean hasBomb = !(menu.getSlot(DemolitionTableMenu.TE_INVENTORY_FIRST_SLOT_INDEX + 0).getItem().isEmpty());
        boolean hasUpgrade = !(menu.getSlot(DemolitionTableMenu.TE_INVENTORY_FIRST_SLOT_INDEX + 1).getItem().isEmpty());
        boolean hasCasing = !(menu.getSlot(DemolitionTableMenu.TE_INVENTORY_FIRST_SLOT_INDEX + 2).getItem().isEmpty());

        if(!hasBomb){
            return EMPTY_TEXTURE;
        } else if(!hasUpgrade){
            return BOMB_TEXTURE;
        } else if(!hasCasing){
            return MODIFIER_TEXTURE;
        }
        return UPGRADE_TEXTURE;
    }
}
