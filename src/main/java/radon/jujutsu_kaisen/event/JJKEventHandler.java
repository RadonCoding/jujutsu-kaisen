package radon.jujutsu_kaisen.event;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerNegotiationEvent;
import net.minecraftforge.event.level.SleepFinishedTimeEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkEvent;
import radon.jujutsu_kaisen.ChantHandler;
import radon.jujutsu_kaisen.item.base.CursedToolItem;
import radon.jujutsu_kaisen.util.CuriosUtil;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.VeilHandler;
import radon.jujutsu_kaisen.ability.AbilityTriggerEvent;
import radon.jujutsu_kaisen.ability.CursedEnergyCostEvent;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.LivingHitByDomainEvent;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.misc.Barrage;
import radon.jujutsu_kaisen.ability.misc.Slam;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.client.particle.LightningParticle;
import radon.jujutsu_kaisen.client.particle.ParticleColors;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import radon.jujutsu_kaisen.entity.base.ISorcerer;
import radon.jujutsu_kaisen.entity.base.JJKPartEntity;
import radon.jujutsu_kaisen.entity.projectile.ThrownChainProjectile;
import radon.jujutsu_kaisen.entity.sorcerer.HeianSukunaEntity;
import radon.jujutsu_kaisen.entity.sorcerer.SukunaEntity;
import radon.jujutsu_kaisen.entity.ten_shadows.MahoragaEntity;
import radon.jujutsu_kaisen.item.CursedEnergyFleshItem;
import radon.jujutsu_kaisen.item.JJKItems;
import radon.jujutsu_kaisen.item.cursed_tool.KamutokeDaggerItem;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;
import radon.jujutsu_kaisen.sound.JJKSounds;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JJKEventHandler {
    @Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class JJKEventHandlerForgeEvents {
        @SubscribeEvent
        public static void onSleepFinished(SleepFinishedTimeEvent event) {
            if (!(event.getLevel() instanceof ServerLevel level)) return;

            for (ServerPlayer player : level.players()) {
                if (player.isSleepingLongEnough()) {
                    if (!player.getCapability(SorcererDataHandler.INSTANCE).isPresent()) continue;

                    ISorcererData cap = player.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
                    cap.setEnergy(cap.getMaxEnergy());

                    PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(cap.serializeNBT()), player);
                }
            }
        }

        @SubscribeEvent
        public static void onAttackEntity(AttackEntityEvent event) {
            if (event.getTarget() instanceof JJKPartEntity<?>) {
                Entity parent = ((JJKPartEntity<?>) event.getTarget()).getParent();
                if (parent != null) event.getEntity().attack(parent);
                event.setCanceled(true);
            }
        }

        @SubscribeEvent
        public static void onMobSpawn(MobSpawnEvent.FinalizeSpawn event) {
            if (!VeilHandler.canSpawn(event.getEntity(), event.getX(), event.getY(), event.getZ())) {
                event.setSpawnCancelled(true);
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
        public static void onPlayerClone(PlayerEvent.Clone event) {
            Player original = event.getOriginal();
            Player player = event.getEntity();

            original.reviveCaps();

            ISorcererData oldCap = original.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
            ISorcererData newCap = player.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

            newCap.deserializeNBT(oldCap.serializeNBT());

            if (event.isWasDeath()) {
                newCap.setEnergy(newCap.getMaxEnergy());
                newCap.resetCooldowns();
                newCap.resetBurnout();
                newCap.clearToggled();
                newCap.revive(false);
                newCap.resetBlackFlash();
                newCap.resetExtraEnergy();
                newCap.resetSpeedStacks();

                if (!player.level().isClientSide) {
                    PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(newCap.serializeNBT()), (ServerPlayer) player);
                }
            }
            original.invalidateCaps();
        }

        @SubscribeEvent
        public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
            if (event.getObject() instanceof LivingEntity entity) {
                if (entity instanceof Player || entity instanceof ISorcerer) {
                    SorcererDataHandler.SorcererDataProvider provider = new SorcererDataHandler.SorcererDataProvider();

                    ISorcererData cap = provider.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
                    cap.init(entity);

                    event.addCapability(SorcererDataHandler.SorcererDataProvider.IDENTIFIER, provider);
                }
            }
        }

        @SubscribeEvent
        public static void onLivingDamage(LivingDamageEvent event) {
            LivingEntity victim = event.getEntity();

            if (!(event.getSource().getEntity() instanceof LivingEntity owner)) return;

            owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap ->
                    cap.attack(event.getSource(), victim));
        }

        @SubscribeEvent
        public static void onLivingTick(LivingEvent.LivingTickEvent event) {
            LivingEntity owner = event.getEntity();

            if (owner.isDeadOrDying()) return;

            owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                cap.tick(owner);

                if (cap.hasTrait(Trait.SIX_EYES)) {
                    owner.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 220, 0, false, false, false));
                }

                if (cap.getType() == JujutsuType.CURSE) {
                    if (owner instanceof Player player) {
                        player.getFoodData().setFoodLevel(20);
                    }
                }
            });
        }

        @SubscribeEvent
        public static void onLivingFall(LivingFallEvent event) {
            event.getEntity().getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                if (cap.hasTrait(Trait.HEAVENLY_RESTRICTION)) {
                    event.setDistance(event.getDistance() * 0.25F);
                }
            });

            if (Slam.TARGETS.containsKey(event.getEntity().getUUID())) {
                Slam.onHitGround(event.getEntity(), event.getDistance());
                event.setDamageMultiplier(0.25F);
            }
        }

        @SubscribeEvent(priority = EventPriority.HIGHEST)
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

            if (JJKAbilities.getType(victim) == JujutsuType.CURSE) {
                boolean cursed = false;

                if (event.getSource() instanceof JJKDamageSources.JujutsuDamageSource) {
                    cursed = true;
                } else if (HelperMethods.isMelee(source) && (stacks.stream().anyMatch(item -> item instanceof CursedToolItem))) {
                    cursed = true;
                } else if (attacker.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
                    ISorcererData cap = attacker.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
                    cursed = cap.getEnergy() > 0.0F;
                }

                if (!cursed) {
                    event.setCanceled(true);
                    return;
                }
            }

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
            }

            if (HelperMethods.isMelee(source)) {
                if (!source.is(JJKDamageSources.SPLIT_SOUL_KATANA) && stacks.contains(JJKItems.SPLIT_SOUL_KATANA.get())) {
                    if (attacker.canAttack(victim)) {
                        if (victim.hurt(JJKDamageSources.splitSoulKatanaAttack(attacker), event.getAmount())) {
                            victim.invulnerableTime = 0;
                        }
                    }
                } else if (stacks.contains(JJKItems.PLAYFUL_CLOUD.get())) {
                    Vec3 pos = attacker.getEyePosition().add(attacker.getLookAngle());
                    attacker.level().explode(attacker, attacker.damageSources().explosion(attacker, null), null, pos.x, pos.y, pos.z, 1.0F, false, Level.ExplosionInteraction.NONE);
                } else if (stacks.contains(JJKItems.INVERTED_SPEAR_OF_HEAVEN.get())) {
                    victim.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                        for (Ability ability : cap.getToggled()) {
                            if (!ability.isTechnique()) continue;

                            cap.toggle(ability);
                        }
                        if (victim instanceof ServerPlayer player) {
                            PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(cap.serializeNBT()), player);
                        }
                    });
                } else if (stacks.contains(JJKItems.KAMUTOKE_DAGGER.get())) {
                    attacker.getCapability(SorcererDataHandler.INSTANCE).ifPresent(attackerCap -> {
                        if (!(attacker instanceof Player player) || !player.getAbilities().instabuild) {
                            float cost = KamutokeDaggerItem.MELEE_COST * (attackerCap.hasTrait(Trait.SIX_EYES) ? 0.5F : 1.0F);
                            if (attackerCap.getEnergy() < cost) return;
                            attackerCap.useEnergy(cost);
                        }

                        if (victim.hurt(JJKDamageSources.jujutsuAttack(attacker, null), KamutokeDaggerItem.MELEE_DAMAGE * attackerCap.getRealPower())) {
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
                    });
                }
            }

            if (attacker instanceof MahoragaEntity) {
                victim.getCapability(SorcererDataHandler.INSTANCE).ifPresent(victimCap -> {
                    attacker.getCapability(SorcererDataHandler.INSTANCE).ifPresent(attackerCap -> {
                        Set<Ability> toggled = new HashSet<>(victimCap.getToggled());

                        for (Ability ability : toggled) {
                            if (!attackerCap.isAdaptedTo(ability)) continue;
                            victimCap.toggle(ability);
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
                    event.setAmount(event.getAmount() * 0.1F);
                }
            }

            if (source.getEntity() instanceof LivingEntity attacker) {
                if (JJKAbilities.hasTrait(attacker, Trait.PERFECT_BODY)) {
                    if (HelperMethods.isMelee(source)) {
                        event.setAmount(event.getAmount() * 2.0F);
                    }
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

            if (victim.level().isClientSide) return;

            if (!victim.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return;
            ISorcererData victimCap = victim.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

            switch (victimCap.getType()) {
                case SORCERER -> {
                    if (HelperMethods.RANDOM.nextInt(ConfigHolder.SERVER.sorcererFleshRarity.get()) == 0) {
                        ItemStack stack = new ItemStack(JJKItems.SORCERER_FLESH.get());
                        CursedEnergyFleshItem.setGrade(stack, HelperMethods.getGrade(victimCap.getExperience()));
                        victim.spawnAtLocation(stack);
                    }
                }
                case CURSE -> {
                    if (HelperMethods.RANDOM.nextInt(ConfigHolder.SERVER.curseFleshRarity.get()) == 0) {
                        ItemStack stack = new ItemStack(JJKItems.CURSE_FLESH.get());
                        CursedEnergyFleshItem.setGrade(stack, HelperMethods.getGrade(victimCap.getExperience()));
                        victim.spawnAtLocation(stack);
                    }
                }
            }

            if (event.getSource().getEntity() instanceof LivingEntity attacker) {
                if (attacker instanceof ServerPlayer player) {
                    if (victim instanceof HeianSukunaEntity && victimCap.getFingers() == 20) {
                        HelperMethods.giveAdvancement(player, "the_strongest_of_all_time");
                    }
                }

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
                        if (!victimCap.isUnlocked(JJKAbilities.RCT1.get())) {
                            victim.setHealth(victim.getMaxHealth() / 2);
                            victimCap.unlock(JJKAbilities.RCT1.get());

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
                owner.hurt(owner.level().damageSources().generic(), event.getCost() * 0.1F);
            }
        }

        @SubscribeEvent
        public static void onAbilityTrigger(AbilityTriggerEvent.Pre event) {
            Ability ability = event.getAbility();

            CursedTechnique technique = JJKAbilities.getTechnique(ability);

            LivingEntity owner = event.getEntity();

            ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

            // Handling removal of absorbed techniques from curse manipulation
            if (technique != null && cap.getAbsorbed().contains(technique)) {
                cap.unabsorb(technique);
            }

            // Sukuna has multiple arms
            if (owner instanceof HeianSukunaEntity entity && ability == JJKAbilities.BARRAGE.get()) {
                entity.setBarrage(Barrage.DURATION * 2);
            }

            // Making mobs use chants
            if (owner.level() instanceof ServerLevel level) {
                if (owner instanceof Mob) {
                    List<String> chants = new ArrayList<>(cap.getFirstChants(ability));

                    if (!chants.isEmpty() && HelperMethods.RANDOM.nextInt(Math.max(1, (int) (50 * (cap.getEnergy() / cap.getMaxEnergy())))) == 0) {
                        for (int i = 0; i < HelperMethods.RANDOM.nextInt(chants.size()); i++) {
                            ChantHandler.onChant(owner, chants.get(i));

                            for (ServerPlayer player : level.players()) {
                                if (player.distanceTo(owner) > 32.0D) continue;

                                ResourceLocation key = owner.level().registryAccess().registryOrThrow(Registries.ENTITY_TYPE).getKey(owner.getType());

                                if (key != null) {
                                    player.sendSystemMessage(Component.literal(String.format("<%s> %s", owner.getName().getString(), chants.get(i))));
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}