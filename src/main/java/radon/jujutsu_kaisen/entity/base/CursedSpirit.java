package radon.jujutsu_kaisen.entity.base;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.StructureTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.entity.ai.goal.*;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SetOverlayMessageS2CPacket;
import radon.jujutsu_kaisen.util.HelperMethods;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.UUID;

public abstract class CursedSpirit extends TamableAnimal implements GeoEntity, ISorcerer, ICommandable {
    private static final int RARITY = 5;

    private static final EntityDataAccessor<Integer> DATA_TIME = SynchedEntityData.defineId(CursedSpirit.class, EntityDataSerializers.INT);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    @Nullable
    private UUID ownerUUID;
    @Nullable
    private LivingEntity cachedOwner;

    protected CursedSpirit(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);

        this.setTame(false);
    }

    private boolean isInVillage() {
        HolderSet.Named<Structure> structures = this.level().registryAccess().registryOrThrow(Registries.STRUCTURE).getTag(StructureTags.VILLAGE).orElseThrow();

        boolean success = false;

        for (Holder<Structure> holder : structures) {
            if (((ServerLevel) this.level()).structureManager().getStructureWithPieceAt(this.blockPosition(), holder.get()).isValid()) {
                success = true;
                break;
            }
        }
        return success;
    }

    private boolean isInFortress() {
        Structure structure = this.level().registryAccess().registryOrThrow(Registries.STRUCTURE).get(BuiltinStructures.FORTRESS);
        if (structure == null) return false;
        return ((ServerLevel) this.level()).structureManager().getStructureWithPieceAt(this.blockPosition(), structure).isValid();
    }

    @Override
    public boolean checkSpawnRules(@NotNull LevelAccessor pLevel, @NotNull MobSpawnType pSpawnReason) {
        if (pSpawnReason == MobSpawnType.NATURAL || pSpawnReason == MobSpawnType.CHUNK_GENERATION) {
            if (this.isInVillage()) {
                if (this.random.nextInt(Mth.floor(RARITY * HelperMethods.getPower(this.getGrade().getRequiredExperience()) * (this.level().isNight() ? 0.5F : 1.0F))) != 0) return false;
                if (this.getGrade().ordinal() == SorcererGrade.SPECIAL_GRADE.ordinal()) return false;
            } else if (!this.isInFortress()) {
                return false;
            }

            if (this.getGrade().ordinal() < SorcererGrade.SPECIAL_GRADE.ordinal()) {
                if (!this.isInVillage() && !this.isInFortress()) return false;
            } else if (!this.isInFortress()) {
                return false;
            }
        }

        if (!pLevel.getEntitiesOfClass(CursedSpirit.class, AABB.ofSize(this.position(), 16.0D, 8.0D, 16.0D)).isEmpty())
            return false;

        if (this.getGrade().ordinal() >= SorcererGrade.GRADE_1.ordinal()) {
            if (!pLevel.getEntitiesOfClass(this.getClass(), AABB.ofSize(this.position(), 64.0D, 32.0D, 64.0D)).isEmpty())
                return false;
        }
        return super.checkSpawnRules(pLevel, pSpawnReason);
    }

    @Override
    public boolean isPersistenceRequired() {
        return this.getGrade().ordinal() > SorcererGrade.GRADE_1.ordinal();
    }

    @Override
    public SorcererGrade getGrade() {
        if (!this.isAddedToWorld()) {
            return HelperMethods.getGrade(this.getExperience());
        }
        ISorcererData cap = this.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        return HelperMethods.getGrade(cap.getExperience());
    }

    protected abstract boolean isCustom();

    protected boolean canFly() { return false; }

    protected boolean hasMeleeAttack() { return true; }

    protected boolean targetsCurses() { return false; }
    protected boolean targetsSorcerers() { return true; }

    private void createGoals() {
        int target = 1;
        int goal = 1;

        this.goalSelector.addGoal(goal++, new WaterWalkingFloatGoal(this));

        if (this.hasMeleeAttack()) {
            this.goalSelector.addGoal(goal++, new MeleeAttackGoal(this, 1.2D, true));
        }
        this.goalSelector.addGoal(goal++, new LookAtTargetGoal(this));
        this.goalSelector.addGoal(goal++, this.canPerformSorcery() || !this.getCustom().isEmpty() ? new SorcererGoal(this) : new HealingGoal(this));
        this.goalSelector.addGoal(goal, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(target++, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(target++, new NearestAttackableTargetGoal<>(this, IronGolem.class, false));
        this.targetSelector.addGoal(target++, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false));

        if (this.targetsSorcerers()) {
            this.targetSelector.addGoal(target++, new NearestAttackableSorcererGoal(this, true));
        }
        if (this.targetsCurses()) {
            this.targetSelector.addGoal(target, new NearestAttackableCurseGoal(this, true));
        }
    }

    @Override
    public boolean canAttack(@NotNull LivingEntity pTarget) {
        return (!this.isTame() || pTarget != this.getOwner()) && super.canAttack(pTarget);
    }

    @Override
    protected void actuallyHurt(@NotNull DamageSource pDamageSource, float pDamageAmount) {
        super.actuallyHurt(pDamageSource, pDamageAmount);

        if (pDamageSource.getEntity() instanceof LivingEntity attacker && this.canAttack(attacker) && attacker != this) {
            this.setTarget(attacker);
        }
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
    public void aiStep() {
        this.updateSwingTime();

        super.aiStep();
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();

        this.setSprinting(this.getDeltaMovement().lengthSqr() > 0.01D && this.moveControl.getSpeedModifier() > 1.0D);
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();

        if (!this.isCustom()) this.createGoals();

        this.getCapability(SorcererDataHandler.INSTANCE).ifPresent(this::init);

        if (this.canChangeTarget() && this.getOwner() instanceof ServerPlayer player) {
            PacketHandler.sendToClient(new SetOverlayMessageS2CPacket(Component.translatable(String.format("chat.%s.set_target_info", JujutsuKaisen.MOD_ID)),
                    false), player);
        }
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);

        if (this.ownerUUID != null) {
            pCompound.putUUID("owner", this.ownerUUID);
        }
        pCompound.putInt("time", this.entityData.get(DATA_TIME));
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);

        if (pCompound.hasUUID("owner")) {
            this.ownerUUID = pCompound.getUUID("owner");
        }
        this.entityData.set(DATA_TIME, pCompound.getInt("time"));
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();

        this.entityData.define(DATA_TIME, 0);
    }

    public int getTime() {
        return this.entityData.get(DATA_TIME);
    }

    private void setTime(int time) {
        this.entityData.set(DATA_TIME, time);
    }

    @Override
    public void tick() {
        super.tick();

        int time = this.getTime();
        this.setTime(++time);

        if (this.isTame()) {
            LivingEntity target = this.getTarget();
            this.setOrderedToSit(target != null && !target.isRemoved() && target.isAlive());
        }
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(@NotNull ServerLevel pLevel, @NotNull AgeableMob pOtherParent) {
        return null;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public JujutsuType getJujutsuType() {
        return JujutsuType.CURSE;
    }

    @Override
    public boolean canChangeTarget() {
        return this.isTame();
    }

    @Override
    public void changeTarget(LivingEntity target) {
        this.setTarget(target);
    }
}
