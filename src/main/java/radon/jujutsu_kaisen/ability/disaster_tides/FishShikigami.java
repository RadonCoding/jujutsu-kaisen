package radon.jujutsu_kaisen.ability.disaster_tides;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.DisplayType;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import radon.jujutsu_kaisen.entity.base.FishShikigamiProjectile;
import radon.jujutsu_kaisen.entity.projectile.EelShikigamiProjectile;
import radon.jujutsu_kaisen.entity.projectile.PiranhaShikigamiProjectile;
import radon.jujutsu_kaisen.entity.projectile.SharkShikigamiProjectile;
import radon.jujutsu_kaisen.util.HelperMethods;

public class FishShikigami extends Ability implements Ability.IDomainAttack {
    public static final double RANGE = 30.0D;

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return target != null && this.getTarget(owner) == target;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    private @Nullable LivingEntity getTarget(LivingEntity owner) {
        LivingEntity result = null;

        if (owner instanceof Player) {
            if (HelperMethods.getLookAtHit(owner, RANGE) instanceof EntityHitResult hit && hit.getEntity() instanceof LivingEntity target) {
                if (owner.canAttack(target)) {
                    result = target;
                }
            }
        }
        return result;
    }

    @Override
    public void run(LivingEntity owner) {
        LivingEntity target = this.getTarget(owner);

        if (target == null) return;

        this.perform(owner, null, target);
    }

    @Override
    public Status checkTriggerable(LivingEntity owner) {
        LivingEntity target = this.getTarget(owner);

        if (target == null) {
            return Status.FAILURE;
        }
        return super.checkTriggerable(owner);
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 50.0F;
    }

    @Override
    public int getCooldown() {
        return 10 * 20;
    }

    @Override
    public boolean isTechnique() {
        return true;
    }

    @Override
    public Classification getClassification() {
        return Classification.WATER;
    }

    @Override
    public DisplayType getDisplayType() {
        return DisplayType.SCROLL;
    }

    @Override
    public void perform(LivingEntity owner, @Nullable DomainExpansionEntity domain, @Nullable LivingEntity target) {
        if (owner.level.isClientSide || target == null) return;

        owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
            for (int i = 0; i < 12; i++) {
                float xOffset = (HelperMethods.RANDOM.nextFloat() - 0.5F) * 5.0F;
                float yOffset = (HelperMethods.RANDOM.nextFloat() - 0.5F) * 5.0F;

                FishShikigamiProjectile[] projectiles = new FishShikigamiProjectile[] {
                        new EelShikigamiProjectile(owner, target, xOffset, yOffset),
                        new SharkShikigamiProjectile(owner, target, xOffset, yOffset),
                        new PiranhaShikigamiProjectile(owner, target, xOffset, yOffset)
                };

                int delay = i * 2;
                cap.delayTickEvent(() ->
                        owner.level.addFreshEntity(projectiles[HelperMethods.RANDOM.nextInt(projectiles.length)]), delay);
            }
        });
    }
}
