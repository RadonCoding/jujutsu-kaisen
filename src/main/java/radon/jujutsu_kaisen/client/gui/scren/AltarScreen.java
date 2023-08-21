package radon.jujutsu_kaisen.client.gui.scren;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.ItemCombinerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.block.menu.AltarMenu;
import radon.jujutsu_kaisen.item.JJKItems;

public class AltarScreen extends ItemCombinerScreen<AltarMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/gui/container/altar.png");

    public AltarScreen(AltarMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle, TEXTURE);

        this.titleLabelX = 29;
        this.titleLabelY = 6;
    }

    private void renderSlot(PoseStack pPoseStack, Slot pSlot) {
        int i = pSlot.x;
        int j = pSlot.y;
        String s = null;

        pPoseStack.pushPose();
        pPoseStack.translate(0.0F, 0.0F, 100.0F);

        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        if (this.minecraft != null && this.minecraft.player != null) {
            this.itemRenderer.renderAndDecorateItem(pPoseStack, this.minecraft.player, JJKItems.VEIL_ROD.get().getDefaultInstance(), i, j, pSlot.x + pSlot.y * this.imageWidth);
            this.itemRenderer.renderGuiItemDecorations(pPoseStack, this.font, JJKItems.VEIL_ROD.get().getDefaultInstance(), i, j, s);
        }

        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        pPoseStack.popPose();
    }

    @Override
    public void render(@NotNull PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);

        Slot slot = this.menu.slots.get(4);

        if (!slot.hasItem()) {
            int i = this.leftPos;
            int j = this.topPos;

            pPoseStack.pushPose();
            pPoseStack.translate((float)i, (float)j, 0.0F);

            this.renderSlot(pPoseStack, slot);

            pPoseStack.popPose();
        }
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