package radon.jujutsu_kaisen.client.visual.visual;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.RenderLivingEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import org.joml.Vector3f;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.IAttack;
import radon.jujutsu_kaisen.ability.base.IChanneled;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.ICharged;
import radon.jujutsu_kaisen.ability.base.IDomainAttack;
import radon.jujutsu_kaisen.ability.base.IDurationable;
import radon.jujutsu_kaisen.ability.base.ITenShadowsAttack;
import radon.jujutsu_kaisen.ability.base.IToggled;
import radon.jujutsu_kaisen.ability.base.ITransformation;
import radon.jujutsu_kaisen.client.particle.ParticleColors;
import radon.jujutsu_kaisen.client.particle.TravelParticle;
import radon.jujutsu_kaisen.client.visual.ClientVisualHandler;
import radon.jujutsu_kaisen.client.visual.base.IVisual;
import radon.jujutsu_kaisen.util.HelperMethods;

public class IdleTransfigurationVisual implements IVisual {
    private static final float RADIUS = 1.5F;
    private static final float PARTICLE_SIZE = RADIUS * 0.2F;

    @Override
    public boolean isValid(LivingEntity entity, ClientVisualHandler.ClientData client) {
        return client.toggled.contains(JJKAbilities.IDLE_TRANSFIGURATION.get());
    }

    @Override
    public void tick(LivingEntity entity, ClientVisualHandler.ClientData client) {
        Vec3 right = BlueFistsVisual.getArmPos(entity, HumanoidArm.RIGHT).add(0.0D, PARTICLE_SIZE / 2.0F, 0.0D);
        spawn(entity.level(), right, ParticleColors.getCursedEnergyColor(entity));

        Vec3 left = BlueFistsVisual.getArmPos(entity, HumanoidArm.LEFT).add(0.0D, PARTICLE_SIZE / 2.0F, 0.0D);
        spawn(entity.level(), left, ParticleColors.getCursedEnergyColor(entity));
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
}
