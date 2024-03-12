package radon.jujutsu_kaisen.client.gui.screen.tab;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.gui.widget.ExtendedSlider;
import org.joml.Vector3f;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.client.gui.screen.JujutsuScreen;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.c2s.SetCursedEnergyColorC2SPacket;

public class CursedEnergyColorTab extends JJKTab {
    private static final Component TITLE = Component.translatable(String.format("gui.%s.cursed_energy_color", JujutsuKaisen.MOD_ID));

    private ExtendedSlider rSlider;
    private ExtendedSlider gSlider;
    private ExtendedSlider bSlider;

    private float oldR, oldG, oldB;

    public CursedEnergyColorTab(Minecraft minecraft, JujutsuScreen screen, JJKTabType type, int index, int page) {
        super(minecraft, screen, type, index, page, Items.RED_DYE.getDefaultInstance(), TITLE, false);
    }

    @Override
    public void tick() {
        float r = (float) this.rSlider.getValue();
        float g = (float) this.gSlider.getValue();
        float b = (float) this.bSlider.getValue();

        if (r != this.oldR || g != this.oldG || b != this.oldB) {
            if (this.minecraft != null && this.minecraft.player != null) {
                int color = FastColor.ARGB32.color(255, Math.round(r), Math.round(g), Math.round(b));

                IJujutsuCapability cap = this.minecraft.player.getCapability(JujutsuCapabilityHandler.INSTANCE);

if (cap == null) return;

ISorcererData data = cap.getSorcererData();

                PacketHandler.sendToServer(new SetCursedEnergyColorC2SPacket(color));
                data.setCursedEnergyColor(color);
            }
            this.oldR = r;
            this.oldG = g;
            this.oldB = b;
        }
    }

    @Override
    public void drawContents(GuiGraphics pGuiGraphics, int pX, int pY) {
        super.drawContents(pGuiGraphics, pX, pY);

        int i = (this.screen.width - JujutsuScreen.WINDOW_WIDTH) / 2;
        int j = (this.screen.height - JujutsuScreen.WINDOW_HEIGHT) / 2;

        int centerY = j + (JujutsuScreen.WINDOW_HEIGHT / 2);

        pGuiGraphics.drawString(this.minecraft.font, Component.translatable(String.format("gui.%s.cursed_energy_color.red", JujutsuKaisen.MOD_ID)),
                i + ((JujutsuScreen.WINDOW_WIDTH - 110) / 2), centerY - 32 - this.minecraft.font.lineHeight - 2, 16777215, true);
        pGuiGraphics.drawString(this.minecraft.font, Component.translatable(String.format("gui.%s.cursed_energy_color.green", JujutsuKaisen.MOD_ID)),
                i + ((JujutsuScreen.WINDOW_WIDTH - 110) / 2), centerY - this.minecraft.font.lineHeight - 2, 16777215, true);
        pGuiGraphics.drawString(this.minecraft.font, Component.translatable(String.format("gui.%s.cursed_energy_color.blue", JujutsuKaisen.MOD_ID)),
                i + ((JujutsuScreen.WINDOW_WIDTH - 110) / 2), centerY + 32 - this.minecraft.font.lineHeight - 2, 16777215, true);
    }

    @Override
    public void addWidgets() {
        if (this.minecraft == null || this.minecraft.player == null) return;

        IJujutsuCapability cap = this.minecraft.player.getCapability(JujutsuCapabilityHandler.INSTANCE);

if (cap == null) return;

ISorcererData data = cap.getSorcererData();


        Vector3f color = Vec3.fromRGB24(data.getCursedEnergyColor()).toVector3f();

        int i = (this.screen.width - JujutsuScreen.WINDOW_WIDTH) / 2;
        int j = (this.screen.height - JujutsuScreen.WINDOW_HEIGHT) / 2;

        int centerY = j + (JujutsuScreen.WINDOW_HEIGHT / 2);

        this.rSlider = new ExtendedSlider(i + ((JujutsuScreen.WINDOW_WIDTH - 110) / 2), centerY - 32, 110, 16, Component.empty(), Component.empty(),
                0.0F, 255.0F, color.x * 255, 0.1D, 0, true);
        this.addRenderableWidget(this.rSlider);

        this.gSlider = new ExtendedSlider(i + ((JujutsuScreen.WINDOW_WIDTH - 110) / 2), centerY, 110, 16, Component.empty(), Component.empty(),
                0.0F, 255.0F, color.y * 255, 0.1D, 0, true);
        this.addRenderableWidget(this.gSlider);

        this.bSlider = new ExtendedSlider(i + ((JujutsuScreen.WINDOW_WIDTH - 110) / 2), centerY + 32, 110, 16, Component.empty(), Component.empty(),
                0.0F, 255.0F, color.z * 255, 0.1D, 0, true);
        this.addRenderableWidget(this.bSlider);
    }
}
