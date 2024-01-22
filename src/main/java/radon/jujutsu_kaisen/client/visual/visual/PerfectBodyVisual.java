package radon.jujutsu_kaisen.client.visual.visual;

import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.ITransformation;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.client.visual.ClientVisualHandler;
import radon.jujutsu_kaisen.client.visual.base.IVisual;

import java.util.UUID;

public class PerfectBodyVisual implements IVisual {
    private static final int MAX_MOUTH_FRAMES = 4;

    public static void onChant(UUID identifier) {
        ClientVisualHandler.ClientData data = ClientVisualHandler.get(identifier);

        if (data == null) return;

        data.mouth++;
    }

    public static boolean shouldRenderExtraArms(LivingEntity entity, ClientVisualHandler.ClientData data) {
        if (Minecraft.getInstance().player == entity && Minecraft.getInstance().options.getCameraType() == CameraType.FIRST_PERSON) return false;

        for (Ability ability : data.toggled) {
            if (!(ability instanceof ITransformation transformation)) continue;
            if (transformation.getBodyPart() != ITransformation.Part.BODY || !transformation.isReplacement()) continue;
            return false;
        }
        return data.traits.contains(Trait.PERFECT_BODY);
    }

    @Override
    public boolean isValid(LivingEntity entity, ClientVisualHandler.ClientData data) {
        return data.mouth > 0 && entity.level().getGameTime() % 5 != 0;
    }

    @Override
    public void tick(LivingEntity entity, ClientVisualHandler.ClientData data) {
        if (++data.mouth >= MAX_MOUTH_FRAMES) data.mouth = 0;
    }
}
