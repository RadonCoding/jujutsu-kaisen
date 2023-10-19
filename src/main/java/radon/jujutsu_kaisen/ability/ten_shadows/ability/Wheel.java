package radon.jujutsu_kaisen.ability.ten_shadows.ability;

import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Summon;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.TenShadowsMode;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.ten_shadows.WheelEntity;
import radon.jujutsu_kaisen.entity.ten_shadows.MahoragaEntity;

import java.util.List;

public class Wheel extends Summon<WheelEntity> {
    public Wheel() {
        super(WheelEntity.class);
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        if (owner instanceof MahoragaEntity) return true;
        if (target == null) return false;

        ISorcererData ownerCap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        if (target.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
            ISorcererData targetCap = target.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

            if (targetCap.hasToggled(JJKAbilities.INFINITY.get())) {
                return !ownerCap.isAdaptedTo(JJKAbilities.INFINITY.get());
            }
            return targetCap.getTechnique() != null && !ownerCap.isAdaptedTo(targetCap.getTechnique());
        }
        return false;
    }

    @Override
    public boolean isValid(LivingEntity owner) {
        if (!super.isValid(owner)) return false;
        if (!owner.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return false;
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        return !JJKAbilities.hasToggled(owner, JJKAbilities.MAHORAGA.get()) &&
                cap.hasTamed(owner.level().registryAccess().registryOrThrow(Registries.ENTITY_TYPE), JJKEntities.MAHORAGA.get()) &&
                (JJKAbilities.hasToggled(owner, this) || cap.getMode() == TenShadowsMode.ABILITY);
    }

    @Override
    public List<EntityType<?>> getTypes() {
        return List.of(JJKEntities.WHEEL.get());
    }

    @Override
    public boolean isTenShadows() {
        return false;
    }

    @Override
    protected WheelEntity summon(int index, LivingEntity owner) {
        return new WheelEntity(owner);
    }

    @Override
    public Status checkStatus(LivingEntity owner) {
        if (!owner.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return Status.FAILURE;
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        if (owner.level() instanceof ServerLevel level) {
            if (cap.hasSummonOfClass(level, MahoragaEntity.class)) return Status.FAILURE;
        }
        return super.checkStatus(owner);
    }

    @Override
    public Status checkToggleable(LivingEntity owner) {
        if (!owner.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return Status.FAILURE;
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        if (owner.level() instanceof ServerLevel level) {
            if (cap.hasSummonOfClass(level, MahoragaEntity.class)) return Status.FAILURE;
        }
        return super.checkToggleable(owner);
    }

    @Override
    public void run(LivingEntity owner) {

    }

    @Override
    public float getCost(LivingEntity owner) {
        return 0;
    }

    @Override
    public boolean shouldLog() {
        return false;
    }
}
