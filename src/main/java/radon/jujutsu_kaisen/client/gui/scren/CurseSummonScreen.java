package radon.jujutsu_kaisen.client.gui.scren;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.c2s.CurseSummonC2SPacket;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class CurseSummonScreen extends Screen {
    private static final float RADIUS_IN = 50.0F;
    private static final float RADIUS_OUT = RADIUS_IN * 2.0F;

    private final Map<EntityType<?>, Integer> curses = new HashMap<>();

    private int hovered = -1;

    public CurseSummonScreen() {
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

        Map<EntityType<?>, Integer> curses = this.getCurses();

        if (curses == null) return;

        this.curses.putAll(curses);

        if (this.curses.isEmpty()) {
            this.onClose();
        }
    }

    private @Nullable Map<EntityType<?>, Integer> getCurses() {
        AtomicReference<Map<EntityType<?>, Integer>> summons = new AtomicReference<>();

        if (this.minecraft == null || this.minecraft.level == null || this.minecraft.player == null) return null;

        this.minecraft.player.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap ->
                summons.set(cap.getCurses(this.minecraft.level.registryAccess().registryOrThrow(Registries.ENTITY_TYPE))));
        return summons.get();
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
            if (this.minecraft != null && this.minecraft.level != null) {
                EntityType<?> type = (EntityType<?>) this.curses.keySet().toArray()[this.hovered];
                Registry<EntityType<?>> registry = this.minecraft.level.registryAccess().registryOrThrow(Registries.ENTITY_TYPE);
                PacketHandler.sendToServer(new CurseSummonC2SPacket(registry.getKey(type)));
            }
        }
        super.onClose();
    }

    @Override
    public void render(@NotNull PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTicks) {
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTicks);

        if (this.minecraft == null || this.minecraft.level == null || this.minecraft.player == null) return;

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

        for (int i = 0; i < this.curses.size(); i++) {
            float startAngle = getAngleFor(i - 0.5F);
            float endAngle = getAngleFor(i + 0.5F);

            int white = HelperMethods.toRGB24(255, 255, 255, 150);
            int black = HelperMethods.toRGB24(0, 0, 0, 150);

            this.drawSlot(pPoseStack, buffer, centerX, centerY, startAngle, endAngle, this.hovered == i ? white : black);
        }

        tesselator.end();
        RenderSystem.disableBlend();
        pPoseStack.popPose();
        float radius = (RADIUS_IN + RADIUS_OUT) / 2.0F;

        int i = 0;

        for (Map.Entry<EntityType<?>, Integer> entry : this.curses.entrySet()) {
            float start = getAngleFor(i - 0.5F);
            float end = getAngleFor(i + 0.5F);
            float middle = (start + end) / 2.0F;
            int posX = (int) (centerX + radius * (float) Math.cos(middle));
            int posY = (int) (centerY + radius * (float) Math.sin(middle));

            EntityType<?> type = entry.getKey();

            if (!(type.create(this.minecraft.level) instanceof LivingEntity entity)) continue;

            if (this.hovered == i) {
                List<Component> lines = new ArrayList<>();

                Component countText = Component.translatable(String.format("gui.%s.curse_summon_overlay.count", JujutsuKaisen.MOD_ID), entry.getValue());
                lines.add(countText);

                int x = this.width / 2;
                int y = this.height / 2 - this.font.lineHeight / 2 - ((lines.size() - 1) * this.font.lineHeight);

                for (Component line : lines) {
                    drawCenteredString(pPoseStack, this.font, line, x, y, 0xFFFFFF);
                    y += this.font.lineHeight;
                }
            }

            float height = entity.getBbHeight();
            int scale = (int) Math.max(1.0F, 10.0F - entity.getBbHeight());
            InventoryScreen.renderEntityInInventoryFollowsAngle(pPoseStack, posX, (int) (posY + (height * scale / 2.0F)), scale, -1.0F,
                    0.0F, entity);

            i++;
        }
    }

    @Override
    public void mouseMoved(double pMouseX, double pMouseY) {
        super.mouseMoved(pMouseX, pMouseY);

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        double mouseAngle = Math.atan2(pMouseY - centerY, pMouseX - centerX);
        double mousePos = Math.sqrt(Math.pow(pMouseX - centerX, 2.0D) + Math.pow(pMouseY - centerY, 2.0D));

        if (this.curses.size() > 0) {
            float startAngle = getAngleFor(-0.5F);
            float endAngle = getAngleFor(this.curses.size() - 0.5F);

            while (mouseAngle < startAngle) {
                mouseAngle += Mth.TWO_PI;
            }
            while (mouseAngle >= endAngle) {
                mouseAngle -= Mth.TWO_PI;
            }
        }

        this.hovered = -1;

        for (int i = 0; i < this.curses.size(); i++) {
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
        if (this.curses.size() == 0) {
            return 0;
        }
        return (float) (((i / this.curses.size()) + 0.25) * Mth.TWO_PI + Math.PI);
    }
}