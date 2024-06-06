package radon.jujutsu_kaisen.entity.effect;


import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.ability.registry.JJKAbilities;
import radon.jujutsu_kaisen.client.particle.EmittingLightningParticle;
import radon.jujutsu_kaisen.client.particle.ParticleColors;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.registry.JJKEntities;
import radon.jujutsu_kaisen.entity.projectile.JujutsuProjectile;
import radon.jujutsu_kaisen.sound.JJKSounds;
import radon.jujutsu_kaisen.util.EntityUtil;

public class ElectricBlastEntity extends JujutsuProjectile {
    public static final int DURATION = 2 * 20;
    private static final float RADIUS = 4.0F;
    private static final float MAX_RADIUS = 32.0F;
    private static final float DAMAGE = 35.0F;

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
        return Math.max(RADIUS, Math.min(MAX_RADIUS, RADIUS * this.getPower()));
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

        if (!(this.getOwner() instanceof LivingEntity owner)) return;

        if (this.getTime() - 1 == 0) {
            this.playSound(JJKSounds.ELECTRIC_BLAST.get(), 3.0F, 1.0F);

            AABB bounds = this.getBoundingBox();

            for (Entity entity : EntityUtil.getTouchableEntities(Entity.class, this.level(), owner, bounds)) {
                entity.hurt(JJKDamageSources.indirectJujutsuAttack(this, owner, JJKAbilities.LIGHTNING.get()), DAMAGE * this.getPower());
            }
        }

        float radius = this.getRadius();

        Vec3 center = new Vec3(this.getX(), this.getY() + (this.getBbHeight() / 2), this.getZ());

        for (int i = 0; i < radius; i++) {
            double theta = this.random.nextDouble() * Math.PI * 2;
            double phi = this.random.nextDouble() * Math.PI;

            double xOffset = radius * 0.75F * Math.sin(phi) * Math.cos(theta);
            double yOffset = radius * 0.75F * Math.sin(phi) * Math.sin(theta);
            double zOffset = radius * 0.75F * Math.cos(phi);

            this.level().addParticle(new EmittingLightningParticle.Options(ParticleColors.getCursedEnergyColorBright(owner),
                    new Vec3(xOffset * 0.1F, yOffset * 0.1F, zOffset * 0.1F), radius * this.random.nextFloat(), 8),
                    center.x, center.y, center.z, 0.0D, 0.0D, 0.0D);
        }
    }
}