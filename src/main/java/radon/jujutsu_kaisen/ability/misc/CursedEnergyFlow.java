package radon.jujutsu_kaisen.ability.misc;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.CombatRules;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.enchantment.ThornsEnchantment;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.AbilityDisplayInfo;
import radon.jujutsu_kaisen.ability.AbilityHandler;
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
import radon.jujutsu_kaisen.config.ServerConfig;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.entity.JujutsuLightningEntity;
import radon.jujutsu_kaisen.entity.base.ISorcerer;
import radon.jujutsu_kaisen.item.JJKItems;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;
import radon.jujutsu_kaisen.sound.JJKSounds;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.UUID;

public class CursedEnergyFlow extends Ability implements Ability.IToggled {
    private static final float LIGHTNING_DAMAGE = 5.0F;

    private static final UUID MOVEMENT_SPEED_UUID = UUID.fromString("f8067e42-8642-46f5-88d3-3d8d060df1d4");

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
    public void run(LivingEntity owner) {
        if (!owner.level().getBlockState(owner.blockPosition()).getFluidState().isEmpty()) {
            Vec3 movement = owner.getDeltaMovement();

            if (!owner.level().getBlockState(owner.blockPosition().above()).getFluidState().isEmpty()) {
                owner.setDeltaMovement(movement.x, 0.1D, movement.z);
            } else if (movement.y < 0.0D) {
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

        for (int i = 0; i < 12 * scale; i++) {
            double x = owner.getX() + (HelperMethods.RANDOM.nextDouble() - 0.5D) * (owner.getBbWidth() * 1.5F * scale) - owner.getLookAngle().scale(0.35D).x;
            double y = owner.getY() + HelperMethods.RANDOM.nextDouble() * owner.getBbHeight();
            double z = owner.getZ() + (HelperMethods.RANDOM.nextDouble() - 0.5D) * (owner.getBbWidth() * 1.5F * scale) - owner.getLookAngle().scale(0.35D).z;
            double speed = (owner.getBbHeight() * 0.3F) * HelperMethods.RANDOM.nextDouble();
            level.sendParticles(new CursedEnergyParticle.CursedEnergyParticleOptions(ParticleColors.getCursedEnergyColor(owner), owner.getBbWidth() * 0.5F * scale,
                            0.2F * scale, 6), x, y, z, 0, 0.0D, speed * scale, 0.0D, 1.0D);
        }

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
                            double x = entity.getX() + (HelperMethods.RANDOM.nextDouble() - 0.5D) * (entity.getBbWidth() * 2 * scale);
                            double y = entity.getY() + HelperMethods.RANDOM.nextDouble() * (entity.getBbHeight() * 1.25F * scale);
                            double z = entity.getZ() + (HelperMethods.RANDOM.nextDouble() - 0.5D) * (entity.getBbWidth() * 2 * scale);
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
    public AbilityDisplayInfo getDisplay(LivingEntity owner) {
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        Vec2 coordinates = this.getDisplayCoordinates();
        return new AbilityDisplayInfo(String.format("%s_%s", JJKAbilities.getKey(this).getPath(), cap.getType().name().toLowerCase()), coordinates.x, coordinates.y);
    }

    @Override
    public void onEnabled(LivingEntity owner) {
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        double movement = cap.getExperience() * 0.001D;
        HelperMethods.applyModifier(owner, Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED_UUID, "Movement speed",
                Math.min(owner.getAttributeBaseValue(Attributes.MOVEMENT_SPEED) * 2,  movement), AttributeModifier.Operation.ADDITION);
    }

    @Override
    public void onDisabled(LivingEntity owner) {
        HelperMethods.removeModifier(owner, Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED_UUID);
    }

    @Override
    public Status checkStatus(LivingEntity owner) {
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        return cap.getEnergy() == 0.0F ? Status.FAILURE : super.checkStatus(owner);
    }

    @Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class CursedEnergyFlowForgeEvents {
        @SubscribeEvent
        public static void onLivingDamage(LivingDamageEvent event) {
            DamageSource source = event.getSource();
            if (!(source.getEntity() instanceof LivingEntity attacker)) return;

            if (attacker.level().isClientSide) return;

            boolean melee = !source.isIndirect() && (source.is(DamageTypes.MOB_ATTACK) || source.is(DamageTypes.PLAYER_ATTACK) || source.is(JJKDamageSources.SOUL));

            if (JJKAbilities.hasToggled(attacker, JJKAbilities.CURSED_ENERGY_FLOW.get())) {
                if (attacker.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
                    ISorcererData attackerCap = attacker.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

                    if (melee) {
                        float increased = event.getAmount() * (1.0F + attackerCap.getExperience() * 0.001F);

                        switch (attackerCap.getNature()) {
                            case LIGHTNING -> increased *= (attacker.getItemInHand(InteractionHand.MAIN_HAND).is(JJKItems.NYOI_STAFF.get()) ? 2.0F : 1.0F);
                            case ROUGH -> increased *= 1.5F;
                        };
                        float increase = increased - event.getAmount();

                        if (!(attacker instanceof Player player) || !player.getAbilities().instabuild) {
                            float cost = increase * (attackerCap.hasTrait(Trait.SIX_EYES) ? 0.5F : 1.0F);
                            if (attackerCap.getEnergy() < cost) return;
                            attackerCap.useEnergy(cost);

                            if (attacker instanceof ServerPlayer player) {
                                PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(attackerCap.serializeNBT()), player);
                            }
                        }
                        event.setAmount(increased);
                    }
                }
            }

            LivingEntity victim = event.getEntity();

            if (melee) {
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

                    if (melee) {
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

                    float armor = victimCap.getExperience() * 0.005F * (JJKAbilities.hasToggled(victim, JJKAbilities.CURSED_ENERGY_SHIELD.get()) ? 2.0F : 1.0F);
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