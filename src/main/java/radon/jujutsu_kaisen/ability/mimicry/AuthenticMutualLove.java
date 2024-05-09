package radon.jujutsu_kaisen.ability.mimicry;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.event.ItemStackedOnOtherEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.DomainExpansion;
import radon.jujutsu_kaisen.block.JJKBlocks;
import radon.jujutsu_kaisen.cursed_technique.ICursedTechnique;
import radon.jujutsu_kaisen.entity.DomainExpansionEntity;
import radon.jujutsu_kaisen.entity.domain.AuthenticMutualLoveEntity;
import radon.jujutsu_kaisen.item.registry.JJKItems;

import java.util.List;

public class AuthenticMutualLove extends DomainExpansion implements DomainExpansion.IClosedDomain {
    @Override
    public void onHitEntity(DomainExpansionEntity domain, LivingEntity owner, LivingEntity entity, boolean instant) {
        super.onHitEntity(domain, owner, entity, instant);

        ICursedTechnique technique = ((AuthenticMutualLoveEntity) domain).getTechnique();

        if (technique == null) return;

        if (!(technique.getDomain() instanceof DomainExpansion copied)) return;

        copied.onHitEntity(domain, owner, entity, instant);
    }

    @Override
    public void onHitBlock(DomainExpansionEntity domain, LivingEntity owner, BlockPos pos, boolean instant) {
        ICursedTechnique technique = ((AuthenticMutualLoveEntity) domain).getTechnique();

        if (technique == null) return;

        if (!(technique.getDomain() instanceof DomainExpansion copied)) return;

        copied.onHitBlock(domain, owner, pos, instant);
    }

    @Override
    protected DomainExpansionEntity createBarrier(LivingEntity owner) {
        AuthenticMutualLoveEntity domain = new AuthenticMutualLoveEntity(owner, this);
        owner.level().addFreshEntity(domain);

        return domain;
    }

    @Override
    public List<Block> getBlocks() {
        return List.of(JJKBlocks.AUTHENTIC_MUTUAL_LOVE.get());
    }

    @Override
    public List<Block> getFloorBlocks() {
        return List.of(JJKBlocks.AUTHENTIC_MUTUAL_LOVE_ONE.get(), JJKBlocks.AUTHENTIC_MUTUAL_LOVE_TWO.get(), JJKBlocks.AUTHENTIC_MUTUAL_LOVE_THREE.get());
    }

    @EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void onItemStackedOnOther(ItemStackedOnOtherEvent event) {
            ItemStack stack = event.getCarriedItem();

            if (stack.is(JJKItems.MIMICRY_KATANA_BLACK.get()) || stack.is(JJKItems.MIMICRY_KATANA_WHITE.get())) {
                event.setCanceled(true);
            }
        }
    }
}
