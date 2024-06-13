package radon.jujutsu_kaisen.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.particle.SlicedEntityParticle;
import radon.jujutsu_kaisen.util.EntityUtil;

@EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class Test {
    @Nullable
    private static Vec3 start;

    @SubscribeEvent
    public static void onMouseButton(InputEvent.MouseButton.Pre event) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.level == null || mc.player == null) return;

        if (!mc.player.getMainHandItem().isEmpty()) return;

        if (event.getAction() != InputConstants.PRESS) return;

        if (event.getButton() == InputConstants.MOUSE_BUTTON_RIGHT) {
            if (start == null) {
                start = mc.player.getLookAngle();
            } else {
                Vec3 look = mc.player.getLookAngle();

                Vec3 plane = start.cross(look);

                Vec3 eyes = mc.player.getEyePosition();

                for (float i = 0; i <= 1; i += 0.1F) {
                    Vec3 direction = new Vec3(look.x + (start.x - look.x) * i, look.y + (start.y - look.y) * i, look.z + (start.z - look.z) * i)
                            .normalize();
                    Vec3 end = eyes.add(direction.scale(16.0D));

                    for (LivingEntity entity : EntityUtil.getEntities(LivingEntity.class, mc.level, mc.player,
                            new AABB(eyes.x, eyes.y, eyes.z, end.x, end.y, end.z))) {
                        float distance = -(float) plane.dot(eyes.subtract(entity.position()));

                        mc.level.addParticle(new SlicedEntityParticle.Options(entity.getId(), plane.toVector3f(), distance, 1.0F),
                                entity.getX(), entity.getY(), entity.getZ(), 0.0D, 0.0D, 0.0D);
                    }
                }

                start = null;
            }
        }
    }

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null) return;

        if (start == null) return;

        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_LEVEL) {
            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder builder = tesselator.getBuilder();

            RenderSystem.setShader(GameRenderer::getRendertypeLinesShader);

            RenderSystem.depthMask(false);
            RenderSystem.disableCull();

            RenderSystem.lineWidth(4.0F);

            builder.begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_COLOR_NORMAL);

            PoseStack poseStack = new PoseStack();

            poseStack.mulPose(Axis.XP.rotationDegrees(mc.player.getXRot()));
            poseStack.mulPose(Axis.YP.rotationDegrees(mc.player.getYRot() + 180.0F));

            Matrix4f matrix4f = poseStack.last().pose();

            Vec3 look = mc.player.getLookAngle();

            builder.vertex(matrix4f, (float) start.x, (float) start.y, (float) start.z)
                    .color(255, 255, 255, 255)
                    .normal(1.0F, 1.0F, 1.0F)
                    .endVertex();
            builder.vertex(matrix4f, (float) look.x, (float) look.y, (float) look.z)
                    .color(255, 255, 255, 255)
                    .normal(1.0F, 1.0F, 1.0F)
                    .endVertex();

            tesselator.end();

            RenderSystem.lineWidth(1.0F);

            RenderSystem.enableCull();
            RenderSystem.depthMask(true);
        }
    }
}
