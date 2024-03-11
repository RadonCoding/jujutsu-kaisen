package radon.jujutsu_kaisen.ability.dismantle_and_cleave;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.DomainExpansion;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import radon.jujutsu_kaisen.entity.projectile.DismantleProjectile;
import radon.jujutsu_kaisen.sound.JJKSounds;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

public class Dismantle extends Ability implements Ability.IChannelened, Ability.IDurationable, Ability.IDomainAttack {
    public static final float SPEED = 5.0F;

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        if (target == null || target.isDeadOrDying() || !owner.hasLineOfSight(target)) return false;

        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return false;

        IAbilityData data = cap.getAbilityData();

        if (data.isChanneling(this)) {
            return HelperMethods.RANDOM.nextInt(20) != 0;
        }
        return HelperMethods.RANDOM.nextInt(20) == 0;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.CHANNELED;
    }

    @Override
    public void run(LivingEntity owner) {
        if (this.getCharge(owner) % 2 == 0) {
            owner.swing(InteractionHand.MAIN_HAND);

            if (owner.level().isClientSide) return;

            DismantleProjectile dismantle = new DismantleProjectile(owner, this.getPower(owner), (owner.isShiftKeyDown() ? 90.0F : 0.0F) + (HelperMethods.RANDOM.nextFloat() - 0.5F) * 60.0F);
            dismantle.setDeltaMovement(RotationUtil.getTargetAdjustedLookAngle(owner).scale(SPEED));
            owner.level().addFreshEntity(dismantle);

            owner.level().playSound(null, owner.getX(), owner.getY(), owner.getZ(), JJKSounds.SLASH.get(), SoundSource.MASTER, 1.0F, 1.0F);
        }
    }

    @Override
    public void performBlock(LivingEntity owner, DomainExpansionEntity domain, BlockPos pos) {
        float power = this.getPower(owner) * DomainExpansion.getStrength(owner, false);

        DismantleProjectile dismantle = new DismantleProjectile(owner, power,
                (HelperMethods.RANDOM.nextFloat() - 0.5F) * 360.0F, pos.getCenter(),
                HelperMethods.RANDOM.nextInt(DismantleProjectile.MIN_LENGTH, (DismantleProjectile.MAX_LENGTH + 1) * 2),
                false, true);
        dismantle.setDomain(true);
        owner.level().addFreshEntity(dismantle);

        if (!owner.level().isClientSide) {
            owner.level().playSound(null, pos.getX(), pos.getY(), pos.getZ(), JJKSounds.SLASH.get(), SoundSource.MASTER, 0.05F, 1.0F);
        }
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 20.0F;
    }

    @Override
    public int getCooldown() {
        return 5 * 20;
    }

    @Override
    public int getDuration() {
        return 5;
    }

    @Override
    public Classification getClassification() {
        return Classification.SLASHING;
    }

    @Override
    public MenuType getMenuType(LivingEntity owner) {
        return MenuType.MELEE;
    }
}
