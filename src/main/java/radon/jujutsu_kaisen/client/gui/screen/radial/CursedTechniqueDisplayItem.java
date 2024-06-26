package radon.jujutsu_kaisen.client.gui.screen.radial;


import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

public abstract class CursedTechniqueDisplayItem extends DisplayItem {
    private final CursedTechnique technique;

    public CursedTechniqueDisplayItem(Minecraft minecraft, RadialScreen screen, Runnable select, CursedTechnique technique) {
        super(minecraft, screen, select);

        this.technique = technique;
    }

    public CursedTechnique getTechnique() {
        return this.technique;
    }

    @Override
    public void drawHover(GuiGraphics graphics, int x, int y) {

    }

    @Override
    public void draw(GuiGraphics graphics, int x, int y) {
        graphics.pose().pushPose();
        graphics.pose().scale(0.5F, 0.5F, 0.0F);
        graphics.pose().translate(x, y - (float) this.minecraft.font.lineHeight / 2, 0.0F);
        graphics.drawCenteredString(this.minecraft.font, this.technique.getName(), x, y - this.minecraft.font.lineHeight / 2, 0xAA00AA);
        graphics.pose().popPose();
    }
}
