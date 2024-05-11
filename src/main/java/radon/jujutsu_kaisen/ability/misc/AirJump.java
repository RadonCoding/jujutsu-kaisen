package radon.jujutsu_kaisen.ability.misc;

import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.registry.JJKAbilities;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.sorcerer.Trait;

public class AirJump extends Ability {
    @Override
    public boolean isScalable(LivingEntity owner) {
        return false;
    }

    @Override
    public boolean isPhysical() {
        return true;
    }

    @Override
    public boolean isTechnique() {
        return false;
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return owner.fallDistance > 1.0F;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    @Override
    public boolean isValid(LivingEntity owner) {
        if (owner.onGround() || (owner instanceof Player player && player.getAbilities().flying)) {
            return false;
        }
        return super.isValid(owner);
    }

    @Override
    public void run(LivingEntity owner) {
        if (owner.level() instanceof ServerLevel level) {
            level.sendParticles(ParticleTypes.CLOUD, owner.getX(), owner.getY(), owner.getZ(), 0, 0.0D, 0.0D, 0.0D, 1.0D);
            owner.level().playSound(null, owner.getX(), owner.getY(), owner.getZ(), SoundEvents.GENERIC_SMALL_FALL, SoundSource.MASTER, 1.0F, 0.3F);
        }

        Vec3 vec3 = owner.getDeltaMovement();
        owner.setDeltaMovement(vec3.x, 0.42F, vec3.z);

        if (owner.isSprinting()) {
            float f = owner.getYRot() * (Mth.PI / 180.0F);
            owner.setDeltaMovement(owner.getDeltaMovement().add(-Mth.sin(f) * 0.2F, 0.0D, Mth.cos(f) * 0.2F));
        }
        owner.hasImpulse = true;
    }

    @Nullable
    @Override
    public Ability getParent(LivingEntity owner) {
        return JJKAbilities.CURSED_ENERGY_FLOW.get();
    }

    @Override
    public int getPointsCost() {
        return ConfigHolder.SERVER.airJumpCost.get();
    }

    @Override
    public boolean isUnlocked(LivingEntity owner) {
        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return false;

        ISorcererData data = cap.getSorcererData();

        return data.hasTrait(Trait.HEAVENLY_RESTRICTION_BODY) || super.isUnlocked(owner);
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 0;
    }

    @Override
    public int getCooldown() {
        return 20;
    }

    @Override
    public MenuType getMenuType(LivingEntity owner) {
        return MenuType.NONE;
    }
}
