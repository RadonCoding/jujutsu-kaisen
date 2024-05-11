package radon.jujutsu_kaisen.client.visual.visual;

import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.world.entity.LivingEntity;
import radon.jujutsu_kaisen.client.visual.ClientVisualHandler;
import radon.jujutsu_kaisen.client.visual.base.IVisual;

import java.util.UUID;

public class PerfectBodyVisual implements IVisual {
    private static final int MAX_MOUTH_FRAMES = 4;

    public static void onChant(UUID identifier) {
        ClientVisualHandler.ClientData client = ClientVisualHandler.get(identifier);

        if (client == null) return;

        client.mouth = 1;
    }

    @Override
    public boolean isValid(LivingEntity entity, ClientVisualHandler.ClientData client) {
        return client.mouth > 0 && entity.level().getGameTime() % 5 == 0;
    }

    @Override
    public void tick(LivingEntity entity, ClientVisualHandler.ClientData client) {
        if (++client.mouth >= MAX_MOUTH_FRAMES) client.mouth = 0;
    }
}
