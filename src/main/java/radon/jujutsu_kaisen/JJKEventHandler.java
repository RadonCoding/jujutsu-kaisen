package radon.jujutsu_kaisen;

import net.minecraft.core.particles.ParticleOptions;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.AbilityTriggerEvent;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.LivingHitByDomainEvent;
import radon.jujutsu_kaisen.capability.data.IOverlayData;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.OverlayDataHandler;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.client.particle.ParticleColors;
import radon.jujutsu_kaisen.client.particle.VaporParticle;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import radon.jujutsu_kaisen.entity.base.ISorcerer;
import radon.jujutsu_kaisen.entity.projectile.ThrownChainItemProjectile;
import radon.jujutsu_kaisen.entity.sorcerer.MegunaRyomenEntity;
import radon.jujutsu_kaisen.entity.sorcerer.SaturoGojoEntity;
import radon.jujutsu_kaisen.entity.sorcerer.SukunaRyomenEntity;
import radon.jujutsu_kaisen.entity.ten_shadows.MahoragaEntity;
import radon.jujutsu_kaisen.entity.ten_shadows.WheelEntity;
import radon.jujutsu_kaisen.item.JJKItems;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncOverlayDataRemoteS2CPacket;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.HashSet;
import java.util.Set;

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

                for (Player player1 : player.level.players()) {
                    if (!player1.getCapability(OverlayDataHandler.INSTANCE).isPresent()) continue;
                    IOverlayData cap = player1.getCapability(OverlayDataHandler.INSTANCE).resolve().orElseThrow();
                    PacketHandler.sendToClient(new SyncOverlayDataRemoteS2CPacket(player1.getUUID(), cap.serializeNBT()), player);
                }
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
                        newCap.revive(!newCap.hasTechnique(CursedTechnique.TEN_SHADOWS));
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

        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public static void onLivingAttack(LivingAttackEvent event) {
            DamageSource source = event.getSource();
            Entity attacker = source.getEntity();
            LivingEntity victim = event.getEntity();

            boolean melee = source.getDirectEntity() == source.getEntity() && (source.is(DamageTypes.MOB_ATTACK) || source.is(DamageTypes.PLAYER_ATTACK));

            if (attacker instanceof TamableAnimal tamable1 && attacker instanceof ISorcerer) {
                if (tamable1.isTame() && tamable1.getOwner() == victim) {
                    event.setCanceled(true);
                    return;
                } else if (victim instanceof TamableAnimal tamable2 && victim instanceof ISorcerer) {
                    if (tamable1.isTame() && tamable2.isTame() && tamable1.getOwner() == tamable2.getOwner()) {
                        event.setCanceled(true);
                        return;
                    }
                }
            } else if (victim instanceof TamableAnimal tamable && victim instanceof ISorcerer) {
                if (tamable.isTame() && tamable.getOwner() == attacker) {
                    event.setCanceled(true);
                    return;
                }
            }

            if (attacker instanceof LivingEntity living) {
                ItemStack stack = null;

                if (source.getDirectEntity() instanceof ThrownChainItemProjectile chain) {
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
                        victim.hurt(JJKDamageSources.soulAttack(living), event.getAmount());
                        event.setCanceled(true);
                    } else if (stack.is(JJKItems.PLAYFUL_CLOUD.get())) {
                        Vec3 pos = living.getEyePosition().add(living.getLookAngle());
                        living.level.explode(living, living.damageSources().explosion(attacker, null), null, pos.x(), pos.y(), pos.z(), 1.0F, false, Level.ExplosionInteraction.NONE);
                    } else if (stack.is(JJKItems.INVERTED_SPEAR_OF_HEAVEN.get())) {
                        victim.getCapability(SorcererDataHandler.INSTANCE).ifPresent(ISorcererData::clearToggled);
                    }
                }

                if (attacker instanceof MahoragaEntity mahoraga) {
                    victim.getCapability(SorcererDataHandler.INSTANCE).ifPresent(victimCap -> {
                        mahoraga.getCapability(SorcererDataHandler.INSTANCE).ifPresent(attackerCap -> {
                            Set<Ability> toggled = new HashSet<>(victimCap.getToggled());

                            for (Ability ability : toggled) {
                                if (!attackerCap.isAdaptedTo(ability)) continue;
                                victimCap.toggle(victim, ability);
                            }
                        });
                    });
                }
            }

            if (!(victim instanceof MahoragaEntity)) return;

            ISorcererData cap = victim.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

            if (cap.isAdaptedTo(event.getSource())) {
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

                DamageSource source = event.getSource();

                if (source.getEntity() == source.getDirectEntity() && (source.is(DamageTypes.MOB_ATTACK) || source.is(DamageTypes.PLAYER_ATTACK))) {
                    if (source.getEntity() instanceof LivingEntity attacker) {
                        if (attacker.getItemInHand(InteractionHand.MAIN_HAND).is(JJKItems.PLAYFUL_CLOUD.get())) {
                            Vec3 pos = attacker.getEyePosition().add(attacker.getLookAngle());
                            attacker.level.explode(attacker, attacker instanceof Player player ? attacker.damageSources().playerAttack(player) : attacker.damageSources().mobAttack(attacker),
                                    null, pos.x(), pos.y(), pos.z(), 1.0F, false, Level.ExplosionInteraction.NONE);
                        } else if (attacker.getItemInHand(InteractionHand.MAIN_HAND).is(JJKItems.INVERTED_SPEAR_OF_HEAVEN.get())) {
                            victim.getCapability(SorcererDataHandler.INSTANCE).ifPresent(ISorcererData::clearToggled);
                        }
                    }
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
                if (!victim.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return;
                ISorcererData victimCap = victim.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

                LivingEntity killer;

                if (source instanceof TamableAnimal tamable && tamable.isTame()) {
                    LivingEntity owner = tamable.getOwner();
                    killer = owner == null ? source : owner;
                } else {
                    killer = source;
                }

                if (victim instanceof TamableAnimal tamable && tamable.isTame()) return;

                if (killer.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
                    ISorcererData killerCap = killer.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

                    if (killerCap.getType() == JujutsuType.CURSE ? victim instanceof SaturoGojoEntity : (victim instanceof MegunaRyomenEntity || victim instanceof SukunaRyomenEntity)) {
                        killerCap.addTrait(Trait.STRONGEST);

                        if (killer instanceof ServerPlayer player) {
                            PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(killerCap.serializeNBT()), player);
                        }
                    }

                    if (victimCap.getType() == JujutsuType.CURSE) {
                        double width = victim.getBbWidth();
                        double height = victim.getBbHeight();
                        ParticleOptions one = new VaporParticle.VaporParticleOptions(ParticleColors.PINK_COLOR, (float) width * 2.0F, 0.5F, false, 20);
                        ParticleOptions two = new VaporParticle.VaporParticleOptions(ParticleColors.PURPLE_COLOR, (float) width * 2.0F, 0.5F, false, 20);

                        for (int j = 0; j < 8; j++) {
                            double x = victim.getX() + (HelperMethods.RANDOM.nextDouble() - 0.5D) * width;
                            double y = victim.getY() + HelperMethods.RANDOM.nextDouble() * height;
                            double z = victim.getZ() + (HelperMethods.RANDOM.nextDouble() - 0.5D) * width;
                            ((ServerLevel) victim.level).sendParticles(one, x, y, z, 0,
                                    0.0D, 0.0D, 0.0D, 0.0D);
                        }
                        for (int j = 0; j < 8; j++) {
                            double x = victim.getX() + (HelperMethods.RANDOM.nextDouble() - 0.5D) * width;
                            double y = victim.getY() + HelperMethods.RANDOM.nextDouble() * height;
                            double z = victim.getZ() + (HelperMethods.RANDOM.nextDouble() - 0.5D) * width;
                            ((ServerLevel) victim.level).sendParticles(two, x, y, z, 0,
                                    0.0D, 0.0D, 0.0D, 0.0D);
                        }
                    }

                    if (killerCap.getType() == JujutsuType.SORCERER && victimCap.getType() == JujutsuType.CURSE ||
                            killerCap.getType() == JujutsuType.CURSE && victimCap.getType() == JujutsuType.SORCERER) {
                        SorcererGrade grade = SorcererGrade.values()[victimCap.getGrade().ordinal()];

                        killerCap.exorcise(killer, grade);

                        if (killer instanceof ServerPlayer player) {
                            PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(killerCap.serializeNBT()), player);
                        }
                    }
                }

                if (victimCap.getType() != JujutsuType.SORCERER || victimCap.hasTrait(Trait.HEAVENLY_RESTRICTION)) return;

                int chance = 10;

                for (InteractionHand hand : InteractionHand.values()) {
                    ItemStack stack = victim.getItemInHand(hand);

                    if (stack.is(Items.TOTEM_OF_UNDYING)) {
                        chance = 7;
                    }
                }

                if (HelperMethods.RANDOM.nextInt(chance) == 0) {
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

        @SubscribeEvent
        public static void onAbilityTrigger(AbilityTriggerEvent event) {
            CursedTechnique technique = JJKAbilities.getTechnique(event.getAbility());

            ISorcererData cap = event.getEntity().getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

            if (technique != null && cap.getAbsorbed().contains(technique)) {
                cap.unabsorb(technique);
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
        public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
            event.register(ISorcererData.class);
            event.register(IOverlayData.class);
        }
    }
}
