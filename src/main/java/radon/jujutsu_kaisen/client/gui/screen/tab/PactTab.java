package radon.jujutsu_kaisen.client.gui.screen.tab;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.registries.DeferredHolder;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.data.contract.IContractData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.client.gui.screen.JujutsuScreen;
import radon.jujutsu_kaisen.client.gui.screen.widget.PactListWidget;
import radon.jujutsu_kaisen.client.gui.screen.widget.PlayerListWidget;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.c2s.QuestionCreatePactC2SPacket;
import radon.jujutsu_kaisen.network.packet.c2s.QuestionRemovePactC2SPacket;
import radon.jujutsu_kaisen.pact.JJKPacts;
import radon.jujutsu_kaisen.pact.Pact;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Function;

public class PactTab extends JJKTab {
    private static final Component TITLE = Component.translatable(String.format("gui.%s.pact", JujutsuKaisen.MOD_ID));

    private Button create;
    private Button remove;
    private MultiLineTextWidget description;

    @Nullable
    private PlayerListWidget.Entry player;
    @Nullable
    private PactListWidget.Entry pact;

    public PactTab(Minecraft minecraft, JujutsuScreen screen, JJKTabType type, int index, int page) {
        super(minecraft, screen, type, index, page, Items.WRITABLE_BOOK.getDefaultInstance(), TITLE, false);
    }

    public void setSelectedPlayer(PlayerListWidget.Entry entry) {
        this.player = entry;
    }

    public void setSelectedPact(PactListWidget.Entry entry) {
        this.pact = entry;
        this.description.setMessage(entry.get().getDescription());
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
        for (DeferredHolder<Pact, ? extends Pact> entry : JJKPacts.PACTS.getEntries()) {
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

        pGuiGraphics.drawString(this.minecraft.font, Component.translatable(String.format("gui.%s.pact.players", JujutsuKaisen.MOD_ID)),
                xOffset, yOffset, 16777215, true);
        pGuiGraphics.drawString(this.minecraft.font, Component.translatable(String.format("gui.%s.pact.pacts", JujutsuKaisen.MOD_ID)),
                xOffset + 64, yOffset, 16777215, true);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.minecraft == null || this.minecraft.player == null) return;

        this.create.active = false;
        this.remove.active = false;

        if (this.player == null || this.pact == null) return;

        IJujutsuCapability cap = this.minecraft.player.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        IContractData data = cap.getContractData();

        this.create.active = !data.hasPact(this.player.get().getProfile().getId(), this.pact.get());
        this.remove.active = data.hasPact(this.player.get().getProfile().getId(), this.pact.get());
    }

    @Override
    public void addWidgets() {
        if (this.minecraft == null || this.minecraft.player == null) return;

        int i = (this.screen.width - JujutsuScreen.WINDOW_WIDTH) / 2;
        int j = (this.screen.height - JujutsuScreen.WINDOW_HEIGHT) / 2;

        int xOffset = i + (JujutsuScreen.WINDOW_WIDTH - JujutsuScreen.WINDOW_INSIDE_WIDTH);
        int yOffset = j + (JujutsuScreen.WINDOW_HEIGHT - JujutsuScreen.WINDOW_INSIDE_HEIGHT);

        this.addRenderableWidget(new PlayerListWidget(this::buildPlayerList, this::setSelectedPlayer, this.minecraft, 56, 85,
                xOffset, yOffset + this.minecraft.font.lineHeight + 1));
        this.addRenderableWidget(new PactListWidget(this::buildPactList, this::setSelectedPact, this.minecraft, 56, 85,
                xOffset + 64, yOffset + this.minecraft.font.lineHeight + 1));

        this.create = Button.builder(Component.translatable(String.format("gui.%s.pact.create", JujutsuKaisen.MOD_ID)), pButton -> {
            if (this.player == null || this.pact == null) return;

            IJujutsuCapability cap = this.minecraft.player.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap == null) return;

            IContractData data = cap.getContractData();

            this.minecraft.player.sendSystemMessage(Component.translatable(String.format("chat.%s.pact_request_create", JujutsuKaisen.MOD_ID), this.player.get().getProfile().getName()));
            PacketHandler.sendToServer(new QuestionCreatePactC2SPacket(this.player.get().getProfile().getId(), this.pact.get()));
            data.createPactCreationRequest(this.player.get().getProfile().getId(), this.pact.get());
        }).size(40, 20).pos(xOffset + 128, yOffset + this.minecraft.font.lineHeight + 66).build();
        this.addRenderableWidget(this.create);

        this.remove = Button.builder(Component.translatable(String.format("gui.%s.pact.remove", JujutsuKaisen.MOD_ID)), pButton -> {
            if (this.player == null || this.pact == null) return;

            IJujutsuCapability cap = this.minecraft.player.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap == null) return;

            IContractData data = cap.getContractData();

            this.minecraft.player.sendSystemMessage(Component.translatable(String.format("chat.%s.pact_request_remove", JujutsuKaisen.MOD_ID), this.player.get().getProfile().getName()));
            PacketHandler.sendToServer(new QuestionRemovePactC2SPacket(this.player.get().getProfile().getId(), this.pact.get()));
            data.createPactRemovalRequest(this.player.get().getProfile().getId(), this.pact.get());
        }).size(40, 20).pos(xOffset + 176, yOffset + this.minecraft.font.lineHeight + 66).build();
        this.addRenderableWidget(this.remove);

        this.description = new MultiLineTextWidget(xOffset + 128, yOffset + this.minecraft.font.lineHeight + 1, Component.empty(), this.minecraft.font)
                .setMaxWidth(87);
        this.addRenderableWidget(this.description);
    }
}
