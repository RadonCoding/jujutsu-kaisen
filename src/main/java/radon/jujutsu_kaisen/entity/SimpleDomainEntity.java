package radon.jujutsu_kaisen.entity;


import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.VeilHandler;
import radon.jujutsu_kaisen.ability.registry.JJKAbilities;
import radon.jujutsu_kaisen.client.particle.ParticleColors;
import radon.jujutsu_kaisen.client.particle.VaporParticle;
import radon.jujutsu_kaisen.data.DataProvider;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.domain.IDomainData;
import radon.jujutsu_kaisen.data.registry.JJKAttachmentTypes;
import radon.jujutsu_kaisen.data.stat.ISkillData;
import radon.jujutsu_kaisen.data.stat.Skill;
import radon.jujutsu_kaisen.entity.registry.JJKEntities;
import radon.jujutsu_kaisen.util.HelperMethods;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public class SimpleDomainEntity extends Entity implements IDomain {
    public static final float RADIUS = 2.0F;
    public static final float MAX_RADIUS = 4.0F;
    private static final EntityDataAccessor<Float> DATA_RADIUS = SynchedEntityData.defineId(SimpleDomainEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_ENLARGEMENT = SynchedEntityData.defineId(SimpleDomainEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_MAX_HEALTH = SynchedEntityData.defineId(SimpleDomainEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_HEALTH = SynchedEntityData.defineId(SimpleDomainEntity.class, EntityDataSerializers.FLOAT);
    private static final double X_STEP = 0.025D;
    @Nullable
    private UUID ownerUUID;
    @Nullable
    private LivingEntity cachedOwner;

    public SimpleDomainEntity(EntityType<?> pType, Level pLevel) {
        super(pType, pLevel);
    }

    public SimpleDomainEntity(LivingEntity owner) {
        super(JJKEntities.SIMPLE_DOMAIN.get(), owner.level());

        this.setOwner(owner);

        this.setPos(owner.position());

        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        ISkillData data = cap.getSkillData();

        this.setRadius(getRadius(owner));
        this.setMaxHealth(1.0F + data.getSkill(Skill.BARRIER));
        this.setHealth(this.getMaxHealth());
    }

    public static float getRadius(LivingEntity owner) {
        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return 0.0F;

        ISkillData data = cap.getSkillData();

        return Math.min(MAX_RADIUS, RADIUS * (1.0F + (data.getSkill(Skill.BARRIER) * 0.1F)));
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();

        if (!this.level().isClientSide) {
            VeilHandler.barrier(this.level().dimension(), this.getUUID());
        }
    }

    public float getRadius() {
        return this.entityData.get(DATA_RADIUS) * (1.0F + this.getEnlargement());
    }

    private void setRadius(float radius) {
        this.entityData.set(DATA_RADIUS, radius);
    }

    public float getEnlargement() {
        return this.entityData.get(DATA_ENLARGEMENT);
    }

    public void setEnlargement(float enlargement) {
        this.entityData.set(DATA_ENLARGEMENT, enlargement);
    }

    public boolean canEnlarge() {
        LivingEntity owner = this.getOwner();

        if (owner == null) return false;

        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return false;

        ISkillData data = cap.getSkillData();

        return this.getEnlargement() < 1.0F + (data.getSkill(Skill.BARRIER) * 0.01F);
    }

    public void enlarge() {
        this.setEnlargement(this.getEnlargement() + 0.1F);
    }

    private float getMaxHealth() {
        return this.entityData.get(DATA_MAX_HEALTH);
    }

    private void setMaxHealth(float maxHealth) {
        this.entityData.set(DATA_MAX_HEALTH, maxHealth);
    }

    public float getHealth() {
        return this.entityData.get(DATA_HEALTH);
    }

    public void setHealth(float health) {
        this.entityData.set(DATA_HEALTH, Mth.clamp(health, 0.0F, this.getMaxHealth()));
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    public @NotNull EntityDimensions getDimensions(@NotNull Pose pPose) {
        float radius = (float) (this.getRadius() * 2.0D);
        return EntityDimensions.fixed(radius, radius);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder pBuilder) {
        pBuilder.define(DATA_RADIUS, 0.0F);
        pBuilder.define(DATA_ENLARGEMENT, 0.0F);
        pBuilder.define(DATA_MAX_HEALTH, 0.0F);
        pBuilder.define(DATA_HEALTH, 0.0F);
    }

    @Override
    public @NotNull Vec3 getDeltaMovement() {
        return Vec3.ZERO;
    }

    @Override
    public void tick() {
        if (this.getHealth() == 0.0F) {
            this.discard();
            return;
        }

        this.refreshDimensions();

        LivingEntity owner = this.getOwner();

        if (!this.level().isClientSide && owner != null) {
            IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap == null) return;

            IAbilityData data = cap.getAbilityData();

            if (!data.hasToggled(JJKAbilities.SIMPLE_DOMAIN.get())) {
                this.discard();
                return;
            }
        }

        if (!this.level().isClientSide && (owner == null || owner.isRemoved() || !owner.isAlive())) {
            this.discard();
        } else {
            super.tick();

            if (owner == null) return;

            this.setPos(owner.position());

            float factor = this.getHealth() / this.getMaxHealth();

            ParticleOptions particle = new VaporParticle.Options(ParticleColors.SIMPLE_DOMAIN, 1.0F,
                    1.0F, true, 1);

            double circumference = Math.PI * 2 * factor;

            float radius = this.getRadius();

            for (double phi = 0.0D; phi < circumference; phi += circumference / radius * X_STEP) {
                double x = this.getX() + radius * Math.cos(phi);
                double y = this.getY();
                double z = this.getZ() + radius * Math.sin(phi);
                this.level().addParticle(particle, true, x, y, z, 0.0D, HelperMethods.RANDOM.nextDouble(), 0.0D);
            }
        }
    }

    @Override
    @Nullable
    public LivingEntity getOwner() {
        if (this.cachedOwner != null && !this.cachedOwner.isRemoved()) {
            return this.cachedOwner;
        } else if (this.ownerUUID != null && this.level() instanceof ServerLevel) {
            this.cachedOwner = (LivingEntity) ((ServerLevel) this.level()).getEntity(this.ownerUUID);
            return this.cachedOwner;
        } else {
            return null;
        }
    }

    public void setOwner(@Nullable LivingEntity pOwner) {
        if (pOwner != null) {
            this.ownerUUID = pOwner.getUUID();
            this.cachedOwner = pOwner;
        }
    }

    @Override
    public boolean isInsidePhysicalBarrier(BlockPos pos) {
        return this.isPhysicalBarrier(pos);
    }

    @Override
    public boolean isPhysicalBarrier(BlockPos pos) {
        float radius = this.getRadius();
        BlockPos center = BlockPos.containing(this.position().add(0.0D, radius, 0.0D));
        BlockPos relative = pos.subtract(center);
        return relative.distSqr(Vec3i.ZERO) < radius * radius;
    }

    @Override
    public boolean isInsideVirtualBarrier(BlockPos pos) {
        return this.isInsidePhysicalBarrier(pos);
    }

    @Override
    public AABB getPhysicalBounds() {
        return this.getBoundingBox();
    }

    @Override
    public AABB getVirtualBounds() {
        return this.getPhysicalBounds();
    }

    @Override
    public boolean hasSureHitEffect() {
        return false;
    }

    @Override
    public boolean checkSureHitEffect() {
        return false;
    }

    @Override
    public boolean isInWall() {
        return false;
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        if (this.ownerUUID != null) {
            pCompound.putUUID("owner", this.ownerUUID);
        }
        pCompound.putFloat("radius", this.getRadius());
        pCompound.putFloat("enlargement", this.getEnlargement());
        pCompound.putFloat("max_health", this.getMaxHealth());
        pCompound.putFloat("health", this.getHealth());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        if (pCompound.hasUUID("owner")) {
            this.ownerUUID = pCompound.getUUID("owner");
        }
        this.setRadius(pCompound.getFloat("radius"));
        this.setEnlargement(pCompound.getFloat("enlargement"));
        this.setMaxHealth(pCompound.getFloat("max_health"));
        this.setHealth(pCompound.getFloat("health"));
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        Entity entity = this.getOwner();
        return new ClientboundAddEntityPacket(this, entity == null ? 0 : entity.getId());
    }

    @Override
    public void recreateFromPacket(@NotNull ClientboundAddEntityPacket pPacket) {
        super.recreateFromPacket(pPacket);

        LivingEntity owner = (LivingEntity) this.level().getEntity(pPacket.getData());

        if (owner != null) {
            this.setOwner(owner);
        }
    }

    @Override
    public @Nullable ServerLevel getVirtual() {
        return null;
    }

    @Override
    public float getScale() {
        return 1.0F + this.getEnlargement();
    }

    @Override
    public void setInstant(boolean instant) {

    }

    @Override
    public boolean isInstant() {
        return false;
    }

    @Override
    public void doSureHitEffect(LivingEntity owner) {

    }
}
