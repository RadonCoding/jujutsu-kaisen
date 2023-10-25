package radon.jujutsu_kaisen.ability.limitless;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.JJKAbilities;

public class Fly extends Ability implements Ability.IChannelened {
    @Override
    public boolean isChantable() {
        return false;
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return false;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.CHANNELED;
    }

    @Override
    public MenuType getMenuType() {
        return MenuType.SCROLL;
    }

    @Override
    public boolean isValid(LivingEntity owner) {
        return JJKAbilities.hasToggled(owner, JJKAbilities.INFINITY.get());
    }

    @Override
    public void run(LivingEntity owner) {
        Vec3 movement = owner.getDeltaMovement();
        owner.setDeltaMovement(movement.x(), 0.3D, movement.z());
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 1.0F;
    }

    @Override
    public void onStart(LivingEntity owner) {

    }

    @Override
    public void onRelease(LivingEntity owner) {

    }
}
