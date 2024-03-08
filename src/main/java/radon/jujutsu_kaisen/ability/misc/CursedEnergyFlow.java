package radon.jujutsu_kaisen.ability.misc;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.CombatRules;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.ThornsEnchantment;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.living.LivingHurtEvent;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.CursedEnergyNature;
import radon.jujutsu_kaisen.data.sorcerer.Trait;
import radon.jujutsu_kaisen.client.particle.LightningParticle;
import radon.jujutsu_kaisen.client.particle.ParticleColors;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.entity.JujutsuLightningEntity;
import radon.jujutsu_kaisen.item.JJKItems;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;
import radon.jujutsu_kaisen.sound.JJKSounds;
import radon.jujutsu_kaisen.util.DamageUtil;
import radon.jujutsu_kaisen.util.EntityUtil;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

import java.util.Objects;
import java.util.UUID;

public class CursedEnergyFlow extends Ability implements Ability.IToggled {
    private static final UUID MOVEMENT_SPEED_UUID = UUID.fromString("641b629b-f7b7-4066-a486-8e1d670a7439");

    private static final double SPEED = 0.02D;

    private static final float LIGHTNING_DAMAGE = 5.0F;

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return target != null && !target.isDeadOrDying();
    }

    @Override
    public boolean isTechnique() {
        return false;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.TOGGLED;
    }

    @Override
    public boolean isCursedEnergyColor() {
        return true;
    }

    @Override
    public void run(LivingEntity owner) {
        if (!owner.level().getBlockState(owner.blockPosition()).getFluidState().isEmpty()) {
            Vec3 movement = owner.getDeltaMovement();

            if (movement.y < 0.0D) {
                owner.setDeltaMovement(movement.x, 0.01D, movement.z);
            }
            owner.setOnGround(true);
        }

        if (owner instanceof Player player) {
            float f;

            if (owner.onGround() && !owner.isDeadOrDying() && !owner.isSwimming()) {
                f = Math.min(0.1F, (float) owner.getDeltaMovement().horizontalDistance());
            } else {
                f = 0.0F;
            }
            player.bob += (f - player.bob) * 0.4F;
        }

        if (!(owner.level() instanceof ServerLevel level)) return;

        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        ISorcererData sorcererData = cap.getSorcererData();
        IAbilityData abilityData = cap.getAbilityData();

        float scale = abilityData.isChanneling(JJKAbilities.CURSED_ENERGY_SHIELD.get()) ? 1.5F : 1.0F;

        if (sorcererData.getNature() == CursedEnergyNature.LIGHTNING) {
            for (int i = 0; i < 4; i++) {
                double x = owner.getX() + (HelperMethods.RANDOM.nextDouble() - 0.5D) * (owner.getBbWidth() * 2 * scale);
                double y = owner.getY() + HelperMethods.RANDOM.nextDouble() * (owner.getBbHeight() * 1.25F * scale);
                double z = owner.getZ() + (HelperMethods.RANDOM.nextDouble() - 0.5D) * (owner.getBbWidth() * 2 * scale);
                level.sendParticles(new LightningParticle.LightningParticleOptions(ParticleColors.getCursedEnergyColorBright(owner), 0.2F, 1),
                        x, y, z, 0, 0.0D, 0.0D, 0.0D, 0.0D);
            }

            if (owner.isInWater()) {
                for (Entity entity : owner.level().getEntities(owner, owner.getBoundingBox().inflate(16.0D))) {
                    if (!entity.isInWater()) continue;

                    if (entity.hurt(JJKDamageSources.jujutsuAttack(owner, this), LIGHTNING_DAMAGE * this.getPower(owner))) {
                        for (int i = 0; i < 16; i++) {
                            double x = entity.getX() + (HelperMethods.RANDOM.nextDouble() - 0.5D) * (entity.getBbWidth() * 2);
                            double y = entity.getY() + HelperMethods.RANDOM.nextDouble() * (entity.getBbHeight() * 1.25F);
                            double z = entity.getZ() + (HelperMethods.RANDOM.nextDouble() - 0.5D) * (entity.getBbWidth() * 2);
                            level.sendParticles(new LightningParticle.LightningParticleOptions(ParticleColors.getCursedEnergyColorBright(owner), 0.2F, 1),
                                    x, y, z, 0, 0.0D, 0.0D, 0.0D, 0.0D);
                        }
                        owner.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), JJKSounds.ELECTRICITY.get(), SoundSource.MASTER, 1.0F, 1.0F);
                    }
                }
            }
        }
    }

    @Override
    public void applyModifiers(LivingEntity owner) {
        EntityUtil.applyModifier(owner, Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED_UUID, "Movement speed",
                SPEED * this.getPower(owner), AttributeModifier.Operation.ADDITION);
    }

    @Override
    public void removeModifiers(LivingEntity owner) {
        EntityUtil.removeModifier(owner, Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED_UUID);
    }

    @Override
    public float getCost(LivingEntity owner) {
        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return 0.0F;

        ISorcererData data = cap.getSorcererData();

        if (data == null) return 0.0F;

        if (owner.isInWater() && data.getNature() == CursedEnergyNature.LIGHTNING) {
            return 5.0F;
        }
        return 0.01F;
    }

    @Override
    public boolean isUnlocked(LivingEntity owner) {
        return true;
    }

    @Override
    public boolean isDisplayed(LivingEntity owner) {
        return true;
    }

    @Override
    public void onEnabled(LivingEntity owner) {
    }

    @Override
    public void onDisabled(LivingEntity owner) {
    }

    @Override
    public Status isStillUsable(LivingEntity owner) {
        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return Status.FAILURE;

        ISorcererData data = cap.getSorcererData();

        if (data == null) return Status.FAILURE;

        return data.getEnergy() == 0.0F ? Status.FAILURE : super.isStillUsable(owner);
    }

    public static void attack(LivingHurtEvent event) {
        LivingEntity victim = event.getEntity();

        if (victim.level().isClientSide) return;

        DamageSource source = event.getSource();

        float amount = event.getAmount();

        if (!DamageUtil.isMelee(source)) return;

        if (!(source.getEntity() instanceof LivingEntity attacker)) return;

        IJujutsuCapability cap = attacker.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        ISorcererData sorcererData = cap.getSorcererData();
        IAbilityData abilityData = cap.getAbilityData();

        if (!abilityData.hasToggled(JJKAbilities.CURSED_ENERGY_FLOW.get())) return;

        float increase = sorcererData.getAbilityPower() * 0.5F;

        switch (sorcererData.getNature()) {
            case ROUGH -> increase *= 1.5F;
            case LIGHTNING -> {
                increase *= (attacker.getItemInHand(InteractionHand.MAIN_HAND).is(JJKItems.NYOI_STAFF.get()) ? 2.0F : 1.0F);

                victim.addEffect(new MobEffectInstance(JJKEffects.STUN.get(), 20 * (attacker.getItemInHand(InteractionHand.MAIN_HAND).is(JJKItems.NYOI_STAFF.get()) ? 2 : 1), 0, false, false, false));
                victim.playSound(SoundEvents.LIGHTNING_BOLT_IMPACT, 1.0F, 0.5F + HelperMethods.RANDOM.nextFloat() * 0.2F);

                for (int i = 0; i < 8; i++) {
                    double offsetX = HelperMethods.RANDOM.nextGaussian() * 1.5D;
                    double offsetY = HelperMethods.RANDOM.nextGaussian() * 1.5D;
                    double offsetZ = HelperMethods.RANDOM.nextGaussian() * 1.5D;
                    ((ServerLevel) attacker.level()).sendParticles(new LightningParticle.LightningParticleOptions(ParticleColors.getCursedEnergyColor(attacker), 0.5F, 1),
                            victim.getX() + offsetX, victim.getY() + offsetY, victim.getZ() + offsetZ,
                            0, 0.0D, 0.0D, 0.0D, 0.0D);
                }
            }
            case DIVERGENT -> {
                Vec3 look = RotationUtil.getTargetAdjustedLookAngle(attacker);

                abilityData.delayTickEvent(() -> {
                    victim.invulnerableTime = 0;

                    Vec3 pos = victim.position().add(0.0D, victim.getBbHeight() / 2.0F, 0.0D);

                    if (victim.hurt(JJKDamageSources.jujutsuAttack(attacker, null), amount)) {
                        ((ServerLevel) victim.level()).sendParticles(ParticleTypes.EXPLOSION, pos.x, pos.y, pos.z, 0, 1.0D, 0.0D, 0.0D, 1.0D);
                        victim.level().playSound(null, pos.x, pos.y, pos.z, SoundEvents.GENERIC_EXPLODE, SoundSource.MASTER, 1.0F, 1.0F);

                        victim.setDeltaMovement(look.scale(1.0F + (sorcererData.getAbilityPower() * 0.1F)));
                        victim.hurtMarked = true;
                    }
                }, 5);
            }
        };

        if (!(attacker instanceof Player player) || !player.getAbilities().instabuild) {
            float cost = increase * (sorcererData.hasTrait(Trait.SIX_EYES) ? 0.5F : 1.0F);

            if (sorcererData.getEnergy() < cost) return;

            sorcererData.useEnergy(cost);

            if (attacker instanceof ServerPlayer player) {
                PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(sorcererData.serializeNBT()), player);
            }
        }
        event.setAmount(amount + increase);
    }

    public static void shield(LivingHurtEvent event) {
        LivingEntity victim = event.getEntity();

        if (victim.level().isClientSide) return;

        DamageSource source = event.getSource();
        float amount = event.getAmount();

        if (source.is(DamageTypeTags.BYPASSES_ARMOR)) return;

        if (!(source.getEntity() instanceof LivingEntity attacker)) return;

        IJujutsuCapability cap = victim.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        ISorcererData sorcererData = cap.getSorcererData();
        IAbilityData abilityData = cap.getAbilityData();

        if (!abilityData.hasToggled(JJKAbilities.CURSED_ENERGY_FLOW.get())) return;

        if (sorcererData.getNature() == CursedEnergyNature.LIGHTNING) {
            if ((source.getDirectEntity() instanceof JujutsuLightningEntity) || (source instanceof JJKDamageSources.JujutsuDamageSource jujutsu &&
                    jujutsu.getAbility() != null && jujutsu.getAbility().getClassification() == Classification.LIGHTNING)) {
                event.setCanceled(true);
            }
        }

        if (DamageUtil.isMelee(source)) {
            if (sorcererData.getNature() == CursedEnergyNature.ROUGH) {
                if (ThornsEnchantment.shouldHit(3, victim.getRandom())) {
                    attacker.hurt(JJKDamageSources.jujutsuAttack(victim, null), (float) ThornsEnchantment.getDamage(3, victim.getRandom()));
                }
            }
        }

        float armor = sorcererData.getAbilityPower() * (abilityData.isChanneling(JJKAbilities.CURSED_ENERGY_SHIELD.get()) ? 5.0F : 2.5F);
        float toughness = armor * 0.1F;

        float f = 2.0F + toughness / 4.0F;
        float f1 = Mth.clamp(armor - amount / f, armor * 0.2F, 23.75F);
        float blocked = amount * (1.0F - f1 / 25.0F);

        if (!(attacker instanceof Player player) || !player.getAbilities().instabuild) {
            float cost = blocked * (sorcererData.hasTrait(Trait.SIX_EYES) ? 0.5F : 1.0F);

            if (sorcererData.getEnergy() < cost) return;

            sorcererData.useEnergy(cost);

            if (victim instanceof ServerPlayer player) {
                PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(sorcererData.serializeNBT()), player);
            }
        }
        event.setAmount(blocked);
    }

    @Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void onLivingHurt(LivingHurtEvent event) {
            CursedEnergyFlow.attack(event);
            CursedEnergyFlow.shield(event);
        }
    }
}