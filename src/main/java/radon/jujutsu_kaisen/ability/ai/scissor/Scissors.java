package radon.jujutsu_kaisen.ability.ai.scissor;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.entity.curse.KuchisakeOnnaEntity;
import radon.jujutsu_kaisen.entity.effect.ScissorEntity;
import radon.jujutsu_kaisen.util.HelperMethods;

public class Scissors extends Ability {
    @Override
    public boolean isScalable(LivingEntity owner) {
        return false;
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return false;
    }

    @Override
    public boolean isValid(LivingEntity owner) {
        return owner instanceof KuchisakeOnnaEntity;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    @Override
    public boolean isTechnique() {
        return false;
    }

    @Override
    public void run(LivingEntity owner) {
        if (!(owner.level() instanceof ServerLevel level)) return;

        ((KuchisakeOnnaEntity) owner).getCurrent().ifPresent(identifier -> {
            LivingEntity target = (LivingEntity) level.getEntity(identifier);

            if (target == null) return;

            owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                for (int i = 0; i < HelperMethods.RANDOM.nextInt(4, 10); i++) {
                    ScissorEntity scissor = new ScissorEntity(owner, this.getPower(owner), target);
                    owner.level().addFreshEntity(scissor);
                    cap.addSummon(scissor);
                }
            });
        });
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 100.0F;
    }

    @Override
    public int getCooldown() {
        return 5 * 20;
    }
}
