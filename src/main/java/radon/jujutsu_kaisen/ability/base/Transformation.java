package radon.jujutsu_kaisen.ability.base;

import net.minecraft.world.entity.LivingEntity;
import radon.jujutsu_kaisen.ability.AbilityHandler;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;

public abstract class Transformation extends Ability implements Ability.IToggled, ITransformation {
    @Override
    public Status checkTriggerable(LivingEntity owner) {
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        for (Ability ability : cap.getToggled()) {
            if (!(ability instanceof ITransformation transformation)) continue;

            if (transformation.getBodyPart() == this.getBodyPart()) {
                cap.toggle(ability);
            }
        }
        return super.checkTriggerable(owner);
    }

    @Override
    public void onEnabled(LivingEntity owner) {
        this.applyModifiers(owner);
    }

    @Override
    public void onDisabled(LivingEntity owner) {
        this.removeModifiers(owner);
    }
}
