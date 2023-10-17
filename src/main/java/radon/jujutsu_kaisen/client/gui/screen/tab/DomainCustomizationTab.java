package radon.jujutsu_kaisen.client.gui.screen.tab;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.client.gui.widget.ForgeSlider;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.client.gui.screen.JujutsuScreen;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.c2s.SetDomainSizeC2SPacket;

public class DomainCustomizationTab extends JJKTab {
    private static final Component TITLE = Component.translatable(String.format("gui.%s.domain_customization", JujutsuKaisen.MOD_ID));
    private static final ResourceLocation BACKGROUND = new ResourceLocation("textures/gui/advancements/backgrounds/stone.png");

    private ForgeSlider sizeSlider;
    private float oldSize;

    public DomainCustomizationTab(Minecraft minecraft, JujutsuScreen screen, JJKTabType type, int index, int page) {
        super(minecraft, screen, type, index, page, Items.NETHER_STAR.getDefaultInstance(), TITLE, BACKGROUND);
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
    public void drawContents(GuiGraphics pGuiGraphics, int pX, int pY) {
        super.drawContents(pGuiGraphics, pX, pY);

        int i = (this.screen.width - JujutsuScreen.WINDOW_WIDTH) / 2;
        int j = (this.screen.height - JujutsuScreen.WINDOW_HEIGHT) / 2;
        pGuiGraphics.drawString(this.getFontRenderer(), Component.translatable(String.format("gui.%s.domain_customization.size", JujutsuKaisen.MOD_ID)),
                i + ((JujutsuScreen.WINDOW_WIDTH - 110) / 2), j + ((JujutsuScreen.WINDOW_HEIGHT - 16 - this.getFontRenderer().lineHeight - 8) / 2), 16777215, true);
    }

    @Override
    public void addWidgets() {
        if (this.minecraft == null || this.minecraft.player == null) return;

        ISorcererData cap = this.minecraft.player.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        int i = (this.screen.width - JujutsuScreen.WINDOW_WIDTH) / 2;
        int j = (this.screen.height - JujutsuScreen.WINDOW_HEIGHT) / 2;
        this.sizeSlider = new ForgeSlider(i + ((JujutsuScreen.WINDOW_WIDTH - 110) / 2), j + ((JujutsuScreen.WINDOW_HEIGHT - 16) / 2), 110, 16, Component.empty(), Component.empty(),
                0.5F, 1.5F, cap.getDomainSize(), 0.1D, 0, true);
        this.addRenderableWidget(this.sizeSlider);
    }
}
