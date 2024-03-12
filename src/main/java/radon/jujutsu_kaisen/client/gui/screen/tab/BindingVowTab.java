package radon.jujutsu_kaisen.client.gui.screen.tab;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.registries.DeferredHolder;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.binding_vow.BindingVow;
import radon.jujutsu_kaisen.binding_vow.JJKBindingVows;
import radon.jujutsu_kaisen.data.contract.IContractData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
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

    private Button add;
    private Button remove;

    private MultiLineTextWidget description;

    @Nullable
    private BindingVowListWidget.Entry vow;

    public BindingVowTab(Minecraft minecraft, JujutsuScreen screen, JJKTabType type, int index, int page) {
        super(minecraft, screen, type, index, page, Items.ENCHANTED_GOLDEN_APPLE.getDefaultInstance(), TITLE, false);
    }

    public void setSelectedBindingVow(BindingVowListWidget.Entry entry) {
        this.vow = entry;
        this.description.setMessage(entry.get().getDescription());
    }

    public <T extends ObjectSelectionList.Entry<T>> void buildBindingVowList(Consumer<T> consumer, Function<BindingVow, T> result) {
        for (DeferredHolder<BindingVow, ? extends BindingVow> entry : JJKBindingVows.BINDING_VOWS.getEntries()) {
            consumer.accept(result.apply(entry.get()));
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

        if (this.minecraft.player == null) return;

        IJujutsuCapability cap = this.minecraft.player.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        IContractData data = cap.getContractData();

        if (this.vow != null && data != null && !data.isCooldownDone(this.vow.get())) {
            int seconds = data.getRemainingCooldown(this.vow.get()) / 20;

            int minutes = seconds / 60;
            int remaining = seconds - (minutes * 60);

            if (seconds > 60) {
                pGuiGraphics.drawString(this.minecraft.font, Component.translatable(String.format("gui.%s.binding_vow.cooldown.minutes", JujutsuKaisen.MOD_ID),
                        minutes, remaining), xOffset + 86, yOffset + this.minecraft.font.lineHeight + 1 - this.minecraft.font.lineHeight + 1, 16777215, true);
            } else if (seconds % 60 != 0) {
                pGuiGraphics.drawString(this.minecraft.font, Component.translatable(String.format("gui.%s.binding_vow.cooldown.seconds", JujutsuKaisen.MOD_ID),
                        remaining), xOffset + 86, yOffset + this.minecraft.font.lineHeight + 1 - this.minecraft.font.lineHeight + 1, 16777215, true);
            }
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (this.minecraft.player == null) return;

        IJujutsuCapability cap = this.minecraft.player.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        IContractData data = cap.getContractData();

        this.add.active = this.vow != null && !data.hasBindingVow(this.vow.get()) && data.isCooldownDone(this.vow.get());
        this.remove.active = this.vow != null && data.hasBindingVow(this.vow.get()) && data.isCooldownDone(this.vow.get());
    }

    @Override
    public void addWidgets() {
        if (this.minecraft == null || this.minecraft.player == null) return;

        int i = (this.screen.width - JujutsuScreen.WINDOW_WIDTH) / 2;
        int j = (this.screen.height - JujutsuScreen.WINDOW_HEIGHT) / 2;

        int xOffset = i + (JujutsuScreen.WINDOW_WIDTH - JujutsuScreen.WINDOW_INSIDE_WIDTH);
        int yOffset = j + (JujutsuScreen.WINDOW_HEIGHT - JujutsuScreen.WINDOW_INSIDE_HEIGHT);

        this.addRenderableWidget(new BindingVowListWidget(this::buildBindingVowList, this::setSelectedBindingVow, this.minecraft, 78, 85,
                xOffset, yOffset + this.minecraft.font.lineHeight + 1));

        this.add = Button.builder(Component.translatable(String.format("gui.%s.binding_vow.add", JujutsuKaisen.MOD_ID)), pButton -> {
            if (this.vow == null) return;

            IJujutsuCapability cap = this.minecraft.player.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap == null) return;

            IContractData data = cap.getContractData();

            PacketHandler.sendToServer(new AddBindingVowC2SPacket(this.vow.get()));
            data.addBindingVow(this.vow.get());

            this.add.active = !data.hasBindingVow(this.vow.get());
            this.remove.active = data.hasBindingVow(this.vow.get());

            data.addBindingVowCooldown(this.vow.get());
        }).size(61, 20).pos(xOffset + 86, yOffset + this.minecraft.font.lineHeight + 66).build();
        this.addRenderableWidget(this.add);

        this.remove = Button.builder(Component.translatable(String.format("gui.%s.binding_vow.remove", JujutsuKaisen.MOD_ID)), pButton -> {
            if (this.vow == null) return;

            IJujutsuCapability cap = this.minecraft.player.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap == null) return;

            IContractData data = cap.getContractData();

            PacketHandler.sendToServer(new RemoveBindingVowC2SPacket(this.vow.get()));
            data.removeBindingVow(this.vow.get());

            this.add.active = !data.hasBindingVow(this.vow.get());
            this.remove.active = data.hasBindingVow(this.vow.get());

            data.addBindingVowCooldown(this.vow.get());
        }).size(61, 20).pos(xOffset + 155, yOffset + this.minecraft.font.lineHeight + 66).build();
        this.addRenderableWidget(this.remove);

        this.description = new MultiLineTextWidget(xOffset + 86, yOffset + this.minecraft.font.lineHeight + 1, Component.empty(), this.minecraft.font)
                .setMaxWidth(129);
        this.addRenderableWidget(this.description);
    }
}