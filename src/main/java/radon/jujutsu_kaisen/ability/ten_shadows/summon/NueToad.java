package radon.jujutsu_kaisen.ability.ten_shadows.summon;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Summon;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.ten_shadows.NueEntity;
import radon.jujutsu_kaisen.entity.ten_shadows.ToadEntity;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.concurrent.atomic.AtomicBoolean;

public class NueToad extends Summon<ToadEntity> {
    public NueToad() {
        super(ToadEntity.class);
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        if (JJKAbilities.hasToggled(owner, this)) {
            return target != null;
        }
        return target != null && HelperMethods.RANDOM.nextInt(10) == 0;
    }

    @Override
    public boolean isUnlocked(LivingEntity owner) {
        if (!super.isUnlocked(owner)) return false;

        AtomicBoolean result = new AtomicBoolean();

        Registry<EntityType<?>> registry = owner.level.registryAccess().registryOrThrow(Registries.ENTITY_TYPE);

        owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap ->
                result.set(cap.hasTamed(registry, JJKEntities.NUE.get())));
        return result.get();
    }

    @Override
    public Status checkStatus(LivingEntity owner) {
        AtomicBoolean result = new AtomicBoolean();

        if (owner.level instanceof ServerLevel level) {
            owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap ->
                    result.set(cap.hasSummonOfClass(level, NueEntity.class) || cap.hasSummonOfClass(level, ToadEntity.class)));
        }
        return result.get() ? Status.FAILURE : super.checkStatus(owner);
    }

    @Override
    public Status checkToggleable(LivingEntity owner) {
        AtomicBoolean result = new AtomicBoolean();

        if (owner.level instanceof ServerLevel level) {
            owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap ->
                    result.set(cap.hasSummonOfClass(level, NueEntity.class) || cap.hasSummonOfClass(level, ToadEntity.class)));
        }
        return result.get() ? Status.FAILURE : super.checkToggleable(owner);
    }


    @Override
    public float getCost(LivingEntity owner) {
        return 0.15F;
    }

    @Override
    public int getCooldown() {
        return 30 * 20;
    }

    @Override
    public boolean isTechnique() {
        return true;
    }

    @Override
    public EntityType<ToadEntity> getType() {
        return JJKEntities.TOAD.get();
    }

    @Override
    public boolean isTenShadows() {
        return true;
    }

    @Override
    protected ToadEntity summon(int index, LivingEntity owner) {
        return new ToadEntity(owner, false, true);
    }
}
