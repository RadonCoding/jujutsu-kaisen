package radon.jujutsu_kaisen.ability.ten_shadows;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.AbilityHandler;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.ten_shadows.GreatSerpentEntity;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

public class GreatSerpentGrab extends Ability {
    public static final double RANGE = 16.0D;

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return HelperMethods.RANDOM.nextInt(3) == 0 && target != null && this.getTarget(owner) == target;
    }

    @Override
    public boolean isValid(LivingEntity owner) {
        if (!super.isValid(owner)) return false;
        return JJKAbilities.hasTamed(owner, JJKEntities.GREAT_SERPENT.get()) && JJKAbilities.GREAT_SERPENT.get().getStatus(owner) == Status.SUCCESS;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    private @Nullable LivingEntity getTarget(LivingEntity owner) {
        if (RotationUtil.getLookAtHit(owner, RANGE) instanceof EntityHitResult hit && hit.getEntity() instanceof LivingEntity target) {
            if (!owner.canAttack(target)) return null;

            return target;
        }
        return null;
    }

    @Override
    public void run(LivingEntity owner) {
        if (owner.level().isClientSide) return;

        LivingEntity target = this.getTarget(owner);

        if (target == null) return;

        AbilityHandler.trigger(owner, JJKAbilities.GREAT_SERPENT.get());

        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        GreatSerpentEntity serpent = cap.getSummonByClass(GreatSerpentEntity.class);

        if (serpent == null) return;

        serpent.grab(target);
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
        return 0;
    }

    @Override
    protected int getCooldown() {
        return 10 * 20;
    }

    @Override
    public MenuType getMenuType() {
        return MenuType.MELEE;
    }
}