package radon.jujutsu_kaisen.client.gui.screen.tab;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.client.gui.widget.ExtendedSlider;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.client.gui.screen.JujutsuScreen;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.c2s.SetDomainSizeC2SPacket;

public class DomainCustomizationTab extends JJKTab {
    private static final Component TITLE = Component.translatable(String.format("gui.%s.domain_customization", JujutsuKaisen.MOD_ID));
    private static final ResourceLocation BACKGROUND = new ResourceLocation("textures/gui/advancements/backgrounds/stone.png");

    private ExtendedSlider sizeSlider;
    private float oldSize;

    public DomainCustomizationTab(Minecraft minecraft, JujutsuScreen screen, JJKTabType type, int index, int page) {
        super(minecraft, screen, type, index, page, Items.NETHER_STAR.getDefaultInstance(), TITLE, BACKGROUND, false);
    }

    @Override
    public void tick() {
        float size = (float) this.sizeSlider.getValue();

        if (this.oldSize == 0.0F) {
            this.oldSize = size;
        } else if (size != this.oldSize) {
            if (this.minecraft != null && this.minecraft.player != null) {
                IJujutsuCapability cap = this.minecraft.player.getCapability(JujutsuCapabilityHandler.INSTANCE);

                if (cap == null) return;

                ISorcererData data = cap.getSorcererData();

                PacketHandler.sendToServer(new SetDomainSizeC2SPacket(size));
                data.setDomainSize(size);
            }
            this.oldSize = size;
        }
    }

    @Override
    public void drawContents(GuiGraphics pGuiGraphics, int pX, int pY) {
        super.drawContents(pGuiGraphics, pX, pY);

        int i = (this.screen.width - JujutsuScreen.WINDOW_WIDTH) / 2;
        int j = (this.screen.height - JujutsuScreen.WINDOW_HEIGHT) / 2;
        pGuiGraphics.drawString(this.minecraft.font, Component.translatable(String.format("gui.%s.domain_customization.size", JujutsuKaisen.MOD_ID)),
                i + ((JujutsuScreen.WINDOW_WIDTH - 110) / 2), j + ((JujutsuScreen.WINDOW_HEIGHT - 16 - this.minecraft.font.lineHeight - 8) / 2), 16777215, true);
    }

    @Override
    public void addWidgets() {
        if (this.minecraft == null || this.minecraft.player == null) return;

        IJujutsuCapability cap = this.minecraft.player.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        ISorcererData data = cap.getSorcererData();

        int i = (this.screen.width - JujutsuScreen.WINDOW_WIDTH) / 2;
        int j = (this.screen.height - JujutsuScreen.WINDOW_HEIGHT) / 2;
        this.sizeSlider = new ExtendedSlider(i + ((JujutsuScreen.WINDOW_WIDTH - 110) / 2), j + ((JujutsuScreen.WINDOW_HEIGHT - 16) / 2), 110, 16, Component.empty(), Component.empty(),
                ConfigHolder.SERVER.minimumDomainSize.get().floatValue(), ConfigHolder.SERVER.maximumDomainSize.get().floatValue(), data.getDomainSize(), 0.1D, 0, true);
        this.addRenderableWidget(this.sizeSlider);
    }
}
