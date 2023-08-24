package radon.jujutsu_kaisen;

import net.minecraft.server.level.ServerLevel;
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
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.LivingHitByDomainEvent;
import radon.jujutsu_kaisen.capability.data.OverlayDataHandler;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.WheelEntity;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import radon.jujutsu_kaisen.entity.base.ISorcerer;
import radon.jujutsu_kaisen.entity.base.SummonEntity;
import radon.jujutsu_kaisen.entity.sorcerer.MegunaRyomenEntity;
import radon.jujutsu_kaisen.entity.sorcerer.SaturoGojoEntity;
import radon.jujutsu_kaisen.entity.sorcerer.SukunaRyomenEntity;
import radon.jujutsu_kaisen.entity.ten_shadows.MahoragaEntity;
import radon.jujutsu_kaisen.item.JJKItems;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.concurrent.atomic.AtomicBoolean;

public class JJKEventHandler {
    @Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
            if (event.getEntity() instanceof ServerPlayer player) {
                player.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap ->
                        PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(cap.serializeNBT()), player));

                player.getCapability(OverlayDataHandler.INSTANCE).ifPresent(cap ->
                        cap.sync(player));
            }
        }

        @SubscribeEvent
        public static void onPlayerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
            if (event.getEntity() instanceof ServerPlayer player) {
                player.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap ->
                        PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(cap.serializeNBT()), player));
            }
        }

        @SubscribeEvent
        public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
            if (event.getEntity() instanceof ServerPlayer player) {
                player.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap ->
                        PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(cap.serializeNBT()), player));
            }
        }

        @SubscribeEvent
        public static void onPlayerWakeUp(PlayerWakeUpEvent event) {
            Player player = event.getEntity();

            player.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap ->
                    cap.setEnergy(cap.getMaxEnergy()));
        }

        @SubscribeEvent
        public static void onPlayerClone(PlayerEvent.Clone event) {
            Player original = event.getOriginal();
            Player player = event.getEntity();

            original.reviveCaps();

            original.getCapability(SorcererDataHandler.INSTANCE).ifPresent(oldCap -> {
                player.getCapability(SorcererDataHandler.INSTANCE).ifPresent(newCap -> {
                    newCap.deserializeNBT(oldCap.serializeNBT());

                    if (event.isWasDeath()) {
                        newCap.setEnergy(newCap.getMaxEnergy());
                        newCap.resetCooldowns();
                        newCap.resetBurnout();
                        newCap.clearToggled();
                        newCap.setCopied(null);
                        newCap.revive();
                    }
                });
            });
            original.invalidateCaps();
        }

        @SubscribeEvent
        public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
            if (event.getObject() instanceof LivingEntity entity) {
                if (entity instanceof Player || entity instanceof ISorcerer) {
                    if (!entity.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
                        SorcererDataHandler.attach(event);
                    }
                    if (!entity.getCapability(OverlayDataHandler.INSTANCE).isPresent()) {
                        OverlayDataHandler.attach(event);
                    }
                }
            }
        }

        @SubscribeEvent
        public static void onLivingTick(LivingEvent.LivingTickEvent event) {
             LivingEntity owner = event.getEntity();
             owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> cap.tick(owner));
             owner.getCapability(OverlayDataHandler.INSTANCE).ifPresent(cap -> cap.tick(owner));
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
        public static void onLivingAttack(LivingAttackEvent event) {
            DamageSource source = event.getSource();
            Entity attacker = source.getEntity();
            LivingEntity victim = event.getEntity();

            if (attacker instanceof SummonEntity tamable) {
                if (tamable.isTame() && tamable.getOwner() == victim) {
                    event.setCanceled(true);
                    return;
                }
            } else if (victim instanceof SummonEntity tamable) {
                if (tamable.isTame() && tamable.getOwner() == attacker) {
                    event.setCanceled(true);
                    return;
                }
            }

            if (attacker instanceof LivingEntity living) {
                if (source.getDirectEntity() == source.getEntity() && (source.is(DamageTypes.MOB_ATTACK) || source.is(DamageTypes.PLAYER_ATTACK)) && living.getItemInHand(InteractionHand.MAIN_HAND).is(JJKItems.SPLIT_SOUL_KATANA.get())) {
                    victim.hurt(victim.level.damageSources().outOfWorld(), event.getAmount());
                    event.setCanceled(true);
                }
            }

            if (!(victim instanceof MahoragaEntity)) return;

            AtomicBoolean result = new AtomicBoolean();

            victim.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                if (cap.isAdaptedTo(event.getSource())) {
                    result.set(true);
                }
            });

            if (result.get()) {
                event.setCanceled(true);

                victim.level.playSound(null, victim.getX(), victim.getY(), victim.getZ(), SoundEvents.SHIELD_BLOCK, SoundSource.MASTER, 1.0F, 1.0F);
            }
        }

        @SubscribeEvent
        public static void onLivingDamage(LivingDamageEvent event) {
            LivingEntity victim = event.getEntity();

            if (!victim.level.isClientSide) {
                float factor = event.getAmount() / victim.getMaxHealth();

                if (factor > 0.25F) {
                    victim.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                        DomainExpansionEntity domain = cap.getDomain((ServerLevel) victim.level);

                        if (domain != null) {
                            float strength = domain.getStrength();
                            domain.setStrength(strength - factor);
                        }
                    });
                }
            }

            if (JJKAbilities.hasToggled(victim, JJKAbilities.DOMAIN_AMPLIFICATION.get()) || JJKAbilities.hasToggled(victim, JJKAbilities.SIMPLE_DOMAIN.get()) ||
                    !JJKAbilities.hasToggled(victim, JJKAbilities.WHEEL.get())) return;

            victim.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                if (!cap.isAdaptedTo(event.getSource())) {
                    if (!cap.tryAdapt(event.getSource())) return;

                    if (!victim.level.isClientSide) {
                        WheelEntity wheel = cap.getSummonByClass((ServerLevel) victim.level, WheelEntity.class);

                        if (wheel != null) {
                            wheel.spin();
                        }
                    }
                }
            });
        }

        @SubscribeEvent
        public static void onLivingHitByDomain(LivingHitByDomainEvent event) {
            LivingEntity victim = event.getEntity();

            if (victim.is(event.getEntity())) return;

            if (victim instanceof Mob mob) mob.setTarget(event.getEntity());
            if (!JJKAbilities.hasToggled(victim, JJKAbilities.WHEEL.get())) return;

            victim.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                if (!cap.isAdaptedTo(event.getAbility())) {
                    if (!cap.tryAdapt(event.getAbility())) return;

                    if (!victim.level.isClientSide) {
                        WheelEntity wheel = cap.getSummonByClass((ServerLevel) victim.level, WheelEntity.class);

                        if (wheel != null) {
                            wheel.spin();
                        }
                    }
                }
            });
        }

        @SubscribeEvent
        public static void onLivingDeath(LivingDeathEvent event) {
            LivingEntity victim = event.getEntity();

            if (event.getSource().getEntity() instanceof LivingEntity source) {
                victim.getCapability(SorcererDataHandler.INSTANCE).ifPresent(victimCap -> {
                    LivingEntity killer;

                    if (source instanceof TamableAnimal tamable) {
                        LivingEntity owner = tamable.getOwner();
                        killer = owner == null ? source : owner;
                    } else {
                        killer = source;
                    }

                    if (victim instanceof SummonEntity) return;

                    killer.getCapability(SorcererDataHandler.INSTANCE).ifPresent(killerCap -> {
                        if (killerCap.isCurse() ? victim instanceof SaturoGojoEntity : (victim instanceof MegunaRyomenEntity || victim instanceof SukunaRyomenEntity)) {
                            killerCap.addTrait(Trait.STRONGEST);

                            if (killer instanceof ServerPlayer player) {
                                PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(killerCap.serializeNBT()), player);
                            }
                        }

                        if (killerCap.isCurse() != victimCap.isCurse()) {
                            SorcererGrade grade = SorcererGrade.values()[victimCap.getGrade().ordinal()];

                            killerCap.exorcise(killer, grade);

                            if (killer instanceof ServerPlayer player) {
                                PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(killerCap.serializeNBT()), player);
                            }
                        }
                    });

                    if (HelperMethods.RANDOM.nextInt(10) == 0) {
                        if (!victimCap.isCurse() && victimCap.getGrade().ordinal() >= SorcererGrade.GRADE_1.ordinal()) {
                            if (!victimCap.hasTrait(Trait.REVERSE_CURSED_TECHNIQUE)) {
                                victim.setHealth(victim.getMaxHealth() / 2);
                                victimCap.addTrait(Trait.REVERSE_CURSED_TECHNIQUE);
                                event.setCanceled(true);

                                if (victim instanceof ServerPlayer player) {
                                    PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(victimCap.serializeNBT()), player);
                                }
                                return;
                            }
                        }
                    }

                    /*if (!event.getSource().is(JJKDamageSources.JUJUTSU) && !(killer.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof CursedToolItem) &&
                            !victimCap.isCurse() && HelperMethods.RANDOM.nextInt(10) == 0) {
                        if (victim instanceof ServerPlayer player) {
                            PacketHandler.sendToClient(new SetOverlayMessageS2CPacket(Component.translatable(String.format("chat.%s.become_curse",
                                    JujutsuKaisen.MOD_ID)), false), player);
                        }
                        victim.setHealth(victim.getMaxHealth() / 2);
                        victimCap.setCurse(true);
                        event.setCanceled(true);
                    }*/
                });
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
