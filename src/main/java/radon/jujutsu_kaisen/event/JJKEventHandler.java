package radon.jujutsu_kaisen.event;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.VeilHandler;
import radon.jujutsu_kaisen.ability.AbilityTriggerEvent;
import radon.jujutsu_kaisen.ability.CursedEnergyCostEvent;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.LivingHitByDomainEvent;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.misc.Barrage;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import radon.jujutsu_kaisen.entity.base.ISorcerer;
import radon.jujutsu_kaisen.entity.projectile.ThrownChainProjectile;
import radon.jujutsu_kaisen.entity.sorcerer.HeianSukunaEntity;
import radon.jujutsu_kaisen.entity.ten_shadows.MahoragaEntity;
import radon.jujutsu_kaisen.item.JJKItems;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;
import radon.jujutsu_kaisen.sound.JJKSounds;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.HashSet;
import java.util.Set;

public class JJKEventHandler {
    @Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void onMobSpawn(MobSpawnEvent.FinalizeSpawn event) {
            if (!VeilHandler.canSpawn(event.getEntity(), event.getX(), event.getY(), event.getZ())) {
                event.setSpawnCancelled(true);
            }
        }

        @SubscribeEvent
        public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
            if (event.getEntity() instanceof ServerPlayer player) {
                ISorcererData cap = player.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
                PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(cap.serializeNBT()), player);
            }
        }

        @SubscribeEvent
        public static void onPlayerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
            if (event.getEntity() instanceof ServerPlayer player) {
                ISorcererData cap = player.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
                PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(cap.serializeNBT()), player);
            }
        }

        @SubscribeEvent
        public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
            if (event.getEntity() instanceof ServerPlayer player) {
                ISorcererData cap = player.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
                PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(cap.serializeNBT()), player);
            }
        }

        @SubscribeEvent
        public static void onPlayerWakeUp(PlayerWakeUpEvent event) {
            Player player = event.getEntity();
            ISorcererData cap = player.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
            cap.setEnergy(cap.getMaxEnergy(player));
        }

        @SubscribeEvent
        public static void onPlayerClone(PlayerEvent.Clone event) {
            Player original = event.getOriginal();
            Player player = event.getEntity();

            original.reviveCaps();

            ISorcererData oldCap = original.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
            ISorcererData newCap = player.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

            newCap.deserializeNBT(oldCap.serializeNBT());

            if (event.isWasDeath()) {
                newCap.setEnergy(newCap.getMaxEnergy(player));
                newCap.resetCooldowns();
                newCap.resetBurnout();
                newCap.clearToggled();
                newCap.revive(false);
                newCap.resetBlackFlash();
                newCap.resetExtraEnergy();
                newCap.resetSpeedStacks();
            }
            original.invalidateCaps();
        }

        @SubscribeEvent
        public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
            if (event.getObject() instanceof LivingEntity entity) {
                if (entity instanceof Player || entity instanceof ISorcerer) {
                    SorcererDataHandler.attach(event);
                }
            }
        }

        @SubscribeEvent
        public static void onLivingTick(LivingEvent.LivingTickEvent event) {
            LivingEntity owner = event.getEntity();

            if (owner.isDeadOrDying()) return;

            owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> cap.tick(owner));
        }

        @SubscribeEvent
        public static void onLivingFall(LivingFallEvent event) {
            event.getEntity().getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                if (cap.hasTrait(Trait.HEAVENLY_RESTRICTION)) {
                    float distance = event.getDistance();
                    event.setDistance(distance * 0.25F);
                } else {
                    float distance = event.getDistance();
                    event.setDistance(distance * 0.5F);
                }
            });
        }

        @SubscribeEvent
        public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
            ISorcererData cap = event.getEntity().getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
            event.setNewSpeed((float) (event.getNewSpeed() * (1.0D + (cap.getSpeedStacks() * 3.0D))));
        }

        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public static void onLivingAttack(LivingAttackEvent event) {
            DamageSource source = event.getSource();
            Entity attacker = source.getEntity();
            LivingEntity victim = event.getEntity();

            if (victim.level().isClientSide) return;

            boolean melee = !source.isIndirect() && (source.is(DamageTypes.MOB_ATTACK) || source.is(DamageTypes.PLAYER_ATTACK));

            // Checks to prevent tamed creatures from attacking their owners and owners from attacking their tames
            if (attacker instanceof TamableAnimal tamable1 && attacker instanceof ISorcerer) {
                if (tamable1.isTame() && tamable1.getOwner() == victim) {
                    event.setCanceled(true);
                    return;
                } else if (victim instanceof TamableAnimal tamable2 && victim instanceof ISorcerer) {
                    // Prevent tames with the same owner from attacking each other
                    if (!tamable1.is(tamable2) && tamable1.isTame() && tamable2.isTame() && tamable1.getOwner() == tamable2.getOwner()) {
                        event.setCanceled(true);
                        return;
                    }
                }
            } else if (victim instanceof TamableAnimal tamable && victim instanceof ISorcerer) {
                // Prevent the owner from attacking the tame
                if (tamable.isTame() && tamable.getOwner() == attacker) {
                    event.setCanceled(true);
                    return;
                }
            } else if (victim instanceof DomainExpansionEntity domain && domain.getOwner() == attacker) {
                // Prevent the owner from destroying their own domain
                event.setCanceled(true);
                return;
            }

            if (attacker instanceof LivingEntity living) {
                ItemStack stack = null;

                if (source.getDirectEntity() instanceof ThrownChainProjectile chain) {
                    stack = chain.getStack();
                } else if (melee) {
                    stack = living.getItemInHand(InteractionHand.MAIN_HAND);
                }

                if (stack != null) {
                    if (stack.is(JJKItems.SPLIT_SOUL_KATANA.get())) {
                        if (attacker instanceof Player player) {
                            stack.hurtEnemy(victim, player);

                            if (stack.isEmpty()) {
                                player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
                            }
                        }
                        if (((LivingEntity) attacker).canAttack(victim)) {
                            victim.hurt(JJKDamageSources.soulAttack(living), event.getAmount());
                            event.setCanceled(true);
                        }
                    } else if (stack.is(JJKItems.PLAYFUL_CLOUD.get())) {
                        Vec3 pos = living.getEyePosition().add(living.getLookAngle());
                        living.level().explode(living, living.damageSources().explosion(attacker, null), null, pos.x(), pos.y(), pos.z(), 1.0F, false, Level.ExplosionInteraction.NONE);
                    } else if (stack.is(JJKItems.INVERTED_SPEAR_OF_HEAVEN.get())) {
                        victim.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                            cap.clearToggled();

                            if (victim instanceof ServerPlayer player) {
                                PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(cap.serializeNBT()), player);
                            }
                        });
                    }
                }
            }

            if (attacker instanceof MahoragaEntity) {
                victim.getCapability(SorcererDataHandler.INSTANCE).ifPresent(victimCap -> {
                    attacker.getCapability(SorcererDataHandler.INSTANCE).ifPresent(attackerCap -> {
                        Set<Ability> toggled = new HashSet<>(victimCap.getToggled());

                        for (Ability ability : toggled) {
                            if (!attackerCap.isAdaptedTo(ability)) continue;
                            victimCap.toggle(victim, ability);
                        }

                        if (victim instanceof ServerPlayer player) {
                            PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(victimCap.serializeNBT()), player);
                        }
                    });
                });
            }
        }

        @SubscribeEvent
        public static void onLivingHurt(LivingHurtEvent event) {
            LivingEntity victim = event.getEntity();

            if (victim.level().isClientSide) return;

            DamageSource source = event.getSource();

            // Your own cursed energy doesn't do as much damage
            if (source instanceof JJKDamageSources.JujutsuDamageSource) {
                if (source.getEntity() == victim) {
                    event.setAmount(event.getAmount() * 0.25F);
                }
            }

            if (JJKAbilities.hasToggled(victim, JJKAbilities.DOMAIN_AMPLIFICATION.get()) ||
                    !JJKAbilities.hasToggled(victim, JJKAbilities.WHEEL.get())) return;

            ISorcererData cap = victim.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

            if (!cap.isAdaptedTo(event.getSource())) {
                cap.tryAdapt(event.getSource());
            }

            if (!(victim instanceof MahoragaEntity)) return;

            if (cap.isAdaptedTo(event.getSource())) {
                victim.level().playSound(null, victim.getX(), victim.getY(), victim.getZ(), SoundEvents.SHIELD_BLOCK, SoundSource.MASTER, 1.0F, 1.0F);
            }
            event.setAmount(event.getAmount() * (1.0F - cap.getAdaptation(event.getSource())));
        }

        @SubscribeEvent
        public static void onLivingHitByDomain(LivingHitByDomainEvent event) {
            LivingEntity victim = event.getEntity();

            if (victim instanceof Mob mob && mob.canAttack(event.getAttacker())) mob.setTarget(event.getAttacker());
            if (!JJKAbilities.hasToggled(victim, JJKAbilities.WHEEL.get())) return;

            victim.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                if (!cap.isAdaptedTo(event.getAbility())) {
                    cap.tryAdapt(event.getAbility());
                }
            });
        }

        @SubscribeEvent
        public static void onLivingDeath(LivingDeathEvent event) {
            LivingEntity victim = event.getEntity();

            if (event.getSource().getEntity() instanceof LivingEntity) {
                if (!victim.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return;
                ISorcererData victimCap = victim.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

                if (victim instanceof TamableAnimal tamable && tamable.isTame()) return;

                if (victimCap.hasTrait(Trait.HEAVENLY_RESTRICTION)) return;

                int chance = ConfigHolder.SERVER.reverseCursedTechniqueChance.get();

                if (victimCap.getType() == JujutsuType.SORCERER) {
                    for (InteractionHand hand : InteractionHand.values()) {
                        ItemStack stack = victim.getItemInHand(hand);

                        if (stack.is(Items.TOTEM_OF_UNDYING)) {
                            chance /= 2;
                        }
                    }
                }

                if (HelperMethods.RANDOM.nextInt(chance) == 0) {
                    if (victimCap.getType() == JujutsuType.SORCERER) {
                        if (!victimCap.hasTrait(Trait.REVERSE_CURSED_TECHNIQUE)) {
                            victim.setHealth(victim.getMaxHealth() / 2);
                            victimCap.addTrait(Trait.REVERSE_CURSED_TECHNIQUE);

                            if (victim instanceof ServerPlayer player) {
                                PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(victimCap.serializeNBT()), player);
                            }
                            event.setCanceled(true);
                        }
                    }
                }
            }
        }

        @SubscribeEvent
        public static void onCursedEnergyCost(CursedEnergyCostEvent event) {
            LivingEntity owner = event.getEntity();

            if (!owner.level().isClientSide && owner.hasEffect(JJKEffects.CURSED_BUD.get())) {
                owner.hurt(JJKDamageSources.jujutsuAttack(owner, JJKAbilities.CURSED_BUD.get()), event.getCost() * 0.1F);
            }
        }

        @SubscribeEvent
        public static void onAbilityTrigger(AbilityTriggerEvent.Pre event) {
            CursedTechnique technique = JJKAbilities.getTechnique(event.getAbility());

            LivingEntity owner = event.getEntity();

            ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

            if (technique != null && cap.getAbsorbed().contains(technique)) {
                cap.unabsorb(technique);
            }

            if (owner instanceof HeianSukunaEntity entity && event.getAbility() == JJKAbilities.BARRAGE.get()) {
                entity.setBarrage(Barrage.DURATION * 2);
            }

            float cost = event.getAbility().getRealCost(owner);

            if (cost >= ConfigHolder.SERVER.sparkSoundThreshold.get().floatValue()) {
                float volume = cost / ConfigHolder.SERVER.sparkSoundThreshold.get().floatValue();
                owner.level().playSound(null, owner.getX(), owner.getY(), owner.getZ(),
                        JJKSounds.SPARK.get(), SoundSource.MASTER, volume, 1.0F);
            }
        }
    }

    @Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEvents {
        @SubscribeEvent
        public static void onCreateEntityAttributes(EntityAttributeCreationEvent event) {
            JJKEntities.createAttributes(event);
        }
    }
}
