package radon.jujutsu_kaisen.client.gui.screen.radial;


import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

public class ItemStackDisplayItem extends DisplayItem {
    private final ItemStack stack;

    public ItemStackDisplayItem(Minecraft minecraft, RadialScreen screen, Runnable select, ItemStack stack) {
        super(minecraft, screen, select);

        this.stack = stack;
    }

    public ItemStack getStack() {
        return this.stack;
    }

    @Override
    public void drawHover(GuiGraphics graphics, int x, int y) {
        graphics.drawCenteredString(this.minecraft.font, this.stack.getHoverName(), x, y, 0xFFFFFF);
    }

    @Override
    public void draw(GuiGraphics graphics, int x, int y) {
        graphics.pose().pushPose();
        graphics.pose().translate(-8.0F, -8.0F, 0.0F);
        graphics.renderFakeItem(this.stack, x, y);
        graphics.renderItemDecorations(this.minecraft.font, this.stack, x, y);
        graphics.pose().popPose();
    }

    @Override
    public void mouseClicked(int button) {

    }

    @Override
    public boolean isActive() {
        return false;
    }
}
