package radon.jujutsu_kaisen.ability.mimicry;


import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ItemStackedOnOtherEvent;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.DomainExpansion;
import radon.jujutsu_kaisen.ability.IClosedDomain;
import radon.jujutsu_kaisen.block.JJKBlocks;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;
import radon.jujutsu_kaisen.entity.domain.DomainExpansionEntity;
import radon.jujutsu_kaisen.entity.domain.AuthenticMutualLoveEntity;
import radon.jujutsu_kaisen.item.registry.JJKItems;

import java.util.List;

public class AuthenticMutualLove extends DomainExpansion implements IClosedDomain {
    @Override
    public void onHitEntity(DomainExpansionEntity domain, LivingEntity owner, LivingEntity entity, boolean instant) {
        super.onHitEntity(domain, owner, entity, instant);

        CursedTechnique technique = ((AuthenticMutualLoveEntity) domain).getTechnique();

        if (technique == null) return;

        if (!(technique.getDomain() instanceof DomainExpansion copied)) return;

        copied.onHitEntity(domain, owner, entity, instant);
    }

    @Override
    public void onHitBlock(Level level, DomainExpansionEntity domain, LivingEntity owner, BlockPos pos, boolean instant) {
        CursedTechnique technique = ((AuthenticMutualLoveEntity) domain).getTechnique();

        if (technique == null) return;

        if (!(technique.getDomain() instanceof DomainExpansion copied)) return;

        copied.onHitBlock(level, domain, owner, pos, instant);
    }

    @Override
    protected DomainExpansionEntity summon(LivingEntity owner) {
        AuthenticMutualLoveEntity domain = new AuthenticMutualLoveEntity(owner, this);
        owner.level().addFreshEntity(domain);

        return domain;
    }

    @Override
    public List<Block> getBlocks() {
        return List.of(JJKBlocks.DOMAIN_TRANSPARENT.get());
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
