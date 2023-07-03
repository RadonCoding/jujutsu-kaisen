package radon.jujutsu_kaisen.entity.base;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.AbilityHandler;
import radon.jujutsu_kaisen.ability.DomainExpansion;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.util.RenderUtils;

public abstract class SorcererEntity extends PathfinderMob implements GeoAnimatable {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    protected SorcererEntity(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public void aiStep() {
        this.updateSwingTime();

        super.aiStep();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.32D)
                .add(Attributes.ATTACK_DAMAGE, 1.0D)
                .add(Attributes.FOLLOW_RANGE, 64.0D);
    }

    public void init(ISorcererData data) {
        data.setGrade(this.getGrade());
        data.setTechnique(this.getTechnique());
        data.setTrait(this.getTrait());
        data.setEnergy(data.getMaxEnergy());
    }

    public abstract SorcererGrade getGrade();
    public abstract CursedTechnique getTechnique();
    public abstract Trait getTrait();

    public abstract @Nullable Ability getDomain();

    public final void tryTriggerDomain() {
        LivingEntity target = this.getTarget();

        if (target == null) return;

        Ability domain = this.getDomain();

        if (domain == null) return;

        double distance = this.distanceTo(target);

        if (domain instanceof DomainExpansion.IClosedDomain closed) {
            if (distance >= closed.getRadius() / 2.0F) {
                return;
            }
        }
        if (domain instanceof DomainExpansion.IOpenDomain open) {
            if (distance >= open.getWidth() / 2.0F) {
                return;
            }
        }
        AbilityHandler.trigger(this, domain);
    }

    public void onInsideDomain(DomainExpansionEntity domain) {
        AABB bounds = domain.getBounds();
        double radius = Math.sqrt(bounds.getXsize() * bounds.getXsize() + bounds.getYsize() * bounds.getYsize() + bounds.getZsize() * bounds.getZsize()) / 2;

        Vec3 pos = DefaultRandomPos.getPosAway(this, (int) radius, (int) bounds.getYsize(), bounds.getCenter());

        if (pos != null) {
            Path newPath = this.getNavigation().createPath(pos.x(), pos.y(), pos.z(), 0);

            if (newPath != null) {
                this.getNavigation().moveTo(newPath, 1.0D);
            }
        }
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor pLevel, @NotNull DifficultyInstance pDifficulty, @NotNull MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        this.getCapability(SorcererDataHandler.INSTANCE).ifPresent(this::init);

        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level.isClientSide) {
            this.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                for (DomainExpansionEntity domain : cap.getDomains((ServerLevel) this.level)) {
                    this.onInsideDomain(domain);
                }
            });
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public double getTick(Object o) {
        return RenderUtils.getCurrentTick();
    }
}
