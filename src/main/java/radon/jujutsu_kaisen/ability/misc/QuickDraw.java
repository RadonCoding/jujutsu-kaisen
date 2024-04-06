package radon.jujutsu_kaisen.ability.misc;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.entity.living.LivingAttackEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingHurtEvent;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.IAttack;
import radon.jujutsu_kaisen.ability.base.IChanneled;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.ICharged;
import radon.jujutsu_kaisen.ability.base.IDomainAttack;
import radon.jujutsu_kaisen.ability.base.IDurationable;
import radon.jujutsu_kaisen.ability.base.ITenShadowsAttack;
import radon.jujutsu_kaisen.ability.base.IToggled;
import radon.jujutsu_kaisen.client.particle.LightningParticle;
import radon.jujutsu_kaisen.client.particle.ParticleColors;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.entity.SimpleDomainEntity;
import radon.jujutsu_kaisen.util.DamageUtil;
import radon.jujutsu_kaisen.util.EntityUtil;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

public class QuickDraw extends Ability implements IToggled {
    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        if (target == null || target.isDeadOrDying()) return false;
        if (!owner.hasLineOfSight(target)) return false;

        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return false;

        IAbilityData data = cap.getAbilityData();

        if (data.hasToggled(this)) {
            return HelperMethods.RANDOM.nextInt(20) != 0;
        }
        return HelperMethods.RANDOM.nextInt(40) == 0;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.TOGGLED;
    }

    private static void attack(LivingEntity owner, LivingEntity entity) {
        if (entity.invulnerableTime > 0) return;

        owner.lookAt(EntityAnchorArgument.Anchor.EYES, entity.position().add(0.0D, entity.getBbHeight() / 2.0F, 0.0D));

        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        IAbilityData data = cap.getAbilityData();

        for (int i = 0; i < Barrage.DURATION; i++) {
            boolean last = i == Barrage.DURATION - 1;

            data.delayTickEvent(() -> {
                owner.swing(InteractionHand.MAIN_HAND, true);

                Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);

                for (int j = 0; j < 4; j++) {
                    Vec3 pos = owner.getEyePosition().add(look.scale(owner.distanceTo(entity)));
                    ((ServerLevel) owner.level()).sendParticles(owner.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof SwordItem ? ParticleTypes.SWEEP_ATTACK : ParticleTypes.CLOUD,
                            pos.x + (HelperMethods.RANDOM.nextDouble() - 0.5D) * 2.5D,
                            pos.y + (HelperMethods.RANDOM.nextDouble() - 0.5D) * 2.5D,
                            pos.z + (HelperMethods.RANDOM.nextDouble() - 0.5D) * 2.5D,
                            0, 0.0D, 0.0D, 0.0D, 1.0D);
                }
                for (int j = 0; j < 4; j++) {
                    Vec3 pos = owner.getEyePosition().add(look.scale(owner.distanceTo(entity)));
                    ((ServerLevel) owner.level()).sendParticles(ParticleTypes.CRIT,
                            pos.x + (HelperMethods.RANDOM.nextDouble() - 0.5D) * 2.5D,
                            pos.y + (HelperMethods.RANDOM.nextDouble() - 0.5D) * 2.5D,
                            pos.z + (HelperMethods.RANDOM.nextDouble() - 0.5D) * 2.5D,
                            0, 0.0D, 0.0D, 0.0D, 1.0D);
                }

                Vec3 pos = owner.getEyePosition().add(look);
                owner.level().playSound(null, pos.x, pos.y, pos.z, SoundEvents.GENERIC_SMALL_FALL, SoundSource.MASTER, 1.0F, 0.3F);

                if (owner instanceof Player player) {
                    player.attack(entity);
                } else {
                    owner.doHurtTarget(entity);
                }

                if (!last) {
                    entity.invulnerableTime = 0;
                }
            }, i * 2);
        }
    }

    @Override
    public void run(LivingEntity owner) {
        if (owner.level().isClientSide) return;

        owner.addEffect(new MobEffectInstance(JJKEffects.STUN.get(), 2, 0, false, false, false));

        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        ISorcererData data = cap.getSorcererData();

        SimpleDomainEntity domain = data.getSummonByClass(SimpleDomainEntity.class);

        if (domain == null) return;

        for (LivingEntity entity : EntityUtil.getTouchableEntities(LivingEntity.class, owner.level(), owner, domain.getBoundingBox())) {
            if (entity.distanceTo(domain) > domain.getRadius()) continue;

            attack(owner, entity);
        }
    }

    @Override
    public void onEnabled(LivingEntity owner) {

    }

    @Override
    public void onDisabled(LivingEntity owner) {

    }

    @Override
    public int getCooldown() {
        return 5 * 20;
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 0;
    }

    @Override
    public boolean isValid(LivingEntity owner) {
        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return false;

        IAbilityData data = cap.getAbilityData();
        return (data.hasToggled(JJKAbilities.SIMPLE_DOMAIN.get()) || data.hasToggled(JJKAbilities.FALLING_BLOSSOM_EMOTION.get())) && super.isValid(owner);
    }

    @Nullable
    @Override
    public Ability getParent(LivingEntity owner) {
        return JJKAbilities.SIMPLE_DOMAIN.get();
    }

    @Override
    public int getPointsCost() {
        return ConfigHolder.SERVER.quickDrawCost.get();
    }

    @Override
    public boolean isTechnique() {
        return false;
    }

    @Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void onLivingAttack(LivingAttackEvent event) {
            LivingEntity victim = event.getEntity();

            if (victim.level().isClientSide) return;

            IJujutsuCapability cap = victim.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap == null) return;

            IAbilityData data = cap.getAbilityData();

            if (!data.hasToggled(JJKAbilities.QUICK_DRAW.get()) &&
                    !data.hasToggled(JJKAbilities.FALLING_BLOSSOM_EMOTION.get())) return;

            if (!(event.getSource().getDirectEntity() instanceof Projectile projectile)) return;

            ItemStack stack = victim.getItemInHand(InteractionHand.MAIN_HAND);

            if (!(stack.getItem() instanceof SwordItem)) return;

            int amount = Math.round(event.getAmount());
            int remaining = stack.getMaxDamage() - stack.getDamageValue();

            int blocked = Math.min(remaining, amount);

            float reduced = amount - blocked;

            if (reduced > 0.0F) return;

            victim.lookAt(EntityAnchorArgument.Anchor.EYES, projectile.position().add(0.0D, projectile.getBbHeight() / 2.0F, 0.0D));

            victim.swing(InteractionHand.MAIN_HAND, true);

            stack.hurtAndBreak(blocked, victim, entity -> {
                entity.broadcastBreakEvent(InteractionHand.MAIN_HAND);

                if (victim instanceof Player player) {
                    EventHooks.onPlayerDestroyItem(player, stack, InteractionHand.MAIN_HAND);
                }
                entity.stopUsingItem();
            });

            event.setCanceled(true);
        }

        @SubscribeEvent
        public static void onLivingHurt(LivingHurtEvent event) {
            LivingEntity victim = event.getEntity();

            if (victim.level().isClientSide) return;

            IJujutsuCapability cap = victim.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap == null) return;

            IAbilityData data = cap.getAbilityData();

            if (!data.hasToggled(JJKAbilities.QUICK_DRAW.get()) &&
                    !data.hasToggled(JJKAbilities.FALLING_BLOSSOM_EMOTION.get())) return;

            if (!(event.getSource().getDirectEntity() instanceof Projectile projectile)) return;

            ItemStack stack = victim.getItemInHand(InteractionHand.MAIN_HAND);

            if (!(stack.getItem() instanceof SwordItem)) return;

            int amount = Math.round(event.getAmount());
            int remaining = stack.getMaxDamage() - stack.getDamageValue();

            int blocked = Math.min(remaining, amount);

            victim.lookAt(EntityAnchorArgument.Anchor.EYES, projectile.position().add(0.0D, projectile.getBbHeight() / 2.0F, 0.0D));

            victim.swing(InteractionHand.MAIN_HAND, true);

            stack.hurtAndBreak(blocked, victim, entity -> {
                entity.broadcastBreakEvent(InteractionHand.MAIN_HAND);

                if (victim instanceof Player player) {
                    EventHooks.onPlayerDestroyItem(player, stack, InteractionHand.MAIN_HAND);
                }
                entity.stopUsingItem();
            });

            float reduced = amount - blocked;

            event.setAmount(reduced);
        }
    }
}
