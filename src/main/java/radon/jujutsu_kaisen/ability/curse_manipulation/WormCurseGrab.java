package radon.jujutsu_kaisen.ability.curse_manipulation;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.IAttack;
import radon.jujutsu_kaisen.ability.base.IChanneled;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.ICharged;
import radon.jujutsu_kaisen.ability.base.IDomainAttack;
import radon.jujutsu_kaisen.ability.base.IDurationable;
import radon.jujutsu_kaisen.ability.base.ITenShadowsAttack;
import radon.jujutsu_kaisen.ability.base.IToggled;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.curse_manipulation.ICurseManipulationData;
import radon.jujutsu_kaisen.data.curse_manipulation.AbsorbedCurse;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.curse.WormCurseEntity;
import radon.jujutsu_kaisen.ability.curse_manipulation.util.CurseManipulationUtil;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

public class WormCurseGrab extends Ability {
    public static final double RANGE = 16.0D;

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return HelperMethods.RANDOM.nextInt(3) == 0 && target != null && this.getTarget(owner) == target;
    }

    @Override
    public boolean isValid(LivingEntity owner) {
        if (!super.isValid(owner)) return false;

        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return false;

        ICurseManipulationData data = cap.getCurseManipulationData();

        return data.hasCurse(JJKEntities.WORM_CURSE.get());
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    private @Nullable LivingEntity getTarget(LivingEntity owner) {
        if (RotationUtil.getLookAtHit(owner, RANGE) instanceof EntityHitResult hit && hit.getEntity() instanceof LivingEntity target) {
            return target;
        }
        return null;
    }

    @Override
    public void run(LivingEntity owner) {
        LivingEntity target = this.getTarget(owner);

        if (target == null) return;

        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        ICurseManipulationData data = cap.getCurseManipulationData();


        AbsorbedCurse curse = data.getCurse(JJKEntities.WORM_CURSE.get());

        if (!(CurseManipulationUtil.summonCurse(owner, curse, false) instanceof WormCurseEntity worm)) return;

        worm.grab(target);
    }

    @Override
    public Status isTriggerable(LivingEntity owner) {
        LivingEntity target = this.getTarget(owner);

        if (target == null) {
            return Status.FAILURE;
        }
        return super.isTriggerable(owner);
    }

    @Override
    public float getCost(LivingEntity owner) {
        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return 0.0F;

        ICurseManipulationData data = cap.getCurseManipulationData();

        if (data == null) return 0.0F;

        AbsorbedCurse curse = data.getCurse(JJKEntities.WORM_CURSE.get());
        return curse == null ? 0.0F : CurseManipulationUtil.getCurseCost(curse);
    }

    @Override
    public int getCooldown() {
        return 10 * 20;
    }

    @Override
    public MenuType getMenuType(LivingEntity owner) {
        return MenuType.MELEE;
    }
}