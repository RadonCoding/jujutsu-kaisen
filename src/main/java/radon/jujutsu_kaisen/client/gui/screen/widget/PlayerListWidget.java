package radon.jujutsu_kaisen.client.gui.screen.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.capability.data.sorcerer.Pact;

public class PlayerListWidget extends JJKSelectionList<Player, PlayerListWidget.PlayerEntry> {
    public PlayerListWidget(IBuilder<Player, PlayerEntry> parent, ICallback<PlayerEntry> callback, Minecraft minecraft, int width, int height, int x, int y) {
        super(parent, callback, minecraft, width, height, x, y);
    }

    @Override
    public void refreshList() {
        super.refreshList();

        this.builder.build(this::addEntry, PlayerEntry::new);
    }

    public class PlayerEntry extends ObjectSelectionList.Entry<PlayerEntry> {
        private final Player player;

        PlayerEntry(Player player) {
            this.player = player;
        }

        public Player get() {
            return this.player;
        }

        @Override
        public void render(GuiGraphics pGuiGraphics, int pIndex, int pTop, int pLeft, int pWidth, int pHeight, int pMouseX, int pMouseY, boolean pHovering, float pPartialTick) {
            pGuiGraphics.drawString(PlayerListWidget.this.minecraft.font, Language.getInstance().getVisualOrder(FormattedText
                            .composite(PlayerListWidget.this.minecraft.font.substrByWidth(this.player.getName(), PlayerListWidget.this.width))),
                    pLeft + 3, pTop + 2, 0xFFFFFF, false);
        }

        @Override
        public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
            PlayerListWidget.this.callback.setSelected(this);
            PlayerListWidget.this.setSelected(this);
            return false;
        }

        @Override
        public @NotNull Component getNarration() {
            return this.player.getName();
        }
    }
}
