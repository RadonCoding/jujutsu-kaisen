package radon.jujutsu_kaisen.client.gui.screen;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.ItemCombinerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.menu.AltarMenu;
import radon.jujutsu_kaisen.item.registry.JJKItems;

public class AltarScreen extends ItemCombinerScreen<AltarMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/gui/container/altar.png");

    public AltarScreen(AltarMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle, TEXTURE);
    }

    private void renderPlaceholderSlot(GuiGraphics graphics, Slot slot) {
        int i = slot.x;
        int j = slot.y;
        String s = null;

        graphics.pose().pushPose();
        graphics.pose().translate(0.0F, 0.0F, 100.0F);

        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        if (this.minecraft != null && this.minecraft.player != null) {
            graphics.renderFakeItem(JJKItems.VEIL_ROD.get().getDefaultInstance(), i, j);
            graphics.renderItemDecorations(this.font, JJKItems.VEIL_ROD.get().getDefaultInstance(), i, j, s);
        }

        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        graphics.pose().popPose();
    }

    @Override
    public void render(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

        Slot slot = this.menu.slots.get(4);

        if (!slot.hasItem()) {
            int i = this.leftPos;
            int j = this.topPos;

            pGuiGraphics.pose().pushPose();
            pGuiGraphics.pose().translate((float) i, (float) j, 0.0F);

            this.renderPlaceholderSlot(pGuiGraphics, slot);

            pGuiGraphics.pose().popPose();
        }
    }

    @Override
    protected void renderErrorIcon(@NotNull GuiGraphics pGuiGraphics, int pX, int pY) {

    }

    @Override
    public void resize(@NotNull Minecraft pMinecraft, int pWidth, int pHeight) {
        this.init(pMinecraft, pWidth, pHeight);
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (pKeyCode == 256) {
            if (this.minecraft != null && this.minecraft.player != null) {
                this.minecraft.player.closeContainer();
            }
        }
        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }
}