package radon.jujutsu_kaisen.entity.projectile;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.ExplosionHandler;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.curse.base.CursedSpirit;
import radon.jujutsu_kaisen.entity.projectile.base.JujutsuProjectile;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;
import radon.jujutsu_kaisen.util.RotationUtil;
import radon.jujutsu_kaisen.util.SorcererUtil;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class MaximumUzumakiProjectile extends JujutsuProjectile implements GeoEntity {
    private static final int DELAY = 20;
    private static final double RANGE = 10.0D;
    private static final float MAX_POWER = 10.0F;

    private float power;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public MaximumUzumakiProjectile(EntityType<? extends Projectile> pType, Level pLevel) {
        super(pType, pLevel);
    }

    public MaximumUzumakiProjectile(LivingEntity owner, float power) {
        super(JJKEntities.MAXIMUM_UZUMAKI.get(), owner.level(), owner, power);

        Vec3 pos = owner.position()
                .subtract(RotationUtil.getTargetAdjustedLookAngle(owner).multiply(this.getBbWidth(), 0.0D, this.getBbWidth()))
                .add(0.0D, this.getBbHeight(), 0.0D);
        this.moveTo(pos.x, pos.y, pos.z, RotationUtil.getTargetAdjustedYRot(owner), RotationUtil.getTargetAdjustedXRot(owner));

        ISorcererData ownerCap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        for (Entity entity : ownerCap.getSummons()) {
            if (this.power == MAX_POWER) break;
            if (!(entity instanceof CursedSpirit)) continue;

            ISorcererData curseCap = entity.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

            if (SorcererUtil.getGrade(curseCap.getExperience()).ordinal() >= SorcererGrade.SEMI_GRADE_1.ordinal() && curseCap.getTechnique() != null) {
                ownerCap.absorb(curseCap.getTechnique());

                if (owner instanceof ServerPlayer player) {
                    PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(ownerCap.serializeNBT()), player);
                }
            }
            this.power = Math.min(MAX_POWER, this.power + SorcererUtil.getPower(curseCap.getExperience()));
            entity.discard();
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
                            .subtract(RotationUtil.getTargetAdjustedLookAngle(owner).multiply(this.getBbWidth(), 0.0D, this.getBbWidth()))
                            .add(0.0D, this.getBbHeight(), 0.0D);
                    this.moveTo(pos.x, pos.y, pos.z, RotationUtil.getTargetAdjustedYRot(owner), RotationUtil.getTargetAdjustedXRot(owner));
                }
            } else if (this.getTime() - 20 >= DELAY) {
                this.discard();
            } else if (this.getTime() == DELAY) {
                Vec3 start = owner.getEyePosition();
                Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);
                Vec3 end = start.add(look.scale(RANGE));
                HitResult result = RotationUtil.getHitResult(owner, start, end);

                Vec3 pos = result.getType() == HitResult.Type.MISS ? end : result.getLocation();
                this.setPos(pos);

                Vec3 offset = new Vec3(this.getX(), this.getY() + (this.getBbHeight() / 2.0F), this.getZ());
                ExplosionHandler.spawn(this.level().dimension(), offset, this.power * 2.0F, 3 * 20, this.getPower() * 0.5F, owner,
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
