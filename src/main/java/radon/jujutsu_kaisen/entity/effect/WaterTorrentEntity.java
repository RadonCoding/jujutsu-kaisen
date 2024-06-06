package radon.jujutsu_kaisen.entity.effect;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.registry.JJKAbilities;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.entity.registry.JJKEntities;
import radon.jujutsu_kaisen.entity.effect.base.BeamEntity;
import radon.jujutsu_kaisen.util.RotationUtil;

public class WaterTorrentEntity extends BeamEntity {
    public static final double RANGE = 32.0D;

    public WaterTorrentEntity(EntityType<? extends Projectile> pType, Level pLevel) {
        super(pType, pLevel);
    }

    public WaterTorrentEntity(LivingEntity owner, float power) {
        super(JJKEntities.WATER_TORRENT.get(), owner, power);
    }

    @Override
    public int getFrames() {
        return 16;
    }

    @Override
    public float getScale() {
        return 2.0F;
    }

    @Override
    protected double getRange() {
        return RANGE;
    }

    @Override
    protected float getDamage() {
        return 7.5F;
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
    @Nullable
    protected Ability getSource() {
        return JJKAbilities.WATER_TORRENT.get();
    }

    @Override
    protected boolean breaksBlocks() {
        return false;
    }

    @Override
    protected Vec3 calculateSpawnPos(LivingEntity owner) {
        return new Vec3(owner.getX(), owner.getY() + (owner.getBbHeight() * 0.75F) - (this.getBbHeight() / 2), owner.getZ())
                .add(RotationUtil.getTargetAdjustedLookAngle(owner));
    }
}