package radon.jujutsu_kaisen.ability.ai.cyclops;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.Ability;

public class CyclopsSmash extends Ability {
    private static final float SMASH_EXPLOSION = 5.0F;

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
        if (!owner.level.isClientSide) {
            owner.level.explode(owner, owner.getX(), owner.getY(), owner.getZ(), SMASH_EXPLOSION, false,
                    owner.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) ? Level.ExplosionInteraction.BLOCK : Level.ExplosionInteraction.NONE);
        }
    }

    @Override
    public int getCooldown() {
        return 5 * 20;
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 50.0F;
    }
}
