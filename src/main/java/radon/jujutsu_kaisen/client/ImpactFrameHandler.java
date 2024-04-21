package radon.jujutsu_kaisen.client;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.event.TickEvent;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.effect.JJKPostEffects;
import radon.jujutsu_kaisen.client.effect.PostEffectHandler;
import radon.jujutsu_kaisen.util.HelperMethods;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ImpactFrameHandler {
    @Nullable
    private static ImpactFrame current;

    public static boolean isActive() {
        return current != null;
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.type != TickEvent.Type.CLIENT) return;
        if (event.phase == TickEvent.Phase.START) return;

        if (current == null) return;

        current.age++;

        if (current.age == current.duration) {
            current = null;
        }
    }

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (current == null) return;

        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_ENTITIES) {
            RenderTarget target = JJKPostEffects.IMPACT_FRAME.getCustomTarget();

            if (target == null) return;

            PostEffectHandler.bindFramebuffer(target);

            PoseStack stack = event.getPoseStack();

            stack.pushPose();

            Vec3 cam = event.getCamera().getPosition();
            stack.translate(current.pos.x - cam.x, current.pos.y - cam.y, current.pos.z - cam.z);

            float fraction = (float) Math.pow((current.age + event.getPartialTick()) / current.duration, 0.5F);

            RandomSource random = RandomSource.create(BlockPos.containing(current.pos).asLong());
            VertexConsumer consumer = PostEffectHandler.bufferSource().getBuffer(RenderType.lines());

            for (int i = 0; i < 64; i++) {
                float theta = (float) (random.nextDouble() * Math.PI * 2.0D);
                float phi = (float) (random.nextDouble() * Math.PI);

                float x = (float) Math.sin(phi) * (float) Math.cos(theta);
                float y = (float) Math.sin(phi) * (float) Math.sin(theta);
                float z = (float) Math.cos(phi);

                float x1 = Mth.lerp(event.getPartialTick(), current.xOld, x * (HelperMethods.RANDOM.nextFloat() + 0.5F) * 5.0F);
                float y1 = Mth.lerp(event.getPartialTick(), current.yOld, y * (HelperMethods.RANDOM.nextFloat() + 0.5F) * 5.0F);
                float z1 = Mth.lerp(event.getPartialTick(), current.zOld, z * (HelperMethods.RANDOM.nextFloat() + 0.5F) * 5.0F);

                current.xOld = x1;
                current.yOld = y1;
                current.zOld = z1;

                float x2 = current.radius * x1 * fraction;
                float y2 = current.radius * y1 * fraction;
                float z2 = current.radius * z1 * fraction;

                consumer.vertex(stack.last().pose(), x1, y1, z1)
                        .color(255, 255, 255, 255)
                        .normal(0.0F, 1.0F, 0.0F)
                        .endVertex();
                consumer.vertex(stack.last().pose(), x2, y2, z2)
                        .color(255, 255, 255, 255)
                        .normal(0.0F, 1.0F, 0.0F)
                        .endVertex();
            }

            stack.popPose();

            PostEffectHandler.bufferSource().endBatch();

            PostEffectHandler.unbindFramebuffer(target);
        }
    }

    public static void impact(Vec3 pos, int duration, float radius) {
        current = new ImpactFrame(pos, duration, radius);
    }

    public static class ImpactFrame {
        public Vec3 pos;
        public int duration;
        public float radius;
        public int age;

        public float xOld;
        public float yOld;
        public float zOld;

        public ImpactFrame(Vec3 pos, int duration, float radius) {
            this.pos = pos;
            this.duration = duration;
            this.radius = radius;
        }
    }
}
