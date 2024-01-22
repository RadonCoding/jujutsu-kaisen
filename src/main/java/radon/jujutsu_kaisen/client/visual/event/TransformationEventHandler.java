package radon.jujutsu_kaisen.client.visual.event;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.ITransformation;
import radon.jujutsu_kaisen.client.visual.ClientVisualHandler;

@Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class TransformationEventHandler {
    @SubscribeEvent
    public static void onRenderLivingPre(RenderLivingEvent.Pre<?, ?> event) {
        LivingEntity entity = event.getEntity();

        ClientVisualHandler.ClientData data = ClientVisualHandler.get(entity);

        if (data == null) return;

        if (!(event.getRenderer().getModel() instanceof PlayerModel<?> player)) return;

        for (Ability ability : data.toggled) {
            if (!(ability instanceof ITransformation transformation)) continue;

            if (transformation.isReplacement()) {
                switch (transformation.getBodyPart()) {
                    case HEAD -> {
                        player.head.visible = false;
                        player.hat.visible = false;
                    }
                    case BODY -> player.setAllVisible(false);
                    case RIGHT_ARM -> {
                        player.rightArm.visible = false;
                        player.rightSleeve.visible = false;
                    }
                    case LEFT_ARM -> {
                        player.leftArm.visible = false;
                        player.rightSleeve.visible = false;
                    }
                    case LEGS -> {
                        player.rightLeg.visible = false;
                        player.rightPants.visible = false;
                        player.leftLeg.visible = false;
                        player.leftPants.visible = false;
                    }
                }
            }

            HumanoidModel.ArmPose pose = IClientItemExtensions.of(transformation.getItem()).getArmPose(event.getEntity(), InteractionHand.MAIN_HAND, transformation.getItem().getDefaultInstance());

            if (pose != null) {
                if (transformation.getBodyPart() == ITransformation.Part.RIGHT_ARM) {
                    player.rightArmPose = pose;
                } else if (transformation.getBodyPart() == ITransformation.Part.LEFT_ARM) {
                    player.leftArmPose = pose;
                }
            }
        }
    }

}
