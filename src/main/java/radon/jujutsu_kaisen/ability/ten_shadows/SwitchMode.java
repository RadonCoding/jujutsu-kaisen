package radon.jujutsu_kaisen.ability.ten_shadows;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.TenShadowsMode;
import radon.jujutsu_kaisen.util.HelperMethods;

public class SwitchMode extends Ability {
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
                    return true;
                }
            }
        }
        return JJKAbilities.hasToggled(owner, this) == (HelperMethods.RANDOM.nextInt(5) != 0);
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    @Override
    public void run(LivingEntity owner) {
        owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
            cap.setMode(cap.getMode() == TenShadowsMode.SUMMON ? TenShadowsMode.ABILITY : TenShadowsMode.SUMMON);

            if (!owner.level().isClientSide && owner instanceof Player) {
                owner.sendSystemMessage(Component.translatable(String.format("chat.%s.switch_mode", JujutsuKaisen.MOD_ID), cap.getMode().name().toLowerCase()));
            }
        });
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 0;
    }
}
