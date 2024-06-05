package radon.jujutsu_kaisen.ability.ten_shadows;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.ability.IClosedDomain;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.AbilityTriggerEvent;
import radon.jujutsu_kaisen.ability.registry.JJKAbilities;
import radon.jujutsu_kaisen.ability.DomainExpansion;
import radon.jujutsu_kaisen.ability.Summon;
import radon.jujutsu_kaisen.block.JJKBlocks;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.entity.domain.ChimeraShadowGardenEntity;
import radon.jujutsu_kaisen.entity.DomainExpansionEntity;
import radon.jujutsu_kaisen.entity.domain.base.ClosedDomainExpansionEntity;
import radon.jujutsu_kaisen.entity.ten_shadows.TenShadowsSummon;
import radon.jujutsu_kaisen.util.RotationUtil;

import java.util.ArrayList;
import java.util.List;

public class ChimeraShadowGarden extends DomainExpansion implements IClosedDomain {
    @Override
    protected DomainExpansionEntity summon(LivingEntity owner) {
        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return null;

        ISorcererData data = cap.getSorcererData();

        ClosedDomainExpansionEntity domain = new ClosedDomainExpansionEntity(owner, this);
        owner.level().addFreshEntity(domain);

        ChimeraShadowGardenEntity center = new ChimeraShadowGardenEntity(domain);

        Vec3 pos = owner.position()
                .subtract(RotationUtil.calculateViewVector(0.0F, owner.getYRot())
                        .multiply(center.getBbWidth() / 2.0F, 0.0D, center.getBbWidth() / 2.0F));
        center.moveTo(pos.x, pos.y, pos.z, 180.0F - RotationUtil.getTargetAdjustedYRot(owner), 0.0F);

        owner.level().addFreshEntity(center);

        if (owner.level() instanceof ServerLevel) {
            List<TenShadowsSummon> summons = new ArrayList<>();

            for (Entity entity : data.getSummons()) {
                if (entity instanceof TenShadowsSummon summon && summon.isTame()) summons.add(summon);
            }

            for (TenShadowsSummon summon : summons) {
                if (summons.stream().noneMatch(x -> x.getJujutsuType() == summon.getJujutsuType() && x.isClone())) {
                    summon.getAbility().spawn(owner, true);
                }
            }
        }
        return domain;
    }

    @Override
    public List<Block> getBlocks() {
        return List.of(JJKBlocks.CHIMERA_SHADOW_GARDEN.get());
    }

    @EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void onAbilityTrigger(AbilityTriggerEvent.Pre event) {
            LivingEntity owner = event.getEntity();

            IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap == null) return;

            IAbilityData data = cap.getAbilityData();

            if (!data.hasToggled(JJKAbilities.CHIMERA_SHADOW_GARDEN.get())) return;

            if (event.getAbility() instanceof Summon<?> summon && summon.isTamed(owner)) {
                summon.spawn(owner, true);
            }
        }
    }
}
