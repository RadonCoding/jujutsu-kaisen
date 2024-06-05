package radon.jujutsu_kaisen.client.gui.screen.widget;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.binding_vow.BindingVow;

public class BindingVowListWidget extends JJKSelectionList<BindingVow, BindingVowListWidget.Entry> {
    public BindingVowListWidget(IBuilder<BindingVow, Entry> builder, ICallback<Entry> callback, Minecraft minecraft, int width, int height, int x, int y) {
        super(builder, callback, minecraft, width, height, x, y);
    }

    @Override
    public void refreshList() {
        super.refreshList();

        this.builder.build(this::addEntry, BindingVowListWidget.Entry::new);
    }

    public class Entry extends ObjectSelectionList.Entry<Entry> {
        private final BindingVow vow;

        Entry(BindingVow pact) {
            this.vow = pact;
        }

        public BindingVow get() {
            return this.vow;
        }

        @Override
        public void render(@NotNull GuiGraphics pGuiGraphics, int pIndex, int pTop, int pLeft, int pWidth, int pHeight, int pMouseX, int pMouseY, boolean pHovering, float pPartialTick) {
            FormattedText formatted = FormattedText.of(this.vow.getName().getString());

            int delta = BindingVowListWidget.this.minecraft.font.width(formatted) - pWidth;

            formatted = BindingVowListWidget.this.minecraft.font.substrByWidth(formatted, pWidth);

            if (delta > 0) {
                String text = formatted.getString();
                formatted = FormattedText.of(String.format("%s...", text.substring(0, text.length() - 3)));
            }
            pGuiGraphics.drawString(BindingVowListWidget.this.minecraft.font, Language.getInstance()
                            .getVisualOrder(FormattedText.composite(formatted)),
                    pLeft + 3, pTop + 2, 0xFFFFFF, false);
        }

        @Override
        public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
            BindingVowListWidget.this.callback.setSelected(this);
            BindingVowListWidget.this.setSelected(this);
            return false;
        }

        @Override
        public @NotNull Component getNarration() {
            return this.vow.getName();
        }
    }
}
