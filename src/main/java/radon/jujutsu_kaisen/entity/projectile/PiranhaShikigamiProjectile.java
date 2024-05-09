package radon.jujutsu_kaisen.entity.projectile;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import radon.jujutsu_kaisen.entity.registry.JJKEntities;
import radon.jujutsu_kaisen.entity.projectile.base.FishShikigamiProjectile;

public class PiranhaShikigamiProjectile extends FishShikigamiProjectile {
    public PiranhaShikigamiProjectile(EntityType<? extends Projectile> pType, Level pLevel) {
        super(pType, pLevel);
    }

    public PiranhaShikigamiProjectile(LivingEntity owner, float power, LivingEntity target, float xOffset, float yOffset) {
        super(JJKEntities.PIRANHA_SHIKIGAMI.get(), owner, power, target, xOffset, yOffset);
    }
}
