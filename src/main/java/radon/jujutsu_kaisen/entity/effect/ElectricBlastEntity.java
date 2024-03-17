package radon.jujutsu_kaisen.entity.effect;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.ExplosionHandler;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.client.particle.CursedEnergyParticle;
import radon.jujutsu_kaisen.client.particle.EmittingLightningParticle;
import radon.jujutsu_kaisen.client.particle.ParticleColors;
import radon.jujutsu_kaisen.client.particle.TravelParticle;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.projectile.base.JujutsuProjectile;
import radon.jujutsu_kaisen.sound.JJKSounds;

public class ElectricBlastEntity extends JujutsuProjectile {
    public static final int DURATION = 2 * 20;
    private static final float RADIUS = 4.0F;
    private static final float MAX_RADIUS = 32.0F;
    private static final float DAMAGE = 15.0F;

    public ElectricBlastEntity(EntityType<? extends Projectile> pType, Level pLevel) {
        super(pType, pLevel);

        this.noCulling = true;
    }

    public ElectricBlastEntity(LivingEntity owner, float power, Vec3 pos) {
        this(JJKEntities.ELECTRIC_BLAST.get(), owner.level());

        this.setOwner(owner);
        this.setPower(power);

        float radius = this.getRadius();
        this.setPos(pos.subtract(0.0D, radius / 2.0F, 0.0D));
    }

    private float getRadius() {
        return Math.max(Mth.PI, Math.min(MAX_RADIUS, RADIUS * this.getPower()));
    }

    @Override
    public @NotNull Vec3 getDeltaMovement() {
        return Vec3.ZERO;
    }

    @Override
    public @NotNull EntityDimensions getDimensions(@NotNull Pose pPose) {
        float radius = this.getRadius();
        return EntityDimensions.fixed(radius, radius);
    }

    @Override
    public void tick() {
        super.tick();

        this.refreshDimensions();

        if (this.getTime() >= DURATION) {
            this.discard();
            return;
        }

        if (this.getTime() - 1 == 0) {
            this.playSound(JJKSounds.ELECTRIC_BLAST.get(), 3.0F, 1.0F);
        }

        if (!(this.getOwner() instanceof LivingEntity owner)) return;

        float radius = this.getRadius();

        Vec3 center = new Vec3(this.getX(), this.getY() + (this.getBbHeight() / 2.0F), this.getZ());

        for (int i = 0; i < radius * 4; i++) {
            this.level().addParticle(new EmittingLightningParticle.EmittingLightningParticleOptions(ParticleColors.getCursedEnergyColorBright(owner),
                            radius * this.random.nextFloat(), 1), center.x, center.y, center.z,
                    0.0D, 0.0D, 0.0D);
        }

        for (int i = 0; i < radius * 4; i++) {
            double theta = this.random.nextDouble() * Math.PI * 2.0D;
            double phi = this.random.nextDouble() * Math.PI;

            double xOffset = radius * 4.0F * Math.sin(phi) * Math.cos(theta);
            double yOffset = radius * 4.0F * Math.sin(phi) * Math.sin(theta);
            double zOffset = radius * 4.0F * Math.cos(phi);

            double x = center.x + xOffset * 0.1F;
            double y = center.y + yOffset * 0.1F;
            double z = center.z + zOffset * 0.1F;

            Vec3 offset = new Vec3(x, y, z);

            this.level().addParticle(new TravelParticle.TravelParticleOptions(offset.toVector3f(), ParticleColors.getCursedEnergyColorBright(owner),
                            radius * 0.1F, 1.0F, true, 5), true,
                    center.x, center.y, center.z, 0.0D, 0.0D, 0.0D);
        }

        AABB bounds = this.getBoundingBox();

        for (Entity entity : this.level().getEntitiesOfClass(LivingEntity.class, bounds, entity -> entity != owner)) {
            entity.hurt(JJKDamageSources.indirectJujutsuAttack(this, owner, JJKAbilities.LIGHTNING.get()), DAMAGE * this.getPower());
        }
    }
}