package radon.jujutsu_kaisen.event;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.CombatRules;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.ExplosionEvent;
import net.neoforged.neoforge.event.level.SleepFinishedTimeEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.VeilHandler;
import radon.jujutsu_kaisen.ability.*;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.misc.CursedEnergyFlow;
import radon.jujutsu_kaisen.ability.misc.Slam;
import radon.jujutsu_kaisen.binding_vow.JJKBindingVows;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.curse_manipulation.ICurseManipulationData;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.cursed_technique.JJKCursedTechniques;
import radon.jujutsu_kaisen.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.data.sorcerer.Trait;
import radon.jujutsu_kaisen.cursed_technique.base.ICursedTechnique;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.data.stat.ISkillData;
import radon.jujutsu_kaisen.data.stat.Skill;
import radon.jujutsu_kaisen.entity.base.JJKPartEntity;
import radon.jujutsu_kaisen.entity.projectile.ThrownChainProjectile;
import radon.jujutsu_kaisen.entity.sorcerer.HeianSukunaEntity;
import radon.jujutsu_kaisen.entity.sorcerer.SukunaEntity;
import radon.jujutsu_kaisen.item.CursedEnergyFleshItem;
import radon.jujutsu_kaisen.item.JJKItems;
import radon.jujutsu_kaisen.item.base.CursedToolItem;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;
import radon.jujutsu_kaisen.pact.JJKPacts;
import radon.jujutsu_kaisen.util.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class JJKEventHandler {
    @Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void onLivingHurt(LivingHurtEvent event) {
            LivingEntity victim = event.getEntity();

            if (victim.level().isClientSide) return;

            DamageSource source = event.getSource();

            float amount = event.getAmount();

            // Your own cursed energy doesn't do as much damage
            if (source instanceof JJKDamageSources.JujutsuDamageSource) {
                if (source.getEntity() == victim) {
                    event.setAmount(amount * 0.01F);
                }
            }

            // Perfect body generic melee increase
            if (source.getEntity() instanceof LivingEntity attacker) {
                IJujutsuCapability cap = attacker.getCapability(JujutsuCapabilityHandler.INSTANCE);

                if (cap == null) return;

                ISorcererData data = cap.getSorcererData();

                if (data != null && data.hasTrait(Trait.PERFECT_BODY)) {
                    if (DamageUtil.isMelee(source)) {
                        event.setAmount(amount * 2.0F);
                    }
                }
            }

            if (source.is(DamageTypeTags.BYPASSES_RESISTANCE)) return;

            IJujutsuCapability cap = victim.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap == null) return;

            ISorcererData sorcererData = cap.getSorcererData();
            IAbilityData abilityData = cap.getAbilityData();
            ISkillData skillData = cap.getSkillData();

            float armor = skillData.getSkill(Skill.REINFORCEMENT) * 0.2F;

            if (sorcererData.hasTrait(Trait.HEAVENLY_RESTRICTION)) {
                armor *= 15.0F;
            }

            if (abilityData.hasToggled(JJKAbilities.CURSED_ENERGY_FLOW.get())) {
                float shielded = armor * (abilityData.isChanneling(JJKAbilities.CURSED_ENERGY_SHIELD.get()) ? 10.0F : 5.0F);

                float toughness = shielded * 0.1F;

                float f = 2.0F + toughness / 4.0F;
                float f1 = Mth.clamp(armor - amount / f, armor * 0.2F, 23.75F);
                float blocked = amount * (1.0F - f1 / 25.0F);

                if (!(victim instanceof Player player) || !player.getAbilities().instabuild) {
                    float cost = blocked * (sorcererData.hasTrait(Trait.SIX_EYES) ? 0.5F : 1.0F);

                    if (sorcererData.getEnergy() >= cost) {
                        sorcererData.useEnergy(cost);

                        if (victim instanceof ServerPlayer player) {
                            PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(sorcererData.serializeNBT()), player);
                        }
                    }
                }
                armor = shielded;
            }

            float toughness = armor * 0.1F;

            float f = 2.0F + toughness / 4.0F;
            float f1 = Mth.clamp(armor - amount / f, armor * 0.2F, 23.75F);
            float blocked = amount * (1.0F - f1 / 25.0F);

            event.setAmount(blocked);
        }

        @SubscribeEvent
        public static void onExplosion(ExplosionEvent.Detonate event) {
            Explosion explosion = event.getExplosion();
            LivingEntity instigator = explosion.getIndirectSourceEntity();

            Iterator<BlockPos> iter = explosion.getToBlow().iterator();

            while (iter.hasNext()) {
                BlockPos pos = iter.next();
                Vec3 center = pos.getCenter();

                if (!VeilHandler.canDestroy(instigator, event.getLevel(), center.x, center.y, center.z)) {
                    iter.remove();
                }
            }
        }

        @SubscribeEvent
        public static void onEntityPlaceBlock(BlockEvent.EntityPlaceEvent event) {
            Entity entity = event.getEntity();
            Vec3 center = event.getPos().getCenter();

            if (!VeilHandler.canDestroy(entity, (Level) event.getLevel(), center.x, center.y, center.z)) {
                event.setCanceled(true);
            }
        }

        @SubscribeEvent
        public static void onLivingDestroyBlock(LivingDestroyBlockEvent event) {
            LivingEntity entity = event.getEntity();
            Vec3 center = event.getPos().getCenter();

            if (!VeilHandler.canDestroy(entity, entity.level(), center.x, center.y, center.z)) {
                event.setCanceled(true);
            }
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

                PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(data.serializeNBT()), player);
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
        public static void onLivingTick(LivingEvent.LivingTickEvent event) {
            LivingEntity owner = event.getEntity();

            if (owner.isDeadOrDying()) return;

            IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap == null) return;

            ISorcererData data = cap.getSorcererData();

            if (data.hasTrait(Trait.SIX_EYES) && !owner.getItemBySlot(EquipmentSlot.HEAD).is(JJKItems.BLINDFOLD.get())) {
                owner.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 220, 0, false, false, false));
            }

            if (data.getType() == JujutsuType.CURSE) {
                if (owner instanceof Player player) {
                    player.getFoodData().setFoodLevel(20);
                }
            }
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

            if (sorcererData.hasTrait(Trait.HEAVENLY_RESTRICTION)) {
                event.setDistance(event.getDistance() * 0.1F);
            }
        }

        @SubscribeEvent
        public static void onLivingAttack(LivingAttackEvent event) {
            DamageSource source = event.getSource();

            if (!(source.getEntity() instanceof LivingEntity attacker)) return;

            LivingEntity victim = event.getEntity();

            if (victim.level().isClientSide) return;

            if (ConfigHolder.SERVER.realisticCurses.get()) {
                IJujutsuCapability victimCap = victim.getCapability(JujutsuCapabilityHandler.INSTANCE);

                if (victimCap != null) {
                    ISorcererData victimData = victimCap.getSorcererData();

                    if (victimData.getType() == JujutsuType.CURSE) {
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
        public static void onLivingDeath(LivingDeathEvent event) {
            LivingEntity victim = event.getEntity();

            if (victim.level().isClientSide) return;

            IJujutsuCapability cap = victim.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap == null) return;

            ISorcererData data = cap.getSorcererData();

            switch (data.getType()) {
                case SORCERER -> {
                    if (HelperMethods.RANDOM.nextInt(ConfigHolder.SERVER.sorcererFleshRarity.get()) == 0) {
                        ItemStack stack = new ItemStack(JJKItems.SORCERER_FLESH.get());
                        CursedEnergyFleshItem.setGrade(stack, SorcererUtil.getGrade(data.getExperience()));
                        victim.spawnAtLocation(stack);
                    }
                }
                case CURSE -> {
                    if (HelperMethods.RANDOM.nextInt(ConfigHolder.SERVER.curseFleshRarity.get()) == 0) {
                        ItemStack stack = new ItemStack(JJKItems.CURSE_FLESH.get());
                        CursedEnergyFleshItem.setGrade(stack, SorcererUtil.getGrade(data.getExperience()));
                        victim.spawnAtLocation(stack);
                    }
                }
            }

            DamageSource source = event.getSource();

            if (!(source.getEntity() instanceof LivingEntity attacker)) return;

            if (attacker instanceof ServerPlayer player) {
                if (victim instanceof HeianSukunaEntity && data.getFingers() == 20) {
                    PlayerUtil.giveAdvancement(player, "the_strongest_of_all_time");
                }
            }
        }

        @SubscribeEvent
        public static void onAbilityStop(AbilityStopEvent event) {
            Ability ability = event.getAbility();

            ICursedTechnique technique = JJKCursedTechniques.getTechnique(ability);

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

            ICursedTechnique technique = JJKCursedTechniques.getTechnique(ability);

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
        }
    }

    @Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
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