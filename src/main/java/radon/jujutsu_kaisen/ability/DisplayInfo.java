package radon.jujutsu_kaisen.ability;


import net.minecraft.resources.ResourceLocation;

public class DisplayInfo {
    private final ResourceLocation icon;
    private final float x;
    private final float y;

    public DisplayInfo(ResourceLocation icon, float x, float y) {
        this.icon = icon;
        this.x = x;
        this.y = y;
    }

    public ResourceLocation getIcon() {
        return this.icon;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }
}