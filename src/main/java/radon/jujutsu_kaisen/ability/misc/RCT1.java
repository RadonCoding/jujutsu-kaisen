package radon.jujutsu_kaisen.ability.misc;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.config.ConfigHolder;


public class RCT1 extends Ability implements Ability.IChannelened {
    @Override
    public boolean isScalable(LivingEntity owner) {
        return false;
    }

    @Override
    public boolean isTechnique() {
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
        owner.heal((float) (ConfigHolder.SERVER.sorcererHealingAmount.get().floatValue() * Math.pow(this.getPower(owner), 2)));
    }

    @Override
    public float getCost(LivingEntity owner) {
        if (owner.getHealth() < owner.getMaxHealth()) {
            return (float) (ConfigHolder.SERVER.sorcererHealingAmount.get().floatValue() * Math.pow(this.getPower(owner), 2) * this.getMultiplier());
        }
        return 0;
    }

    @Override
    public boolean isUnlockable() {
        return this == JJKAbilities.RCT1.get() || super.isUnlockable();
    }

    @Override
    public boolean isDisplayed(LivingEntity owner) {
        return true;
    }

    @Override
    public boolean canUnlock(LivingEntity owner) {
        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return false;

        ISorcererData data = cap.getSorcererData();

        return data.getType() == JujutsuType.SORCERER && super.canUnlock(owner);
    }

    @Nullable
    @Override
    public Ability getParent(LivingEntity owner) {
        return JJKAbilities.CURSED_ENERGY_FLOW.get();
    }

    @Override
    public MenuType getMenuType(LivingEntity owner) {
        return MenuType.NONE;
    }

    protected int getMultiplier() {
        return 2;
    }

    @Override
    public boolean isValid(LivingEntity owner) {
        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return false;

        ISorcererData data = cap.getSorcererData();

        if (data.getType() == JujutsuType.CURSE) return false;

        return super.isValid(owner);
    }
}
