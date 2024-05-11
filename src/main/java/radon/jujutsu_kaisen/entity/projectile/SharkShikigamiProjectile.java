package radon.jujutsu_kaisen.entity.projectile;

import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import radon.jujutsu_kaisen.entity.registry.JJKEntities;
import radon.jujutsu_kaisen.entity.projectile.base.FishShikigamiProjectile;

public class SharkShikigamiProjectile extends FishShikigamiProjectile {
    public SharkShikigamiProjectile(EntityType<? extends Projectile> pType, Level pLevel) {
        super(pType, pLevel);
    }

    public SharkShikigamiProjectile(LivingEntity owner, float power, LivingEntity target, float xOffset, float yOffset) {
        super(JJKEntities.SHARK_SHIKIGAMI.get(), owner, power, target, xOffset, yOffset);
    }
}
