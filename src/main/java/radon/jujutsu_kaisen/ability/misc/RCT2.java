package radon.jujutsu_kaisen.ability.misc;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.IAttack;
import radon.jujutsu_kaisen.ability.base.IChanneled;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.ICharged;
import radon.jujutsu_kaisen.ability.base.IDomainAttack;
import radon.jujutsu_kaisen.ability.base.IDurationable;
import radon.jujutsu_kaisen.ability.base.ITenShadowsAttack;
import radon.jujutsu_kaisen.ability.base.IToggled;
import radon.jujutsu_kaisen.config.ConfigHolder;

import java.util.ArrayList;
import java.util.List;


public class RCT2 extends RCT1 {
    private static final List<MobEffect> HARMFUL = new ArrayList<>();

    static {
        HARMFUL.add(MobEffects.BLINDNESS);
        HARMFUL.add(MobEffects.POISON);
        HARMFUL.add(MobEffects.WITHER);
        HARMFUL.add(MobEffects.CONFUSION);
        HARMFUL.add(MobEffects.WEAKNESS);
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        for (MobEffect effect : owner.getActiveEffectsMap().keySet()) {
            if (HARMFUL.contains(effect)) return true;
        }
        return super.shouldTrigger(owner, target);
    }

    @Override
    public void run(LivingEntity owner) {
        super.run(owner);

        owner.getActiveEffects().removeIf(instance -> HARMFUL.contains(instance.getEffect()));
    }

    @Override
    public float getCost(LivingEntity owner) {
        float cost = super.getCost(owner);

        for (MobEffect effect : owner.getActiveEffectsMap().keySet()) {
            if (!HARMFUL.contains(effect)) continue;

            cost += (1.0F / 20);
            break;
        }
        return cost;
    }

    @Nullable
    @Override
    public Ability getParent(LivingEntity owner) {
        return JJKAbilities.RCT1.get();
    }

    @Override
    public int getPointsCost() {
        return ConfigHolder.SERVER.rct2Cost.get();
    }

    @Override
    protected int getMultiplier() {
        return 4;
    }
}