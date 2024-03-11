package radon.jujutsu_kaisen.entity.projectile;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import radon.jujutsu_kaisen.ExplosionHandler;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.data.curse_manipulation.ICurseManipulationData;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.SorcererGrade;
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
    private static final float RADIUS = 5.0F;
    private static final float MAX_EXPLOSION = 15.0F;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public MaximumUzumakiProjectile(EntityType<? extends Projectile> pType, Level pLevel) {
        super(pType, pLevel);
    }

    public MaximumUzumakiProjectile(LivingEntity owner) {
        super(JJKEntities.MAXIMUM_UZUMAKI.get(), owner.level(), owner);

        Vec3 pos = owner.position()
                .subtract(RotationUtil.getTargetAdjustedLookAngle(owner).multiply(this.getBbWidth(), 0.0D, this.getBbWidth()))
                .add(0.0D, this.getBbHeight(), 0.0D);
        this.moveTo(pos.x, pos.y, pos.z, RotationUtil.getTargetAdjustedYRot(owner), RotationUtil.getTargetAdjustedXRot(owner));

        IJujutsuCapability ownerCap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (ownerCap == null) return;

        ISorcererData ownerSorcererData = ownerCap.getSorcererData();
        ICurseManipulationData ownerCurseManipulationData = ownerCap.getCurseManipulationData();

        for (Entity entity : ownerSorcererData.getSummons()) {
            if (!(entity instanceof CursedSpirit)) continue;

            IJujutsuCapability curseCap = entity.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (curseCap == null) return;

            ISorcererData curseData = curseCap.getSorcererData();

            if (curseData == null) continue;

            if (SorcererUtil.getGrade(curseData.getExperience()).ordinal() >= SorcererGrade.SEMI_GRADE_1.ordinal() && curseData.getTechnique() != null) {
                ownerCurseManipulationData.absorb(curseData.getTechnique());

                if (owner instanceof ServerPlayer player) {
                    PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(ownerSorcererData.serializeNBT()), player);
                }
            }

            this.setPower(Math.min(ownerSorcererData.getAbilityOutput() * 1.25F, this.getPower() + curseData.getBaseOutput()));

            entity.discard();

            if (this.getPower() == ownerSorcererData.getAbilityOutput() * 1.25F) break;
        }
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

                float radius = Math.min(MAX_EXPLOSION, RADIUS * this.getPower());
                Vec3 offset = new Vec3(this.getX(), this.getY() + (this.getBbHeight() / 2.0F), this.getZ());
                ExplosionHandler.spawn(this.level().dimension(), offset, radius, 3 * 20, this.getPower() * 0.1F, owner,
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
