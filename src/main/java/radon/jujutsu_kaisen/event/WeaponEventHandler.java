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
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.client.particle.LightningParticle;
import radon.jujutsu_kaisen.client.particle.ParticleColors;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.entity.projectile.ThrownChainProjectile;
import radon.jujutsu_kaisen.item.JJKItems;
import radon.jujutsu_kaisen.item.cursed_tool.DragonBoneItem;
import radon.jujutsu_kaisen.item.cursed_tool.KamutokeDaggerItem;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;
import radon.jujutsu_kaisen.util.CuriosUtil;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

import java.util.ArrayList;
import java.util.List;

public class WeaponEventHandler {
    @Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class WeaponEventHandlerForgeEvents {
        @SubscribeEvent
        public static void onLivingAttack(LivingAttackEvent event) {
            DamageSource source = event.getSource();

            if (!(source.getEntity() instanceof LivingEntity attacker)) return;

            LivingEntity victim = event.getEntity();

            if (victim.level().isClientSide) return;

            ItemStack stack = source.getDirectEntity() instanceof ThrownChainProjectile chain ? chain.getStack() : attacker.getItemInHand(InteractionHand.MAIN_HAND);

            List<Item> stacks = new ArrayList<>();
            stacks.add(stack.getItem());
            stacks.addAll(CuriosUtil.findSlots(attacker, attacker.getMainArm() == HumanoidArm.RIGHT ? "right_hand" : "left_hand")
                    .stream().map(ItemStack::getItem).toList());

            if (HelperMethods.isMelee(source)) {
                if (stacks.contains(JJKItems.INVERTED_SPEAR_OF_HEAVEN.get())) {
                    victim.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                        List<Ability> remove = new ArrayList<>();

                        for (Ability ability : cap.getToggled()) {
                            if (!ability.isTechnique()) continue;

                            remove.add(ability);
                        }
                        remove.forEach(cap::toggle);

                        if (victim instanceof ServerPlayer player) {
                            PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(cap.serializeNBT()), player);
                        }
                    });
                }
            }
        }

        @SubscribeEvent
        public static void onLivingDamage(LivingDamageEvent event) {
            DamageSource source = event.getSource();

            if (!(source.getEntity() instanceof LivingEntity attacker)) return;

            LivingEntity victim = event.getEntity();

            if (victim.level().isClientSide) return;

            ItemStack stack = source.getDirectEntity() instanceof ThrownChainProjectile chain ? chain.getStack() : attacker.getItemInHand(InteractionHand.MAIN_HAND);

            List<Item> stacks = new ArrayList<>();
            stacks.add(stack.getItem());
            stacks.addAll(CuriosUtil.findSlots(attacker, attacker.getMainArm() == HumanoidArm.RIGHT ? "right_hand" : "left_hand")
                    .stream().map(ItemStack::getItem).toList());

            if (stacks.contains(JJKItems.DRAGON_BONE.get()) && (JJKAbilities.hasToggled(victim, JJKAbilities.CURSED_ENERGY_FLOW.get()) ||
                    JJKAbilities.hasToggled(victim, JJKAbilities.FALLING_BLOSSOM_EMOTION.get()))) {
                DragonBoneItem.addEnergy(stack, 10.0F);
            }
        }

        @SubscribeEvent
        public static void onLivingHurt(LivingHurtEvent event) {
            DamageSource source = event.getSource();

            if (!(source.getEntity() instanceof LivingEntity attacker)) return;

            LivingEntity victim = event.getEntity();

            if (victim.level().isClientSide) return;

            ItemStack stack = source.getDirectEntity() instanceof ThrownChainProjectile chain ? chain.getStack() : attacker.getItemInHand(InteractionHand.MAIN_HAND);

            List<Item> stacks = new ArrayList<>();
            stacks.add(stack.getItem());
            stacks.addAll(CuriosUtil.findSlots(attacker, attacker.getMainArm() == HumanoidArm.RIGHT ? "right_hand" : "left_hand")
                    .stream().map(ItemStack::getItem).toList());

            if (HelperMethods.isMelee(source)) {
                if (JJKAbilities.hasTrait(attacker, Trait.HEAVENLY_RESTRICTION) && !source.is(JJKDamageSources.SPLIT_SOUL_KATANA) && stacks.contains(JJKItems.SPLIT_SOUL_KATANA.get())) {
                    victim.invulnerableTime = 0;

                    if (victim.hurt(JJKDamageSources.splitSoulKatanaAttack(attacker), event.getAmount())) {
                        if (victim.isDeadOrDying()) {
                            victim.invulnerableTime = 20;

                            event.setAmount(0.0F);
                            return;
                        }
                    }
                }

                if (stacks.contains(JJKItems.PLAYFUL_CLOUD.get())) {
                    Vec3 pos = attacker.getEyePosition().add(RotationUtil.getTargetAdjustedLookAngle(attacker));
                    attacker.level().explode(attacker, attacker.damageSources().explosion(attacker, null), null, pos.x, pos.y, pos.z, 1.0F, false, Level.ExplosionInteraction.NONE);
                }

                if (stacks.contains(JJKItems.INVERTED_SPEAR_OF_HEAVEN.get())) {
                    victim.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                        List<Ability> remove = new ArrayList<>();

                        for (Ability ability : cap.getToggled()) {
                            if (!ability.isTechnique()) continue;

                            remove.add(ability);
                        }
                        remove.forEach(cap::toggle);

                        if (victim instanceof ServerPlayer player) {
                            PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(cap.serializeNBT()), player);
                        }
                    });
                }

                if (stacks.contains(JJKItems.KAMUTOKE_DAGGER.get())) {
                    if (attacker.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
                        ISorcererData attackerCap = attacker.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

                        if (!(attacker instanceof Player player) || !player.getAbilities().instabuild) {
                            float cost = KamutokeDaggerItem.MELEE_COST * (attackerCap.hasTrait(Trait.SIX_EYES) ? 0.5F : 1.0F);
                            if (attackerCap.getEnergy() < cost) return;
                            attackerCap.useEnergy(cost);
                        }

                        victim.invulnerableTime = 0;

                        if (victim.hurt(JJKDamageSources.jujutsuAttack(attacker, null), KamutokeDaggerItem.MELEE_DAMAGE * attackerCap.getRealPower())) {
                            if (victim.isDeadOrDying()) {
                                victim.invulnerableTime = 20;

                                event.setAmount(0.0F);
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
                            PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(attackerCap.serializeNBT()), player);
                        }
                    }
                }
            }
        }
    }
}
