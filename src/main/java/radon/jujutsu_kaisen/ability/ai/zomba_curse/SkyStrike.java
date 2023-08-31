package radon.jujutsu_kaisen.ability.ai.zomba_curse;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.entity.effect.SkyStrikeEntity;

public class SkyStrike extends Ability {
    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return false;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    @Override
    public void run(LivingEntity owner) {
        owner.swing(InteractionHand.MAIN_HAND);

        LivingEntity target = ((Mob) owner).getTarget();

        if (target != null) {
            SkyStrikeEntity strike = new SkyStrikeEntity(owner, target.position());
            owner.level.addFreshEntity(strike);
        }
    }

    @Override
    public int getCooldown() {
        return 3 * 20;
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 100.0F;
    }
}
