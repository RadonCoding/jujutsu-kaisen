package radon.jujutsu_kaisen.ability.mimicry;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.event.ItemStackedOnOtherEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.base.DomainExpansion;
import radon.jujutsu_kaisen.block.JJKBlocks;
import radon.jujutsu_kaisen.cursed_technique.base.ICursedTechnique;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import radon.jujutsu_kaisen.entity.domain.base.AllEncompassingUnequiovocalLove;
import radon.jujutsu_kaisen.item.JJKItems;

import java.util.List;

public class AllEncompassingUnequivocalLove extends DomainExpansion implements DomainExpansion.IClosedDomain {
    @Override
    public void onHitEntity(DomainExpansionEntity domain, LivingEntity owner, LivingEntity entity, boolean instant) {
        super.onHitEntity(domain, owner, entity, instant);

        ICursedTechnique technique = ((AllEncompassingUnequiovocalLove) domain).getTechnique();

        if (technique == null) return;

        if (!(technique.getDomain() instanceof DomainExpansion copied)) return;

        copied.onHitEntity(domain, owner, entity, instant);
    }

    @Override
    public void onHitBlock(DomainExpansionEntity domain, LivingEntity owner, BlockPos pos) {
        ICursedTechnique technique = ((AllEncompassingUnequiovocalLove) domain).getTechnique();

        if (technique == null) return;

        if (!(technique.getDomain() instanceof DomainExpansion copied)) return;

        copied.onHitBlock(domain, owner, pos);
    }

    @Override
    protected DomainExpansionEntity createBarrier(LivingEntity owner) {
        int radius = Math.round(this.getRadius(owner));

        AllEncompassingUnequiovocalLove domain = new AllEncompassingUnequiovocalLove(owner, this, radius);
        owner.level().addFreshEntity(domain);

        return domain;
    }

    @Override
    public List<Block> getBlocks() {
        return List.of(JJKBlocks.NIGHT_SKY.get());
    }

    @Override
    public List<Block> getFloorBlocks() {
        return List.of(JJKBlocks.ALL_ENCOMPASSING_UNEQUIVOCAL_LOVE_ONE.get(), JJKBlocks.ALL_ENCOMPASSING_UNEQUIVOCAL_LOVE_TWO.get(), JJKBlocks.ALL_ENCOMPASSING_UNEQUIVOCAL_LOVE_THREE.get());
    }

    @Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class GenuineMutualLoveForgeEvents {
        @SubscribeEvent
        public static void onItemStackedOnOther(ItemStackedOnOtherEvent event) {
            ItemStack stack = event.getCarriedItem();

            if (stack.is(JJKItems.MIMICRY_KATANA_BLACK.get()) || stack.is(JJKItems.MIMICRY_KATANA_WHITE.get())) {
                event.setCanceled(true);
            }
        }
    }
}
