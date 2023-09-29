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
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.entity.ChimeraShadowGardenEntity;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import radon.jujutsu_kaisen.entity.base.TenShadowsSummon;

import java.util.ArrayList;
import java.util.List;

public class ChimeraShadowGarden extends DomainExpansion implements DomainExpansion.IOpenDomain {
    @Override
    public void onHitEntity(DomainExpansionEntity domain, LivingEntity owner, LivingEntity entity) {

    }

    @Override
    public void onHitBlock(DomainExpansionEntity domain, LivingEntity owner, BlockPos pos) {

    }

    @Override
    protected void createBarrier(LivingEntity owner) {
        owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
            int width = Math.round(this.getWidth() * cap.getDomainSize());
            int height = Math.round(this.getWidth() * cap.getDomainSize());

            ChimeraShadowGardenEntity domain = new ChimeraShadowGardenEntity(owner, this, width, height);
            owner.level().addFreshEntity(domain);

            cap.setDomain(domain);

            if (owner.level() instanceof ServerLevel level) {
                List<TenShadowsSummon> summons = new ArrayList<>();

                for (Entity entity : cap.getSummons(level)) {
                    if (entity instanceof TenShadowsSummon summon && summon.isTame()) summons.add(summon);
                }

                for (TenShadowsSummon summon : summons) {
                    if (summons.stream().noneMatch(x -> x.getJujutsuType() == summon.getJujutsuType() && x.isClone())) {
                        summon.getAbility().spawn(owner, true);
                    }
                }
            }
        });
    }

    @Override
    public int getWidth() {
        return 32;
    }

    @Override
    public int getHeight() {
        return 16;
    }

    @Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void onAbilityTrigger(AbilityTriggerEvent event) {
            LivingEntity owner = event.getEntity();

            if (JJKAbilities.hasToggled(owner, JJKAbilities.CHIMERA_SHADOW_GARDEN.get())) {
                if (event.getAbility() instanceof Summon<?> summon && summon.isTamed(owner)) {
                    summon.spawn(owner, true);
                }
            }
        }
    }
}
