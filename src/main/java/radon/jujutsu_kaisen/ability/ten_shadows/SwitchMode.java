package radon.jujutsu_kaisen.ability.ten_shadows;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.ten_shadows.ITenShadowsData;
import radon.jujutsu_kaisen.capability.data.ten_shadows.TenShadowsDataHandler;
import radon.jujutsu_kaisen.capability.data.ten_shadows.TenShadowsMode;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.util.HelperMethods;

public class SwitchMode extends Ability {
    @Override
    public boolean isScalable(LivingEntity owner) {
        return false;
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        if (target == null) return false;

        ITenShadowsData ownerCap = owner.getCapability(TenShadowsDataHandler.INSTANCE).resolve().orElseThrow();

        if (target.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
            ISorcererData targetCap = target.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

            if (JJKAbilities.hasTamed(owner, JJKEntities.MAHORAGA.get())) {
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
        }
        return HelperMethods.RANDOM.nextInt(10) == 0;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    @Override
    public void run(LivingEntity owner) {
        ITenShadowsData cap = owner.getCapability(TenShadowsDataHandler.INSTANCE).resolve().orElseThrow();
        cap.setMode(cap.getMode() == TenShadowsMode.SUMMON ? TenShadowsMode.ABILITY : TenShadowsMode.SUMMON);

        if (owner instanceof ServerPlayer player) {
            player.sendSystemMessage(Component.translatable(String.format("chat.%s.switch_mode", JujutsuKaisen.MOD_ID), cap.getMode()));
        }
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 0;
    }
}
