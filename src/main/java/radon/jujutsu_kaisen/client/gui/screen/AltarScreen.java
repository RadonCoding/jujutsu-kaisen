package radon.jujutsu_kaisen.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
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
import radon.jujutsu_kaisen.item.JJKItems;

public class AltarScreen extends ItemCombinerScreen<AltarMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/gui/container/altar.png");

    public AltarScreen(AltarMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle, TEXTURE);
    }

    private void renderSlot(GuiGraphics pGuiGraphics, Slot pSlot) {
        int i = pSlot.x;
        int j = pSlot.y;
        String s = null;

        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().translate(0.0F, 0.0F, 100.0F);

        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        if (this.minecraft != null && this.minecraft.player != null) {
            pGuiGraphics.renderFakeItem(JJKItems.VEIL_ROD.get().getDefaultInstance(), i, j);
            pGuiGraphics.renderItemDecorations(this.font, JJKItems.VEIL_ROD.get().getDefaultInstance(), i, j, s);
        }

        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        pGuiGraphics.pose().popPose();
    }

    @Override
    public void render(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

        Slot slot = this.menu.slots.get(4);

        if (!slot.hasItem()) {
            int i = this.leftPos;
            int j = this.topPos;

            pGuiGraphics.pose().pushPose();
            pGuiGraphics.pose().translate((float)i, (float)j, 0.0F);

            this.renderSlot(pGuiGraphics, slot);

            pGuiGraphics.pose().popPose();
        }
    }

    @Override
    protected void renderErrorIcon(@NotNull GuiGraphics pGuiGraphics, int pX, int pY) {

    }

    public void resize(@NotNull Minecraft pMinecraft, int pWidth, int pHeight) {
        this.init(pMinecraft, pWidth, pHeight);
    }

    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (pKeyCode == 256) {
            if (this.minecraft != null && this.minecraft.player != null) {
                this.minecraft.player.closeContainer();
            }
        }
        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    protected void renderErrorIcon(@NotNull PoseStack pPoseStack, int pX, int pY) {

    }
}