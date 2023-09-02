package radon.jujutsu_kaisen.entity.base;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public abstract class TenShadowsSummon extends SummonEntity implements ICommandable, ISorcerer {
    protected static final int MAX_DISTANCE = 64;

    private static final EntityDataAccessor<Boolean> DATA_CLONE = SynchedEntityData.defineId(TenShadowsSummon.class, EntityDataSerializers.BOOLEAN);

    protected final List<UUID> participants = new ArrayList<>();

    protected TenShadowsSummon(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public boolean canAttack(@NotNull LivingEntity pTarget) {
        return super.canAttack(pTarget) && !(pTarget.getType() == this.getType() && ((TenShadowsSummon) pTarget).isClone()) &&
                !(pTarget instanceof TamableAnimal tamable && tamable.getOwner() == this.getOwner() && tamable.isTame() == this.isTame());
    }

    public void setClone(boolean clone) {
        this.entityData.set(DATA_CLONE, clone);
    }

    @Override
    public @NotNull EntityDimensions getDimensions(@NotNull Pose pPose) {
        return super.getDimensions(pPose).scale(this.getScale());
    }

    private void spawnParticles() {
        this.playSound(SoundEvents.FISHING_BOBBER_SPLASH, 0.25F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);

        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < this.getBbHeight() * this.getBbHeight(); j++) {
                this.level.addParticle(ParticleTypes.SMOKE, this.getX() + (this.getBbWidth() * this.random.nextGaussian() * 0.1F), this.getY(),
                        this.getZ() + (this.getBbWidth() * this.random.nextGaussian() * 0.1F),
                        this.random.nextGaussian() * 0.075D, this.random.nextGaussian() * 0.25D, this.random.nextGaussian() * 0.075D);
                this.level.addParticle(ParticleTypes.LARGE_SMOKE, this.getX() + (this.getBbWidth() * this.random.nextGaussian() * 0.1F), this.getY(),
                        this.getZ() + (this.getBbWidth() * this.random.nextGaussian() * 0.1F),
                        this.random.nextGaussian() * 0.075D, this.random.nextGaussian() * 0.25D, this.random.nextGaussian() * 0.075D);
            }
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();

        this.entityData.define(DATA_CLONE, false);
    }

    @Override
    public boolean canChangeTarget() {
        return this.isTame();
    }

    @Override
    public void changeTarget(LivingEntity target) {
        this.setTarget(target);
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();

        this.spawnParticles();

        if (!this.isTame()) {
            Vec3 center = this.position();
            AABB area = new AABB(center.x() - 16.0D, center.y() - 16.0D, center.z() - 16.0D,
                    center.x() + 16.0D, center.y() + 16.0D, center.z() + 16.0D);

            for (LivingEntity participant : this.level.getEntitiesOfClass(LivingEntity.class, area)) {
                if ((participant.getType() == this.getType() && ((TenShadowsSummon) participant).isClone()) || participant == this) continue;
                if (!participant.getCapability(SorcererDataHandler.INSTANCE).isPresent()) continue;
                this.participants.add(participant.getUUID());
            }
        }
    }

    public boolean isClone() {
        return this.entityData.get(DATA_CLONE);
    }

    @Override
    public void onRemovedFromWorld() {
        super.onRemovedFromWorld();

        this.spawnParticles();
    }

    @Override
    public void die(@NotNull DamageSource pCause) {
        super.die(pCause);

        if (this.isClone()) return;

        LivingEntity owner = this.getOwner();

        if (owner != null && !owner.level.isClientSide) {
            owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                if (cap.getTechnique() == CursedTechnique.TEN_SHADOWS || cap.getAdditional() == CursedTechnique.TEN_SHADOWS) {
                    if (!this.isTame()) {
                        if (pCause.getEntity() == owner) {
                            cap.tame(this.level.registryAccess().registryOrThrow(Registries.ENTITY_TYPE), this.getType());

                            if (owner instanceof ServerPlayer player) {
                                PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(cap.serializeNBT()), player);
                            }
                        }
                    } else {
                        if (this.getAbility().canDie()) {
                            cap.kill(this.level.registryAccess().registryOrThrow(Registries.ENTITY_TYPE), this.getType());

                            if (owner instanceof ServerPlayer player) {
                                PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(cap.serializeNBT()), player);
                            }
                        }
                    }
                }
            });
        }
    }

    private void checkParticipants() {
        Iterator<UUID> iter = this.participants.iterator();

        while (iter.hasNext()) {
            UUID identifier = iter.next();

            LivingEntity participant = (LivingEntity) ((ServerLevel) this.level).getEntity(identifier);

            if (participant == null || participant.isRemoved() || !participant.isAlive() || participant.distanceTo(this) > MAX_DISTANCE) {
                iter.remove();
            }
        }
    }

    @Override
    public void tick() {
        LivingEntity owner = this.getOwner();

        if (this.isTame() && !this.level.isClientSide && (owner == null || owner.isRemoved() || !owner.isAlive() ||
                (!this.isDeadOrDying() && !JJKAbilities.hasToggled(owner, this.getAbility())))) {
            this.discard();
        } else {
            super.tick();

            if (!this.level.isClientSide) {
                if (!this.isTame()) {
                    this.checkParticipants();

                    boolean disappear = this.participants.isEmpty();

                    if (disappear) {
                        this.discard();
                    }
                } else {
                    LivingEntity target = this.getTarget();
                    this.setOrderedToSit(target != null && !target.isRemoved() && target.isAlive());
                }

                if (owner != null && this.isClone() && !JJKAbilities.hasToggled(owner, JJKAbilities.CHIMERA_SHADOW_GARDEN.get())) {
                    this.discard();
                }
            }
        }
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);

        ListTag participantsTag = new ListTag();

        for (UUID identifier : this.participants) {
            participantsTag.add(NbtUtils.createUUID(identifier));
        }
        pCompound.put("participants", participantsTag);

        pCompound.putBoolean("clone", this.entityData.get(DATA_CLONE));
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);

        for (Tag key : pCompound.getList("participants", Tag.TAG_INT_ARRAY)) {
            this.participants.add(NbtUtils.loadUUID(key));
        }
        this.entityData.set(DATA_CLONE, pCompound.getBoolean("clone"));
    }

    @Override
    public boolean isOwnedBy(@NotNull LivingEntity pEntity) {
        return this.isTame() && super.is(pEntity);
    }

    @Override
    public JujutsuType getJujutsuType() {
        return JujutsuType.SHIKIGAMI;
    }

    @Override
    public SorcererGrade getGrade() {
        AtomicReference<SorcererGrade> result = new AtomicReference<>(SorcererGrade.GRADE_4);

        LivingEntity owner = this.getOwner();

        if (owner != null) {
            owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                SorcererGrade grade = cap.getGrade();
                int index = Mth.clamp(grade.ordinal() - 1, 0, SorcererGrade.values().length - 1);
                result.set(SorcererGrade.values()[index]);
            });
        }
        return result.get();
    }

    @Override
    public @Nullable CursedTechnique getTechnique() {
        return null;
    }

    @Override
    public @NotNull List<Trait> getTraits() {
        return List.of();
    }

    @Override
    public @Nullable Ability getDomain() {
        return null;
    }
}
