package radon.jujutsu_kaisen.ability.idle_transfiguration;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.IFlight;
import radon.jujutsu_kaisen.ability.base.Transformation;
import radon.jujutsu_kaisen.item.JJKItems;
import radon.jujutsu_kaisen.util.RotationUtil;

public class Wings extends Transformation implements IFlight {
    private static final float SPEED = 0.01F;

    @Override
    public boolean isScalable(LivingEntity owner) {
        return false;
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return owner.fallDistance > 2.0F && !owner.isInFluidType();
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.TOGGLED;
    }

    @Override
    public void run(LivingEntity owner) {
        owner.resetFallDistance();

        Vec3 movement = owner.getDeltaMovement();
        Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);
        owner.setDeltaMovement(movement.x, look.y, movement.z);

        float f = owner.xxa * 0.5F;
        float f1 = owner.zza;

        if (f1 <= 0.0F) {
            f1 *= 0.25F;
        }
        owner.moveRelative(SPEED, new Vec3(f, 0.0F, f1));
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 0.2F;
    }

    @Override
    public boolean isReplacement() {
        return false;
    }

    @Override
    public Item getItem() {
        return JJKItems.WINGS.get();
    }

    @Override
    public Part getBodyPart() {
        return Part.BODY;
    }

    @Override
    public void onRightClick(LivingEntity owner) {

    }

    @Override
    public void onEnabled(LivingEntity owner) {
        
    }

    @Override
    public void onDisabled(LivingEntity owner) {

    }

    @Override
    public boolean canFly(LivingEntity owner) {
        return !owner.onGround() && new Vec3(owner.xxa, 0.0D, owner.zza).length() > 1.0E-7D;
    }
}