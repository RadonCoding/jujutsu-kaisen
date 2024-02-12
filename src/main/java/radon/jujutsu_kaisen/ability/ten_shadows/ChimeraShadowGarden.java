package radon.jujutsu_kaisen.ability.ten_shadows;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.AbilityTriggerEvent;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.DomainExpansion;
import radon.jujutsu_kaisen.ability.base.Summon;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;
import radon.jujutsu_kaisen.entity.domain.ChimeraShadowGardenEntity;
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
        ISorcererData data = owner.getData(JJKAttachmentTypes.SORCERER);

        if (data == null) return null;

        int width = Math.round(this.getWidth() * data.getDomainSize());
        int height = Math.round(this.getHeight() * data.getDomainSize());

        ChimeraShadowGardenEntity domain = new ChimeraShadowGardenEntity(owner, this, width, height);
        owner.level().addFreshEntity(domain);

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
    public int getWidth() {
        return 32;
    }

    @Override
    public int getHeight() {
        return 8;
    }

    @Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void onAbilityTrigger(AbilityTriggerEvent.Pre event) {
            LivingEntity owner = event.getEntity();

            ISorcererData data = owner.getData(JJKAttachmentTypes.SORCERER);

            if (!data.hasToggled(JJKAbilities.CHIMERA_SHADOW_GARDEN.get())) return;

            if (event.getAbility() instanceof Summon<?> summon && summon.isTamed(owner)) {
                summon.spawn(owner, true);
            }
        }
    }
}
