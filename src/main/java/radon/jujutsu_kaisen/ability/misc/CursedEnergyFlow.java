package radon.jujutsu_kaisen.ability.misc;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.CombatRules;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.enchantment.ThornsEnchantment;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedEnergyNature;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.client.particle.CursedEnergyParticle;
import radon.jujutsu_kaisen.client.particle.LightningParticle;
import radon.jujutsu_kaisen.client.particle.ParticleColors;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.entity.JujutsuLightningEntity;
import radon.jujutsu_kaisen.item.JJKItems;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;
import radon.jujutsu_kaisen.sound.JJKSounds;
import radon.jujutsu_kaisen.util.EntityUtil;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

import java.util.UUID;

public class CursedEnergyFlow extends Ability implements Ability.IToggled {
    private static final UUID MOVEMENT_SPEED_UUID = UUID.fromString("641b629b-f7b7-4066-a486-8e1d670a7439");

    private static final double MAX_SPEED = 0.495D;

    private static final float LIGHTNING_DAMAGE = 5.0F;

    @Override
    public boolean isScalable(LivingEntity owner) {
        return false;
    }

    @Override
    public boolean isTechnique() {
        return false;
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return target != null;
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

        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        float scale = cap.isChanneling(JJKAbilities.CURSED_ENERGY_SHIELD.get()) ? 1.5F : 1.0F;

        if (cap.getNature() == CursedEnergyNature.LIGHTNING) {
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
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        EntityUtil.applyModifier(owner, Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED_UUID, "Movement speed",
                MAX_SPEED * (cap.getExperience() / ConfigHolder.SERVER.maximumExperienceAmount.get()), AttributeModifier.Operation.ADDITION);
    }

    @Override
    public void removeModifiers(LivingEntity owner) {
        EntityUtil.removeModifier(owner, Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED_UUID);
    }

    @Override
    public float getCost(LivingEntity owner) {
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        if (owner.isInWater() && cap.getNature() == CursedEnergyNature.LIGHTNING) {
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
    public Vec2 getDisplayCoordinates() {
        return new Vec2(0.0F, 2.0F);
    }

    @Override
    public void onEnabled(LivingEntity owner) {
    }

    @Override
    public void onDisabled(LivingEntity owner) {
    }

    @Override
    public Status isStillUsable(LivingEntity owner) {
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        return cap.getEnergy() == 0.0F ? Status.FAILURE : super.isStillUsable(owner);
    }

    @Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class CursedEnergyFlowForgeEvents {
        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public static void onLivingHurt(LivingHurtEvent event) {
            DamageSource source = event.getSource();
            if (!(source.getEntity() instanceof LivingEntity attacker)) return;

            if (attacker.level().isClientSide) return;

            LivingEntity victim = event.getEntity();

            if (JJKAbilities.hasToggled(attacker, JJKAbilities.CURSED_ENERGY_FLOW.get())) {
                if (attacker.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
                    ISorcererData attackerCap = attacker.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

                    if (HelperMethods.isMelee(source)) {
                        float increase = attackerCap.getExperience() * 0.005F;

                        switch (attackerCap.getNature()) {
                            case ROUGH -> increase *= 1.5F;
                            case LIGHTNING -> increase *= (attacker.getItemInHand(InteractionHand.MAIN_HAND).is(JJKItems.NYOI_STAFF.get()) ? 2.0F : 1.0F);
                            case DIVERGENT -> {
                                Vec3 look = RotationUtil.getTargetAdjustedLookAngle(attacker);

                                attackerCap.delayTickEvent(() -> {
                                    victim.invulnerableTime = 0;

                                    Vec3 pos = victim.position().add(0.0D, victim.getBbHeight() / 2.0F, 0.0D);

                                    if (victim.hurt(JJKDamageSources.jujutsuAttack(attacker, null), event.getAmount())) {
                                        ((ServerLevel) victim.level()).sendParticles(ParticleTypes.EXPLOSION, pos.x, pos.y, pos.z, 0, 1.0D, 0.0D, 0.0D, 1.0D);
                                        victim.level().playSound(null, pos.x, pos.y, pos.z, SoundEvents.GENERIC_EXPLODE, SoundSource.MASTER, 1.0F, 1.0F);

                                        victim.setDeltaMovement(look.scale(1.0F + (attackerCap.getAbilityPower() * 0.1F)));
                                        victim.hurtMarked = true;
                                    }
                                }, 5);
                            }
                        };

                        if (!(attacker instanceof Player player) || !player.getAbilities().instabuild) {
                            float cost = increase * (attackerCap.hasTrait(Trait.SIX_EYES) ? 0.5F : 1.0F);
                            if (attackerCap.getEnergy() < cost) return;
                            attackerCap.useEnergy(cost);

                            if (attacker instanceof ServerPlayer player) {
                                PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(attackerCap.serializeNBT()), player);
                            }
                        }
                        event.setAmount(event.getAmount() + increase);
                    }
                }
            }

            if (HelperMethods.isMelee(source)) {
                if (attacker.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
                    ISorcererData attackerCap = attacker.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

                    if (JJKAbilities.hasToggled(attacker, JJKAbilities.CURSED_ENERGY_FLOW.get())) {
                        if (attackerCap.getNature() == CursedEnergyNature.LIGHTNING) {
                            victim.addEffect(new MobEffectInstance(JJKEffects.STUN.get(), 20 * (attacker.getItemInHand(InteractionHand.MAIN_HAND).is(JJKItems.NYOI_STAFF.get()) ? 2 : 1), 0, false, false, false));
                            victim.playSound(SoundEvents.LIGHTNING_BOLT_IMPACT, 1.0F, 0.5F + HelperMethods.RANDOM.nextFloat() * 0.2F);

                            if (!attacker.level().isClientSide) {
                                for (int i = 0; i < 8; i++) {
                                    double offsetX = HelperMethods.RANDOM.nextGaussian() * 1.5D;
                                    double offsetY = HelperMethods.RANDOM.nextGaussian() * 1.5D;
                                    double offsetZ = HelperMethods.RANDOM.nextGaussian() * 1.5D;
                                    ((ServerLevel) attacker.level()).sendParticles(new LightningParticle.LightningParticleOptions(ParticleColors.getCursedEnergyColor(attacker), 0.5F, 1),
                                            victim.getX() + offsetX, victim.getY() + offsetY, victim.getZ() + offsetZ,
                                            0, 0.0D, 0.0D, 0.0D, 0.0D);
                                }
                            }
                        }
                    }
                }
            }

            if (source.is(DamageTypeTags.BYPASSES_ARMOR)) return;

            // Shield
            if (JJKAbilities.hasToggled(victim, JJKAbilities.CURSED_ENERGY_FLOW.get())) {
                if (victim.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
                    ISorcererData victimCap = victim.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

                    if (victimCap.getNature() == CursedEnergyNature.LIGHTNING) {
                        if ((source.getDirectEntity() instanceof JujutsuLightningEntity) || (source instanceof JJKDamageSources.JujutsuDamageSource jujutsu &&
                                jujutsu.getAbility() != null && jujutsu.getAbility().getClassification() == Classification.LIGHTNING)) {
                            event.setCanceled(true);
                        }
                    }

                    if (HelperMethods.isMelee(source)) {
                        switch (victimCap.getNature()) {
                            case LIGHTNING -> attacker.addEffect(new MobEffectInstance(JJKEffects.STUN.get(), 20, 0,
                                    false, false, false));
                            case ROUGH -> {
                                if (ThornsEnchantment.shouldHit(3, victim.getRandom())) {
                                    attacker.hurt(JJKDamageSources.jujutsuAttack(victim, null), (float) ThornsEnchantment.getDamage(3, victim.getRandom()));
                                }
                            }
                        }
                    }

                    float armor = victimCap.getExperience() * (JJKAbilities.hasToggled(victim, JJKAbilities.CURSED_ENERGY_SHIELD.get()) ? 0.05F : 0.025F);
                    float blocked = CombatRules.getDamageAfterAbsorb(event.getAmount(), armor, armor * 0.1F);
                    float block = event.getAmount() - blocked;

                    if (!(attacker instanceof Player player) || !player.getAbilities().instabuild) {
                        float cost = block * (victimCap.hasTrait(Trait.SIX_EYES) ? 0.5F : 1.0F);
                        if (victimCap.getEnergy() < cost) return;
                        victimCap.useEnergy(cost);

                        if (victim instanceof ServerPlayer player) {
                            PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(victimCap.serializeNBT()), player);
                        }
                    }
                    event.setAmount(blocked);
                }
            }
        }
    }
}