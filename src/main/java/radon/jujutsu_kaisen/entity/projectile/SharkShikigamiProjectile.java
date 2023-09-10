package radon.jujutsu_kaisen.entity.projectile;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.base.FishShikigamiProjectile;

public class SharkShikigamiProjectile extends FishShikigamiProjectile {
    public SharkShikigamiProjectile(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public SharkShikigamiProjectile(LivingEntity pShooter, LivingEntity target, float xOffset, float yOffset) {
        super(JJKEntities.SHARK_SHIKIGAMI.get(), pShooter, target, xOffset, yOffset);
    }
}
