package radon.jujutsu_kaisen.client.gui.screen.widget;

public enum AbilityWidgetType {
    OBTAINED(0),
    UNOBTAINED(1);

    private final int y;

    AbilityWidgetType(int pY) {
        this.y = pY;
    }

    public int getIndex() {
        return this.y;
    }
}