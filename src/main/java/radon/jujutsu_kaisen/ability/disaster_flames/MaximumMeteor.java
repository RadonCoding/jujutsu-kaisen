package radon.jujutsu_kaisen.ability.disaster_flames;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.IAttack;
import radon.jujutsu_kaisen.ability.base.IChanneled;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.ICharged;
import radon.jujutsu_kaisen.ability.base.IDomainAttack;
import radon.jujutsu_kaisen.ability.base.IDurationable;
import radon.jujutsu_kaisen.ability.base.ITenShadowsAttack;
import radon.jujutsu_kaisen.ability.base.IToggled;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.cursed_technique.base.ICursedTechnique;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.entity.effect.MeteorEntity;

public class MaximumMeteor extends Ability {
    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        if (target == null || target.isDeadOrDying()) return false;
        if (!owner.hasLineOfSight(target)) return false;

        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return false;

        ISorcererData sorcererData = cap.getSorcererData();
        IAbilityData abilityData = cap.getAbilityData();

        ICursedTechnique technique = sorcererData.getTechnique();

        if (technique != null) {
            Ability domain = technique.getDomain();

            if (domain != null) {
                if (abilityData.hasToggled(domain)) {
                    return false;
                }
            }
        }
        return (sorcererData.getType() == JujutsuType.CURSE || JJKAbilities.RCT1.get().isUnlocked(owner) ? owner.getHealth() / owner.getMaxHealth() < 0.9F :
                owner.getHealth() / owner.getMaxHealth() < 0.4F);
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    private static boolean canSpawn(LivingEntity owner, float power) {
        Vec3 offset = owner.position().add(0.0D, MeteorEntity.HEIGHT + MeteorEntity.getSize(power), 0.0D);
        BlockHitResult hit = owner.level().clip(new ClipContext(owner.position(), offset, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, CollisionContext.empty()));
        return hit.getType() != HitResult.Type.BLOCK;
    }

    @Override
    public void run(LivingEntity owner) {
        if (canSpawn(owner, this.getOutput(owner))) {
            owner.swing(InteractionHand.MAIN_HAND);

            MeteorEntity meteor = new MeteorEntity(owner, this.getOutput(owner));
            owner.level().addFreshEntity(meteor);
        }
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 1000.0F;
    }

    @Override
    public int getCooldown() {
        return 30 * 20;
    }

    @Override
    public Status isTriggerable(LivingEntity owner) {
        if (owner.hasEffect(JJKEffects.STUN.get()) || !canSpawn(owner, this.getOutput(owner))) {
            return Status.FAILURE;
        }
        return super.isTriggerable(owner);
    }

    @Override
    public Classification getClassification() {
        return Classification.FIRE;
    }
}