package radon.jujutsu_kaisen.entity.effect;

import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.registry.JJKAbilities;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.entity.projectile.base.JujutsuProjectile;
import radon.jujutsu_kaisen.entity.registry.JJKEntities;
import radon.jujutsu_kaisen.entity.effect.base.BeamEntity;
import radon.jujutsu_kaisen.entity.curse.RikaEntity;
import radon.jujutsu_kaisen.util.RotationUtil;

public class PureLoveBeamEntity extends JujutsuProjectile {
    public static final double RANGE = 16.0D;
    public static final int CHARGE = (int) (2.5F * 20);
    public static final int DURATION = 3 * 20;

    public PureLoveBeamEntity(EntityType<? extends Projectile> pType, Level pLevel) {
        super(pType, pLevel);
    }

    public PureLoveBeamEntity(LivingEntity owner, float power) {
        super(JJKEntities.PURE_LOVE.get(), owner.level(), owner, power);
    }
}