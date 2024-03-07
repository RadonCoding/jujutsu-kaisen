package radon.jujutsu_kaisen.client.gui.screen.base;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.datafixers.types.templates.Sum;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.cursed_speech.base.CursedSpeech;
import radon.jujutsu_kaisen.ability.cursed_speech.base.ICursedSpeech;
import radon.jujutsu_kaisen.ability.idle_transfiguration.base.ITransfiguredSoul;
import radon.jujutsu_kaisen.ability.base.Summon;
import radon.jujutsu_kaisen.client.particle.ParticleColors;
import radon.jujutsu_kaisen.cursed_technique.JJKCursedTechniques;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.curse_manipulation.ICurseManipulationData;
import radon.jujutsu_kaisen.data.mimicry.IMimicryData;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.client.gui.screen.DisplayItem;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.c2s.*;
import radon.jujutsu_kaisen.ability.curse_manipulation.util.CurseManipulationUtil;
import radon.jujutsu_kaisen.util.HelperMethods;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public abstract class RadialScreen extends Screen {
    public static ResourceLocation HEALTH_BAR = new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/gui/overlay/health_bar.png");
    private static final float HEALTH_BAR_SCALE = 0.3F;

    protected static final int RADIUS_IN = 50;
    protected static final int RADIUS_OUT = RADIUS_IN * 2;

    protected static final int MAX_ITEMS = 12;

    private final List<List<DisplayItem>> pages = new ArrayList<>();

    protected int hovered = -1;
    protected static int page;
    private int hover;

    public RadialScreen() {
        super(Component.nullToEmpty(null));
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

        List<DisplayItem> items = this.getItems();

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

    public List<DisplayItem> getCurrent() {
        return this.pages.get(page);
    }

    protected abstract List<DisplayItem> getItems();

    private void drawSlot(PoseStack poseStack, BufferBuilder buffer, float centerX, float centerY, float startAngle, float endAngle, int color) {
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
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (this.minecraft == null || this.minecraft.player == null) return super.mouseClicked(pMouseX, pMouseY, pButton);

        IJujutsuCapability cap = this.minecraft.player.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return false;

        IMimicryData data = cap.getMimicryData();

        if (this.hovered >= 0 && this.hovered < this.getCurrent().size()) {
            DisplayItem item = this.getCurrent().get(this.hovered);

            if (pButton == InputConstants.MOUSE_BUTTON_RIGHT) {
                if (item.type == DisplayItem.Type.COPIED) {
                    PacketHandler.sendToServer(new UncopyAbilityC2SPacket(item.copied));
                    data.uncopy(item.copied);

                    this.init();
                }
            }
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    private static void renderEntityInInventoryFollowsAngle(PoseStack pPoseStack, int pX, int pY, int pScale, float angleXComponent, float angleYComponent, Entity pEntity) {
        Quaternionf quaternionf = (new Quaternionf()).rotateZ(Mth.PI);
        Quaternionf quaternionf1 = (new Quaternionf()).rotateX(angleYComponent * 20.0F * (Mth.PI / 180.0F));
        quaternionf.mul(quaternionf1);

        if (pEntity instanceof LivingEntity living) {
            living.yBodyRot = 180.0F + angleXComponent * 20.0F;
        }
        pEntity.setYRot(180.0F + angleXComponent * 40.0F);
        pEntity.setXRot(-angleYComponent * 20.0F);

        if (pEntity instanceof LivingEntity living) {
            living.yHeadRot = pEntity.getYRot();
            living.yHeadRotO = pEntity.getYRot();
        }
        renderEntityInInventory(pPoseStack, pX, pY, pScale, quaternionf, quaternionf1, pEntity);
    }

    private static void renderEntityInInventory(PoseStack pPoseStack, int pX, int pY, int pScale, Quaternionf p_275229_, @Nullable Quaternionf pCameraOrientation, Entity pEntity) {
        PoseStack posestack = RenderSystem.getModelViewStack();
        posestack.pushPose();
        posestack.translate(0.0D, 0.0D, 1000.0D);
        RenderSystem.applyModelViewMatrix();
        pPoseStack.pushPose();
        pPoseStack.translate(pX, pY, -950.0D);
        pPoseStack.mulPoseMatrix((new Matrix4f()).scaling((float) pScale, (float) pScale, (float) (-pScale)));
        pPoseStack.mulPose(p_275229_);
        Lighting.setupForEntityInInventory();
        EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();

        if (pCameraOrientation != null) {
            pCameraOrientation.conjugate();
            dispatcher.overrideCameraOrientation(pCameraOrientation);
        }
        dispatcher.setRenderShadow(false);
        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        RenderSystem.runAsFancy(() ->
                dispatcher.render(pEntity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, pPoseStack, buffer, 15728880));
        buffer.endBatch();
        dispatcher.setRenderShadow(true);
        pPoseStack.popPose();
        Lighting.setupFor3DItems();
        posestack.popPose();
        RenderSystem.applyModelViewMatrix();
    }

    protected boolean isActive(DisplayItem item) {
        if (this.minecraft == null || this.minecraft.player == null) return false;

        IJujutsuCapability cap = this.minecraft.player.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return false;

        IAbilityData abilityData = cap.getAbilityData();
        ICurseManipulationData curseManipulationData = cap.getCurseManipulationData();
        IMimicryData mimicryData = cap.getMimicryData();

        return item.type == DisplayItem.Type.ABILITY && abilityData.hasToggled(item.ability) ||
                item.type == DisplayItem.Type.COPIED && mimicryData.getCurrentCopied() == item.copied ||
                item.type == DisplayItem.Type.ABSORBED && curseManipulationData.getCurrentAbsorbed() == item.absorbed;
    }

    @Override
    public void render(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTicks) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTicks);

        if (this.minecraft == null || this.minecraft.level == null || this.minecraft.player == null) return;

        IJujutsuCapability cap = this.minecraft.player.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        ISorcererData data = cap.getSorcererData();

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
            float startAngle = this.getAngleFor(i - 0.5F);
            float endAngle = this.getAngleFor(i + 0.5F);

            DisplayItem item = this.getCurrent().get(i);
            int white = HelperMethods.toRGB24(255, 255, 255, 150);
            int black = HelperMethods.toRGB24(0, 0, 0, 150);

            int color;

            if (this.isActive(item)) {
                color = this.hovered == i ? black : white;
            } else {
                color = this.hovered == i ? white : black;
            }
            this.drawSlot(pGuiGraphics.pose(), buffer, centerX, centerY, startAngle, endAngle, color);
        }

        tesselator.end();
        RenderSystem.disableBlend();
        pGuiGraphics.pose().popPose();

        if (this.pages.size() > 1) {
            if (this.pages.size() - 1 > page) {
                String symbol = ">";

                int x = centerX + RADIUS_OUT + 20;
                int y = centerY - this.minecraft.font.lineHeight;

                pGuiGraphics.drawCenteredString(this.font, symbol, x, y, 16777215);
            }
            if (page > 0) {
                String symbol = "<";

                int x = centerX - RADIUS_OUT - 20;
                int y = centerY - this.minecraft.font.lineHeight;

                pGuiGraphics.drawCenteredString(this.font, symbol, x, y, 16777215);
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
                List<Component> lines = new ArrayList<>();

                if (item.type == DisplayItem.Type.ABILITY) {
                    float cost = item.ability.getRealCost(this.minecraft.player);

                    if (cost > 0.0F) {
                        lines.add(Component.translatable(String.format("gui.%s.ability_overlay.cost", JujutsuKaisen.MOD_ID), cost));
                    }

                    int cooldown = item.ability.getRealCooldown(this.minecraft.player);

                    if (cooldown > 0) {
                        lines.add(Component.translatable(String.format("gui.%s.ability_overlay.cooldown", JujutsuKaisen.MOD_ID), Math.round((float) cooldown / 20)));
                    }

                    if (item instanceof Ability.IDurationable durationable) {
                        int duration = durationable.getRealDuration(this.minecraft.player);

                        if (duration > 0) {
                            Component durationText = Component.translatable(String.format("gui.%s.ability_overlay.duration", JujutsuKaisen.MOD_ID), (float) duration / 20);
                            lines.add(durationText);
                        }
                    }

                    if (item instanceof ITransfiguredSoul soul) {
                        Component soulCostText = Component.translatable(String.format("gui.%s.ability_overlay.soul_cost", JujutsuKaisen.MOD_ID), soul.getSoulCost());
                        lines.add(soulCostText);
                    }

                    if (item instanceof ICursedSpeech speech) {
                        Component throatDamageText = Component.translatable(String.format("gui.%s.ability_overlay.throat_damage", JujutsuKaisen.MOD_ID), speech.getThroatDamage());
                        lines.add(throatDamageText);
                    }
                } else if (item.type == DisplayItem.Type.CURSE) {
                    Component costText = Component.translatable(String.format("gui.%s.ability_overlay.cost", JujutsuKaisen.MOD_ID),
                            CurseManipulationUtil.getCurseCost(item.curse.getKey()));
                    lines.add(costText);

                    Component experienceText = Component.translatable(String.format("gui.%s.ability_overlay.experience", JujutsuKaisen.MOD_ID),
                            CurseManipulationUtil.getCurseExperience(item.curse.getKey()));
                    lines.add(experienceText);
                } else if (item.type == DisplayItem.Type.ITEM) {
                    lines.add(item.item.getHoverName());
                }

                int x = this.width / 2;
                int y = this.height / 2 - this.font.lineHeight / 2 - ((lines.size() - 1) * this.font.lineHeight);

                for (Component line : lines) {
                    pGuiGraphics.drawCenteredString(this.font, line, x, y, 0xFFFFFF);
                    y += this.font.lineHeight;
                }
            }

            if ((item.ability instanceof Summon<?> summon && summon.display()) || item.type == DisplayItem.Type.CURSE) {
                Entity tmp = item.type == DisplayItem.Type.ABILITY ? ((Summon<?>) item.ability).getTypes().get(0).create(this.minecraft.level) :
                        CurseManipulationUtil.createCurse(this.minecraft.player, item.curse.getKey());

                if (tmp == null) continue;

                float height = tmp.getBbHeight();
                int scale = (int) Math.max(3.0F, 10.0F - tmp.getBbHeight());
                renderEntityInInventoryFollowsAngle(pGuiGraphics.pose(), posX, (int) (posY + (height * scale / 2.0F)), scale, -1.0F, -0.5F, tmp);

                if (item.ability instanceof Summon<?> summon) {
                    if (summon.getActivationType(this.minecraft.player) == Ability.ActivationType.TOGGLED) {
                        int y = Math.round((posY + (height * scale / 2.0F) + (this.font.lineHeight / 2.0F)) * (1.0F / HEALTH_BAR_SCALE));

                        for (Entity entity : data.getSummonsByClass(summon.getClazz())) {
                            if (!(entity instanceof LivingEntity living)) continue;

                            pGuiGraphics.pose().pushPose();
                            pGuiGraphics.pose().scale(HEALTH_BAR_SCALE, HEALTH_BAR_SCALE, HEALTH_BAR_SCALE);

                            RenderSystem.disableDepthTest();
                            RenderSystem.depthMask(false);
                            RenderSystem.defaultBlendFunc();
                            RenderSystem.setShader(GameRenderer::getPositionTexShader);

                            int x = Math.round(posX * (1.0F / HEALTH_BAR_SCALE) - 46.5F);

                            pGuiGraphics.blit(HEALTH_BAR, x, y, 0, 0, 93, 10, 93, 18);

                            float health = (living.getHealth() / living.getMaxHealth()) * 93.0F;
                            pGuiGraphics.blit(HEALTH_BAR, x, y + 1, 0, 10, (int) health, 8, 93, 18);

                            RenderSystem.depthMask(true);
                            RenderSystem.enableDepthTest();

                            pGuiGraphics.pose().popPose();

                            y += 10 + 2;
                        }
                    }
                }
            } else if (item.type == DisplayItem.Type.ABILITY) {
                int y = posY - this.font.lineHeight / 2;

                pGuiGraphics.pose().pushPose();
                pGuiGraphics.pose().scale(0.5F, 0.5F, 0.0F);
                pGuiGraphics.pose().translate(posX, y, 0.0F);
                pGuiGraphics.drawCenteredString(this.font, item.ability.getName(), posX, y, 0xFFFFFF);
                pGuiGraphics.pose().popPose();
            } else if (item.type == DisplayItem.Type.COPIED || item.type == DisplayItem.Type.ABSORBED) {
                int y = posY - this.font.lineHeight / 2;

                pGuiGraphics.pose().pushPose();
                pGuiGraphics.pose().scale(0.5F, 0.5F, 0.0F);
                pGuiGraphics.pose().translate(posX, y, 0.0F);
                pGuiGraphics.drawCenteredString(this.font, item.type == DisplayItem.Type.COPIED ? item.copied.getName() : item.absorbed.getName(), posX, y, 0xAA00AA);
                pGuiGraphics.pose().popPose();
            } else if (item.type == DisplayItem.Type.ITEM) {
                ItemStack stack = item.item;

                pGuiGraphics.pose().pushPose();
                pGuiGraphics.pose().translate(-8.0F, -8.0F, 0.0F);
                pGuiGraphics.renderFakeItem(stack, posX, posY);
                pGuiGraphics.renderItemDecorations(this.font, stack, posX, posY);
                pGuiGraphics.pose().popPose();
            }
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