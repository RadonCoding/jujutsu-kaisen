package radon.jujutsu_kaisen.client.gui.screen.tab;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.BindingVow;
import radon.jujutsu_kaisen.client.gui.screen.JujutsuScreen;
import radon.jujutsu_kaisen.client.gui.screen.widget.BindingVowListWidget;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.c2s.AddBindingVowC2SPacket;
import radon.jujutsu_kaisen.network.packet.c2s.RemoveBindingVowC2SPacket;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Function;

public class BindingVowTab extends JJKTab {
    private static final Component TITLE = Component.translatable(String.format("gui.%s.binding_vow", JujutsuKaisen.MOD_ID));
    private static final ResourceLocation BACKGROUND = new ResourceLocation("textures/gui/advancements/backgrounds/stone.png");

    private Button add;
    private Button remove;

    private MultiLineTextWidget description;

    @Nullable
    private BindingVowListWidget.Entry vow;

    public BindingVowTab(Minecraft minecraft, JujutsuScreen screen, JJKTabType type, int index, int page) {
        super(minecraft, screen, type, index, page, Items.ENCHANTED_GOLDEN_APPLE.getDefaultInstance(), TITLE, BACKGROUND, false);
    }

    public void setSelectedBindingVow(BindingVowListWidget.Entry entry) {
        this.vow = entry;
        this.description.setMessage(entry.get().getDescription());

        if (this.minecraft.player == null) return;

        ISorcererData cap = this.minecraft.player.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        this.add.active = !cap.hasBindingVow(this.vow.get());
        this.remove.active = cap.hasBindingVow(this.vow.get());
    }

    public <T extends ObjectSelectionList.Entry<T>> void buildBindingVowList(Consumer<T> consumer, Function<BindingVow, T> result) {
        for (BindingVow vow : BindingVow.values()) {
            consumer.accept(result.apply(vow));
        }
    }

    @Override
    public void drawContents(GuiGraphics pGuiGraphics, int pX, int pY) {
        super.drawContents(pGuiGraphics, pX, pY);

        int i = (this.screen.width - JujutsuScreen.WINDOW_WIDTH) / 2;
        int j = (this.screen.height - JujutsuScreen.WINDOW_HEIGHT) / 2;

        int xOffset = i + (JujutsuScreen.WINDOW_WIDTH - JujutsuScreen.WINDOW_INSIDE_WIDTH);
        int yOffset = j + (JujutsuScreen.WINDOW_HEIGHT - JujutsuScreen.WINDOW_INSIDE_HEIGHT);

        pGuiGraphics.drawString(this.minecraft.font, Component.translatable(String.format("gui.%s.binding_vow.binding_vows", JujutsuKaisen.MOD_ID)),
                xOffset, yOffset, 16777215, true);
    }

    @Override
    protected void drawCustom(GuiGraphics graphics, int x, int y) {

    }

    @Override
    public void addWidgets() {
        if (this.minecraft == null || this.minecraft.player == null) return;

        int i = (this.screen.width - JujutsuScreen.WINDOW_WIDTH) / 2;
        int j = (this.screen.height - JujutsuScreen.WINDOW_HEIGHT) / 2;

        int xOffset = i + (JujutsuScreen.WINDOW_WIDTH - JujutsuScreen.WINDOW_INSIDE_WIDTH);
        int yOffset = j + (JujutsuScreen.WINDOW_HEIGHT - JujutsuScreen.WINDOW_INSIDE_HEIGHT);

        this.addRenderableWidget(new BindingVowListWidget(this::buildBindingVowList, this::setSelectedBindingVow, this.minecraft, 80, 85,
                xOffset, yOffset + this.minecraft.font.lineHeight + 1));

        this.add = Button.builder(Component.translatable(String.format("gui.%s.binding_vow.add", JujutsuKaisen.MOD_ID)), pButton -> {
            if (this.vow == null) return;

            ISorcererData cap = this.minecraft.player.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

            PacketHandler.sendToServer(new AddBindingVowC2SPacket(this.vow.get()));
            cap.addBindingVow(this.vow.get());

            this.add.active = !cap.hasBindingVow(this.vow.get());
            this.remove.active = cap.hasBindingVow(this.vow.get());
        }).size(62, 20).pos(xOffset + 86, yOffset + this.minecraft.font.lineHeight + 66).build();
        this.addRenderableWidget(this.add);

        this.remove = Button.builder(Component.translatable(String.format("gui.%s.binding_vow.remove", JujutsuKaisen.MOD_ID)), pButton -> {
            if (this.vow == null) return;

            ISorcererData cap = this.minecraft.player.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

            PacketHandler.sendToServer(new RemoveBindingVowC2SPacket(this.vow.get()));
            cap.removeBindingVow(this.vow.get());

            this.add.active = !cap.hasBindingVow(this.vow.get());
            this.remove.active = cap.hasBindingVow(this.vow.get());
        }).size(62, 20).pos(xOffset + 154, yOffset + this.minecraft.font.lineHeight + 66).build();
        this.addRenderableWidget(this.remove);

        this.description = new MultiLineTextWidget(xOffset + 86, yOffset + this.minecraft.font.lineHeight + 1, Component.empty(), this.minecraft.font)
                .setMaxWidth(129);
        this.addRenderableWidget(this.description);
    }

    @Override
    public void mouseClicked(double pMouseX, double pMouseY, int pButton) {

    }
}