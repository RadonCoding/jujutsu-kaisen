package radon.jujutsu_kaisen.client.gui.screen.tab;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.StringSplitter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.advancements.AdvancementWidgetType;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.Pact;
import radon.jujutsu_kaisen.client.gui.screen.JujutsuScreen;
import radon.jujutsu_kaisen.client.gui.screen.widget.PactListWidget;
import radon.jujutsu_kaisen.client.gui.screen.widget.PlayerListWidget;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.c2s.QuestionCreatePactC2SPacket;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class PactsTab extends JJKTab {
    private static final Component TITLE = Component.translatable(String.format("gui.%s.pacts", JujutsuKaisen.MOD_ID));
    private static final ResourceLocation BACKGROUND = new ResourceLocation("textures/gui/advancements/backgrounds/stone.png");
    private static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation("textures/gui/advancements/widgets.png");

    private static final int[] TEST_SPLIT_OFFSETS = new int[] { 0, 10, -10, 25, -25 };

    @Nullable
    private FormattedCharSequence title;
    @Nullable
    private List<FormattedCharSequence> description;

    private int width;

    private Button create;

    @Nullable
    private PlayerListWidget.PlayerEntry player;
    @Nullable
    private PactListWidget.PactEntry pact;

    public PactsTab(Minecraft minecraft, JujutsuScreen screen, JJKTabType type, int index, int page) {
        super(minecraft, screen, type, index, page, Items.CHAIN.getDefaultInstance(), TITLE, BACKGROUND);
    }

    private static float getMaxWidth(StringSplitter pManager, List<FormattedText> pText) {
        return (float)pText.stream().mapToDouble(pManager::stringWidth).max().orElse(0.0D);
    }

    private List<FormattedText> findOptimalLines(Component pComponent, int pMaxWidth) {
        StringSplitter stringsplitter = this.minecraft.font.getSplitter();
        List<FormattedText> list = null;
        float f = Float.MAX_VALUE;

        for(int i : TEST_SPLIT_OFFSETS) {
            List<FormattedText> list1 = stringsplitter.splitLines(pComponent, pMaxWidth - i, Style.EMPTY);
            float f1 = Math.abs(getMaxWidth(stringsplitter, list1) - (float)pMaxWidth);

            if (f1 <= 10.0F) {
                return list1;
            }
            if (f1 < f) {
                f = f1;
                list = list1;
            }
        }
        return list;
    }

    @Nullable
    public PlayerListWidget.PlayerEntry getSelectedPlayer() {
        return this.player;
    }

    public void setSelectedPlayer(PlayerListWidget.PlayerEntry entry) {
        this.player = entry;
    }

    public  void setSelectedPact(PactListWidget.PactEntry entry) {
        this.pact = entry;
        this.title = Language.getInstance().getVisualOrder(this.getFontRenderer().substrByWidth(entry.get().getName(), 163));
        int l = 29 + this.getFontRenderer().width(this.title);
        this.description = Language.getInstance().getVisualOrder(this.findOptimalLines(entry.get().getDescription().copy(), l));

        for (FormattedCharSequence formattedcharsequence : this.description) {
            l = Math.max(l, this.getFontRenderer().width(formattedcharsequence));
        }
        this.width = l + 3 + 5;
    }

    public <T extends ObjectSelectionList.Entry<T>> void buildPlayerList(Consumer<T> consumer, Function<PlayerInfo, T> result) {
        if (this.minecraft.player == null) return;

        ClientPacketListener connection = this.minecraft.getConnection();

        if (connection == null) return;

        for (PlayerInfo player : connection.getOnlinePlayers()) {
            if (player.getProfile().getId().equals(this.minecraft.player.getUUID())) continue;
            consumer.accept(result.apply(player));
        }
    }

    public <T extends ObjectSelectionList.Entry<T>> void buildPactList(Consumer<T> consumer, Function<Pact, T> result) {
        for (Pact pact : Pact.values()) {
            consumer.accept(result.apply(pact));
        }
    }

    @Override
    public void drawContents(GuiGraphics pGuiGraphics, int pX, int pY) {
        super.drawContents(pGuiGraphics, pX, pY);

        int i = (this.screen.width - JujutsuScreen.WINDOW_WIDTH) / 2;
        int j = (this.screen.height - JujutsuScreen.WINDOW_HEIGHT) / 2;

        int xOffset = i + (JujutsuScreen.WINDOW_WIDTH - JujutsuScreen.WINDOW_INSIDE_WIDTH);
        int yOffset = j + (JujutsuScreen.WINDOW_HEIGHT - JujutsuScreen.WINDOW_INSIDE_HEIGHT);

        pGuiGraphics.drawString(this.getFontRenderer(), Component.translatable(String.format("gui.%s.pacts.players", JujutsuKaisen.MOD_ID)),
                xOffset, yOffset, 16777215, true);
        pGuiGraphics.drawString(this.getFontRenderer(), Component.translatable(String.format("gui.%s.pacts.pacts", JujutsuKaisen.MOD_ID)),
                xOffset + 75, yOffset, 16777215, true);
    }

    @Override
    public void drawTooltips(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, int pWidth, int pHeight) {
        super.drawTooltips(pGuiGraphics, pMouseX, pMouseY, pWidth, pHeight);

        if (this.title != null && this.description != null && this.create.isMouseOver(pMouseX, pMouseY)) {
            boolean flag = pWidth + pMouseX + this.width + 26 >= this.screen.width;

            int j = this.width / 2;
            int k = this.width - j;

            int x;

            if (flag) {
                x = pMouseX - this.width + 26 + 6 + this.width - 26;
            } else {
                x = pMouseX + this.width - 32;
            }
            int y = pMouseY - 32 - this.description.size() * 9;

            int j1 = 32 + this.description.size() * 9;

            if (!this.description.isEmpty()) {
                pGuiGraphics.blitNineSliced(WIDGETS_LOCATION, x, y, this.width, j1, 10, 200, 26, 0, 52);
            }
            pGuiGraphics.blit(WIDGETS_LOCATION, x, y, 0, 0, j, 26);
            pGuiGraphics.blit(WIDGETS_LOCATION, x + j, y, 200 - k, 0, k, 26);

            if (flag) {
                pGuiGraphics.drawString(this.minecraft.font, this.title, x + 5, y + 9, -1);
            } else {
                pGuiGraphics.drawString(this.minecraft.font, this.title, pMouseX + 32, y + 9, -1);
            }

            for (int l1 = 0; l1 < this.description.size(); l1++) {
                pGuiGraphics.drawString(this.minecraft.font, this.description.get(l1), x + 5, y + 9 + 17 + l1 * 9, -5592406, false);
            }
        }
    }

    @Override
    public void addWidgets() {
        if (this.minecraft == null || this.minecraft.player == null) return;

        int i = (this.screen.width - JujutsuScreen.WINDOW_WIDTH) / 2;
        int j = (this.screen.height - JujutsuScreen.WINDOW_HEIGHT) / 2;

        int xOffset = i + (JujutsuScreen.WINDOW_WIDTH - JujutsuScreen.WINDOW_INSIDE_WIDTH);
        int yOffset = j + (JujutsuScreen.WINDOW_HEIGHT - JujutsuScreen.WINDOW_INSIDE_HEIGHT);

        this.addRenderableWidget(new PlayerListWidget(this::buildPlayerList, this::setSelectedPlayer, this.minecraft, 68, 85,
                xOffset, yOffset + this.getFontRenderer().lineHeight + 1));
        this.addRenderableWidget(new PactListWidget(this::buildPactList, this::setSelectedPact, this.minecraft, 68, 85,
                xOffset + 74, yOffset + this.getFontRenderer().lineHeight + 1, this));

        this.create = Button.builder(Component.translatable(String.format("gui.%s.pacts.create", JujutsuKaisen.MOD_ID)), pButton -> {
            if (this.player == null || this.pact == null) return;

            this.minecraft.player.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                this.minecraft.player.sendSystemMessage(Component.translatable(String.format("chat.%s.pact_request", JujutsuKaisen.MOD_ID), this.player.get().getProfile().getName()));
                PacketHandler.sendToServer(new QuestionCreatePactC2SPacket(this.player.get().getProfile().getId(), this.pact.get()));
                cap.createPactRequest(this.player.get().getProfile().getId(), this.pact.get());
            });
        }).size(60, 20).pos(xOffset + 152, yOffset + this.getFontRenderer().lineHeight + 66).build();
        this.addRenderableWidget(this.create);
    }
}
