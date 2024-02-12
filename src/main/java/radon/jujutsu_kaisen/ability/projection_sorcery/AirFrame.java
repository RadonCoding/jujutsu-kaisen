package radon.jujutsu_kaisen.ability.projection_sorcery;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.entity.effect.AirFrameEntity;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

public class AirFrame extends Ability implements Ability.IChannelened, Ability.IDurationable {
    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        if (target == null || target.isDeadOrDying() || !owner.hasLineOfSight(target)) return false;

        IJujutsuCapability jujutsuCap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (jujutsuCap == null) return false;

        ISorcererData data = jujutsuCap.getSorcererData();

        if (data.isChanneling(this)) {
            return HelperMethods.RANDOM.nextInt(5) != 0;
        }
        return HelperMethods.RANDOM.nextInt(3) == 0;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.CHANNELED;
    }

    @Override
    public void run(LivingEntity owner) {
        owner.swing(InteractionHand.MAIN_HAND);

        if (owner.level().isClientSide) return;

        int charge = this.getCharge(owner);

        int speed = 3;

        AirFrameEntity frame = new AirFrameEntity(owner, this.getPower(owner));
        Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);
        Vec3 spawn = new Vec3(owner.getX(), owner.getEyeY() - (frame.getBbHeight() / 2.0F), owner.getZ())
                .add(look.scale(charge * speed));
        frame.moveTo(spawn.x, spawn.y, spawn.z, RotationUtil.getTargetAdjustedYRot(owner), RotationUtil.getTargetAdjustedXRot(owner));

        owner.level().addFreshEntity(frame);
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 5.0F;
    }

    @Override
    public int getCooldown() {
        return 10 * 20;
    }

    @Override
    public int getDuration() {
        return 5;
    }

    @Override
    public MenuType getMenuType(LivingEntity owner) {
        return MenuType.MELEE;
    }

    @Override
    public Classification getClassification() {
        return Classification.PROJECTION;
    }
}
