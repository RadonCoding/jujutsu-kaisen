package radon.jujutsu_kaisen.event;


import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.common.world.chunk.RegisterTicketControllersEvent;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.SleepFinishedTimeEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.AbilityStopEvent;
import radon.jujutsu_kaisen.ability.AbilityTriggerEvent;
import radon.jujutsu_kaisen.ability.curse_manipulation.util.CurseManipulationUtil;
import radon.jujutsu_kaisen.ability.event.LivingHitByDomainEvent;
import radon.jujutsu_kaisen.ability.misc.Slam;
import radon.jujutsu_kaisen.ability.registry.JJKAbilities;
import radon.jujutsu_kaisen.binding_vow.JJKBindingVows;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;
import radon.jujutsu_kaisen.cursed_technique.registry.JJKCursedTechniques;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.damage.JJKDamageTypeTags;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.curse_manipulation.AbsorbedCurse;
import radon.jujutsu_kaisen.data.curse_manipulation.ICurseManipulationData;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.data.sorcerer.Trait;
import radon.jujutsu_kaisen.data.stat.ISkillData;
import radon.jujutsu_kaisen.data.stat.Skill;
import radon.jujutsu_kaisen.data.ten_shadows.ITenShadowsData;
import radon.jujutsu_kaisen.entity.JJKPartEntity;
import radon.jujutsu_kaisen.entity.curse.CursedSpirit;
import radon.jujutsu_kaisen.entity.domain.ClosedDomainExpansionEntity;
import radon.jujutsu_kaisen.entity.projectile.ThrownChainProjectile;
import radon.jujutsu_kaisen.entity.sorcerer.HeianSukunaEntity;
import radon.jujutsu_kaisen.entity.sorcerer.SukunaEntity;
import radon.jujutsu_kaisen.item.CursedToolItem;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;
import radon.jujutsu_kaisen.pact.JJKPacts;
import radon.jujutsu_kaisen.sound.JJKSounds;
import radon.jujutsu_kaisen.tags.JJKEntityTypeTags;
import radon.jujutsu_kaisen.tags.JJKItemTags;
import radon.jujutsu_kaisen.util.CuriosUtil;
import radon.jujutsu_kaisen.util.DamageUtil;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.PlayerUtil;

import java.util.ArrayList;
import java.util.List;

public class JJKEventHandler {
    @EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void onPlayerInteractEntity(PlayerInteractEvent.EntityInteract event) {
            LivingEntity attacker = event.getEntity();

            ItemStack stack = attacker.getItemInHand(event.getHand());

            if (!stack.is(JJKItemTags.CURSED_OBJECT) || !stack.has(DataComponents.FOOD)) return;

            if (!(event.getTarget() instanceof LivingEntity target)) return;

            if (target.isDeadOrDying()) return;

            if (!target.getType().is(JJKEntityTypeTags.FORCE_FEEDABLE)) return;

            IJujutsuCapability cap = target.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap == null) return;

            boolean feedable = target.getHealth() / target.getMaxHealth() <= ConfigHolder.SERVER.forceFeedHealthRequirement.get();

            if (!feedable) {
                if (target instanceof TamableAnimal tamable1) {
                    LivingEntity owner = tamable1.getOwner();

                    while (owner instanceof TamableAnimal tamable2 && tamable2.isTame()) {
                        owner = tamable2.getOwner();

                        if (owner == null) break;
                    }
                    feedable = owner == attacker;
                }
            }

            if (feedable) {
                ItemStack copy = stack.copy();
                stack.getItem().finishUsingItem(stack, target.level(), target);

                if (stack != copy) {
                    event.setCancellationResult(InteractionResult.SUCCESS);
                }
            }
        }

        @SubscribeEvent
        public static void onLivingHurt(LivingHurtEvent event) {
            LivingEntity victim = event.getEntity();

            if (victim.level().isClientSide) return;

            DamageSource source = event.getSource();

            float amount = event.getAmount();

            if (source.is(DamageTypes.FELL_OUT_OF_WORLD)) return;

            if (source.is(NeoForgeMod.POISON_DAMAGE)) {
                IJujutsuCapability cap = victim.getCapability(JujutsuCapabilityHandler.INSTANCE);

                if (cap != null) {
                    ISorcererData sorcererData = cap.getSorcererData();

                    if (sorcererData.getType() == JujutsuType.CURSE) {
                        event.setCanceled(true);
                        return;
                    }
                }
            }

            // We don't want to increase/decrease soul damage
            if (source.is(JJKDamageTypeTags.SOUL)) return;

            // Your own cursed energy doesn't do as much damage
            if (source instanceof JJKDamageSources.JujutsuDamageSource) {
                if (source.getEntity() == victim) {
                    event.setAmount(amount * 0.01F);
                }
            }

            // Perfect body generic melee increase
            if (DamageUtil.isMelee(source)) {
                if (source.getEntity() instanceof LivingEntity attacker) {
                    IJujutsuCapability cap = attacker.getCapability(JujutsuCapabilityHandler.INSTANCE);

                    if (cap != null) {
                        ISorcererData data = cap.getSorcererData();

                        if (data.hasTrait(Trait.PERFECT_BODY)) {
                            event.setAmount(amount * 2.0F);
                        }
                    }
                }
            }

            IJujutsuCapability cap = victim.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap == null) return;

            ISorcererData sorcererData = cap.getSorcererData();
            IAbilityData abilityData = cap.getAbilityData();
            ISkillData skillData = cap.getSkillData();

            boolean heavenly = sorcererData.hasTrait(Trait.HEAVENLY_RESTRICTION_BODY);

            Skill skill = heavenly
                    ? Skill.SHIELDING
                    : Skill.REINFORCEMENT;
            float armor = skillData.getSkill(skill);

            float reduction = (float) Math.log1p(1.0D + armor);

            boolean flow = abilityData.hasToggled(JJKAbilities.CURSED_ENERGY_FLOW.get());
            boolean shield = abilityData.isChanneling(JJKAbilities.CURSED_ENERGY_SHIELD.get());

            if (heavenly) {
                reduction *= 4.0F;
            } else {
                if (flow) {
                    reduction *= 2.0F;
                } else if (shield) {
                    reduction *= 4.0F;
                }
            }

            float blocked = amount / (1.0F + reduction);

            if (flow && !(victim instanceof Player player && player.getAbilities().instabuild)) {
                float cost = blocked * (sorcererData.hasTrait(Trait.SIX_EYES) ? 0.5F : 1.0F);

                if (sorcererData.getEnergy() < cost) return;

                sorcererData.useEnergy(cost);

                if (victim instanceof ServerPlayer player) {
                    PacketDistributor.sendToPlayer(
                            player,
                            new SyncSorcererDataS2CPacket(sorcererData.serializeNBT(player.registryAccess()))
                    );
                }
            }

            event.setAmount(blocked);
        }

        @SubscribeEvent
        public static void onSleepFinished(SleepFinishedTimeEvent event) {
            if (!(event.getLevel() instanceof ServerLevel level)) return;

            for (ServerPlayer player : level.players()) {
                if (!player.isSleepingLongEnough()) continue;

                IJujutsuCapability cap = player.getCapability(JujutsuCapabilityHandler.INSTANCE);

                if (cap == null) continue;

                ISorcererData data = cap.getSorcererData();

                data.setEnergy(data.getMaxEnergy());

                PacketDistributor.sendToPlayer(player, new SyncSorcererDataS2CPacket(data.serializeNBT(player.registryAccess())));
            }
        }

        @SubscribeEvent
        public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
            if (!(event.getEntity() instanceof ServerPlayer player)) return;

            for (SukunaEntity sukuna : player.level().getEntitiesOfClass(SukunaEntity.class, AABB.ofSize(player.position(),
                    8.0D, 8.0D, 8.0D))) {
                if (sukuna.getOwner() == player) {
                    player.setGameMode(sukuna.getOriginal(player));
                }
            }
        }

        @SubscribeEvent
        public static void onLivingDamage(LivingDamageEvent event) {
            LivingEntity victim = event.getEntity();

            if (!(event.getSource().getEntity() instanceof LivingEntity owner)) return;

            IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap == null) return;

            IAbilityData data = cap.getAbilityData();

            // If the target is dead we should not trigger any IAttack's
            if (victim.getHealth() - event.getAmount() <= 0.0F) return;

            data.attack(event.getSource(), victim);

            // If the target died from the IAttack's then cancel (yes this is very scuffed lmao)
            if (victim.getHealth() - event.getAmount() <= 0.0F) event.setCanceled(true);
        }

        @SubscribeEvent
        public static void onLivingFall(LivingFallEvent event) {
            LivingEntity victim = event.getEntity();

            if (Slam.TARGETS.containsKey(victim.getUUID())) {
                Slam.onHitGround(victim, event.getDistance());
                event.setDamageMultiplier(0.0F);
            }

            IJujutsuCapability cap = victim.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap == null) return;

            ISorcererData sorcererData = cap.getSorcererData();
            IAbilityData abilityData = cap.getAbilityData();

            if (abilityData.hasToggled(JJKAbilities.CURSED_ENERGY_FLOW.get())) {
                event.setDistance(event.getDistance() * 0.5F);
            }

            if (sorcererData.hasTrait(Trait.HEAVENLY_RESTRICTION_BODY)) {
                event.setDistance(event.getDistance() * 0.1F);
            }
        }

        @SubscribeEvent
        public static void onLivingAttack(LivingAttackEvent event) {
            LivingEntity victim = event.getEntity();

            if (victim.level().isClientSide) return;

            DamageSource source = event.getSource();

            if (!(source.getEntity() instanceof LivingEntity attacker)) return;

            if (ConfigHolder.SERVER.realisticCurses.get()) {
                IJujutsuCapability victimCap = victim.getCapability(JujutsuCapabilityHandler.INSTANCE);

                if (victimCap != null) {
                    ISorcererData victimData = victimCap.getSorcererData();

                    if (victimData.getType() == JujutsuType.CURSE || victimData.getType() == JujutsuType.SHIKIGAMI) {
                        ItemStack stack = source.getDirectEntity() instanceof ThrownChainProjectile chain ? chain.getStack() : attacker.getItemInHand(InteractionHand.MAIN_HAND);

                        List<Item> stacks = new ArrayList<>();
                        stacks.add(stack.getItem());
                        stacks.addAll(CuriosUtil.findSlots(attacker, attacker.getMainArm() == HumanoidArm.RIGHT ? "right_hand" : "left_hand")
                                .stream().map(ItemStack::getItem).toList());

                        boolean cursed = false;

                        if (event.getSource() instanceof JJKDamageSources.JujutsuDamageSource) {
                            cursed = true;
                        } else if (DamageUtil.isMelee(source) && (stacks.stream().anyMatch(item -> item instanceof CursedToolItem))) {
                            cursed = true;
                        } else {
                            IJujutsuCapability attackerCap = attacker.getCapability(JujutsuCapabilityHandler.INSTANCE);

                            if (attackerCap != null) {
                                ISorcererData attackerData = attackerCap.getSorcererData();
                                cursed = attackerData.getEnergy() > 0.0F;
                            }
                        }

                        if (!cursed) {
                            event.setCanceled(true);
                        }
                    }
                }
            }
        }

        @SubscribeEvent
        public static void onAttackEntity(AttackEntityEvent event) {
            if (event.getTarget() instanceof JJKPartEntity<?>) {
                Entity parent = ((JJKPartEntity<?>) event.getTarget()).getParent();
                event.getEntity().attack(parent);
                event.setCanceled(true);
            }
        }

        @SubscribeEvent
        public static void onLivingDeath(LivingDeathEvent event) {
            LivingEntity victim = event.getEntity();

            if (victim.level().isClientSide) return;

            IJujutsuCapability cap = victim.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap == null) return;

            ISorcererData sorcererData = cap.getSorcererData();

            DamageSource source = event.getSource();

            if (!(source.getEntity() instanceof LivingEntity attacker)) return;

            if (attacker instanceof ServerPlayer player) {
                if (victim instanceof HeianSukunaEntity && sorcererData.getFingers() == 20) {
                    PlayerUtil.giveAdvancement(player, "the_strongest_of_all_time");
                }
            }
        }

        @SubscribeEvent
        public static void onAbilityStop(AbilityStopEvent event) {
            Ability ability = event.getAbility();

            CursedTechnique technique = JJKCursedTechniques.getTechnique(ability);

            LivingEntity owner = event.getEntity();

            IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap == null) return;

            ICurseManipulationData data = cap.getCurseManipulationData();

            // Handling removal of absorbed techniques from curse manipulation
            if (technique != null && data.getAbsorbed().contains(technique)) {
                data.unabsorb(technique);
            }
        }

        @SubscribeEvent
        public static void onAbilityTrigger(AbilityTriggerEvent.Pre event) {
            Ability ability = event.getAbility();

            CursedTechnique technique = JJKCursedTechniques.getTechnique(ability);

            LivingEntity owner = event.getEntity();

            IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap == null) return;

            ICurseManipulationData data = cap.getCurseManipulationData();

            if (ability.getActivationType(owner) == Ability.ActivationType.INSTANT) {
                // Handling removal of absorbed techniques from curse manipulation
                if (technique != null && data.getAbsorbed().contains(technique)) {
                    data.unabsorb(technique);
                }
            }

            if (ability.getCost(owner) >= 500.0F) {
                owner.level().playSound(null, owner.getX(), owner.getY(), owner.getZ(),
                        JJKSounds.SPARK.get(), SoundSource.MASTER, 2.0F, 1.0F);
            }
        }
    }

    @EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
    public static class JJKEventHandlerModEvents {
        @SubscribeEvent
        public static void onNewRegistry(NewRegistryEvent event) {
            event.register(JJKAbilities.ABILITY_REGISTRY);
            event.register(JJKCursedTechniques.CURSED_TECHNIQUE_REGISTRY);
            event.register(JJKBindingVows.BINDING_VOW_REGISTRY);
            event.register(JJKPacts.PACT_REGISTRY);
        }
    }
}