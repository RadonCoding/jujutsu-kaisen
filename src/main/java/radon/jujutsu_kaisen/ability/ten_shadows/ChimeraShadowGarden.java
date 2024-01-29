package radon.jujutsu_kaisen.ability.ten_shadows;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.AbilityTriggerEvent;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.DomainExpansion;
import radon.jujutsu_kaisen.ability.base.Summon;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.entity.ChimeraShadowGardenEntity;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import radon.jujutsu_kaisen.entity.ten_shadows.base.TenShadowsSummon;

import java.util.ArrayList;
import java.util.List;

public class ChimeraShadowGarden extends DomainExpansion implements DomainExpansion.IOpenDomain {
    @Override
    public void onHitEntity(DomainExpansionEntity domain, LivingEntity owner, LivingEntity entity, boolean instant) {

    }

    @Override
    public void onHitBlock(DomainExpansionEntity domain, LivingEntity owner, BlockPos pos) {

    }

    @Override
    protected DomainExpansionEntity createBarrier(LivingEntity owner) {
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        int width = Math.round(this.getWidth() * cap.getDomainSize());
        int height = Math.round(this.getHeight() * cap.getDomainSize());

        ChimeraShadowGardenEntity domain = new ChimeraShadowGardenEntity(owner, this, width, height);
        owner.level().addFreshEntity(domain);

        if (owner.level() instanceof ServerLevel level) {
            List<TenShadowsSummon> summons = new ArrayList<>();

            for (Entity entity : cap.getSummons()) {
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
    public int getWidth() {
        return 32;
    }

    @Override
    public int getHeight() {
        return 8;
    }

    @Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ChimeraShadowGardenForgeEvents {
        @SubscribeEvent
        public static void onAbilityTrigger(AbilityTriggerEvent.Pre event) {
            LivingEntity owner = event.getEntity();

            if (JJKAbilities.hasToggled(owner, JJKAbilities.CHIMERA_SHADOW_GARDEN.get())) {
                if (event.getAbility() instanceof Summon<?> summon && summon.isTamed(owner)) {
                    summon.spawn(owner, true);
                }
            }
        }
    }
}
