package radon.jujutsu_kaisen.event;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.LivingAttackEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingHurtEvent;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.entity.projectile.ThrownChainProjectile;
import radon.jujutsu_kaisen.item.cursed_tool.ICursedTool;
import radon.jujutsu_kaisen.util.CuriosUtil;
import radon.jujutsu_kaisen.util.DamageUtil;

import java.util.ArrayList;
import java.util.List;

public class WeaponEventHandler {
    private static List<ItemStack> collectStacks(LivingEntity entity, DamageSource source) {
        List<ItemStack> stacks = new ArrayList<>();

        if (source.getDirectEntity() instanceof ThrownChainProjectile chain) {
            stacks.add(chain.getStack());
        } else {
            stacks.add(entity.getItemInHand(InteractionHand.MAIN_HAND));
            stacks.addAll(CuriosUtil.findSlots(entity, entity.getMainArm() == HumanoidArm.RIGHT ? "right_hand" : "left_hand"));
        }
        stacks.removeIf(ItemStack::isEmpty);

        return stacks;
    }

    @EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void onLivingAttack(LivingAttackEvent event) {
            LivingEntity victim = event.getEntity();

            if (victim.level().isClientSide) return;

            DamageSource source = event.getSource();

            if (!(source.getEntity() instanceof LivingEntity attacker)) return;

            if (!DamageUtil.isMelee(source) && !(source.getDirectEntity() instanceof ThrownChainProjectile)) return;

            List<ItemStack> stacks = collectStacks(attacker, source);

            for (ItemStack stack : stacks) {
                if (!(stack.getItem() instanceof ICursedTool tool)) continue;

                if (event.isCanceled()) break;

                event.setCanceled(tool.doPreHurtEffects(stack, source, attacker, victim, event.getAmount()));
            }
        }

        @SubscribeEvent
        public static void onLivingHurt(LivingHurtEvent event) {
            DamageSource source = event.getSource();

            if (!(source.getEntity() instanceof LivingEntity attacker)) return;

            LivingEntity victim = event.getEntity();

            if (victim.level().isClientSide) return;

            if (!DamageUtil.isMelee(source) && !(source.getDirectEntity() instanceof ThrownChainProjectile)) return;

            List<ItemStack> stacks = collectStacks(attacker, source);

            for (ItemStack stack : stacks) {
                if (!(stack.getItem() instanceof ICursedTool tool)) continue;

                event.setAmount(tool.doHurtEffects(stack, source, attacker, victim, event.getAmount()));
            }
        }

        @SubscribeEvent
        public static void onLivingDamage(LivingDamageEvent event) {
            DamageSource source = event.getSource();

            if (!(source.getEntity() instanceof LivingEntity attacker)) return;

            LivingEntity victim = event.getEntity();

            if (victim.level().isClientSide) return;

            if (!DamageUtil.isMelee(source) && !(source.getDirectEntity() instanceof ThrownChainProjectile)) return;

            List<ItemStack> stacks = collectStacks(attacker, source);

            for (ItemStack stack : stacks) {
                if (!(stack.getItem() instanceof ICursedTool tool)) continue;

                tool.doPostHurtEffects(stack,source, attacker, victim);
            }
        }
    }
}
