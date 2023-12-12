package radon.jujutsu_kaisen.ability.misc;

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


public class RCT3 extends RCT2 {
    @Override
    public void run(LivingEntity owner) {
        super.run(owner);

        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        if (cap.hasTrait(Trait.SIX_EYES) || HelperMethods.isExperienced(cap.getExperience())) {
            int burnout = cap.getBurnout();

            if (burnout > 0) {
                cap.setBurnout(Math.max(0, burnout - 10));
            }
        }
    }

    @Override
    public float getCost(LivingEntity owner) {
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        if (owner.getHealth() == owner.getMaxHealth() && cap.getBurnout() > 0) {
            return 1.0F / 20;
        }
        return super.getCost(owner);
    }

    @Nullable
    @Override
    public Ability getParent(LivingEntity owner) {
        return JJKAbilities.RCT2.get();
    }

    @Override
    public Vec2 getDisplayCoordinates() {
        return new Vec2(2.0F, 2.0F);
    }

    @Override
    public int getPointsCost() {
        return ConfigHolder.SERVER.rct3Cost.get();
    }

    @Override
    protected int getMultiplier() {
        return 8;
    }
}
