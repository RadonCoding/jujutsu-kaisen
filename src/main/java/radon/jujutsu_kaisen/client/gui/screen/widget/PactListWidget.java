package radon.jujutsu_kaisen.client.gui.screen.widget;

import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.pact.Pact;

public class PactListWidget extends JJKSelectionList<Pact, PactListWidget.Entry> {
    public PactListWidget(IBuilder<Pact, Entry> builder, ICallback<Entry> callback, Minecraft minecraft, int width, int height, int x, int y) {
        super(builder, callback, minecraft, width, height, x, y);
    }

    @Override
    public void refreshList() {
        super.refreshList();

        this.builder.build(this::addEntry, PactListWidget.Entry::new);
    }

    public class Entry extends ObjectSelectionList.Entry<Entry> {
        private final Pact pact;

        Entry(Pact pact) {
            this.pact = pact;
        }

        public Pact get() {
            return this.pact;
        }

        @Override
        public void render(@NotNull GuiGraphics pGuiGraphics, int pIndex, int pTop, int pLeft, int pWidth, int pHeight, int pMouseX, int pMouseY, boolean pHovering, float pPartialTick) {
            FormattedText formatted = this.pact.getName();

            int delta = PactListWidget.this.minecraft.font.width(formatted) - pWidth;

            formatted = PactListWidget.this.minecraft.font.substrByWidth(formatted, pWidth);

            if (delta > 0) {
                String text = formatted.getString();
                formatted = FormattedText.of(String.format("%s...", text.substring(0, text.length() - 3)));
            }
            pGuiGraphics.drawString(PactListWidget.this.minecraft.font, Language.getInstance().getVisualOrder(FormattedText.composite(formatted)),
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
