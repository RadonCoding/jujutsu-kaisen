package radon.jujutsu_kaisen.ability.dismantle_and_cleave;

import net.minecraft.world.entity.LivingEntity;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;

public class DismantleBarrage extends Dismantle {
    @Override
    public void run(LivingEntity owner) {
        owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
            for (int i = 0; i < 3; i++) {
                cap.delayTickEvent(() ->
                        super.run(owner), i * 2);
            }
        });
    }

    @Override
    public float getCost(LivingEntity owner) {
        return super.getCost(owner) * 2;
    }

    @Override
    public int getCooldown() {
        return super.getCooldown() * 2;
    }
}
