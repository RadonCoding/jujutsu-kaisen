package radon.jujutsu_kaisen.ability.curse_manipulation;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.curse_manipulation.util.CurseManipulationUtil;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.curse_manipulation.AbsorbedCurse;
import radon.jujutsu_kaisen.data.curse_manipulation.ICurseManipulationData;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.curse.FishCurseEntity;
import radon.jujutsu_kaisen.entity.curse.WormCurseEntity;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

public class FishSwarm extends Ability {

    public static final double RANGE = 16.0D;

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        if (target == null || target.isDeadOrDying()) return false;
        return HelperMethods.RANDOM.nextInt(40) == 0 && this.getTarget(owner) == target;
    }

    @Override
    public boolean isValid(LivingEntity owner)
    {
        if (!super.isValid(owner)) return false;

        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return false;

        ICurseManipulationData data = cap.getCurseManipulationData();

        int amount = 0;

        for (int i = 0; i < data.getCurses().size(); i++)
        {
            if (data.getCurses().get(i).getType() != JJKEntities.FISH_CURSE.get()) continue;

            amount++;
        }
        return amount >= 5;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner)
    {
        return ActivationType.INSTANT;
    }

    private @Nullable LivingEntity getTarget(LivingEntity owner) {
        if (RotationUtil.getLookAtHit(owner, RANGE) instanceof EntityHitResult hit && hit.getEntity() instanceof LivingEntity target) {
            return target;
        }
        return null;
    }

    @Override
    public void run(LivingEntity owner)
    {
        LivingEntity target = this.getTarget(owner);

        if (target == null) return;

        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        ICurseManipulationData data = cap.getCurseManipulationData();

        for (int i = 0; i < 5; i++)
        {
            AbsorbedCurse curse = data.getCurse(JJKEntities.FISH_CURSE.get());

            if (!(CurseManipulationUtil.summonCurse(owner, curse, false) instanceof FishCurseEntity fish)) continue;

            fish.setTarget(target);
        }
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

        AbsorbedCurse curse = data.getCurse(JJKEntities.FISH_CURSE.get());
        return curse == null ? 0.0F : CurseManipulationUtil.getCurseCost(curse);
    }

    @Override
    public int getCooldown() {
        return 5 * 20;
    }

    @Override
    public MenuType getMenuType(LivingEntity owner) {
        return MenuType.MELEE;
    }
}
