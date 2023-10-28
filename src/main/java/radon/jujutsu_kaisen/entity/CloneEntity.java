package radon.jujutsu_kaisen.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.entity.ai.goal.BetterFloatGoal;
import radon.jujutsu_kaisen.entity.ai.goal.LookAtTargetGoal;
import radon.jujutsu_kaisen.entity.ai.goal.SorcererGoal;
import radon.jujutsu_kaisen.entity.base.ISorcerer;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

public class CloneEntity extends PathfinderMob implements ISorcerer {
    private static final int TELEPORT_RADIUS = 32;

    @Nullable
    private UUID ownerUUID;
    @Nullable
    private LivingEntity cachedOwner;

    private ResourceLocation original;

    protected CloneEntity(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);

        Arrays.fill(this.armorDropChances, 0.0F);
        Arrays.fill(this.handDropChances, 0.0F);
    }

    public CloneEntity(LivingEntity owner, ResourceLocation original) {
        super(JJKEntities.CLONE.get(), owner.level());

        this.moveTo(owner.getX(), owner.getY(), owner.getZ(), owner.getYRot(), owner.getXRot());

        this.setOwner(owner);

        this.original = original;
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();

        LivingEntity owner = this.getOwner();

        if (owner != null) {
            owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(src ->
                    this.getCapability(SorcererDataHandler.INSTANCE).ifPresent(dst -> dst.deserializeNBT(src.serializeNBT())));
        }
    }

    @Override
    public void tick() {
        LivingEntity owner = this.getOwner();

        if (!this.level().isClientSide && (owner == null || owner.isRemoved() || !owner.isAlive())) {
            this.discard();
        } else {
            super.tick();
        }
    }

    @Override
    protected void customServerAiStep() {
        LivingEntity owner = this.getOwner();

        if (owner != null) {
            if (!this.getMainHandItem().is(owner.getMainHandItem().getItem())) {
                this.setItemInHand(InteractionHand.MAIN_HAND, owner.getMainHandItem().copy());
            }
            if (!this.getOffhandItem().is(owner.getOffhandItem().getItem())) {
                this.setItemInHand(InteractionHand.OFF_HAND, owner.getOffhandItem().copy());
            }
            this.setItemSlot(EquipmentSlot.HEAD, owner.getItemBySlot(EquipmentSlot.HEAD));
            this.setItemSlot(EquipmentSlot.CHEST, owner.getItemBySlot(EquipmentSlot.CHEST));
            this.setItemSlot(EquipmentSlot.LEGS, owner.getItemBySlot(EquipmentSlot.LEGS));
            this.setItemSlot(EquipmentSlot.FEET, owner.getItemBySlot(EquipmentSlot.FEET));

            if (this.distanceTo(owner) >= this.getAttributeValue(Attributes.FOLLOW_RANGE)) {
                double d0 = owner.getX() + ((HelperMethods.RANDOM.nextDouble() - HelperMethods.RANDOM.nextDouble()) + 0.1D) * TELEPORT_RADIUS + 0.5D;
                double d1 = owner.getY() + HelperMethods.RANDOM.nextInt(3) - 1;
                double d2 = owner.getZ() + ((HelperMethods.RANDOM.nextDouble() - HelperMethods.RANDOM.nextDouble()) + 0.1D) * TELEPORT_RADIUS + 0.5D;

                if (this.level().noCollision(this.getType().getAABB(d0, d1, d2))) {
                    this.setPos(d0, d1, d2);
                }
            }
        }
    }

    @Override
    public void die(@NotNull DamageSource pDamageSource) {
        super.die(pDamageSource);

        LivingEntity owner = this.getOwner();

        if (owner == null) return;

        MinecraftServer server = this.level().getServer();

        if (server == null) return;

        ServerLevel dimension = server.getLevel(ResourceKey.create(Registries.DIMENSION, this.original));

        if (dimension == null) return;

        if (owner.level() != this.level()) return;

        BlockPos pos = HelperMethods.findSafePos(dimension, owner);
        owner.teleportTo(dimension, pos.getX(), pos.getY(), pos.getZ(), Set.of(), owner.getYRot(), owner.getXRot());
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);

        if (this.ownerUUID != null) {
            pCompound.putUUID("owner", this.ownerUUID);
        }
        pCompound.putString("original", this.original.toString());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);

        if (pCompound.hasUUID("owner")) {
            this.ownerUUID = pCompound.getUUID("owner");
        }
        this.original = new ResourceLocation(pCompound.getString("original"));
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new BetterFloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(3, new LookAtTargetGoal(this));
        this.goalSelector.addGoal(4, new SorcererGoal(this));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
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

    @Override
    public @NotNull Component getName() {
        LivingEntity owner = this.getOwner();
        return owner == null ? super.getName() : owner.getName();
    }

    @Override
    public boolean isPersistenceRequired() {
        return true;
    }

    @Override
    public boolean canPerformSorcery() {
        return true;
    }

    @Override
    public SorcererGrade getGrade() {
        return null;
    }

    @Override
    public @Nullable CursedTechnique getTechnique() {
        return null;
    }

    @Override
    public JujutsuType getJujutsuType() {
        return null;
    }

    @Override
    public @Nullable Ability getDomain() {
        return null;
    }
}
