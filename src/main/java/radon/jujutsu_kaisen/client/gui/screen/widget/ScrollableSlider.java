package radon.jujutsu_kaisen.client.gui.screen.widget;

import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.client.gui.widget.ExtendedSlider;

public class ScrollableSlider extends ExtendedSlider {
    public ScrollableSlider(int x, int y, int width, int height, Component prefix, Component suffix, double minValue, double maxValue, double currentValue, double stepSize, int precision, boolean drawString) {
        super(x, y, width, height, prefix, suffix, minValue, maxValue, currentValue, stepSize, precision, drawString);
    }

    public ScrollableSlider(int x, int y, int width, int height, Component prefix, Component suffix, double minValue, double maxValue, double currentValue, boolean drawString) {
        this(x, y, width, height, prefix, suffix, minValue, maxValue, currentValue, 1.0D, 0, drawString);
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pScrollX, double pScrollY) {
        this.setValueFromScroll(pScrollY * this.stepSize);

        return super.mouseScrolled(pMouseX, pMouseY, pScrollX, pScrollY);
    }


    @Override
    public void onClick(double mouseX, double mouseY) {
        super.onClick(mouseX, mouseY);

        this.setValueFromMouse(mouseX, mouseY);
    }

    @Override
    protected void onDrag(double mouseX, double mouseY, double dragX, double dragY) {
        super.onDrag(mouseX, mouseY, dragX, dragY);

        this.setValueFromMouse(mouseX, mouseY);
    }


    protected void setValueFromScroll(double scroll) {
        this.setSliderValue(this.value + scroll);
    }

    protected void setValueFromMouse(double mouseX, double mouseY) {
        this.setSliderValue((Math.abs(mouseX) - (this.getX() + 4)) / (this.width - 8));
    }

    protected void setSliderValue(double value) {
        double oldValue = this.value;
        this.value = this.snapToNearest(value);

        if (!Mth.equal(oldValue, this.value)) this.applyValue();

        this.updateMessage();
    }

    protected double snapToNearest(double value) {
        if (this.stepSize <= 0.0D) return Mth.clamp(value, 0.0D, 1.0D);

        value = Mth.lerp(Mth.clamp(value, 0.0D, 1.0D), this.minValue, this.maxValue);
        value = (this.stepSize * Math.round(value / this.stepSize));

        if (this.minValue > this.maxValue) {
            value = Mth.clamp(value, this.maxValue, this.minValue);
        } else {
            value = Mth.clamp(value, this.minValue, this.maxValue);
        }
        return Mth.map(value, this.minValue, this.maxValue, 0.0D, 1.0D);
    }
}