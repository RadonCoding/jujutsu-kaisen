package radon.jujutsu_kaisen.entity.effect;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.VeilHandler;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.effect.base.JJKEffect;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.projectile.base.JujutsuProjectile;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.UUID;

public class ScissorEntity extends JujutsuProjectile implements GeoEntity {
    private static final EntityDataAccessor<Integer> DATA_ACTIVE = SynchedEntityData.defineId(ScissorEntity.class, EntityDataSerializers.INT);

    private static final float DAMAGE = 20.0F;
    private static final int CUT_DURATION = 5;
    private static final int DELAY = 20;
    private static final int DURATION = 5 * 20;
    private static final double SPEED = 2.5D;
    private static final double RANGE = 3.0D;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final RawAnimation CUT = RawAnimation.begin().thenPlay("misc.cut");

    @Nullable
    private UUID victimUUID;
    @Nullable
    private LivingEntity cachedVictim;

    private Vec3 start;

    public ScissorEntity(EntityType<? extends Projectile> pType, Level pLevel) {
        super(pType, pLevel);
    }

    public ScissorEntity(LivingEntity owner, float power, LivingEntity victim) {
        super(JJKEntities.SCISSOR.get(), owner.level(), owner, power);

        this.setVictim(victim);

        this.start = victim.position();

        double offsetX = this.random.nextDouble() * 4.0D - 2.0D;
        double offsetZ = this.random.nextDouble() * 4.0D - 2.0D;
        this.setPos(victim.position().add(offsetX, victim.getBbHeight() * 1.5F, offsetZ));
    }

    public int getActive() {
        return this.entityData.get(DATA_ACTIVE);
    }

    public boolean isActive() {
        return this.entityData.get(DATA_ACTIVE) != -1;
    }

    public void setActive(int time) {
        this.entityData.set(DATA_ACTIVE, time);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();

        this.entityData.define(DATA_ACTIVE, -1);
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);

        pCompound.putDouble("start_x", this.start.x);
        pCompound.putDouble("start_y", this.start.y);
        pCompound.putDouble("start_z", this.start.z);

        if (this.victimUUID != null) {
            pCompound.putUUID("victim", this.victimUUID);
        }
        pCompound.putInt("active", this.getActive());
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);

        this.start = new Vec3(pCompound.getDouble("start_x"), pCompound.getDouble("start_y"), pCompound.getDouble("start_z"));

        if (pCompound.hasUUID("victim")) {
            this.victimUUID = pCompound.getUUID("victim");
        }
        this.setActive(pCompound.getInt("active"));
    }

    public void setVictim(@Nullable LivingEntity victim) {
        if (victim != null) {
            this.victimUUID = victim.getUUID();
            this.cachedVictim = victim;
        }
    }

    @Nullable
    public LivingEntity getVictim() {
        if (this.cachedVictim != null && !this.cachedVictim.isRemoved()) {
            return this.cachedVictim;
        } else if (this.victimUUID != null && this.level() instanceof ServerLevel) {
            this.cachedVictim = (LivingEntity) ((ServerLevel) this.level()).getEntity(this.victimUUID);
            return this.cachedVictim;
        } else {
            return null;
        }
    }

    private PlayState cutPredicate(AnimationState<ScissorEntity> animationState) {
        if (this.getTime() - CUT_DURATION > 0) {
            return animationState.setAndContinue(CUT);
        }
        return PlayState.STOP;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "Cut", this::cutPredicate));
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult pResult) {
        if (!(this.getOwner() instanceof LivingEntity owner)) return;

        pResult.getEntity().hurt(JJKDamageSources.indirectJujutsuAttack(this, owner, JJKAbilities.SCISSORS.get()),
                DAMAGE * this.getPower());
        this.discard();
    }

    @Override
    public boolean shouldRender(double pX, double pY, double pZ) {
        return this.isActive() && super.shouldRender(pX, pY, pZ);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide) return;

        if (!(this.getOwner() instanceof LivingEntity owner)) return;

        LivingEntity victim = this.getVictim();

        if (victim == null || victim.isRemoved() || !victim.isAlive()) {
            this.discard();
            return;
        }

        if (VeilHandler.isInsideBarrier((ServerLevel) this.level(), victim.blockPosition())) {
            this.discard();
            return;
        }

        this.lookAt(EntityAnchorArgument.Anchor.FEET, victim.position());

        if (!this.isActive()) {
            if (this.getTime() == DURATION || Math.sqrt(victim.distanceToSqr(this.start)) > RANGE) {
                this.setActive(this.getTime());
            } else {
                return;
            }
        }

        if (this.getTime() - this.getActive() == DELAY) {
            owner.swing(InteractionHand.MAIN_HAND, true);

            this.setDeltaMovement(victim.position().subtract(this.position()).normalize().scale(SPEED));
        }
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        LivingEntity entity = this.getVictim();
        return new ClientboundAddEntityPacket(this, entity == null ? 0 : entity.getId());
    }

    @Override
    public void recreateFromPacket(@NotNull ClientboundAddEntityPacket pPacket) {
        super.recreateFromPacket(pPacket);

        LivingEntity victim = (LivingEntity) this.level().getEntity(pPacket.getData());

        if (victim != null) {
            this.setVictim(victim);
        }
    }
}
