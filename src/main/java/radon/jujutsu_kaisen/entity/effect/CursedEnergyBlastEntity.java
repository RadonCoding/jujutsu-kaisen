package radon.jujutsu_kaisen.entity.effect;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.client.particle.LightningParticle;
import radon.jujutsu_kaisen.client.particle.ParticleColors;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.base.JujutsuProjectile;
import radon.jujutsu_kaisen.sound.JJKSounds;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

public class CursedEnergyBlastEntity extends JujutsuProjectile {
    public static final int DURATION = 20;
    private static final float DAMAGE = 10.0F;

    public CursedEnergyBlastEntity(EntityType<? extends Projectile> pType, Level pLevel) {
        super(pType, pLevel);
    }

    public CursedEnergyBlastEntity(LivingEntity owner, float power) {
        super(JJKEntities.CURSED_ENERGY_BLAST.get(), owner.level(), owner, power);

        Vec3 spawn = new Vec3(owner.getX(), owner.getY() + (owner.getBbHeight() / 2.0F) - (this.getBbHeight() / 2.0F), owner.getZ());
        this.moveTo(spawn.x, spawn.y, spawn.z, RotationUtil.getTargetAdjustedYRot(owner), RotationUtil.getTargetAdjustedXRot(owner));
    }

    @Override
    public @NotNull EntityDimensions getDimensions(@NotNull Pose pPose) {
        float age = this.getTime();
        float scale = (float) Math.pow(age, 0.5F) * 5.0F;
        return super.getDimensions(pPose).scale(scale);
    }

    @Override
    public void tick() {
        super.tick();

        this.refreshDimensions();

        if (this.getTime() >= DURATION) {
            this.discard();
            return;
        }

        if (this.level().isClientSide) return;

        if (!(this.getOwner() instanceof LivingEntity owner)) return;

        for (Entity entity : this.level().getEntities(owner, this.getBoundingBox())) {
            if (!entity.hurt(JJKDamageSources.jujutsuAttack(owner, JJKAbilities.CURSED_ENERGY_BLAST.get()), DAMAGE * this.getPower())) continue;
            entity.setSecondsOnFire(5);
        }
    }

    @Override
    protected boolean isProjectile() {
        return false;
    }
}
