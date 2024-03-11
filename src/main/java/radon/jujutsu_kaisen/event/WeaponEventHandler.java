package radon.jujutsu_kaisen.event;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.event.entity.living.LivingAttackEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.Trait;
import radon.jujutsu_kaisen.client.particle.LightningParticle;
import radon.jujutsu_kaisen.client.particle.ParticleColors;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.entity.projectile.ThrownChainProjectile;
import radon.jujutsu_kaisen.item.JJKItems;
import radon.jujutsu_kaisen.item.cursed_tool.DragonBoneItem;
import radon.jujutsu_kaisen.item.cursed_tool.KamutokeDaggerItem;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncAbilityDataS2CPacket;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;
import radon.jujutsu_kaisen.util.CuriosUtil;
import radon.jujutsu_kaisen.util.DamageUtil;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class WeaponEventHandler {
    @Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEvents {
        @SubscribeEvent(priority = EventPriority.LOWEST)
        public static void onLivingAttackLow(LivingAttackEvent event) {
            LivingEntity victim = event.getEntity();

            if (victim.level().isClientSide) return;

            DamageSource source = event.getSource();

            if (!(source.getEntity() instanceof LivingEntity attacker)) return;

            List<Item> stacks = new ArrayList<>();

            if (source.getDirectEntity() instanceof ThrownChainProjectile chain) {
                stacks.add(chain.getStack().getItem());
            } else {
                stacks.add(attacker.getItemInHand(InteractionHand.MAIN_HAND).getItem());
                stacks.addAll(CuriosUtil.findSlots(attacker, attacker.getMainArm() == HumanoidArm.RIGHT ? "right_hand" : "left_hand")
                        .stream().map(ItemStack::getItem).collect(Collectors.toSet()));
            }

            if (!DamageUtil.isMelee(source) && !(source.getDirectEntity() instanceof ThrownChainProjectile)) return;

            IJujutsuCapability attackerCap = attacker.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (attackerCap != null) {
                ISorcererData attackerData = attackerCap.getSorcererData();

                if (attackerData.hasTrait(Trait.HEAVENLY_RESTRICTION) && !source.is(JJKDamageSources.SPLIT_SOUL_KATANA) && stacks.contains(JJKItems.SPLIT_SOUL_KATANA.get())) {
                    event.setCanceled(victim.hurt(JJKDamageSources.splitSoulKatanaAttack(attacker), event.getAmount()));
                }
            }
        }

        @SubscribeEvent
        public static void onLivingAttack(LivingAttackEvent event) {
            LivingEntity victim = event.getEntity();

            if (victim.level().isClientSide) return;

            DamageSource source = event.getSource();

            if (!(source.getEntity() instanceof LivingEntity attacker)) return;

            List<Item> stacks = new ArrayList<>();

            if (source.getDirectEntity() instanceof ThrownChainProjectile chain) {
                stacks.add(chain.getStack().getItem());
            } else {
                stacks.add(attacker.getItemInHand(InteractionHand.MAIN_HAND).getItem());
                stacks.addAll(CuriosUtil.findSlots(attacker, attacker.getMainArm() == HumanoidArm.RIGHT ? "right_hand" : "left_hand")
                        .stream().map(ItemStack::getItem).collect(Collectors.toSet()));
            }

            if (!DamageUtil.isMelee(source) && !(source.getDirectEntity() instanceof ThrownChainProjectile)) return;

            IJujutsuCapability attackerCap = attacker.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (attackerCap != null) {
                ISorcererData attackerData = attackerCap.getSorcererData();

                if (stacks.contains(JJKItems.KAMUTOKE_DAGGER.get())) {
                    if (!(attacker instanceof Player player) || !player.getAbilities().instabuild) {
                        float cost = KamutokeDaggerItem.MELEE_COST * (attackerData.hasTrait(Trait.SIX_EYES) ? 0.5F : 1.0F);
                        if (attackerData.getEnergy() < cost) return;
                        attackerData.useEnergy(cost);
                    }

                    if (victim.hurt(JJKDamageSources.jujutsuAttack(attacker, null), KamutokeDaggerItem.MELEE_DAMAGE * attackerData.getBaseOutput())) {
                        if (victim.isDeadOrDying()) {
                            event.setCanceled(true);
                            return;
                        }

                        victim.addEffect(new MobEffectInstance(JJKEffects.STUN.get(), KamutokeDaggerItem.STUN, 0, false, false, false));

                        attacker.level().playSound(null, victim.getX(), victim.getY(), victim.getZ(),
                                SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.MASTER, 1.0F, 0.5F + HelperMethods.RANDOM.nextFloat() * 0.2F);

                        for (int i = 0; i < 32; i++) {
                            double offsetX = HelperMethods.RANDOM.nextGaussian() * 1.5D;
                            double offsetY = HelperMethods.RANDOM.nextGaussian() * 1.5D;
                            double offsetZ = HelperMethods.RANDOM.nextGaussian() * 1.5D;
                            ((ServerLevel) attacker.level()).sendParticles(new LightningParticle.LightningParticleOptions(ParticleColors.getCursedEnergyColorBright(attacker), 0.5F, 1),
                                    victim.getX() + offsetX, victim.getY() + offsetY, victim.getZ() + offsetZ,
                                    0, 0.0D, 0.0D, 0.0D, 0.0D);
                        }
                    }

                    if (attacker instanceof ServerPlayer player) {
                        PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(attackerData.serializeNBT()), player);
                    }
                }
            }

            if (stacks.contains(JJKItems.PLAYFUL_CLOUD.get())) {
                Vec3 pos = attacker.getEyePosition().add(RotationUtil.getTargetAdjustedLookAngle(attacker));
                attacker.level().explode(attacker, attacker.damageSources().explosion(attacker, null), null, pos.x, pos.y, pos.z, 1.0F, false, Level.ExplosionInteraction.NONE);
            }

            if (stacks.contains(JJKItems.INVERTED_SPEAR_OF_HEAVEN.get())) {
                IJujutsuCapability victimCap = victim.getCapability(JujutsuCapabilityHandler.INSTANCE);

                if (victimCap != null) {
                    IAbilityData victimData = victimCap.getAbilityData();

                    for (Ability ability : victimData.getToggled()) {
                        if (!ability.isTechnique()) continue;

                        victimData.disrupt(ability, 20);
                    }

                    if (victim instanceof ServerPlayer player) {
                        PacketHandler.sendToClient(new SyncAbilityDataS2CPacket(victimData.serializeNBT()), player);
                    }
                }
            }
        }

        @SubscribeEvent
        public static void onLivingDamage(LivingDamageEvent event) {
            DamageSource source = event.getSource();

            if (!(source.getEntity() instanceof LivingEntity attacker)) return;

            LivingEntity victim = event.getEntity();

            if (victim.level().isClientSide) return;

            IJujutsuCapability cap = victim.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap == null) return;

            IAbilityData data = cap.getAbilityData();

            List<ItemStack> stacks = new ArrayList<>();

            if (source.getDirectEntity() instanceof ThrownChainProjectile chain) {
                stacks.add(chain.getStack());
            } else {
                stacks.add(attacker.getItemInHand(InteractionHand.MAIN_HAND));
                stacks.addAll(CuriosUtil.findSlots(attacker, attacker.getMainArm() == HumanoidArm.RIGHT ? "right_hand" : "left_hand"));
            }
            stacks.removeIf(ItemStack::isEmpty);

            if (!DamageUtil.isMelee(source) && !(source.getDirectEntity() instanceof ThrownChainProjectile)) return;

            for (ItemStack stack : stacks) {
                if (stack.is(JJKItems.DRAGON_BONE.get()) && (data.hasToggled(JJKAbilities.CURSED_ENERGY_FLOW.get()) ||
                        data.hasToggled(JJKAbilities.FALLING_BLOSSOM_EMOTION.get()))) {
                    DragonBoneItem.addEnergy(stack, 10.0F);
                }
            }
        }
    }
}
