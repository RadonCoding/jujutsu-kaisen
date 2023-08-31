package radon.jujutsu_kaisen.client.gui.scren.base;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.client.ability.ClientAbilityHandler;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.c2s.TriggerAbilityC2SPacket;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.ArrayList;
import java.util.List;

public abstract class RadialScreen extends Screen {
    private static final float RADIUS_IN = 50.0F;
    private static final float RADIUS_OUT = RADIUS_IN * 2.0F;

    private final List<Ability> abilities = new ArrayList<>();

    private int hovered = -1;

    public RadialScreen() {
        super(Component.nullToEmpty(null));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    protected void init() {
        super.init();

        assert this.minecraft != null;
        this.abilities.addAll(this.getAbilities());

        if (this.abilities.isEmpty()) {
            this.onClose();
        }
    }

    protected abstract List<Ability> getAbilities();

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
            Ability ability = this.abilities.get(this.hovered);
            PacketHandler.sendToServer(new TriggerAbilityC2SPacket(JJKAbilities.getKey(ability)));
            ClientAbilityHandler.trigger(ability);
        }
        super.onClose();
    }

    public static void drawCenteredString(@NotNull PoseStack pPoseStack, Font pFont, Component pText, int pX, int pY, int pColor) {
        FormattedCharSequence sql = pText.getVisualOrderText();
        pFont.drawShadow(pPoseStack, sql, (float)(pX - pFont.width(sql) / 2), (float)pY, pColor);
    }

    @Override
    public void render(@NotNull PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTicks) {
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTicks);

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        pPoseStack.pushPose();

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        assert this.minecraft != null;
        assert this.minecraft.player != null;

        for (int i = 0; i < this.abilities.size(); i++) {
            float startAngle = getAngleFor(i - 0.5F);
            float endAngle = getAngleFor(i + 0.5F);

            Ability ability = this.abilities.get(i);
            int white = HelperMethods.toRGB24(255, 255, 255, 150);
            int black = HelperMethods.toRGB24(0, 0, 0, 150);

            int color;

            if (JJKAbilities.hasToggled(this.minecraft.player, ability)) {
                color = this.hovered == i ? black : white;
            }
            else {
                color = this.hovered == i ? white : black;
            }
            this.drawSlot(pPoseStack, buffer, centerX, centerY, startAngle, endAngle, color);
        }

        tesselator.end();
        RenderSystem.disableBlend();
        pPoseStack.popPose();
        float radius = (RADIUS_IN + RADIUS_OUT) / 2.0F;

        for (int i = 0; i < this.abilities.size(); i++) {
            float start = getAngleFor(i - 0.5F);
            float end = getAngleFor(i + 0.5F);
            float middle = (start + end) / 2.0F;
            int posX = (int) (centerX + radius * (float) Math.cos(middle));
            int posY = (int) (centerY + radius * (float) Math.sin(middle));

            Ability ability = this.abilities.get(i);

            if (this.hovered == i) {
                List<Component> lines = new ArrayList<>();

                float cost = ability.getRealCost(this.minecraft.player);

                if (cost > 0.0F) {
                    Component costText = Component.translatable(String.format("gui.%s.ability_overlay.cost", JujutsuKaisen.MOD_ID), cost);
                    lines.add(costText);
                }

                if (ability instanceof Ability.IDurationable durationable) {
                    int duration = durationable.getRealDuration(this.minecraft.player);

                    if (duration > 0) {
                        Component durationText = Component.translatable(String.format("gui.%s.ability_overlay.duration", JujutsuKaisen.MOD_ID), duration / 20);
                        lines.add(durationText);
                    }
                }


                int offset = 0;

                for (Component line : lines) {
                    if (this.font.width(line) > offset) {
                        offset = this.font.width(line);
                    }
                }

                int x = this.width / 2;
                int y = this.height / 2 - this.font.lineHeight / 2 - ((lines.size() - 1) * this.font.lineHeight);

                for (Component line : lines) {
                    drawCenteredString(pPoseStack, this.font, line, x, y, 0xFFFFFF);
                    y += this.font.lineHeight;
                }
            }

            int y = posY - this.font.lineHeight / 2;

            float scale = 0.5F;

            pPoseStack.pushPose();
            pPoseStack.scale(scale, scale, 0.0F);
            pPoseStack.translate(posX, y, 0.0F);

            drawCenteredString(pPoseStack, this.font, ability.getName(), posX, y, 0xFFFFFF);

            pPoseStack.popPose();
        }
    }

    @Override
    public void mouseMoved(double pMouseX, double pMouseY) {
        super.mouseMoved(pMouseX, pMouseY);

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        double mouseAngle = Math.atan2(pMouseY - centerY, pMouseX - centerX);
        double mousePos = Math.sqrt(Math.pow(pMouseX - centerX, 2.0D) + Math.pow(pMouseY - centerY, 2.0D));

        if (this.abilities.size() > 0) {
            float startAngle = getAngleFor(-0.5F);
            float endAngle = getAngleFor(this.abilities.size() - 0.5F);

            while (mouseAngle < startAngle) {
                mouseAngle += Mth.TWO_PI;
            }
            while (mouseAngle >= endAngle) {
                mouseAngle -= Mth.TWO_PI;
            }
        }

        this.hovered = -1;

        for (int i = 0; i < this.abilities.size(); i++) {
            float startAngle = getAngleFor(i - 0.5F);
            float endAngle = getAngleFor(i + 0.5F);

            if (mouseAngle >= startAngle && mouseAngle < endAngle && mousePos >= RADIUS_IN && mousePos < RADIUS_OUT) {
                this.hovered = i;
                break;
            }
        }
    }

    private float getAngleFor(double i)
    {
        if (this.abilities.size() == 0) {
            return 0;
        }
        return (float) (((i / this.abilities.size()) + 0.25) * Mth.TWO_PI + Math.PI);
    }
}