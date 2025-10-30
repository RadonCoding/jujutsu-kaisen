package radon.jujutsu_kaisen.client.gui.screen.radial;


import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.ArrayList;
import java.util.List;

public abstract class RadialScreen extends Screen {
    protected static final int RADIUS_IN = 50;
    protected static final int RADIUS_OUT = RADIUS_IN * 2;

    protected static final int MAX_ITEMS = 12;
    protected static int page;
    private final List<List<? extends DisplayItem>> pages = new ArrayList<>();
    private int hovered = -1;
    private int hover;

    public RadialScreen() {
        super(Component.nullToEmpty(null));
    }

    @Override
    public void onClose() {
        super.onClose();

        if (this.hovered == -1) return;

        List<? extends DisplayItem> items = this.pages.get(page);

        if (this.hovered >= items.size()) return;

        DisplayItem item = items.get(this.hovered);
        item.select();
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {

    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    protected void init() {
        super.init();

        this.pages.clear();

        List<? extends DisplayItem> items = this.getItems();

        int count = items.size() / MAX_ITEMS;

        for (int i = 0; i < count; i++) {
            int index = i * MAX_ITEMS;
            this.pages.add(items.subList(index, index + MAX_ITEMS));
        }

        int remainder = items.size() % MAX_ITEMS;

        if (remainder > 0) {
            int index = count * MAX_ITEMS;
            this.pages.add(items.subList(index, index + remainder));
        }
        if (page >= this.pages.size()) {
            page = 0;
        }
        if (this.pages.isEmpty()) {
            this.onClose();
        }
    }

    public List<? extends DisplayItem> getCurrent() {
        return this.pages.get(page);
    }

    protected abstract List<? extends DisplayItem> getItems();

    private void drawSlot(PoseStack poseStack, BufferBuilder buffer, float centerX, float centerY, float start, float end, int color) {
        float angle = end - start;
        float precision = 2.5F / 360.0F;
        int sections = Math.max(1, Mth.ceil(angle / precision));

        angle = end - start;

        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        int a = (color >> 24) & 0xFF;

        float slice = angle / sections;

        for (int i = 0; i < sections; i++) {
            float angle1 = start + i * slice;
            float angle2 = start + (i + 1) * slice;

            float x1 = centerX + RADIUS_IN * (float) Math.cos(angle1);
            float y1 = centerY + RADIUS_IN * (float) Math.sin(angle1);
            float x2 = centerX + RADIUS_OUT * (float) Math.cos(angle1);
            float y2 = centerY + RADIUS_OUT * (float) Math.sin(angle1);
            float x3 = centerX + RADIUS_OUT * (float) Math.cos(angle2);
            float y3 = centerY + RADIUS_OUT * (float) Math.sin(angle2);
            float x4 = centerX + RADIUS_IN * (float) Math.cos(angle2);
            float y4 = centerY + RADIUS_IN * (float) Math.sin(angle2);

            Matrix4f matrix4f = poseStack.last().pose();
            buffer.vertex(matrix4f, x2, y2, 0.0F).color(r, g, b, a).endVertex();
            buffer.vertex(matrix4f, x1, y1, 0.0F).color(r, g, b, a).endVertex();
            buffer.vertex(matrix4f, x4, y4, 0.0F).color(r, g, b, a).endVertex();
            buffer.vertex(matrix4f, x3, y3, 0.0F).color(r, g, b, a).endVertex();
        }
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (this.hovered >= 0 && this.hovered < this.getCurrent().size()) {
            DisplayItem item = this.getCurrent().get(this.hovered);
            item.mouseClicked(pButton);
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    protected boolean isActive(DisplayItem item) {
        return item.isActive();
    }

    @Override
    public void render(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTicks) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTicks);

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        pGuiGraphics.pose().pushPose();

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        for (int i = 0; i < this.getCurrent().size(); i++) {
            float start = this.getAngleFor(i - 0.5F);
            float end = this.getAngleFor(i + 0.5F);

            DisplayItem item = this.getCurrent().get(i);
            int white = HelperMethods.toRGB24(255, 255, 255, 150);
            int black = HelperMethods.toRGB24(0, 0, 0, 150);

            int color;

            if (this.isActive(item)) {
                color = this.hovered == i ? black : white;
            } else {
                color = this.hovered == i ? white : black;
            }
            this.drawSlot(pGuiGraphics.pose(), buffer, centerX, centerY, start, end, color);
        }

        tesselator.end();
        RenderSystem.disableBlend();
        pGuiGraphics.pose().popPose();

        if (this.pages.size() > 1) {
            if (this.pages.size() - 1 > page) {
                String symbol = ">";

                int x = centerX + RADIUS_OUT + 20;
                int y = centerY - this.font.lineHeight;

                pGuiGraphics.drawCenteredString(this.font, symbol, x, y, 0xFFFFFF);
            }
            if (page > 0) {
                String symbol = "<";

                int x = centerX - RADIUS_OUT - 20;
                int y = centerY - this.font.lineHeight;

                pGuiGraphics.drawCenteredString(this.font, symbol, x, y, 0xFFFFFF);
            }
        }

        float radius = (RADIUS_IN + RADIUS_OUT) / 2.0F;

        for (int i = 0; i < this.getCurrent().size(); i++) {
            float start = this.getAngleFor(i - 0.5F);
            float end = this.getAngleFor(i + 0.5F);
            float middle = (start + end) / 2.0F;
            int posX = (int) (centerX + radius * (float) Math.cos(middle));
            int posY = (int) (centerY + radius * (float) Math.sin(middle));

            DisplayItem item = this.getCurrent().get(i);

            if (this.hovered == i) {
                int x = this.width / 2;
                int y = this.height / 2 - this.font.lineHeight / 2;
                item.drawHover(pGuiGraphics, x, y);
            }
            item.draw(pGuiGraphics, posX, posY);
        }
    }

    @Override
    public void mouseMoved(double pMouseX, double pMouseY) {
        super.mouseMoved(pMouseX, pMouseY);

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        double mouseAngle = Math.atan2(pMouseY - centerY, pMouseX - centerX);
        double mousePos = Math.sqrt(Math.pow(pMouseX - centerX, 2.0D) + Math.pow(pMouseY - centerY, 2.0D));

        if (!this.getCurrent().isEmpty()) {
            float startAngle = this.getAngleFor(-0.5F);
            float endAngle = this.getAngleFor(this.getCurrent().size() - 0.5F);

            while (mouseAngle < startAngle) {
                mouseAngle += Mth.TWO_PI;
            }
            while (mouseAngle >= endAngle) {
                mouseAngle -= Mth.TWO_PI;
            }

            this.hovered = -1;

            for (int i = 0; i < this.getCurrent().size(); i++) {
                float currentStart = this.getAngleFor(i - 0.5F);
                float currentEnd = this.getAngleFor(i + 0.5F);

                if (mouseAngle >= currentStart && mouseAngle < currentEnd && mousePos >= RADIUS_IN && mousePos < RADIUS_OUT) {
                    this.hovered = i;
                    break;
                }
            }

            if (mousePos < RADIUS_OUT) return;

            if (this.pages.size() > 1) {
                if (this.pages.size() - 1 > page) {
                    if (pMouseX > (double) this.width / 2 && pMouseX < this.width && pMouseY > 0 && pMouseY < this.height) {
                        if (++this.hover == 20) {
                            page++;
                        }
                        if (this.hover == 3 * 20) {
                            this.hover = 0;
                        }
                        return;
                    }
                }
                if (page > 0) {
                    if (pMouseX > 0 && pMouseX < (double) this.width / 2 && pMouseY > 0 && pMouseY < this.height) {
                        if (++this.hover == 20) {
                            page--;
                        }
                        if (this.hover == 3 * 20) {
                            this.hover = 0;
                        }
                        return;
                    }
                }
                if (this.hover > 0) this.hover = 0;
            }
        }
    }

    private float getAngleFor(double i) {
        if (this.getCurrent().isEmpty()) {
            return 0;
        }
        return (float) (((i / this.getCurrent().size()) + 0.25D) * Mth.TWO_PI + Math.PI);
    }
}