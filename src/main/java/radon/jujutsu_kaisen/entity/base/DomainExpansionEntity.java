package radon.jujutsu_kaisen.entity.base;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.VeilHandler;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.DomainExpansion;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.stat.ISkillData;
import radon.jujutsu_kaisen.data.stat.Skill;
import radon.jujutsu_kaisen.data.ten_shadows.ITenShadowsData;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.SimpleDomainEntity;
import radon.jujutsu_kaisen.entity.ten_shadows.MahoragaEntity;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public abstract class DomainExpansionEntity extends Entity {
    private static final EntityDataAccessor<Integer> DATA_TIME = SynchedEntityData.defineId(DomainExpansionEntity.class, EntityDataSerializers.INT);

    public static final int OFFSET = 5;

    @Nullable
    private UUID ownerUUID;
    @Nullable
    private LivingEntity cachedOwner;

    protected DomainExpansion ability;
    protected boolean first = true;

    protected DomainExpansionEntity(EntityType<?> pType, Level pLevel) {
        super(pType, pLevel);
    }

    public DomainExpansionEntity(EntityType<?> pType, LivingEntity owner, DomainExpansion ability) {
        super(pType, owner.level());

        this.setOwner(owner);

        this.ability = ability;
    }

    @Override
    public boolean ignoreExplosion(@NotNull Explosion pExplosion) {
        return true;
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();

        if (!this.level().isClientSide) {
            VeilHandler.domain(this.level().dimension(), this.getUUID());
        }
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_TIME, 0);
    }

    public int getTime() {
        return this.entityData.get(DATA_TIME);
    }

    public void setTime(int time) {
        this.entityData.set(DATA_TIME, time);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        if (this.ownerUUID != null) {
            pCompound.putUUID("owner", this.ownerUUID);
        }
        pCompound.putString("ability", JJKAbilities.getKey(this.ability).toString());
        pCompound.putBoolean("first", this.first);
        pCompound.putInt("time", this.getTime());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        if (pCompound.hasUUID("owner")) {
            this.ownerUUID = pCompound.getUUID("owner");
        }
        this.ability = (DomainExpansion) JJKAbilities.getValue(new ResourceLocation(pCompound.getString("ability")));
        this.first = pCompound.getBoolean("first");
        this.setTime(pCompound.getInt("time"));
    }

    @Override
    public @NotNull Vec3 getDeltaMovement() {
        return Vec3.ZERO;
    }

    public List<LivingEntity> getAffected() {
        return this.level().getEntitiesOfClass(LivingEntity.class, this.getBounds(), this::isAffected);
    }

    public boolean hasSureHitEffect() {
        return true;
    }

    public abstract boolean checkSureHitEffect();

    public Ability getAbility() {
        return this.ability;
    }

    @Override
    protected boolean updateInWaterStateAndDoFluidPushing() {
        return false;
    }

    @Nullable
    public DomainExpansionCenterEntity getDomainCenter() {
        List<DomainExpansionCenterEntity> collisions = this.level().getEntitiesOfClass(DomainExpansionCenterEntity.class, this.getBounds());

        for (DomainExpansionCenterEntity collision : collisions) {
            if (collision.getDomain() == this) {
                return collision;
            }
        }
        return null;
    }

    public void setOwner(@Nullable LivingEntity pOwner) {
        if (pOwner != null) {
            this.ownerUUID = pOwner.getUUID();
            this.cachedOwner = pOwner;
        }
    }

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

    public abstract AABB getBounds();

    public abstract boolean isInsideBarrier(BlockPos pos);

    @Override
    public boolean isInWall() {
        return false;
    }

    @Override
    public void tick() {
        this.setTime(this.getTime() + 1);

        LivingEntity owner = this.getOwner();

        if (!this.level().isClientSide && owner != null) {
            IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap == null) return;

            IAbilityData data = cap.getAbilityData();

            if (!data.hasToggled(this.ability)) {
                this.discard();
                return;
            }
        }

        if (!this.level().isClientSide && (owner == null || owner.isRemoved() || !owner.isAlive())) {
            this.discard();
        } else {
            super.tick();
        }
    }

    public boolean isAffected(BlockPos pos) {
        return this.isInsideBarrier(pos);
    }

    public boolean isAffected(LivingEntity victim) {
        LivingEntity owner = this.getOwner();

        if (owner == null || victim == owner) {
            return false;
        }

        if (!owner.canAttack(victim)) return false;

        if (victim instanceof TamableAnimal tamable && tamable.isTame() && tamable.getOwner() == owner) return false;

        IJujutsuCapability victimCap = victim.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (victimCap != null) {
            ISorcererData victimSorcererData = victimCap.getSorcererData();
            IAbilityData abilityData = victimCap.getAbilityData();
            ITenShadowsData victimTenShadowsData = victimCap.getTenShadowsData();

            if ((victim instanceof MahoragaEntity && victimTenShadowsData.isAdaptedTo(this.ability))) return false;

            if (abilityData.hasToggled(JJKAbilities.SIMPLE_DOMAIN.get())) {
                SimpleDomainEntity simple = victimSorcererData.getSummonByClass(SimpleDomainEntity.class);

                if (simple != null) {
                    IJujutsuCapability ownerCap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

                    if (ownerCap == null) return false;

                    ISkillData data = ownerCap.getSkillData();

                    simple.hurt(JJKDamageSources.indirectJujutsuAttack(this, owner, this.ability),
                            data.getSkill(Skill.BARRIER) * 0.05F);
                }
            }
        }

        for (SimpleDomainEntity simple : this.level().getEntitiesOfClass(SimpleDomainEntity.class, AABB.ofSize(victim.position(), 8.0D, 8.0D, 8.0D))) {
            if (victim.distanceTo(simple) < simple.getRadius()) return false;
        }
        return this.isAffected(victim.blockPosition());
    }

    public boolean shouldCollapse(float strength) {
        return (strength / this.getStrength()) > 1.75F;
    }

    public float getStrength() {
        LivingEntity owner = this.getOwner();

        if (owner == null) return 0.0F;

        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return 0.0F;

        ISkillData data = cap.getSkillData();

        return (data.getSkill(Skill.BARRIER) * 0.01F) * (owner.getHealth() / owner.getMaxHealth());
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double pDistance) {
        double d0 = this.getBoundingBox().getSize() * 10.0D;

        if (Double.isNaN(d0)) {
            d0 = 1.0D;
        }
        d0 *= 64.0D * getViewScale();
        return pDistance < d0 * d0;
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        LivingEntity entity = this.getOwner();
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
}
