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
import radon.jujutsu_kaisen.entity.ten_shadows.GreatSerpentEntity;
import radon.jujutsu_kaisen.entity.ten_shadows.NueEntity;
import radon.jujutsu_kaisen.entity.ten_shadows.NueTotalityEntity;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.concurrent.atomic.AtomicBoolean;

public class NueTotality extends Summon<NueTotalityEntity> {
    public NueTotality() {
        super(NueTotalityEntity.class);
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
                result.set(!cap.isDead(registry, JJKEntities.NUE.get()) && cap.hasTamed(registry, JJKEntities.NUE.get()) &&
                        !cap.isDead(registry, JJKEntities.GREAT_SERPENT.get()) && cap.hasTamed(registry, JJKEntities.GREAT_SERPENT.get())));
        return result.get();
    }

    @Override
    public Status checkStatus(LivingEntity owner) {
        AtomicBoolean result = new AtomicBoolean();

        if (owner.level instanceof ServerLevel level) {
            owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap ->
                    result.set(cap.hasSummonOfClass(level, NueEntity.class) || cap.hasSummonOfClass(level, GreatSerpentEntity.class)));
        }
        return result.get() ? Status.FAILURE : super.checkStatus(owner);
    }

    @Override
    public Status checkToggleable(LivingEntity owner) {
        AtomicBoolean result = new AtomicBoolean();

        if (owner.level instanceof ServerLevel level) {
            owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap ->
                    result.set(cap.hasSummonOfClass(level, NueEntity.class) || cap.hasSummonOfClass(level, GreatSerpentEntity.class)));
        }
        return result.get() ? Status.FAILURE : super.checkToggleable(owner);
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 1.0F;
    }

    @Override
    public EntityType<NueTotalityEntity> getType() {
        return JJKEntities.NUE_TOTALITY.get();
    }

    @Override
    protected NueTotalityEntity summon(int index, LivingEntity owner) {
        return new NueTotalityEntity(owner);
    }

    @Override
    public boolean canDie() {
        return true;
    }

    @Override
    public boolean isTenShadows() {
        return true;
    }

    @Override
    public int getCooldown() {
        return 15 * 20;
    }

    @Override
    public boolean isTechnique() {
        return true;
    }
}
