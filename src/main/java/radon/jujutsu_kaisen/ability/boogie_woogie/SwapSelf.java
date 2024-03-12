package radon.jujutsu_kaisen.ability.boogie_woogie;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.entity.projectile.base.JujutsuProjectile;
import radon.jujutsu_kaisen.entity.projectile.CursedEnergyImbuedItemProjectile;
import radon.jujutsu_kaisen.item.base.CursedToolItem;
import radon.jujutsu_kaisen.sound.JJKSounds;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

public class SwapSelf extends Ability {
    public static final double RANGE = 30.0D;

    @Override
    public boolean isScalable(LivingEntity owner) {
        return false;
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return this.getTarget(owner) == target && HelperMethods.RANDOM.nextInt(20) == 0;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    public static boolean canSwap(Entity target) {
        if (target.isPickable() || target instanceof ItemEntity item && item.getItem().getItem() instanceof CursedToolItem
                || target instanceof CursedEnergyImbuedItemProjectile || target instanceof JujutsuProjectile) {
            return true;
        }

        IJujutsuCapability cap = target.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return false;

        ISorcererData data = cap.getSorcererData();

        return data.getEnergy() > 0.0F;
    }

    public static void swap(Entity first, Entity second) {
        first.level().playSound(null, first.getX(), first.getY(), first.getZ(), JJKSounds.CLAP.get(), SoundSource.MASTER, 1.0F, 1.0F);
        first.level().playSound(null, second.getX(), second.getY(), second.getZ(), JJKSounds.CLAP.get(), SoundSource.MASTER, 1.0F, 1.0F);

        Vec3 pos = second.position();

        Vec2 ownerRot = first.getRotationVector();
        Vec2 targetRot = second.getRotationVector();

        second.teleportTo(first.getX(), first.getY(), first.getZ());
        first.teleportTo(pos.x, pos.y, pos.z);

        second.setYRot(ownerRot.y);
        second.setXRot(ownerRot.x);

        first.setYRot(targetRot.y);
        first.setXRot(targetRot.x);
    }

    private @Nullable Entity getTarget(LivingEntity owner) {
        if (RotationUtil.getLookAtHit(owner, RANGE, target -> !target.isSpectator()) instanceof EntityHitResult hit) {
            Entity target = hit.getEntity();
            return canSwap(target) ? target : null;
        }
        return null;
    }

    @Override
    public void run(LivingEntity owner) {
        owner.swing(InteractionHand.MAIN_HAND);

        if (owner.level().isClientSide) return;

        Entity target = this.getTarget(owner);

        if (target != null) {
            swap(owner, target);
        }
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 5.0F;
    }

    @Override
    public Status isTriggerable(LivingEntity owner) {
        Entity target = this.getTarget(owner);

        if (target == null) {
            return Status.FAILURE;
        }
        return super.isTriggerable(owner);
    }

    @Override
    public MenuType getMenuType(LivingEntity owner) {
        return MenuType.MELEE;
    }
}
