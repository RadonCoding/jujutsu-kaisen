package radon.jujutsu_kaisen.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import radon.jujutsu_kaisen.capability.data.ten_shadows.ITenShadowsData;
import radon.jujutsu_kaisen.capability.data.ten_shadows.TenShadowsDataHandler;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.c2s.ShadowInventoryTakeC2SPacket;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.ArrayList;
import java.util.List;


public class ShadowInventoryScreen extends Screen {
    private static final int RADIUS_IN = 50;
    private static final int RADIUS_OUT = RADIUS_IN * 2;

    private final List<ItemStack> items = new ArrayList<>();

    private int hovered = -1;

    public ShadowInventoryScreen() {
        super(Component.nullToEmpty(null));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    protected void init() {
        super.init();

        if (this.minecraft == null) return;

        List<ItemStack> inventory = this.getItems();

        if (inventory == null) return;

        this.items.addAll(inventory);

        if (this.items.isEmpty()) {
            this.onClose();
        }
    }

    private @Nullable List<ItemStack> getItems() {
        if (this.minecraft == null || this.minecraft.player == null) return null;
        if (!this.minecraft.player.getCapability(TenShadowsDataHandler.INSTANCE).isPresent()) return null;
        ITenShadowsData cap = this.minecraft.player.getCapability(TenShadowsDataHandler.INSTANCE).resolve().orElseThrow();
        return cap.getShadowInventory();
    }

    private void drawSlot(PoseStack poseStack, BufferBuilder buffer, float centerX, float centerY,
                          float startAngle, float endAngle, int color) {
        float angle = endAngle - startAngle;
        float precision = 2.5F / 360.0F;
        int sections = Math.max(1, Mth.ceil(angle / precision));

        angle = endAngle - startAngle;

        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        int a = (color >> 24) & 0xFF;

        float slice = angle / sections;

        for (int i = 0; i < sections; i++) {
            float angle1 = startAngle + i * slice;
            float angle2 = startAngle + (i + 1) * slice;

            float x1 = centerX + RADIUS_IN * (float) Math.cos(angle1);
            float y1 = centerY + RADIUS_IN * (float) Math.sin(angle1);
            float x2 = centerX + RADIUS_OUT * (float) Math.cos(angle1);
            float y2 = centerY + RADIUS_OUT * (float) Math.sin(angle1);
            float x3 = centerX + RADIUS_OUT * (float) Math.cos(angle2);
            float y3 = centerY + RADIUS_OUT * (float) Math.sin(angle2);
            float x4 = centerX + RADIUS_IN * (float) Math.cos(angle2);
            float y4 = centerY + RADIUS_IN * (float) Math.sin(angle2);

            Matrix4f pose = poseStack.last().pose();
            buffer.vertex(pose, x2, y2, 0.0F).color(r, g, b, a).endVertex();
            buffer.vertex(pose, x1, y1, 0.0F).color(r, g, b, a).endVertex();
            buffer.vertex(pose, x4, y4, 0.0F).color(r, g, b, a).endVertex();
            buffer.vertex(pose, x3, y3, 0.0F).color(r, g, b, a).endVertex();
        }
    }

    @Override
    public void onClose() {
        if (this.hovered != -1) {
            PacketHandler.sendToServer(new ShadowInventoryTakeC2SPacket(this.hovered));
        }
        super.onClose();
    }

    public static void drawCenteredString(@NotNull GuiGraphics pGuiGraphics, Font pFont, Component pText, int pX, int pY, int pColor) {
        FormattedCharSequence sql = pText.getVisualOrderText();
        pGuiGraphics.drawString(pFont, sql, pX - pFont.width(sql) / 2, pY, pColor);
    }

    @Override
    public void render(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTicks) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTicks);

        if (this.minecraft == null || this.minecraft.level == null || this.minecraft.player == null) return;

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

        for (int i = 0; i < this.items.size(); i++) {
            float startAngle = getAngleFor(i - 0.5F);
            float endAngle = getAngleFor(i + 0.5F);

            int white = HelperMethods.toRGB24(255, 255, 255, 150);
            int black = HelperMethods.toRGB24(0, 0, 0, 150);

            this.drawSlot(pGuiGraphics.pose(), buffer, centerX, centerY, startAngle, endAngle, this.hovered == i ? white : black);
        }

        tesselator.end();
        RenderSystem.disableBlend();
        pGuiGraphics.pose().popPose();
        float radius = (RADIUS_IN + RADIUS_OUT) / 2.0F;

        for (int i = 0; i < this.items.size(); i++) {
            float start = getAngleFor(i - 0.5F);
            float end = getAngleFor(i + 0.5F);
            float middle = (start + end) / 2.0F;
            int posX = (int) (centerX + radius * (float) Math.cos(middle));
            int posY = (int) (centerY + radius * (float) Math.sin(middle));

            ItemStack stack = this.items.get(i);

            if (this.hovered == i) {
                int x = this.width / 2;
                int y = this.height / 2 - this.font.lineHeight / 2;
                drawCenteredString(pGuiGraphics, this.font, stack.getHoverName(), x, y, 0xFFFFFF);
            }

            pGuiGraphics.pose().pushPose();
            pGuiGraphics.pose().translate(-8.0F, -8.0F, 0.0F);
            pGuiGraphics.renderFakeItem(stack, posX, posY);
            pGuiGraphics.renderItemDecorations(this.font, stack, posX, posY);
            pGuiGraphics.pose().popPose();
        }
    }

    @Override
    public void mouseMoved(double pMouseX, double pMouseY) {
        super.mouseMoved(pMouseX, pMouseY);

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        double mouseAngle = Math.atan2(pMouseY - centerY, pMouseX - centerX);
        double mousePos = Math.sqrt(Math.pow(pMouseX - centerX, 2.0D) + Math.pow(pMouseY - centerY, 2.0D));

        if (this.items.size() > 0) {
            float startAngle = getAngleFor(-0.5F);
            float endAngle = getAngleFor(this.items.size() - 0.5F);

            while (mouseAngle < startAngle) {
                mouseAngle += Mth.TWO_PI;
            }
            while (mouseAngle >= endAngle) {
                mouseAngle -= Mth.TWO_PI;
            }
        }

        this.hovered = -1;

        for (int i = 0; i < this.items.size(); i++) {
            float startAngle = getAngleFor(i - 0.5F);
            float endAngle = getAngleFor(i + 0.5F);

            if (mouseAngle >= startAngle && mouseAngle < endAngle && mousePos >= RADIUS_IN && mousePos < RADIUS_OUT) {
                this.hovered = i;
                break;
            }
        }
    }

    private float getAngleFor(double i) {
        if (this.items.isEmpty()) {
            return 0;
        }
        return (float) (((i / this.items.size()) + 0.25) * Mth.TWO_PI + Math.PI);
    }
}