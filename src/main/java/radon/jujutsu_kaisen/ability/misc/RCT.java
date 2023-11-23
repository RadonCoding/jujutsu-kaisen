package radon.jujutsu_kaisen.ability.misc;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.entity.ten_shadows.AgitoEntity;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.List;


public class RCT extends Ability implements Ability.IChannelened {
    @Override
    public boolean isScalable() {
        return false;
    }

    @Override
    public boolean isTechnique() {
        return false;
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        if (!owner.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return false;
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        return owner.getHealth() < owner.getMaxHealth() || ((cap.hasTrait(Trait.SIX_EYES) || HelperMethods.isStrongest(cap.getExperience())) && cap.getBurnout() > 0);
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.CHANNELED;
    }

    @Override
    public void run(LivingEntity owner) {
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        owner.heal(ConfigHolder.SERVER.sorcererHealingAmount.get().floatValue() * getPower(owner));

        if (cap.hasTrait(Trait.SIX_EYES) || HelperMethods.isStrongest(cap.getExperience())) {
            int burnout = cap.getBurnout();

            if (burnout > 0) {
                cap.setBurnout(Math.max(0, burnout - 10));
            }
        }
    }

    @Override
    public float getCost(LivingEntity owner) {
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        if (owner.getHealth() < owner.getMaxHealth()) {
            return ConfigHolder.SERVER.sorcererHealingAmount.get().floatValue() * getPower(owner) * 8.0F;
        } else if ((cap.hasTrait(Trait.SIX_EYES) || HelperMethods.isStrongest(cap.getExperience())) && cap.getBurnout() > 0) {
            return 1.0F / 20;
        }
        return 0.0F;
    }

    @Override
    public MenuType getMenuType() {
        return MenuType.NONE;
    }

    @Override
    public List<Trait> getRequirements() {
        return List.of(Trait.REVERSE_CURSED_TECHNIQUE);
    }

    @Override
    public boolean isValid(LivingEntity owner) {
        if (!owner.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return false;
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        return cap.getType() != JujutsuType.CURSE && super.isValid(owner);
    }

    @Override
    public void onStart(LivingEntity owner) {

    }

    @Override
    public void onRelease(LivingEntity owner) {

    }
}
