package radon.jujutsu_kaisen.client.visual.base;


import net.minecraft.world.entity.LivingEntity;
import radon.jujutsu_kaisen.client.visual.ClientVisualHandler;

public interface IVisual {
    boolean isValid(LivingEntity entity, ClientVisualHandler.ClientData client);

    void tick(LivingEntity entity, ClientVisualHandler.ClientData client);
}
