package radon.jujutsu_kaisen.ability.disaster_tides;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.DomainExpansion;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import radon.jujutsu_kaisen.entity.projectile.base.FishShikigamiProjectile;
import radon.jujutsu_kaisen.entity.projectile.EelShikigamiProjectile;
import radon.jujutsu_kaisen.entity.projectile.PiranhaShikigamiProjectile;
import radon.jujutsu_kaisen.entity.projectile.SharkShikigamiProjectile;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

public class DeathSwarm extends Ability implements Ability.IDomainAttack {
    public static final double RANGE = 30.0D;

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return target != null && !target.isDeadOrDying() && this.getTarget(owner) == target;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    private @Nullable LivingEntity getTarget(LivingEntity owner) {
        LivingEntity result = null;

        if (RotationUtil.getLookAtHit(owner, RANGE) instanceof EntityHitResult hit && hit.getEntity() instanceof LivingEntity target) {
            result = target;
        }
        return result;
    }

    private void perform(LivingEntity owner, LivingEntity target, @Nullable DomainExpansionEntity domain) {
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        for (int i = 0; i < 12; i++) {
            float xOffset = (HelperMethods.RANDOM.nextFloat() - 0.5F) * 5.0F;
            float yOffset = owner.getBbHeight() + ((HelperMethods.RANDOM.nextFloat() - 0.5F) * 5.0F);

            float power = domain == null ? this.getPower(owner) : this.getPower(owner) * DomainExpansion.getStrength(owner, false);

            FishShikigamiProjectile[] projectiles = new FishShikigamiProjectile[]{
                    new EelShikigamiProjectile(owner, power, target, xOffset, yOffset),
                    new SharkShikigamiProjectile(owner, power, target, xOffset, yOffset),
                    new PiranhaShikigamiProjectile(owner, power, target, xOffset, yOffset)
            };

            int delay = i * 2;

            cap.delayTickEvent(() -> {
                if (target.isAlive() && !target.isRemoved()) {
                    FishShikigamiProjectile projectile = projectiles[HelperMethods.RANDOM.nextInt(projectiles.length)];
                    projectile.setDomain(domain != null);
                    owner.level().addFreshEntity(projectile);
                }
            }, delay);
        }
    }

    @Override
    public void performEntity(LivingEntity owner, LivingEntity target, DomainExpansionEntity domain) {
        this.perform(owner, target, domain);
    }

    @Override
    public void run(LivingEntity owner) {
        LivingEntity target = this.getTarget(owner);

        if (target == null) return;

        this.perform(owner, target, null);
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
    public MenuType getMenuType(LivingEntity owner) {
        return MenuType.MELEE;
    }
}