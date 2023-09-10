package radon.jujutsu_kaisen.client.visual;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.client.particle.ParticleColors;
import radon.jujutsu_kaisen.client.particle.VaporParticle;

public class BlueFistsVisual {
    private static Vec3 rotateRoll(Vec3 pos, float roll) {
        float f = Mth.cos(roll);
        float f1 = Mth.sin(roll);
        double d0 = pos.x() * (double )f - pos.y() * (double) f1;
        double d1 = pos.y() * (double) f + pos.x() * (double) f1;
        double d2 = pos.z();
        return new Vec3(d0, d1, d2);
    }

    private static Vec3 transform3rdPersonRight(Vec3 pos, Vec3 angles, LivingEntity entity, float partialTicks) {
        return rotateRoll(pos, (float) -angles.z()).xRot((float) -angles.x()).yRot((float) -angles.y())
                .add(0.0586F * -6.0F, 1.02F - (entity.isShiftKeyDown() ? 0.3F : 0.0F), 0.0F)
                .yRot((-entity.yBodyRotO - (entity.yBodyRot - entity.yBodyRotO) * partialTicks) * (float) (Math.PI / 180.0D))
                .add(Mth.lerp(partialTicks, entity.xOld, entity.getX()),
                        Mth.lerp(partialTicks, entity.yOld, entity.getY()),
                        Mth.lerp(partialTicks, entity.zOld, entity.getZ()));
    }

    private static Vec3 transform3rdPersonLeft(Vec3 pos, Vec3 angles, LivingEntity entity, float partialTicks) {
        return rotateRoll(pos, (float) -angles.z()).xRot((float) -angles.x()).yRot((float) -angles.y())
                .add(0.0586F * 6.0F, 1.02F - (entity.isShiftKeyDown() ? 0.3F : 0.0F), 0.0F)
                .yRot((-entity.yBodyRotO - (entity.yBodyRot - entity.yBodyRotO) * partialTicks) * (float) (Math.PI / 180.0D))
                .add(Mth.lerp(partialTicks, entity.xOld, entity.getX()),
                        Mth.lerp(partialTicks, entity.yOld, entity.getY()),
                        Mth.lerp(partialTicks, entity.zOld, entity.getZ()));
    }

    public static void tick(ClientVisualHandler.VisualData data, LivingEntity entity) {
        if (data.toggled().contains(JJKAbilities.BLUE_FISTS.get())) {
            run(data, entity);
        }
    }

    private static void run(ClientVisualHandler.VisualData data, LivingEntity entity) {
        Minecraft mc = Minecraft.getInstance();
        EntityRenderDispatcher dispatcher = mc.getEntityRenderDispatcher();
        EntityRenderer<?> renderer = dispatcher.getRenderer(entity);

        if (renderer instanceof LivingEntityRenderer<?, ?> living && living.getModel() instanceof HumanoidModel<?> humanoid) {
            float scalar = 0.8F;

            Vec3 right = transform3rdPersonRight(new Vec3(0.0D, -0.5825D - entity.getBbHeight() * 0.5D, 0.0D),
                    new Vec3(humanoid.rightArm.xRot, humanoid.rightArm.yRot, humanoid.rightArm.zRot), entity, mc.getPartialTick())
                    .add(0.0D, 0.275D - entity.getBbHeight() * 0.5D, 0.0D)
                    .add(0.0D, entity.getBbHeight() / 2.0F + 0.9F, 0.0D);
            entity.level.addParticle(new VaporParticle.VaporParticleOptions(ParticleColors.LIGHT_BLUE_COLOR, scalar, 0.5F, false, 3),
                    right.x(), right.y(), right.z(), 0.0D, 0.1D, 0.0D);

            Vec3 left = transform3rdPersonLeft(new Vec3(0.0D, -0.5825D - entity.getBbHeight() * 0.5D, 0.0D),
                    new Vec3(humanoid.leftArm.xRot, humanoid.leftArm.yRot, humanoid.leftArm.zRot), entity, mc.getPartialTick())
                    .add(0.0D, 0.275D - entity.getBbHeight() * 0.5D, 0.0D)
                    .add(0.0D, entity.getBbHeight() / 2.0F + 0.9F, 0.0D);
            entity.level.addParticle(new VaporParticle.VaporParticleOptions(ParticleColors.LIGHT_BLUE_COLOR, scalar, 0.5F, false, 3),
                    left.x(), left.y(), left.z(), 0.0D, 0.1D, 0.0D);
        }
    }
}
