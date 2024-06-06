package radon.jujutsu_kaisen.ability.disaster_tides;


import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.*;
import radon.jujutsu_kaisen.entity.projectile.FishShikigamiProjectile;
import radon.jujutsu_kaisen.entity.projectile.EelShikigamiProjectile;
import radon.jujutsu_kaisen.entity.projectile.PiranhaShikigamiProjectile;
import radon.jujutsu_kaisen.entity.projectile.SharkShikigamiProjectile;
import radon.jujutsu_kaisen.util.HelperMethods;

public class FishShikigami extends Ability {
    public static final double RANGE = 30.0D;

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        if (target == null || target.isDeadOrDying()) return false;
        if (!owner.hasLineOfSight(target)) return false;
        return HelperMethods.RANDOM.nextInt(40) == 0;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    @Override
    public void run(LivingEntity owner) {
        float xOffset = (HelperMethods.RANDOM.nextFloat() - 0.5F) * 5.0F;
        float yOffset = owner.getBbHeight() + ((HelperMethods.RANDOM.nextFloat() - 0.5F) * 5.0F);

        FishShikigamiProjectile[] projectiles = new FishShikigamiProjectile[]{
                new EelShikigamiProjectile(owner, this.getOutput(owner), xOffset, yOffset),
                new SharkShikigamiProjectile(owner, this.getOutput(owner), xOffset, yOffset),
                new PiranhaShikigamiProjectile(owner, getOutput(owner), xOffset, yOffset)
        };
        owner.level().addFreshEntity(projectiles[HelperMethods.RANDOM.nextInt(projectiles.length)]);
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
    public MenuType getMenuType(LivingEntity owner) {
        return MenuType.MELEE;
    }
}
