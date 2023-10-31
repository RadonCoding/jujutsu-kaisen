package radon.jujutsu_kaisen.ability.disaster_tides;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.entity.base.FishShikigamiProjectile;
import radon.jujutsu_kaisen.entity.projectile.EelShikigamiProjectile;
import radon.jujutsu_kaisen.entity.projectile.PiranhaShikigamiProjectile;
import radon.jujutsu_kaisen.entity.projectile.SharkShikigamiProjectile;
import radon.jujutsu_kaisen.util.HelperMethods;

public class FishShikigami extends Ability {
    public static final double RANGE = 30.0D;

    @Override
    public boolean isChantable() {
        return true;
    }

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

        FishShikigamiProjectile[] projectiles = new FishShikigamiProjectile[]{
                new EelShikigamiProjectile(owner, this.getPower(owner), target, 0.0F, 0.0F),
                new SharkShikigamiProjectile(owner, this.getPower(owner), target, 0.0F, 0.0F),
                new PiranhaShikigamiProjectile(owner, getPower(owner), target, 0.0F, 0.0F)
        };
        owner.level().addFreshEntity(projectiles[HelperMethods.RANDOM.nextInt(projectiles.length)]);
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
        return 100.0F;
    }

    @Override
    public int getCooldown() {
        return 5 * 20;
    }

    @Override
    public boolean isTechnique() {
        return true;
    }

    @Override
    public MenuType getMenuType() {
        return MenuType.SCROLL;
    }
}
