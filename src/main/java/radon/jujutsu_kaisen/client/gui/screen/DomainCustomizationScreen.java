package radon.jujutsu_kaisen.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.widget.ForgeSlider;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.c2s.SetDomainSizeC2SPacket;

public class DomainCustomizationScreen extends Screen {
    private static final ResourceLocation WINDOW_LOCATION = new ResourceLocation("textures/gui/advancements/window.png");
    private static final ResourceLocation BACKGROUND_LOCATION = new ResourceLocation("textures/gui/advancements/backgrounds/stone.png");
    private static final Component TITLE = Component.translatable(String.format("gui.%s.domain_customization", JujutsuKaisen.MOD_ID));

    public static final int WINDOW_WIDTH = 252;
    public static final int WINDOW_HEIGHT = 140;

    private ForgeSlider sizeSlider;
    private float oldSize;

    public DomainCustomizationScreen() {
        super(GameNarrator.NO_TITLE);
    }

    @Override
    public void tick() {
        float size = (float) this.sizeSlider.getValue();

        if (size != this.oldSize) {
            if (this.minecraft != null && this.minecraft.player != null) {
                ISorcererData cap = this.minecraft.player.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
                PacketHandler.sendToServer(new SetDomainSizeC2SPacket(size));
                cap.setDomainSize(size);
            }
            this.oldSize = size;
        }
    }

    @Override
    protected void init() {
        if (this.minecraft == null || this.minecraft.player == null) return;

        ISorcererData cap = this.minecraft.player.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        int i = (this.width - WINDOW_WIDTH) / 2;
        int j = (this.height - WINDOW_HEIGHT) / 2;
        this.sizeSlider = new ForgeSlider(i + ((WINDOW_WIDTH - 110) / 2), j + ((WINDOW_HEIGHT - 16) / 2), 110, 16, Component.empty(), Component.empty(),
                0.5F, 1.5F, cap.getDomainSize(), 0.1D, 0, true);
        this.addRenderableWidget(this.sizeSlider);
        this.setInitialFocus(this.sizeSlider);
    }

    public void render(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        int i = (this.width - WINDOW_WIDTH) / 2;
        int j = (this.height - WINDOW_HEIGHT) / 2;
        this.renderBackground(pGuiGraphics);
        this.renderInside(pGuiGraphics, pMouseX, pMouseY, i, j);
        this.renderWindow(pGuiGraphics, i, j);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }

    private void renderInside(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, int pOffsetX, int pOffsetY) {
        int pX = pOffsetX + 9;
        int pY = pOffsetY + 18;

        pGuiGraphics.enableScissor(pX, pY, pX + 234, pY + 113);
        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().translate((float)pX, (float)pY, 0.0F);
        int i = (this.width - WINDOW_WIDTH) / 2;
        int j = (this.height - WINDOW_HEIGHT) / 2;
        int k = i % 16;
        int l = j % 16;

        for(int i1 = -1; i1 <= 15; ++i1) {
            for(int j1 = -1; j1 <= 8; ++j1) {
                pGuiGraphics.blit(BACKGROUND_LOCATION, k + 16 * i1, l + 16 * j1, 0.0F, 0.0F, 16, 16, 16, 16);
            }
        }
        pGuiGraphics.pose().popPose();
        pGuiGraphics.disableScissor();
    }

    public void renderWindow(GuiGraphics pGuiGraphics, int pOffsetX, int pOffsetY) {
        RenderSystem.enableBlend();
        pGuiGraphics.blit(WINDOW_LOCATION, pOffsetX, pOffsetY, 0, 0, 252, 140);
        pGuiGraphics.drawString(this.font, TITLE, pOffsetX + 8, pOffsetY + 6, 4210752, false);
        pGuiGraphics.drawString(this.font, Component.translatable(String.format("gui.%s.domain_customization.size", JujutsuKaisen.MOD_ID)),
                pOffsetX + ((WINDOW_WIDTH - 110) / 2), pOffsetY + ((WINDOW_HEIGHT - 16 - this.font.lineHeight - 8) / 2), 16777215, true);
    }
}