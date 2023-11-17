package radon.jujutsu_kaisen.entity.projectile;

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
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
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
    private static final float DAMAGE = 1.0F;
    private static final float MAX_POWER = 7.5F;

    private float power;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public MaximumUzumakiProjectile(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public MaximumUzumakiProjectile(LivingEntity owner, float power) {
        super(JJKEntities.MAXIMUM_UZUMAKI.get(), owner.level(), owner, power);

        Vec3 pos = owner.position()
                .subtract(owner.getLookAngle().multiply(this.getBbWidth(), 0.0D, this.getBbWidth()))
                .add(0.0D, this.getBbHeight(), 0.0D);
        this.moveTo(pos.x(), pos.y(), pos.z(), owner.getYRot(), owner.getXRot());

        if (!owner.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return;

        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        Registry<EntityType<?>> registry = this.level().registryAccess().registryOrThrow(Registries.ENTITY_TYPE);
        Map<EntityType<?>, Integer> curses = cap.getCurses(registry);

        for (Map.Entry<EntityType<?>, Integer> entry : curses.entrySet()) {
            if (this.power == MAX_POWER) break;

            Entity entity = entry.getKey().create(this.level());
            if (!(entity instanceof ISorcerer curse)) continue;

            for (int i = 0; i < entry.getValue(); i++) {
                if (this.power == MAX_POWER) break;
                if (curse.getGrade().ordinal() >= SorcererGrade.SEMI_GRADE_1.ordinal() && curse.getTechnique() != null)
                    cap.absorb(curse.getTechnique());
                this.power = Math.min(MAX_POWER, this.power + HelperMethods.getPower(curse.getExperience()));
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

    @Override
    public void tick() {
        super.tick();

        if (this.getOwner() instanceof LivingEntity owner) {
            if (this.level().isClientSide) return;

            if (this.getTime() < DELAY) {
                if (!owner.isAlive()) {
                    this.discard();
                } else {
                    Vec3 pos = owner.position()
                            .subtract(owner.getLookAngle().multiply(this.getBbWidth(), 0.0D, this.getBbWidth()))
                            .add(0.0D, this.getBbHeight(), 0.0D);
                    this.moveTo(pos.x(), pos.y(), pos.z(), owner.getYRot(), owner.getXRot());
                }
            } else if (this.getTime() - 20 >= DELAY) {
                this.discard();
            } else if (this.getTime() == DELAY) {
                Vec3 start = owner.getEyePosition();
                Vec3 look = owner.getLookAngle();
                Vec3 end = start.add(look.scale(RANGE));
                HitResult result = HelperMethods.getHitResult(owner, start, end);

                Vec3 pos = result.getType() == HitResult.Type.MISS ? end : result.getLocation();
                this.setPos(pos);

                Vec3 offset = new Vec3(this.getX(), this.getY() + (this.getBbHeight() / 2.0F), this.getZ());
                ExplosionHandler.spawn(this.level().dimension(), offset, this.power * 2.0F, 3 * 20, DAMAGE * this.getPower(), owner,
                        JJKDamageSources.indirectJujutsuAttack(this, owner, JJKAbilities.MAXIMUM_UZUMAKI.get()), false);
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
