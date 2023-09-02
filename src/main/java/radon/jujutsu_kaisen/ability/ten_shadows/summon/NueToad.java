package radon.jujutsu_kaisen.ability.ten_shadows.summon;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Summon;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.ten_shadows.ToadEntity;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.List;

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
    protected List<EntityType<?>> getFusions() {
        return List.of(JJKEntities.NUE.get(), JJKEntities.TOAD.get());
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 0.2F;
    }

    @Override
    public int getCooldown() {
        return 15 * 20;
    }

    @Override
    public boolean isTechnique() {
        return true;
    }

    @Override
    public List<EntityType<?>> getTypes() {
        return List.of(JJKEntities.TOAD.get());
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
