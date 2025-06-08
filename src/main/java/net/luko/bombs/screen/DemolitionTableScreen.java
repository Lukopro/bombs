package net.luko.bombs.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.luko.bombs.Bombs;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class DemolitionTableScreen extends AbstractContainerScreen<DemolitionTableMenu> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(Bombs.MODID, "textures/gui/demolition_table_gui.png");

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
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
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

}
