package radon.jujutsu_kaisen.entity.projectile;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import radon.jujutsu_kaisen.ExplosionHandler;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.client.particle.ParticleColors;
import radon.jujutsu_kaisen.client.particle.TravelParticle;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.base.ISorcerer;
import radon.jujutsu_kaisen.entity.base.JujutsuProjectile;
import radon.jujutsu_kaisen.util.HelperMethods;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Map;

public class MaximumUzumakiProjectile extends JujutsuProjectile implements GeoEntity {
    private static final int DELAY = 20;
    private static final double RANGE = 10.0D;
    private static final float DAMAGE = 25.0F;
    private static final float MAX_POWER = 7.5F;

    private float power;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public MaximumUzumakiProjectile(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public MaximumUzumakiProjectile(LivingEntity owner) {
        this(JJKEntities.MAXIMUM_UZUMAKI.get(), owner.level);

        this.setOwner(owner);

        Vec3 pos = owner.position()
                .subtract(HelperMethods.getLookAngle(owner).multiply(this.getBbWidth(), 0.0D, this.getBbWidth()))
                .add(0.0D, this.getBbHeight(), 0.0D);
        this.moveTo(pos.x(), pos.y(), pos.z(), owner.getYRot(), owner.getXRot());

        if (!owner.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return;

        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        Registry<EntityType<?>> registry = this.level.registryAccess().registryOrThrow(Registries.ENTITY_TYPE);
        Map<EntityType<?>, Integer> curses = cap.getCurses(registry);

        for (Map.Entry<EntityType<?>, Integer> entry : curses.entrySet()) {
            if (this.power == MAX_POWER) break;

            Entity entity = entry.getKey().create(this.level);
            if (!(entity instanceof ISorcerer curse)) continue;

            for (int i = 0; i < entry.getValue(); i++) {
                if (this.power == MAX_POWER) break;
                this.power = Math.min(MAX_POWER, this.power + curse.getGrade().getPower());
                cap.removeCurse(registry, entity.getType());
            }
        }
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);

        pCompound.putFloat("power", this.power);
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);

        this.power = pCompound.getFloat("power");
    }

    private void hurtEntities() {
        if (!(this.getOwner() instanceof LivingEntity owner)) return;

        float radius = this.power * 2.0F;

        AABB bounds = this.getBoundingBox().inflate(radius);

        for (Entity entity : HelperMethods.getEntityCollisions(this.level, bounds)) {
            if ((entity instanceof LivingEntity living && !owner.canAttack(living)) || entity == owner) continue;
            entity.hurt(JJKDamageSources.indirectJujutsuAttack(this, owner, JJKAbilities.MAXIMUM_UZUMAKI.get()), DAMAGE * this.power);
        }
    }

    private void spawnParticles() {
        Vec3 center = new Vec3(this.getX(), this.getY() + (this.getBbHeight() / 2.0F), this.getZ());

        float radius = 10.0F;
        int count = (int) (radius * 2);

        for (int i = 0; i < count; i++) {
            double theta = this.random.nextDouble() * Math.PI * 2.0D;
            double phi = this.random.nextDouble() * Math.PI;

            double xOffset = radius * Math.sin(phi) * Math.cos(theta);
            double yOffset = radius * Math.sin(phi) * Math.sin(theta);
            double zOffset = radius * Math.cos(phi);

            double x = center.x() + xOffset * (radius * 0.1F);
            double y = center.y() + yOffset * (radius * 0.1F);
            double z = center.z() + zOffset * (radius * 0.1F);

            Vector3f offset = new Vec3(x, y, z).toVector3f();

            this.level.addParticle(new TravelParticle.TravelParticleOptions(offset, ParticleColors.BLACK_COLOR, 0.2F, 1.0F, 10),
                    center.x(), center.y(), center.z(), 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    public void tick() {
        super.tick();

        this.spawnParticles();

        if (this.getOwner() instanceof LivingEntity owner) {
            if (this.getTime() < DELAY) {
                if (!owner.isAlive()) {
                    this.discard();
                } else {
                    Vec3 pos = owner.position()
                            .subtract(HelperMethods.getLookAngle(owner).multiply(this.getBbWidth(), 0.0D, this.getBbWidth()))
                            .add(0.0D, this.getBbHeight(), 0.0D);
                    this.moveTo(pos.x(), pos.y(), pos.z(), owner.getYRot(), owner.getXRot());
                }
            } else if (this.getTime() - 20 >= DELAY) {
                this.discard();
            } else if (this.getTime() == DELAY) {
                Vec3 start = owner.getEyePosition();
                Vec3 look = HelperMethods.getLookAngle(owner);
                Vec3 end = start.add(look.scale(RANGE));
                HitResult result = HelperMethods.getHitResult(owner, start, end);

                Vec3 pos = result.getType() == HitResult.Type.MISS ? end : result.getLocation();
                this.setPos(pos);

                Vec3 offset = new Vec3(this.getX(), this.getY() + (this.getBbHeight() / 2.0F), this.getZ());
                ExplosionHandler.spawn(this.level.dimension(), BlockPos.containing(offset), this.power * 2.0F, 20, owner, JJKAbilities.MAXIMUM_UZUMAKI.get());

                this.hurtEntities();
            }
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
