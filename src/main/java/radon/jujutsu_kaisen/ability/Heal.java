package radon.jujutsu_kaisen.ability;

import net.minecraft.world.entity.LivingEntity;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;

public class Heal extends Ability {
    private static final float AMOUNT = 1.0F;

    @Override
    public ActivationType getActivationType() {
        return ActivationType.INSTANT;
    }

    @Override
    public void run(LivingEntity owner) {
        if (owner.level.getGameTime() % 5 == 0) {
            owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> owner.heal(AMOUNT * cap.getGrade().getPower()));
        }
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 1.0F;
    }
}
