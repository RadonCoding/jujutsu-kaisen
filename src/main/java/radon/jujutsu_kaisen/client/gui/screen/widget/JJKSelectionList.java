package radon.jujutsu_kaisen.client.gui.screen.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class JJKSelectionList<T, E extends ObjectSelectionList.Entry<E>> extends ObjectSelectionList<E> {
    protected final PactListWidget.IBuilder<T, E> builder;
    protected final PactListWidget.ICallback<E> callback;

    public JJKSelectionList(PactListWidget.IBuilder<T, E> builder, PactListWidget.ICallback<E> callback, Minecraft minecraft, int width, int height, int x, int y) {
        super(minecraft, width, height, x, y, minecraft.font.lineHeight * 2 - 2);

        this.x0 = x;
        this.y0 = y;
        this.x1 = this.x0 + width;
        this.y1 = this.y0 + height;

        this.setRenderBackground(false);
        this.setRenderTopAndBottom(false);

        this.builder = builder;
        this.refreshList();

        this.callback = callback;
    }

    @Nullable
    public E getMouseOver(double pMouseX, double pMouseY) {
        int i = this.getRowWidth() / 2;
        int j = this.x0 + this.width / 2;
        int k = j - i;
        int l = j + i;
        int i1 = Mth.floor(pMouseY - (double) this.y0) - this.headerHeight + (int) this.getScrollAmount() - 4;
        int j1 = i1 / this.itemHeight;
        return pMouseX < (double) this.getScrollbarPosition() && pMouseX >= (double) k && pMouseX <= (double) l && j1 >= 0 && i1 >= 0 && j1 < this.getItemCount() ? this.children().get(j1) : null;
    }

    public void refreshList() {
        this.clearEntries();
    }

    @Override
    protected void renderItem(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick, int pIndex, int pLeft, int pTop, int pWidth, int pHeight) {
        var e = this.getEntry(pIndex);

        e.renderBack(pGuiGraphics, pIndex, pTop, pLeft, pWidth, pHeight, pMouseX, pMouseY, Objects.equals(this.getHovered(), e), pPartialTick);

        if (this.isSelectedItem(pIndex)) {
            pGuiGraphics.renderOutline(pLeft - 2, pTop - 2, pWidth, pHeight + 3, -1);
        }
        e.render(pGuiGraphics, pIndex, pTop, pLeft, pWidth, pHeight, pMouseX, pMouseY, Objects.equals(this.getHovered(), e), pPartialTick);
    }

    @Override
    protected void renderBackground(GuiGraphics pGuiGraphics) {
        pGuiGraphics.renderOutline(this.x0, this.y0, this.width, this.height, -16777216);
        pGuiGraphics.fill(this.x0, this.y0, this.x1, this.y1, -1072689136);
    }

    @Override
    protected int getRowTop(int pIndex) {
        return this.y0 + 2 - (int) this.getScrollAmount() + pIndex * this.itemHeight + this.headerHeight;
    }

    @Override
    protected int getScrollbarPosition() {
        return this.x1;
    }

    @Override
    public int getRowWidth() {
        return this.width;
    }

    public interface IBuilder<T, E extends ObjectSelectionList.Entry<E>> {
        void build(Consumer<E> consumer, Function<T, E> result);
    }

    public interface ICallback<E extends ObjectSelectionList.Entry<E>> {
        void setSelected(E entry);
    }
}
