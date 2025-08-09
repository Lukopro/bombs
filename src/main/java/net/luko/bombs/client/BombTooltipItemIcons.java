package net.luko.bombs.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Either;
import net.luko.bombs.Bombs;
import net.luko.bombs.components.ModDataComponents;
import net.luko.bombs.data.modifiers.ModifierManager;
import net.luko.bombs.item.bomb.BombItem;
import net.luko.bombs.util.BombPotionUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.*;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;

import java.util.List;

@EventBusSubscriber(modid = Bombs.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class BombTooltipItemIcons {

    @SubscribeEvent
    public static void applyItemIconsInTooltip(RenderTooltipEvent.GatherComponents event){
        ItemStack stack = event.getItemStack();
        if(!(stack.getItem() instanceof BombItem)) return;
        var tooltip = event.getTooltipElements();

        if(stack.has(ModDataComponents.MODIFIERS.get()) && !stack.getOrDefault(ModDataComponents.MODIFIERS.get(), List.of()).isEmpty()){
            List<String> modifiers = stack.get(ModDataComponents.MODIFIERS.get());

            addFormattedText(tooltip, Component.empty());
            addFormattedText(tooltip, Component.literal("Modifiers:")
                    .withStyle(Style.EMPTY
                            .withColor(TextColor.fromRgb(0xdddddd)))
                    .withStyle(ChatFormatting.BOLD));

            for(String mod : modifiers){
                MutableComponent modifierComponent = Component.literal("- ")
                        .append(Component.translatable("modifier.bombs." + mod));
                ItemStack iconStack = ModifierManager.INSTANCE.getModifierItem(mod).getItems()[0];

                if(mod.equals("laden") || mod.equals("imbued")){
                    int potionColor = stack.get(DataComponents.POTION_CONTENTS) == null
                            ? 0x385dc6
                            : stack.get(DataComponents.POTION_CONTENTS).getColor();

                    String potionDescriptionId = BombPotionUtil.getDescriptionId(stack);

                    modifierComponent.append(Component.literal(" ("))
                            .append(Component.translatable(potionDescriptionId))
                            .append(Component.literal(")"))
                            .withStyle(Style.EMPTY.withColor(potionColor));

                    if(stack.has(DataComponents.POTION_CONTENTS))
                        iconStack.set(DataComponents.POTION_CONTENTS, stack.get(DataComponents.POTION_CONTENTS));
                } else {
                    modifierComponent.withStyle(Style.EMPTY.withColor(ModifierManager.INSTANCE.getColor(mod)));
                }

                tooltip.add(Either.right(new ModifierTooltipComponent(iconStack, modifierComponent)));

                if(Screen.hasShiftDown()){
                    addFormattedText(tooltip, Component.translatable("modifier.bombs." + mod + ".info")
                            .withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY));

                    addFormattedText(tooltip, Component.empty());
                }
            }

            if(!Screen.hasShiftDown()){
                addFormattedText(tooltip, Component.empty());
                addFormattedText(tooltip, Component.literal("SHIFT for descriptions"));
            }

        } else {
            addFormattedText(tooltip, Component.literal("No modifiers"));
        }
    }

    private static void addFormattedText(List<Either<FormattedText, TooltipComponent>> tooltip, MutableComponent component){
        tooltip.add(Either.left(component));
    }

    public record ModifierTooltipComponent(ItemStack icon, Component text) implements TooltipComponent {}

    public static class ClientModifierTooltipComponent implements ClientTooltipComponent {
        private final ItemStack icon;
        private final Component text;
        private static final float itemScale = 0.75F;

        public ClientModifierTooltipComponent(ModifierTooltipComponent component){
            this.icon = component.icon;
            this.text = component.text;
        }

        @Override
        public int getHeight(){
            return Math.max((int)(16F * itemScale) + 2, 10);
        }

        @Override
        public int getWidth(Font font){
            return (int)(16F * itemScale) + 2 + font.width(text);
        }

        @Override
        public void renderImage(Font font, int x, int y, GuiGraphics guiGraphics){
            PoseStack poseStack = guiGraphics.pose();
            poseStack.pushPose();
            poseStack.translate(x, y + 1.5F, 0);
            poseStack.scale(itemScale, itemScale, 1.0F);

            guiGraphics.renderItem(icon, 0, 0);

            poseStack.popPose();

            guiGraphics.drawString(font, text, x + 14, y + 4, 0xFFFFFF, false);
        }
    }
}
