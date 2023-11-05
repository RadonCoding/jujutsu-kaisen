package radon.jujutsu_kaisen.ability.misc;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.util.HelperMethods;


public class Heal extends Ability implements Ability.IChannelened {
    @Override
    public boolean isScalable() {
        return false;
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return owner.getHealth() < owner.getMaxHealth();
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.CHANNELED;
    }

    @Override
    public void run(LivingEntity owner) {
        owner.heal(ConfigHolder.SERVER.curseHealingAmount.get().floatValue() * getPower(owner));
    }

    @Override
    public float getCost(LivingEntity owner) {
        if (owner.getHealth() < owner.getMaxHealth()) {
            return ConfigHolder.SERVER.curseHealingAmount.get().floatValue() * getPower(owner) * 4.0F;
        }
        return 0.0F;
    }

    @Override
    public MenuType getMenuType() {
        return MenuType.NONE;
    }

    @Override
    public boolean isValid(LivingEntity owner) {
        if (!owner.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return false;
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        return cap.getType() == JujutsuType.CURSE && super.isValid(owner);
    }

    @Override
    public void onStart(LivingEntity owner) {

    }

    @Override
    public void onRelease(LivingEntity owner) {

    }
}
