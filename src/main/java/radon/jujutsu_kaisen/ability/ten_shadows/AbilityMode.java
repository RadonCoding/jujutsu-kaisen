package radon.jujutsu_kaisen.ability.ten_shadows;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.TenShadowsMode;
import radon.jujutsu_kaisen.util.HelperMethods;

public class AbilityMode extends Ability implements Ability.IToggled {
    @Override
    public boolean isScalable() {
        return false;
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        if (target == null) return false;

        ISorcererData ownerCap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        if (target.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
            ISorcererData targetCap = target.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

            if (ownerCap.getMode() == TenShadowsMode.SUMMON) {
                if (targetCap.hasToggled(JJKAbilities.INFINITY.get())) {
                    return !ownerCap.isAdaptedTo(JJKAbilities.INFINITY.get());
                }

                if (targetCap.getTechnique() != null && !ownerCap.isAdaptedTo(targetCap.getTechnique())) {
                    return true;
                }
            } else {
                if (targetCap.hasToggled(JJKAbilities.INFINITY.get())) {
                    return ownerCap.isAdaptedTo(JJKAbilities.INFINITY.get());
                }

                if (targetCap.getTechnique() != null && ownerCap.isAdaptedTo(targetCap.getTechnique())) {
                    return false;
                }
            }
        }
        return JJKAbilities.hasToggled(owner, this) == (HelperMethods.RANDOM.nextInt(5) != 0);
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.TOGGLED;
    }

    @Override
    public void run(LivingEntity owner) {

    }


    @Override
    public float getCost(LivingEntity owner) {
        return 0;
    }

    @Override
    public void onEnabled(LivingEntity owner) {
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        cap.setMode(TenShadowsMode.ABILITY);
    }

    @Override
    public void onDisabled(LivingEntity owner) {
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        cap.setMode(TenShadowsMode.SUMMON);
    }
}
