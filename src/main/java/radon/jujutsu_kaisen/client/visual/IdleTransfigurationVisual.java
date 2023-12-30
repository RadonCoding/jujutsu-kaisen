package radon.jujutsu_kaisen.client.visual;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.idle_transfiguration.IdleTransfiguration;
import radon.jujutsu_kaisen.client.particle.ParticleColors;
import radon.jujutsu_kaisen.client.particle.TravelParticle;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.util.HelperMethods;

public class IdleTransfigurationVisual {
    private static final float RADIUS = 1.5F;

    private static Vec3 rotateRoll(Vec3 pos, float roll) {
        float f = Mth.cos(roll);
        float f1 = Mth.sin(roll);
        double d0 = pos.x * (double) f - pos.y * (double) f1;
        double d1 = pos.y * (double) f + pos.x * (double) f1;
        double d2 = pos.z;
        return new Vec3(d0, d1, d2);
    }

    private static Vec3 transform3rdPersonRight(Vec3 pos, Vec3 angles, LivingEntity entity, float partialTicks) {
        return rotateRoll(pos, (float) -angles.z).xRot((float) -angles.x).yRot((float) -angles.y)
                .add(0.05F * -6.0F, 1.02F - (entity.isShiftKeyDown() ? 0.3F : 0.0F), 0.0F)
                .yRot((-entity.yBodyRotO - (entity.yBodyRot - entity.yBodyRotO) * partialTicks) * (float) (Math.PI / 180.0D))
                .add(Mth.lerp(partialTicks, entity.xOld, entity.getX()),
                        Mth.lerp(partialTicks, entity.yOld, entity.getY()),
                        Mth.lerp(partialTicks, entity.zOld, entity.getZ()));
    }

    private static Vec3 transform3rdPersonLeft(Vec3 pos, Vec3 angles, LivingEntity entity, float partialTicks) {
        return rotateRoll(pos, (float) -angles.z).xRot((float) -angles.x).yRot((float) -angles.y)
                .add(0.05F * 6.0F, 1.02F - (entity.isShiftKeyDown() ? 0.3F : 0.0F), 0.0F)
                .yRot((-entity.yBodyRotO - (entity.yBodyRot - entity.yBodyRotO) * partialTicks) * (float) (Math.PI / 180.0D))
                .add(Mth.lerp(partialTicks, entity.xOld, entity.getX()),
                        Mth.lerp(partialTicks, entity.yOld, entity.getY()),
                        Mth.lerp(partialTicks, entity.zOld, entity.getZ()));
    }

    public static void tick(ClientVisualHandler.VisualData data, LivingEntity entity) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.level == null || mc.player == null) return;

        if (data.toggled.contains(JJKAbilities.IDLE_TRANSFIGURATION.get())) {
            run(entity);
        }

        MobEffectInstance instance = entity.getEffect(JJKEffects.TRANSFIGURED_SOUL.get());

        if (instance != null) {
            int amplifier = instance.getAmplifier();

            float attackerStrength = IdleTransfiguration.calculateStrength(mc.player);
            float victimStrength = IdleTransfiguration.calculateStrength(entity);

            int required = Math.round((victimStrength / attackerStrength) * 2);

            if (amplifier >= required) {
                int count = (int) (entity.getBbWidth() * entity.getBbHeight()) / 2;

                for (int i = 0; i < count; i++) {
                    double x = entity.getX() + (HelperMethods.RANDOM.nextDouble() - 0.5D) * (entity.getBbWidth() * 2) - entity.getLookAngle().scale(0.35D).x;
                    double y = entity.getY() + HelperMethods.RANDOM.nextDouble() * entity.getBbHeight();
                    double z = entity.getZ() + (HelperMethods.RANDOM.nextDouble() - 0.5D) * (entity.getBbWidth() * 2) - entity.getLookAngle().scale(0.35D).z;
                    mc.level.addParticle(ParticleTypes.SOUL, x, y, z, 0.0D, HelperMethods.RANDOM.nextDouble() * 0.1D, 0.0D);
                }
            }
        }
    }

    private static void spawn(Level level, Vec3 pos, Vector3f color) {
        int count = (int) (RADIUS * Math.PI * 2);

        for (int i = 0; i < count; i++) {
            double theta = HelperMethods.RANDOM.nextDouble() * Math.PI * 2.0D;
            double phi = HelperMethods.RANDOM.nextDouble() * Math.PI;

            double xOffset = RADIUS * Math.sin(phi) * Math.cos(theta);
            double yOffset = RADIUS * Math.sin(phi) * Math.sin(theta);
            double zOffset = RADIUS * Math.cos(phi);

            double x = pos.x + xOffset * (RADIUS * 0.1F);
            double y = pos.y + yOffset * (RADIUS * 0.1F);
            double z = pos.z + zOffset * (RADIUS * 0.1F);

            level.addParticle(new TravelParticle.TravelParticleOptions(pos.toVector3f(), color, RADIUS * 0.15F, 0.2F, true, 20),
                    x, y, z, 0.0D, 1.0D, 0.0D);
        }
    }

    private static void run(LivingEntity entity) {
        Minecraft mc = Minecraft.getInstance();
        EntityRenderDispatcher dispatcher = mc.getEntityRenderDispatcher();
        EntityRenderer<?> renderer = dispatcher.getRenderer(entity);

        if (renderer instanceof LivingEntityRenderer<?, ?> living && living.getModel() instanceof HumanoidModel<?> humanoid) {
            Vec3 right = transform3rdPersonRight(new Vec3(0.0D, -0.5825D - entity.getBbHeight() * 0.425D, 0.0D),
                    new Vec3(humanoid.rightArm.xRot, humanoid.rightArm.yRot, humanoid.rightArm.zRot), entity, mc.getPartialTick())
                    .add(0.0D, 0.275D - entity.getBbHeight() * 0.5D, 0.0D)
                    .add(0.0D, entity.getBbHeight() / 2.0F + 0.9F, 0.0D);
            spawn(entity.level(), right, ParticleColors.getCursedEnergyColor(entity));

            Vec3 left = transform3rdPersonLeft(new Vec3(0.0D, -0.5825D - entity.getBbHeight() * 0.425D, 0.0D),
                    new Vec3(humanoid.leftArm.xRot, humanoid.leftArm.yRot, humanoid.leftArm.zRot), entity, mc.getPartialTick())
                    .add(0.0D, 0.275D - entity.getBbHeight() * 0.5D, 0.0D)
                    .add(0.0D, entity.getBbHeight() / 2.0F + 0.9F, 0.0D);
            spawn(entity.level(), left, ParticleColors.getCursedEnergyColor(entity));
        }
    }
}
