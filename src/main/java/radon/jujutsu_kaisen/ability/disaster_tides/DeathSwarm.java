package radon.jujutsu_kaisen.ability.disaster_tides;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import radon.jujutsu_kaisen.entity.base.FishShikigamiProjectile;
import radon.jujutsu_kaisen.entity.projectile.EelShikigamiProjectile;
import radon.jujutsu_kaisen.entity.projectile.PiranhaShikigamiProjectile;
import radon.jujutsu_kaisen.entity.projectile.SharkShikigamiProjectile;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

public class DeathSwarm extends Ability implements Ability.IDomainAttack {
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
            if (RotationUtil.getLookAtHit(owner, RANGE) instanceof EntityHitResult hit && hit.getEntity() instanceof LivingEntity target) {
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

        this.performEntity(owner, null, target);
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
        return 300.0F;
    }

    @Override
    public int getCooldown() {
        return 10 * 20;
    }

    @Override
    public MenuType getMenuType() {
        return MenuType.MELEE;
    }

    @Override
    public void performEntity(LivingEntity owner, @Nullable DomainExpansionEntity domain, @Nullable LivingEntity target) {
        if (target == null) return;

        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        for (int i = 0; i < 6; i++) {
            float xOffset = (HelperMethods.RANDOM.nextFloat() - 0.5F) * 5.0F;
            float yOffset = (HelperMethods.RANDOM.nextFloat() - 0.5F) * 5.0F;

            float power = domain == null ? this.getPower(owner) : cap.getRealPower();

            FishShikigamiProjectile[] projectiles = new FishShikigamiProjectile[]{
                    new EelShikigamiProjectile(owner, power, target, xOffset, yOffset),
                    new SharkShikigamiProjectile(owner, power, target, xOffset, yOffset),
                    new PiranhaShikigamiProjectile(owner, power, target, xOffset, yOffset)
            };

            int delay = i * 2;

            cap.delayTickEvent(() -> {
                if (target.isAlive() && !target.isRemoved()) {
                    owner.level().addFreshEntity(projectiles[HelperMethods.RANDOM.nextInt(projectiles.length)]);
                }
            }, delay);
        }
    }
}
