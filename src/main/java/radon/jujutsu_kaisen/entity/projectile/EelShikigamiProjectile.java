package radon.jujutsu_kaisen.entity.projectile;


import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import radon.jujutsu_kaisen.entity.registry.JJKEntities;

public class EelShikigamiProjectile extends FishShikigamiProjectile {
    public EelShikigamiProjectile(EntityType<? extends Projectile> pType, Level pLevel) {
        super(pType, pLevel);
    }

    public EelShikigamiProjectile(LivingEntity owner, float power, float xOffset, float yOffset, LivingEntity target) {
        super(JJKEntities.EEL_SHIKIGAMI.get(), owner, power, xOffset, yOffset, target);
    }

    public EelShikigamiProjectile(LivingEntity owner, float power, float xOffset, float yOffset) {
        super(JJKEntities.EEL_SHIKIGAMI.get(), owner, power, xOffset, yOffset);
    }
}
