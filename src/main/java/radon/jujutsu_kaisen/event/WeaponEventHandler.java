package radon.jujutsu_kaisen.event;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.event.entity.living.LivingAttackEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingHurtEvent;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.registry.JJKAbilities;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.Summon;
import radon.jujutsu_kaisen.cursed_technique.registry.JJKCursedTechniques;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.Trait;
import radon.jujutsu_kaisen.client.particle.LightningParticle;
import radon.jujutsu_kaisen.client.particle.ParticleColors;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.data.stat.ISkillData;
import radon.jujutsu_kaisen.data.stat.Skill;
import radon.jujutsu_kaisen.effect.registry.JJKEffects;
import radon.jujutsu_kaisen.entity.projectile.ThrownChainProjectile;
import radon.jujutsu_kaisen.item.cursed_tool.ICursedTool;
import radon.jujutsu_kaisen.item.registry.JJKItems;
import radon.jujutsu_kaisen.item.cursed_tool.DragonBoneItem;
import radon.jujutsu_kaisen.item.cursed_tool.KamutokeDaggerItem;
import net.neoforged.neoforge.network.PacketDistributor;
import radon.jujutsu_kaisen.network.packet.s2c.SyncAbilityDataS2CPacket;
import radon.jujutsu_kaisen.util.CuriosUtil;
import radon.jujutsu_kaisen.util.DamageUtil;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
