package radon.jujutsu_kaisen.entity.base;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.StructureTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.util.HelperMethods;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Arrays;

public abstract class SorcererEntity extends PathfinderMob implements GeoEntity, ISorcerer {
    private static final int RARITY = 3;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    protected SorcererEntity(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);

        Arrays.fill(this.armorDropChances, 1.0F);
        Arrays.fill(this.handDropChances, 1.0F);
    }

    @Override
    public boolean canPerformSorcery() {
        return true;
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

    @Override
    public AnimatableInstanceCache animatableCacheOverride() {
        return null;
    }

    @Override
    protected void actuallyHurt(@NotNull DamageSource pDamageSource, float pDamageAmount) {
        super.actuallyHurt(pDamageSource, pDamageAmount);

        if (pDamageSource.getEntity() instanceof LivingEntity attacker && this.canAttack(attacker) && attacker != this) {
            this.setTarget(attacker);
        }
    }

    private boolean isInVillage() {
        HolderSet.Named<Structure> structures = this.level().registryAccess().registryOrThrow(Registries.STRUCTURE).getTag(StructureTags.VILLAGE).orElseThrow();

        boolean success = false;

        for (Holder<Structure> holder : structures) {
            if (((ServerLevel) this.level()).structureManager().getStructureAt(this.blockPosition(), holder.get()) != StructureStart.INVALID_START) {
                success = true;
                break;
            }
        }
        return success;
    }

    @Override
    public boolean checkSpawnRules(@NotNull LevelAccessor pLevel, @NotNull MobSpawnType pSpawnReason) {
        ISorcererData cap = this.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        if (pSpawnReason == MobSpawnType.NATURAL || pSpawnReason == MobSpawnType.CHUNK_GENERATION) {
            if (this.random.nextInt(Mth.floor(RARITY * HelperMethods.getPower(this.getExperience()))) != 0)
                return false;
            if (!this.isInVillage()) return false;
            if (!pLevel.getEntitiesOfClass(SorcererEntity.class, AABB.ofSize(this.position(), 64.0D, 16.0D, 64.0D)).isEmpty())
                return false;
        }
        if (HelperMethods.getGrade(cap.getExperience()).ordinal() >= SorcererGrade.GRADE_1.ordinal()) {
            if (!pLevel.getEntitiesOfClass(this.getClass(), AABB.ofSize(this.position(), 128.0D, 32.0D, 128.0D)).isEmpty())
                return false;
        }
        return super.checkSpawnRules(pLevel, pSpawnReason);
    }

    @Override
    public void aiStep() {
        this.updateSwingTime();

        super.aiStep();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return SorcererEntity.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.32D)
                .add(Attributes.ATTACK_DAMAGE)
                .add(Attributes.FOLLOW_RANGE, 64.0D);
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();

        this.getCapability(SorcererDataHandler.INSTANCE).ifPresent(this::init);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
