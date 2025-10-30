package radon.jujutsu_kaisen.client.render.entity.effect;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import radon.jujutsu_kaisen.ability.shrine.Spiderweb;
import radon.jujutsu_kaisen.client.JJKRenderTypes;
import radon.jujutsu_kaisen.entity.effect.SpiderwebEntity;

public class SpiderwebRenderer extends EntityRenderer<SpiderwebEntity> {

    private static final float PULL = 0.05F;
    private static final float CORE = 0.1F;
    private static final float SEGMENT_THICKNESS = 0.015F;
    private static final float CHECK_STEP = 0.25F; // New: Check every 0.25 blocks for support

    public SpiderwebRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public void render(@NotNull SpiderwebEntity pEntity, float pEntityYaw, float pPartialTick, @NotNull PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight) {
        pPoseStack.pushPose();

        float yaw = Mth.lerp(pPartialTick, pEntity.yRotO, pEntity.getYRot());
        float pitch = Mth.lerp(pPartialTick, pEntity.xRotO, pEntity.getXRot());

        float radius = pEntity.getScaledRadius();

        pPoseStack.translate(0.0F, radius, 0.0F);

        Vec3 look = pEntity.getLookAngle();
        Vec3 direction = look.scale(radius);
        pPoseStack.translate(direction.x, direction.y, direction.z);

        pPoseStack.mulPose(Axis.YP.rotationDegrees(yaw));
        pPoseStack.mulPose(Axis.XP.rotationDegrees(pitch));

        float alpha = (float) Math.min(Spiderweb.MAX_CHARGE, pEntity.getTime()) / Spiderweb.MAX_CHARGE;

        int rings = (int) radius;
        int spokes = (int) (radius * 3.0F);
        float core = radius * CORE;

        RenderType type = JJKRenderTypes.spiderweb();
        VertexConsumer consumer = pBuffer.getBuffer(type);
        Matrix4f matrix = pPoseStack.last().pose();

        for (int i = 0; i < spokes; i++) {
            float angle = (float) (i * 2.0D * Math.PI / spokes);
            float x = Mth.cos(angle);
            float y = Mth.sin(angle);
            float x1 = x * core;
            float y1 = y * core;
            float x2 = x * radius;
            float y2 = y * radius;

            drawLine(pEntity, consumer, matrix, yaw, pitch, x1, y1, x2, y2, alpha);
        }

        int total = rings + 1;
        int segments = spokes * 2;
        float step = (float) (2.0D * Math.PI / segments);

        for (int i = 0; i < total; i++) {
            float current;

            if (i == 0) {
                current = core;
            } else {
                float progress = (float) i / rings;
                current = core + (radius - core) * progress;
            }

            float pull = (i == 0) ? 0.0F : PULL;

            for (int j = 0; j < segments; j++) {
                float a1 = j * step;
                float a2 = (j + 1) * step;
                float ridge1 = Mth.abs(Mth.sin(a1 * spokes * 0.5F));
                float ridge2 = Mth.abs(Mth.sin(a2 * spokes * 0.5F));
                float r1 = current - (current * pull * ridge1);
                float r2 = current - (current * pull * ridge2);
                float x1 = Mth.cos(a1) * r1;
                float y1 = Mth.sin(a1) * r1;
                float x2 = Mth.cos(a2) * r2;
                float y2 = Mth.sin(a2) * r2;

                drawLine(pEntity, consumer, matrix, yaw, pitch, x1, y1, x2, y2, alpha);
            }
        }

        pPoseStack.popPose();
    }

    private static void drawLine(SpiderwebEntity entity, VertexConsumer consumer, Matrix4f matrix,
                                 float yaw, float pitch, float xStart, float yStart,
                                 float xEnd, float yEnd, float alpha) {
        float dx = xEnd - xStart;
        float dy = yEnd - yStart;
        float length = Mth.sqrt(dx * dx + dy * dy);

        if (length < CHECK_STEP) {
            float xCheck = xStart + dx / 2;
            float yCheck = yStart + dy / 2;

            if (isValid(entity, yaw, pitch, xCheck, yCheck)) {
                drawQuad(consumer, matrix, xStart, yStart, xEnd, yEnd, alpha);
            }
            return;
        }

        float nx = dx / length;
        float ny = dy / length;

        int segments = Mth.ceil(length / CHECK_STEP);
        float step = length / segments;

        for (int i = 0; i < segments; i++) {
            float current = i * step;
            float next = (i + 1) * step;

            float subX1 = xStart + nx * current;
            float subY1 = yStart + ny * current;
            float subX2 = xStart + nx * next;
            float subY2 = yStart + ny * next;

            float xCheck = (subX1 + subX2) / 2.0F;
            float yCheck = (subY1 + subY2) / 2.0F;

            if (isValid(entity, yaw, pitch, xCheck, yCheck)) {
                drawQuad(consumer, matrix, subX1, subY1, subX2, subY2, alpha);
            } else {
                break;
            }
        }
    }

    private static boolean isValid(SpiderwebEntity entity, float yaw, float pitch, float localX, float localY) {
        Level level = entity.level();

        Vec3 local = new Vec3(localX, localY, 0.0D);

        Quaternionf rotation = new Quaternionf();
        rotation.mul(Axis.YP.rotationDegrees(yaw));
        rotation.mul(Axis.XP.rotationDegrees(pitch));

        Vector3f rotated = local.toVector3f().rotate(rotation);

        float radius = entity.getScaledRadius();
        Vec3 look = entity.getLookAngle();
        Vec3 direction = look.scale(radius);

        Vec3 world = entity.position()
                .add(0.0D, radius, 0.0D)
                .add(direction.x, direction.y, direction.z)
                .add(rotated.x(), rotated.y(), rotated.z());

        BlockPos above = BlockPos.containing(world.add(look.scale(0.05D)));

        if (level.getBlockState(above).isSolidRender(level, above)) return false;

        BlockPos below = BlockPos.containing(world.subtract(look.scale(0.05D)));

        return !level.getBlockState(below).getCollisionShape(level, below).isEmpty();
    }

    private static void drawQuad(VertexConsumer consumer, Matrix4f matrix4f, float x1, float y1, float x2, float y2, float alpha) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        float length = Mth.sqrt(dx * dx + dy * dy);
        float nx = -dy / length * SEGMENT_THICKNESS / 2.0F;
        float ny = dx / length * SEGMENT_THICKNESS / 2.0F;

        float v1x = x1 + nx;
        float v1y = y1 + ny;
        float v2x = x2 + nx;
        float v2y = y2 + ny;
        float v3x = x2 - nx;
        float v3y = y2 - ny;
        float v4x = x1 - nx;
        float v4y = y1 - ny;

        consumer.vertex(matrix4f, v1x, v1y, 0.0F).color(1.0F, 1.0F, 1.0F, alpha).endVertex();
        consumer.vertex(matrix4f, v2x, v2y, 0.0F).color(1.0F, 1.0F, 1.0F, alpha).endVertex();
        consumer.vertex(matrix4f, v3x, v3y, 0.0F).color(1.0F, 1.0F, 1.0F, alpha).endVertex();

        consumer.vertex(matrix4f, v3x, v3y, 0.0F).color(1.0F, 1.0F, 1.0F, alpha).endVertex();
        consumer.vertex(matrix4f, v4x, v4y, 0.0F).color(1.0F, 1.0F, 1.0F, alpha).endVertex();
        consumer.vertex(matrix4f, v1x, v1y, 0.0F).color(1.0F, 1.0F, 1.0F, alpha).endVertex();
    }

    @Override
    protected int getBlockLightLevel(@NotNull SpiderwebEntity pEntity, @NotNull BlockPos pPos) {
        return 15;
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull SpiderwebEntity pEntity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}