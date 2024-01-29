package radon.jujutsu_kaisen.event;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
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
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.event.level.SleepFinishedTimeEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.VeilHandler;
import radon.jujutsu_kaisen.ability.*;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.misc.Slam;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.base.JJKPartEntity;
import radon.jujutsu_kaisen.entity.projectile.ThrownChainProjectile;
import radon.jujutsu_kaisen.entity.sorcerer.HeianSukunaEntity;
import radon.jujutsu_kaisen.entity.sorcerer.SukunaEntity;
import radon.jujutsu_kaisen.item.CursedEnergyFleshItem;
import radon.jujutsu_kaisen.item.JJKItems;
import radon.jujutsu_kaisen.item.base.CursedToolItem;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;
import radon.jujutsu_kaisen.util.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class JJKEventHandler {
    @Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class JJKEventHandlerForgeEvents {
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
        public static void onLivingDestroyBlock(LivingDestroyBlockEvent event) {
            LivingEntity entity = event.getEntity();
            Vec3 center = event.getPos().getCenter();

            if (!VeilHandler.canDestroy(event.getEntity(), entity.level(), center.x, center.y, center.z)) {
                event.setCanceled(true);
            }
        }

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
        public static void onLivingDamage(LivingDamageEvent event) {
            LivingEntity victim = event.getEntity();

            if (!(event.getSource().getEntity() instanceof LivingEntity owner)) return;

            if (!owner.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return;
            ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

            // If the target is dead we should not trigger any IAttack's
            if (victim.getHealth() - event.getAmount() <= 0.0F) return;

            cap.attack(event.getSource(), victim);

            // If the target died from the IAttack's then cancel (yes this is very scuffed lmao)
            if (victim.isDeadOrDying()) event.setCanceled(true);
        }

        @SubscribeEvent
        public static void onLivingTick(LivingEvent.LivingTickEvent event) {
            LivingEntity owner = event.getEntity();

            if (owner.isDeadOrDying()) return;

            if (!owner.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return;
            ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

            cap.tick(owner);

            if (cap.hasTrait(Trait.SIX_EYES) && !owner.getItemBySlot(EquipmentSlot.HEAD).is(JJKItems.BLINDFOLD.get())) {
                owner.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 220, 0, false, false, false));
            }

            if (cap.getType() == JujutsuType.CURSE) {
                if (owner instanceof Player player) {
                    player.getFoodData().setFoodLevel(20);
                }
            }
        }

        @SubscribeEvent
        public static void onLivingFall(LivingFallEvent event) {
            LivingEntity victim = event.getEntity();

            if (JJKAbilities.hasToggled(victim, JJKAbilities.CURSED_ENERGY_FLOW.get())) {
                event.setDistance(event.getDistance() * 0.5F);
            }

            if (JJKAbilities.hasTrait(victim, Trait.HEAVENLY_RESTRICTION)) {
                event.setDistance(event.getDistance() * 0.1F);
            }

            if (Slam.TARGETS.containsKey(victim.getUUID())) {
                Slam.onHitGround(victim, event.getDistance());
                event.setDamageMultiplier(0.0F);
            }
        }

        @SubscribeEvent
        public static void onLivingAttack(LivingAttackEvent event) {
            DamageSource source = event.getSource();

            if (!(source.getEntity() instanceof LivingEntity attacker)) return;

            LivingEntity victim = event.getEntity();

            if (victim.level().isClientSide) return;

            if (ConfigHolder.SERVER.realisticCurses.get()) {
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
                    }
                }
            }
        }

        @SubscribeEvent(priority = EventPriority.LOWEST)
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

            if (source.is(DamageTypeTags.BYPASSES_ARMOR)) return;

            if (!victim.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return;

            ISorcererData cap = victim.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

            float armor = cap.getExperience() * 0.002F;

            if (cap.hasTrait(Trait.HEAVENLY_RESTRICTION)) {
                armor *= 10.0F;
            }
            float blocked = CombatRules.getDamageAfterAbsorb(event.getAmount(), armor, armor * 0.1F);
            event.setAmount(blocked);
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
                        CursedEnergyFleshItem.setGrade(stack, SorcererUtil.getGrade(victimCap.getExperience()));
                        victim.spawnAtLocation(stack);
                    }
                }
                case CURSE -> {
                    if (HelperMethods.RANDOM.nextInt(ConfigHolder.SERVER.curseFleshRarity.get()) == 0) {
                        ItemStack stack = new ItemStack(JJKItems.CURSE_FLESH.get());
                        CursedEnergyFleshItem.setGrade(stack, SorcererUtil.getGrade(victimCap.getExperience()));
                        victim.spawnAtLocation(stack);
                    }
                }
            }

            DamageSource source = event.getSource();

            if (!(source.getEntity() instanceof LivingEntity attacker)) return;

            if (attacker instanceof ServerPlayer player) {
                if (victim instanceof HeianSukunaEntity && victimCap.getFingers() == 20) {
                    PlayerUtil.giveAdvancement(player, "the_strongest_of_all_time");
                }
            }
        }

        @SubscribeEvent
        public static void onAbilityStop(AbilityStopEvent event) {
            Ability ability = event.getAbility();

            CursedTechnique technique = JJKAbilities.getTechnique(ability);

            LivingEntity owner = event.getEntity();

            ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

            // Handling removal of absorbed techniques from curse manipulation
            if (technique != null && cap.getAbsorbed().contains(technique)) {
                cap.unabsorb(technique);
            }
        }

        @SubscribeEvent
        public static void onAbilityTrigger(AbilityTriggerEvent.Pre event) {
            Ability ability = event.getAbility();

            CursedTechnique technique = JJKAbilities.getTechnique(ability);

            LivingEntity owner = event.getEntity();

            ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

            if (ability.getActivationType(owner) == Ability.ActivationType.INSTANT) {
                // Handling removal of absorbed techniques from curse manipulation
                if (technique != null && cap.getAbsorbed().contains(technique)) {
                    cap.unabsorb(technique);
                }
            }
        }
    }
}