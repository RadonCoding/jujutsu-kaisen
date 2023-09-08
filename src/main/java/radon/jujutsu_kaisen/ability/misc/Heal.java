package radon.jujutsu_kaisen.ability.misc;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.DisplayType;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;


public class Heal extends Ability implements Ability.IChannelened {
    private static final float AMOUNT = 1.0F;

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
        owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap ->
                owner.heal(AMOUNT * cap.getGrade().ordinal() / SorcererGrade.values().length));
    }

    @Override
    public float getCost(LivingEntity owner) {
        return owner.getHealth() < owner.getMaxHealth() ? 2.5F : 0.0F;
    }

    @Override
    public DisplayType getDisplayType() {
        return DisplayType.NONE;
    }

    @Override
    public boolean isUnlocked(LivingEntity owner) {
        if (!owner.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return false;
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        return cap.getType() == JujutsuType.CURSE && super.isUnlocked(owner);
    }

    @Override
    public void onRelease(LivingEntity owner, int charge) {

    }
}
