package radon.jujutsu_kaisen.ability.boogie_woogie;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.sound.JJKSounds;
import radon.jujutsu_kaisen.util.HelperMethods;

public class SwapSelf extends Ability {
    public static final double RANGE = 30.0D;

    @Override
    public boolean isScalable(LivingEntity owner) {
        return false;
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return false;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    private @Nullable Entity getTarget(LivingEntity owner) {
        if (HelperMethods.getLookAtHit(owner, RANGE, target -> !target.isSpectator()) instanceof EntityHitResult hit) {
            Entity target = hit.getEntity();

            if (!target.isPickable() && !(target instanceof ItemEntity) && !(target instanceof Projectile) || (target instanceof LivingEntity living &&
                    JJKAbilities.hasTrait(living, Trait.HEAVENLY_RESTRICTION))) return null;

            return target;
        }
        return null;
    }

    @Override
    public void run(LivingEntity owner) {
        owner.swing(InteractionHand.MAIN_HAND);

        if (owner.level().isClientSide) return;

        Entity target = this.getTarget(owner);

        if (target != null) {
            owner.level().playSound(null, owner.getX(), owner.getY(), owner.getZ(), JJKSounds.CLAP.get(), SoundSource.MASTER, 2.0F, 1.0F);
            owner.level().playSound(null, target.getX(), target.getY(), target.getZ(), JJKSounds.CLAP.get(), SoundSource.MASTER, 1.0F, 1.0F);

            Vec3 pos = target.position();

            Vec2 ownerRot = owner.getRotationVector();
            Vec2 targetRot = target.getRotationVector();

            target.teleportTo(owner.getX(), owner.getY(), owner.getZ());
            owner.teleportTo(pos.x, pos.y, pos.z);

            target.setYRot(ownerRot.y);
            target.setXRot(ownerRot.x);

            owner.setYRot(targetRot.y);
            owner.setXRot(targetRot.x);
        }
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 5.0F;
    }

    @Override
    public Status checkTriggerable(LivingEntity owner) {
        Entity target = this.getTarget(owner);

        if (target == null) {
            return Status.FAILURE;
        }
        return super.checkTriggerable(owner);
    }

    @Override
    public MenuType getMenuType() {
        return MenuType.SCROLL;
    }
}
