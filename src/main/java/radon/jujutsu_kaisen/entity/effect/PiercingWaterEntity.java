package radon.jujutsu_kaisen.entity.effect;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.registry.JJKAbilities;
import radon.jujutsu_kaisen.entity.registry.JJKEntities;
import radon.jujutsu_kaisen.entity.effect.base.BeamEntity;
import radon.jujutsu_kaisen.util.RotationUtil;

public class PiercingWaterEntity extends BeamEntity {
    public PiercingWaterEntity(EntityType<? extends Projectile> pType, Level pLevel) {
        super(pType, pLevel);
    }

    public PiercingWaterEntity(LivingEntity owner, float power) {
        super(JJKEntities.PIERCING_WATER.get(), owner, power);
    }

    @Override
    public int getFrames() {
        return 16;
    }

    @Override
    public float getScale() {
        return 0.5F;
    }

    @Override
    protected double getRange() {
        return 32.0D;
    }

    @Override
    protected float getDamage() {
        return 10.0F;
    }

    @Override
    public int getDuration() {
        return 4;
    }

    @Override
    public int getCharge() {
        return 0;
    }

    @Override
    protected @Nullable Ability getSource() {
        return JJKAbilities.PIERCING_WATER.get();
    }

    @Override
    protected Vec3 calculateSpawnPos(LivingEntity owner) {
        return new Vec3(owner.getX(), owner.getY() + (owner.getBbHeight() * 0.75F) - (this.getBbHeight() / 2.0F), owner.getZ())
                .add(RotationUtil.getTargetAdjustedLookAngle(owner));
    }
}