package radon.jujutsu_kaisen.client.gui.screen.tab;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.curse_manipulation.EnhanceCurse;
import radon.jujutsu_kaisen.ability.curse_manipulation.MiniUzumaki;
import radon.jujutsu_kaisen.data.chant.IChantData;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.client.gui.screen.JujutsuScreen;
import radon.jujutsu_kaisen.client.gui.screen.widget.AbilityListWidget;
import radon.jujutsu_kaisen.client.gui.screen.widget.ChantListWidget;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.c2s.AddChantC2SPacket;
import radon.jujutsu_kaisen.network.packet.c2s.RemoveChantC2SPacket;
import radon.jujutsu_kaisen.util.HelperMethods;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class ChantTab extends JJKTab {
    private static final Component TITLE = Component.translatable(String.format("gui.%s.chant", JujutsuKaisen.MOD_ID));

    private ChantListWidget chants;
    private EditBox text;

    private Button add;
    private Button remove;

    @Nullable
    private AbilityListWidget.Entry ability;
    @Nullable
    private ChantListWidget.Entry chant;

    public ChantTab(Minecraft minecraft, JujutsuScreen screen, JJKTabType type, int index, int page) {
        super(minecraft, screen, type, index, page, Items.ENCHANTED_BOOK.getDefaultInstance(), TITLE, false);
    }

    public void setSelectedAbility(AbilityListWidget.Entry entry) {
        this.ability = entry;
        this.chants.refreshList();
    }

    public <T extends ObjectSelectionList.Entry<T>> void buildAbilityList(Consumer<T> consumer, Function<Ability, T> result) {
        if (this.minecraft.player == null) return;

        List<Ability> abilities = JJKAbilities.getAbilities(this.minecraft.player);

        for (Ability ability : abilities) {
            if (!ability.isScalable(this.minecraft.player) || !ability.isChantable()) continue;
            consumer.accept(result.apply(ability));
        }
    }

    public void setSelectedChant(ChantListWidget.Entry entry) {
        this.chant = entry;
    }

    public <T extends ObjectSelectionList.Entry<T>> void buildChantList(Consumer<T> consumer, Function<String, T> result) {
        if (this.ability == null || this.minecraft.player == null) return;

        IJujutsuCapability cap = this.minecraft.player.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        IChantData data = cap.getChantData();
        data.getFirstChants(this.ability.get()).forEach(chant -> consumer.accept(result.apply(chant)));
    }

    @Override
    public void drawContents(GuiGraphics pGuiGraphics, int pX, int pY) {
        super.drawContents(pGuiGraphics, pX, pY);

        int i = (this.screen.width - JujutsuScreen.WINDOW_WIDTH) / 2;
        int j = (this.screen.height - JujutsuScreen.WINDOW_HEIGHT) / 2;

        int xOffset = i + (JujutsuScreen.WINDOW_WIDTH - JujutsuScreen.WINDOW_INSIDE_WIDTH);
        int yOffset = j + (JujutsuScreen.WINDOW_HEIGHT - JujutsuScreen.WINDOW_INSIDE_HEIGHT);

        pGuiGraphics.drawString(this.minecraft.font, Component.translatable(String.format("gui.%s.chant.techniques", JujutsuKaisen.MOD_ID)),
                xOffset, yOffset, 16777215, true);
        pGuiGraphics.drawString(this.minecraft.font, Component.translatable(String.format("gui.%s.chant.chants", JujutsuKaisen.MOD_ID)),
                xOffset + 77, yOffset, 16777215, true);
        pGuiGraphics.drawString(this.minecraft.font, Component.translatable(String.format("gui.%s.chant.chant", JujutsuKaisen.MOD_ID)),
                xOffset + 154, yOffset, 16777215, true);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.minecraft.player == null) return;

        IJujutsuCapability cap = this.minecraft.player.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        IChantData data = cap.getChantData();

        String text = this.text.getValue().toLowerCase();

        boolean unique = true;

        if (!text.isEmpty() && !text.isBlank()) {
            for (ChantListWidget.Entry chant : this.chants.children()) {
                if (HelperMethods.strcmp(chant.get(), text) < ConfigHolder.SERVER.chantSimilarityThreshold.get()) {
                    unique = false;
                    break;
                }
            }
        }
        this.add.active = unique && this.chants.children().size() < ConfigHolder.SERVER.maximumChantCount.get() && !text.isEmpty() && !text.isBlank() &&
                this.ability != null && !data.hasChant(this.ability.get(), text);
        this.remove.active = this.ability != null && this.chant != null;
    }

    @Override
    public void addWidgets() {
        if (this.minecraft.player == null) return;

        int i = (this.screen.width - JujutsuScreen.WINDOW_WIDTH) / 2;
        int j = (this.screen.height - JujutsuScreen.WINDOW_HEIGHT) / 2;

        int xOffset = i + (JujutsuScreen.WINDOW_WIDTH - JujutsuScreen.WINDOW_INSIDE_WIDTH);
        int yOffset = j + (JujutsuScreen.WINDOW_HEIGHT - JujutsuScreen.WINDOW_INSIDE_HEIGHT);

        this.addRenderableWidget(new AbilityListWidget(this::buildAbilityList, this::setSelectedAbility, this.minecraft, 67, 85,
                xOffset, yOffset + this.minecraft.font.lineHeight + 1));

        this.chants = new ChantListWidget(this::buildChantList, this::setSelectedChant, this.minecraft, 67, 85,
                xOffset + 77, yOffset + this.minecraft.font.lineHeight + 1);
        this.addRenderableWidget(this.chants);

        this.text = new EditBox(this.minecraft.font, xOffset + 154, yOffset + this.minecraft.font.lineHeight + 2, 62, 20, Component.empty());
        this.text.setMaxLength(ConfigHolder.SERVER.maximumChantLength.get());
        this.addRenderableWidget(this.text);

        this.add = Button.builder(Component.translatable(String.format("gui.%s.chant.add", JujutsuKaisen.MOD_ID)), pButton -> {
            String text = this.text.getValue();

            if (this.ability == null) return;

            IJujutsuCapability cap = this.minecraft.player.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap == null) return;

            IChantData data = cap.getChantData();

            PacketHandler.sendToServer(new AddChantC2SPacket(JJKAbilities.getKey(this.ability.get()), text));
            data.addChant(this.ability.get(), text);

            this.chants.refreshList();
        }).size(62, 20).pos(xOffset + 154, yOffset + this.minecraft.font.lineHeight + 40).build();
        this.addRenderableWidget(this.add);

        this.remove = Button.builder(Component.translatable(String.format("gui.%s.chant.remove", JujutsuKaisen.MOD_ID)), pButton -> {
            if (this.ability == null || this.chant == null) return;

            IJujutsuCapability cap = this.minecraft.player.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap == null) return;

            IChantData data = cap.getChantData();

            PacketHandler.sendToServer(new RemoveChantC2SPacket(JJKAbilities.getKey(this.ability.get()), this.chant.get()));
            data.removeChant(this.ability.get(), this.chant.get());

            this.chants.refreshList();
        }).size(62, 20).pos(xOffset + 154, yOffset + this.minecraft.font.lineHeight + 66).build();
        this.addRenderableWidget(this.remove);
    }

    @Override
    public void keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (this.add.active && pKeyCode == InputConstants.KEY_RETURN) {
            this.add.onPress();
        } else if (this.remove.active && pKeyCode == InputConstants.KEY_DELETE) {
            this.remove.onPress();
        }
    }
}