package radon.jujutsu_kaisen.ability.ten_shadows;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.entity.ten_shadows.RabbitEscapeEntity;
import radon.jujutsu_kaisen.util.HelperMethods;

public class RabbitEscape extends Ability {
    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return target != null && HelperMethods.RANDOM.nextInt(3) == 0 && owner.getLastHurtByMobTimestamp() - owner.tickCount == 0;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    @Override
    public void run(LivingEntity owner) {
        if (!owner.level.isClientSide) {
            owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                for (int i = 0; i < 8; i++) {
                    cap.delayTickEvent(() -> {
                        for (int j = 0; j < 16; j++) {
                            RabbitEscapeEntity rabbit = new RabbitEscapeEntity(owner);
                            owner.level.addFreshEntity(rabbit);
                            cap.addSummon(rabbit);
                        }
                    }, i * 2);
                }
            });
        }
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 50.0F;
    }
}
