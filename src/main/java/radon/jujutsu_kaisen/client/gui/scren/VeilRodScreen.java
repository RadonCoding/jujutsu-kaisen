package radon.jujutsu_kaisen.client.gui.scren;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.client.gui.widget.ForgeSlider;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.menu.VeilRodMenu;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.c2s.SetFrequencyC2SPacket;

public class VeilRodScreen extends AbstractContainerScreen<VeilRodMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/gui/container/veil_rod.png");

    private ForgeSlider slider;

    private int oldFrequency;

    public VeilRodScreen(VeilRodMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void containerTick() {
        super.containerTick();

        int frequency = this.slider.getValueInt();

        if (frequency != this.oldFrequency) {
            if (this.minecraft != null && this.minecraft.player != null) {
                PacketHandler.sendToServer(new SetFrequencyC2SPacket(frequency));
            }
            this.oldFrequency = frequency;
        }
    }

    @Override
    public void resize(@NotNull Minecraft pMinecraft, int pWidth, int pHeight) {
        double value = this.slider.getValueInt();
        this.init(pMinecraft, pWidth, pHeight);
        this.slider.setValue(value);
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

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        return this.slider.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY) && super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
    }

    @Override
    protected void init() {
        super.init();

        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        this.slider = new ForgeSlider(i + 33, j + 35, 110, 16, Component.empty(), Component.empty(), 0.0D, 100.0D, 0, true);
        this.addRenderableWidget(this.slider);
        this.setInitialFocus(this.slider);
    }

    @Override
    public void render(@NotNull PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pPoseStack);
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        this.renderTooltip(pPoseStack, pMouseX, pMouseY);
    }

    @Override
    protected void renderBg(@NotNull PoseStack pPoseStack, float pPartialTick, int pX, int pY) {
        RenderSystem.setShaderTexture(0, TEXTURE);
        blit(pPoseStack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    public void setFrequency(int frequency) {
        this.slider.setValue(frequency);
    }
}
