package radon.jujutsu_kaisen.client.gui.screen.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.capability.data.sorcerer.Pact;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public class PactListWidget extends JJKSelectionList<Pact, PactListWidget.PactEntry> {
    public PactListWidget(IBuilder<Pact, PactEntry> parent, ICallback<PactEntry> callback, Minecraft minecraft, int width, int height, int x, int y) {
        super(parent, callback, minecraft, width, height, x, y);
    }

    @Override
    public void refreshList() {
        super.refreshList();

        this.builder.build(this::addEntry, PactEntry::new);
    }

    public class PactEntry extends ObjectSelectionList.Entry<PactEntry> {
        private final Pact pact;

        PactEntry(Pact pact) {
            this.pact = pact;
        }

        public Pact get() {
            return this.pact;
        }

        @Override
        public void render(@NotNull GuiGraphics pGuiGraphics, int pIndex, int pTop, int pLeft, int pWidth, int pHeight, int pMouseX, int pMouseY, boolean pHovering, float pPartialTick) {
            String text = this.pact.getName().getString();

            int delta = PactListWidget.this.minecraft.font.width(text) - pWidth;

            if (delta > 0) {
                text = String.format("%s...", text.substring(0, text.length() - 1 - delta));
            }
            pGuiGraphics.drawString(PactListWidget.this.minecraft.font, Language.getInstance()
                            .getVisualOrder(FormattedText.composite(Component.literal(text))),
                    pLeft + 3, pTop + 2, 0xFFFFFF, false);
        }

        @Override
        public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
            PactListWidget.this.callback.setSelected(this);
            PactListWidget.this.setSelected(this);
            return false;
        }

        @Override
        public @NotNull Component getNarration() {
            return this.pact.getName();
        }
    }
}
