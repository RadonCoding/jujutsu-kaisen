package radon.jujutsu_kaisen.client.layer.overlay;

import net.minecraft.world.entity.LivingEntity;

public abstract class IRunnableOverlay extends Overlay {
    public abstract void run(LivingEntity entity);
}
