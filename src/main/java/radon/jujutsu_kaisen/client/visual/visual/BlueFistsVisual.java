package radon.jujutsu_kaisen.client.visual.visual;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.client.particle.ParticleColors;
import radon.jujutsu_kaisen.client.particle.TravelParticle;
import radon.jujutsu_kaisen.client.visual.ClientVisualHandler;
import radon.jujutsu_kaisen.client.visual.base.IVisual;
import radon.jujutsu_kaisen.util.HelperMethods;

public class BlueFistsVisual implements IVisual {
    private static final float RADIUS = 1.5F;
    private static final float PARTICLE_SIZE = RADIUS * 0.2F;

    private static Vec3 rotateRoll(Vec3 pos, float roll) {
        float f = Mth.cos(roll);
        float f1 = Mth.sin(roll);
        double d0 = pos.x * (double) f - pos.y * (double) f1;
        double d1 = pos.y * (double) f + pos.x * (double) f1;
        double d2 = pos.z;
        return new Vec3(d0, d1, d2);
    }

    private static Vec3 transform3rdPerson(Vec3 pos, Vec3 angles, LivingEntity entity, HumanoidArm arm, float partialTicks) {
        return rotateRoll(pos, (float)angles.z).xRot((float)-angles.x).yRot((float)-angles.y)
                .add(0.0586F * (arm == HumanoidArm.RIGHT ? -6.0F : 6.0F), 1.3F - (entity.isShiftKeyDown() ? 0.3F : 0.0F), -0.05F)
                .yRot(-Mth.lerp(partialTicks, entity.yBodyRotO, entity.yBodyRot) * (float) (Math.PI / 180.0D))
                .add(Mth.lerp(partialTicks, entity.xOld, entity.getX()), Mth.lerp(partialTicks, entity.yOld, entity.getY()), Mth.lerp(partialTicks, entity.zOld, entity.getZ()));
    }

    @Override
    public boolean isValid(LivingEntity entity, ClientVisualHandler.ClientData data) {
        return data.toggled.contains(JJKAbilities.BLUE_FISTS.get());
    }

    @Override
    public void tick(LivingEntity entity, ClientVisualHandler.ClientData data) {
        Minecraft mc = Minecraft.getInstance();
        EntityRenderDispatcher dispatcher = mc.getEntityRenderDispatcher();
        EntityRenderer<?> renderer = dispatcher.getRenderer(entity);

        if (renderer instanceof LivingEntityRenderer<?, ?> living && living.getModel() instanceof HumanoidModel<?> humanoid) {
            Vec3 right = transform3rdPerson(new Vec3(0.0D, -0.7D + (PARTICLE_SIZE / 2.0F), 0.0D),
                    new Vec3(humanoid.rightArm.xRot, humanoid.rightArm.yRot, humanoid.rightArm.zRot), entity, HumanoidArm.RIGHT, mc.getPartialTick());
            spawn(entity.level(), right);

            Vec3 left = transform3rdPerson(new Vec3(0.0D, -0.7D + (PARTICLE_SIZE / 2.0F), 0.0D),
                    new Vec3(humanoid.leftArm.xRot, humanoid.leftArm.yRot, humanoid.leftArm.zRot), entity, HumanoidArm.LEFT, mc.getPartialTick());
            spawn(entity.level(), left);
        }
    }

    public static void spawn(Level level, Vec3 pos) {
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

            level.addParticle(new TravelParticle.TravelParticleOptions(pos.toVector3f(), ParticleColors.DARK_BLUE, PARTICLE_SIZE, 0.2F, true, 20),
                    x, y, z, 0.0D, 0.0D, 0.0D);
        }

        for (int i = 0; i < count; i++) {
            double theta = HelperMethods.RANDOM.nextDouble() * Math.PI * 2.0D;
            double phi = HelperMethods.RANDOM.nextDouble() * Math.PI;

            double xOffset = RADIUS * 0.5F * Math.sin(phi) * Math.cos(theta);
            double yOffset = RADIUS * 0.5F * Math.sin(phi) * Math.sin(theta);
            double zOffset = RADIUS * 0.5F * Math.cos(phi);

            double x = pos.x + xOffset * (RADIUS * 0.5F * 0.1F);
            double y = pos.y + yOffset * (RADIUS * 0.5F * 0.1F);
            double z = pos.z + zOffset * (RADIUS * 0.5F * 0.1F);

            level.addParticle(new TravelParticle.TravelParticleOptions(pos.toVector3f(), ParticleColors.LIGHT_BLUE, PARTICLE_SIZE / 2.0F, 0.2F, true, 20),
                    x, y, z, 0.0D, 0.0D, 0.0D);
        }
    }
}
