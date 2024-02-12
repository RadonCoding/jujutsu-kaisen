package radon.jujutsu_kaisen.ability.ten_shadows;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;
import radon.jujutsu_kaisen.data.ten_shadows.ITenShadowsData;
import radon.jujutsu_kaisen.data.ten_shadows.TenShadowsMode;
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

        ITenShadowsData ownerData = owner.getData(JJKAttachmentTypes.TEN_SHADOWS);
        ISorcererData targetData = target.getData(JJKAttachmentTypes.SORCERER);

        if (ownerData == null) return false;

        if (targetData != null) {
            if (ownerData.hasTamed(JJKEntities.MAHORAGA.get())) {
                if (ownerData.getMode() == TenShadowsMode.SUMMON) {
                    if (targetData.hasToggled(JJKAbilities.INFINITY.get())) {
                        return !ownerData.isAdaptedTo(JJKAbilities.INFINITY.get());
                    }

                    if (targetData.getTechnique() != null && !ownerData.isAdaptedTo(targetData.getTechnique())) {
                        return true;
                    }
                } else {
                    if (targetData.hasToggled(JJKAbilities.INFINITY.get())) {
                        return ownerData.isAdaptedTo(JJKAbilities.INFINITY.get());
                    }

                    if (targetData.getTechnique() != null && ownerData.isAdaptedTo(targetData.getTechnique())) {
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
        ITenShadowsData data = owner.getData(JJKAttachmentTypes.TEN_SHADOWS);


        data.setMode(data.getMode() == TenShadowsMode.SUMMON ? TenShadowsMode.ABILITY : TenShadowsMode.SUMMON);

        if (owner instanceof ServerPlayer player) {
            player.sendSystemMessage(Component.translatable(String.format("chat.%s.switch_mode", JujutsuKaisen.MOD_ID), data.getMode().name().toLowerCase()));
        }
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 0;
    }
}
