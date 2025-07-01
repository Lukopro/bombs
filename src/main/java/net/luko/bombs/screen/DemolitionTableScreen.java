package net.luko.bombs.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.luko.bombs.Bombs;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import java.util.List;

public class DemolitionTableScreen extends AbstractContainerScreen<DemolitionTableMenu> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Bombs.MODID, "textures/gui/demolition_table.png");

    public DemolitionTableScreen(DemolitionTableMenu menu, Inventory inventory, Component title){
        super(menu, inventory, Component.empty());
        this.imageWidth = 196;
        this.imageHeight = 189;
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
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY){

    }

    protected void renderInvalidSlots(GuiGraphics guiGraphics){
        List<Integer> invalidRecipeSlots = menu.blockEntity.getInvalidRecipeSlots();
        for(int i : invalidRecipeSlots){
            int x = leftPos + DemolitionTableMenu.SLOT_X_POSITIONS.get(i);
            int y = topPos + DemolitionTableMenu.SLOT_Y_POSITIONS.get(i);
            guiGraphics.fill(x, y, x + 16, y + 16, 0x40FF0000);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick){
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
        renderInvalidSlots(guiGraphics);
    }

    @Override
    protected void containerTick(){
        super.containerTick();
    }

    private ResourceLocation getTexture(){
        return TEXTURE;
    }
}
