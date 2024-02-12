package radon.jujutsu_kaisen.ability.limitless;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;

public class Fly extends Ability implements Ability.IChannelened {
    private static final float SPEED = 0.05F;

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
        return ActivationType.CHANNELED;
    }

    @Override
    public MenuType getMenuType(LivingEntity owner) {
        return MenuType.MELEE;
    }

    @Override
    public boolean isValid(LivingEntity owner) {
        ISorcererData data = owner.getData(JJKAttachmentTypes.SORCERER);

        if (data == null) return false;

        return data.hasToggled(JJKAbilities.INFINITY.get());
    }

    @Override
    public void run(LivingEntity owner) {
        owner.resetFallDistance();

        Vec3 movement = owner.getDeltaMovement();
        owner.setDeltaMovement(movement.x, SPEED * 2, movement.z);

        float f = owner.xxa * 0.5F;
        float f1 = owner.zza;

        if (f1 <= 0.0F) {
            f1 *= 0.25F;
        }
        owner.moveRelative(SPEED, new Vec3(f, 0.0F, f1));
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 1.0F;
    }
}
