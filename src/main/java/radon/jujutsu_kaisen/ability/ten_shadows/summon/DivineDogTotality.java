package radon.jujutsu_kaisen.ability.ten_shadows.summon;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Summon;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.ten_shadows.DivineDogTotalityEntity;

import java.util.concurrent.atomic.AtomicBoolean;

public class DivineDogTotality extends Summon<DivineDogTotalityEntity> {
    public DivineDogTotality() {
        super(DivineDogTotalityEntity.class);
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        if (JJKAbilities.hasToggled(owner, this)) {
            return target != null;
        }
        return target != null && owner.getHealth() / owner.getMaxHealth() <= 0.5F;
    }

    @Override
    public boolean isUnlocked(LivingEntity owner) {
        AtomicBoolean result = new AtomicBoolean();

        owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap ->
                result.set(cap.isDead(owner.level.registryAccess().registryOrThrow(Registries.ENTITY_TYPE), JJKEntities.DIVINE_DOG_WHITE.get()) ||
                        cap.isDead(owner.level.registryAccess().registryOrThrow(Registries.ENTITY_TYPE), JJKEntities.DIVINE_DOG_BLACK.get())));
        return result.get() && super.isUnlocked(owner);
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 0.2F;
    }

    @Override
    public EntityType<DivineDogTotalityEntity> getType() {
        return JJKEntities.DIVINE_DOG_TOTALITY.get();
    }

    @Override
    protected DivineDogTotalityEntity summon(int index, LivingEntity owner) {
        return new DivineDogTotalityEntity(owner);
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
