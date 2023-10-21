package radon.jujutsu_kaisen.client.gui.screen.tab;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.DomainExpansion;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.BindingVow;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.client.gui.screen.JujutsuScreen;
import radon.jujutsu_kaisen.client.gui.screen.widget.AbilityListWidget;
import radon.jujutsu_kaisen.client.gui.screen.widget.BindingVowListWidget;
import radon.jujutsu_kaisen.client.gui.screen.widget.ChantListWidget;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.c2s.*;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class ChantTab extends JJKTab {
    private static final Component TITLE = Component.translatable(String.format("gui.%s.chant", JujutsuKaisen.MOD_ID));
    private static final ResourceLocation BACKGROUND = new ResourceLocation("textures/gui/advancements/backgrounds/stone.png");

    private ChantListWidget chants;
    private EditBox text;

    private Button add;
    private Button remove;

    @Nullable
    private AbilityListWidget.Entry ability;
    @Nullable
    private ChantListWidget.Entry chant;

    public ChantTab(Minecraft minecraft, JujutsuScreen screen, JJKTabType type, int index, int page) {
        super(minecraft, screen, type, index, page, Items.ENCHANTED_BOOK.getDefaultInstance(), TITLE, BACKGROUND, false);
    }

    public void setSelectedAbility(AbilityListWidget.Entry entry) {
        this.ability = entry;
        this.chants.refreshList();
    }

    public <T extends ObjectSelectionList.Entry<T>> void buildAbilityList(Consumer<T> consumer, Function<Ability, T> result) {
        if (this.minecraft.player == null) return;

        List<Ability> abilities = JJKAbilities.getAbilities(this.minecraft.player);

        for (Ability ability : abilities) {
            if (!ability.isChantable()) continue;
            consumer.accept(result.apply(ability));
        }
    }

    public void setSelectedChant(ChantListWidget.Entry entry) {
        this.chant = entry;
    }

    public <T extends ObjectSelectionList.Entry<T>> void buildChantList(Consumer<T> consumer, Function<String, T> result) {
        if (this.ability == null || this.minecraft.player == null) return;

        ISorcererData cap = this.minecraft.player.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        cap.getChants(this.ability.get()).forEach(chant -> consumer.accept(result.apply(chant)));
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

        ISorcererData cap = this.minecraft.player.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        String text = this.text.getValue();
        this.add.active = this.chants.children().size() < 5 && !text.isEmpty() && !text.isBlank() && this.ability != null && !cap.hasChant(this.ability.get(), text);
        this.remove.active = this.ability != null && this.chant != null;
    }

    @Override
    public void addWidgets() {
        if (this.minecraft.player == null) return;

        int i = (this.screen.width - JujutsuScreen.WINDOW_WIDTH) / 2;
        int j = (this.screen.height - JujutsuScreen.WINDOW_HEIGHT) / 2;

        int xOffset = i + (JujutsuScreen.WINDOW_WIDTH - JujutsuScreen.WINDOW_INSIDE_WIDTH);
        int yOffset = j + (JujutsuScreen.WINDOW_HEIGHT - JujutsuScreen.WINDOW_INSIDE_HEIGHT);

        this.addRenderableWidget(new AbilityListWidget(this::buildAbilityList, this::setSelectedAbility, this.minecraft, 65, 85,
                xOffset, yOffset + this.minecraft.font.lineHeight + 1));

        this.chants = new ChantListWidget(this::buildChantList, this::setSelectedChant, this.minecraft, 65, 85,
                xOffset + 77, yOffset + this.minecraft.font.lineHeight + 1);
        this.addRenderableWidget(this.chants);

        this.text = new EditBox(this.minecraft.font, xOffset + 154, yOffset + this.minecraft.font.lineHeight + 2, 62, 20, Component.empty());
        this.addRenderableWidget(this.text);

        this.add = Button.builder(Component.translatable(String.format("gui.%s.chant.add", JujutsuKaisen.MOD_ID)), pButton -> {
            String text = this.text.getValue();

            if (this.ability == null) return;

            ISorcererData cap = this.minecraft.player.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
            PacketHandler.sendToServer(new AddChantC2SPacket(JJKAbilities.getKey(this.ability.get()), text));
            cap.addChant(this.ability.get(), text);

            this.chants.refreshList();
        }).size(62, 20).pos(xOffset + 154, yOffset + this.minecraft.font.lineHeight + 40).build();
        this.addRenderableWidget(this.add);

        this.remove = Button.builder(Component.translatable(String.format("gui.%s.chant.remove", JujutsuKaisen.MOD_ID)), pButton -> {
            if (this.ability == null || this.chant == null) return;

            ISorcererData cap = this.minecraft.player.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
            PacketHandler.sendToServer(new RemoveChantC2SPacket(JJKAbilities.getKey(this.ability.get()), this.chant.get()));
            cap.removeChant(this.ability.get(), this.chant.get());

            this.chants.refreshList();
        }).size(62, 20).pos(xOffset + 154, yOffset + this.minecraft.font.lineHeight + 66).build();
        this.addRenderableWidget(this.remove);
    }

    @Override
    public void mouseClicked(double pMouseX, double pMouseY, int pButton) {

    }

    @Override
    public void keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (pKeyCode == InputConstants.KEY_RETURN) {
            this.add.onPress();
        }
    }
}