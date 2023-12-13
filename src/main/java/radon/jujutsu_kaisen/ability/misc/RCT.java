package radon.jujutsu_kaisen.ability.misc;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
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


public class RCT extends Ability implements Ability.IChannelened {
    @Override
    public boolean isScalable(LivingEntity owner) {
        return false;
    }

    @Override
    public boolean isTechnique() {
        return false;
    }

    private RCT1 getTier(LivingEntity owner) {
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        if (cap.isUnlocked(JJKAbilities.RCT3.get())) return JJKAbilities.RCT3.get();
        if (cap.isUnlocked(JJKAbilities.RCT2.get())) return JJKAbilities.RCT2.get();

        return JJKAbilities.RCT3.get();
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        if (!owner.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return false;
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        return owner.getHealth() < owner.getMaxHealth() || ((cap.hasTrait(Trait.SIX_EYES) || HelperMethods.isExperienced(cap.getExperience())) && cap.getBurnout() > 0);
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.CHANNELED;
    }

    @Override
    public void run(LivingEntity owner) {
        RCT1 tier = getTier(owner);
        tier.run(owner);
    }

    @Override
    public float getCost(LivingEntity owner) {
        RCT1 tier = getTier(owner);
        return tier.getCost(owner);
    }

    @Override
    public MenuType getMenuType() {
        return MenuType.NONE;
    }

    @Override
    public boolean isValid(LivingEntity owner) {
        if (!owner.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return false;
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        return cap.getType() != JujutsuType.CURSE && super.isValid(owner);
    }

    @Override
    public boolean isUnlocked(LivingEntity owner) {
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        return cap.isUnlocked(JJKAbilities.RCT1.get());
    }

    @Override
    public void onStart(LivingEntity owner) {

    }

    @Override
    public void onRelease(LivingEntity owner) {

    }
}
