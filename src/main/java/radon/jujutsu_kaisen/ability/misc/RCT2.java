package radon.jujutsu_kaisen.ability.misc;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.List;


public class RCT2 extends RCT1 {
    @Override
    public void run(LivingEntity owner) {
        super.run(owner);

        if (owner.getHealth() == owner.getMaxHealth()) {
            for (MobEffect effect : owner.getActiveEffectsMap().keySet()) {
                if (!effect.isBeneficial()) owner.removeEffect(effect);
            }
        }
    }

    @Override
    public float getCost(LivingEntity owner) {
        if (owner.getHealth() == owner.getMaxHealth()) {
            for (MobEffect effect : owner.getActiveEffectsMap().keySet()) {
                if (!effect.isBeneficial()) return 1.0F / 20;
            }
        }
        return super.getCost(owner);
    }

    @Nullable
    @Override
    public Ability getParent(LivingEntity owner) {
        return JJKAbilities.RCT1.get();
    }

    @Override
    public Vec2 getDisplayCoordinates() {
        return new Vec2(1.0F, 2.0F);
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