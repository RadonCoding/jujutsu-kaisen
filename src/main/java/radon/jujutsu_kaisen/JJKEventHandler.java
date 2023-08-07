package radon.jujutsu_kaisen;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.OverlayDataHandler;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.CurseGrade;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.WheelEntity;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import radon.jujutsu_kaisen.entity.base.ISorcerer;
import radon.jujutsu_kaisen.entity.base.SummonEntity;
import radon.jujutsu_kaisen.entity.curse.MahoragaEntity;
import radon.jujutsu_kaisen.entity.sorcerer.SukunaRyomenEntity;
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
            LivingEntity victim = event.getEntity();

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

            if (!JJKAbilities.hasToggled(victim, JJKAbilities.WHEEL.get())) return;

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
        public static void onLivingDeath(LivingDeathEvent event) {
            LivingEntity victim = event.getEntity();

            if (event.getSource().getEntity() instanceof LivingEntity source) {
                victim.getCapability(SorcererDataHandler.INSTANCE).ifPresent(victimCap -> {
                    LivingEntity killer;

                    if (source instanceof SummonEntity summon) {
                        LivingEntity owner = summon.getOwner();
                        killer = owner == null ? source : owner;
                    } else {
                        killer = source;
                    }

                    killer.getCapability(SorcererDataHandler.INSTANCE).ifPresent(killerCap -> {
                        if (victim instanceof SukunaRyomenEntity) {
                            killerCap.addTrait(Trait.STRONGEST);
                        }

                        if (killerCap.isCurse() != victimCap.isCurse()) {
                            CurseGrade grade = CurseGrade.values()[victimCap.getGrade().ordinal()];

                            killerCap.exorcise(killer, grade);

                            if (killer instanceof ServerPlayer player) {
                                PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(killerCap.serializeNBT()), player);
                            }
                        }
                    });
                });

                if (HelperMethods.RANDOM.nextInt(100) == 0) {
                    victim.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                        if (!cap.isCurse() && cap.getGrade().ordinal() >= SorcererGrade.GRADE_1.ordinal()) {
                            if (!cap.hasTrait(Trait.REVERSE_CURSED_TECHNIQUE)) {
                                victim.setHealth(victim.getMaxHealth() / 2);
                                cap.addTrait(Trait.REVERSE_CURSED_TECHNIQUE);
                                event.setCanceled(true);
                            }
                        }
                    });
                }
            }
        }
    }

    @Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEvents {
        @SubscribeEvent
        public static void onCreateEntityAttributes(EntityAttributeCreationEvent event) {
            JJKEntities.createAttributes(event);
        }

        @SubscribeEvent
        public static void onRegisterSpawnPlacements(SpawnPlacementRegisterEvent event) {
            JJKEntities.registerSpawnPlacements(event);
        }
    }
}
