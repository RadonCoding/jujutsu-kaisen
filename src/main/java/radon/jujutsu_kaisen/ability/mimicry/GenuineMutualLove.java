package radon.jujutsu_kaisen.ability.mimicry;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.ItemStackedOnOtherEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.base.DomainExpansion;
import radon.jujutsu_kaisen.block.JJKBlocks;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import radon.jujutsu_kaisen.entity.domain.base.GenuineMutualLoveEntity;
import radon.jujutsu_kaisen.entity.projectile.ThrownChainProjectile;
import radon.jujutsu_kaisen.item.JJKItems;
import radon.jujutsu_kaisen.util.CuriosUtil;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.ArrayList;
import java.util.List;

public class GenuineMutualLove extends DomainExpansion implements DomainExpansion.IClosedDomain {
    @Override
    public void onHitBlock(DomainExpansionEntity domain, LivingEntity owner, BlockPos pos) {

    }

    @Override
    protected DomainExpansionEntity createBarrier(LivingEntity owner) {
        int radius = Math.round(this.getRadius(owner));

        GenuineMutualLoveEntity domain = new GenuineMutualLoveEntity(owner, this, radius);
        owner.level().addFreshEntity(domain);

        return domain;
    }

    @Override
    public List<Block> getBlocks() {
        return List.of(JJKBlocks.DOMAIN.get());
    }

    @Override
    public List<Block> getFloorBlocks() {
        return List.of(JJKBlocks.GENUINE_MUTUAL_LOVE_ONE.get(), JJKBlocks.GENUINE_MUTUAL_LOVE_TWO.get(), JJKBlocks.GENUINE_MUTUAL_LOVE_THREE.get());
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
