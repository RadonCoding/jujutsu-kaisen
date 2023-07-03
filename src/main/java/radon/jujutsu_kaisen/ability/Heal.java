package radon.jujutsu_kaisen.ability;

import net.minecraft.world.entity.LivingEntity;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;

public class Heal extends Ability {
    private static final float AMOUNT = 1.0F;
    private static final int DELAY = 20;

    @Override
    public ActivationType getActivationType() {
        return ActivationType.INSTANT;
    }

    @Override
    public void run(LivingEntity owner) {
        owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
            if (owner.level.getGameTime() % (DELAY / (cap.getGrade().ordinal() + 1)) == 0) {
                owner.heal(AMOUNT);
            }
        });
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 1.0F;
    }
}
