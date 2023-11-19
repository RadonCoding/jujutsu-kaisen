package radon.jujutsu_kaisen.ability.ten_shadows.summon;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Summon;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.ten_shadows.AgitoEntity;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.List;

public class Agito extends Summon<AgitoEntity> {
    public Agito() {
        super(AgitoEntity.class);
    }

    @Override
    public boolean isScalable() {
        return false;
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
        return List.of(JJKEntities.NUE.get(), JJKEntities.GREAT_SERPENT.get(), JJKEntities.TRANQUIL_DEER.get());
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 0.3F;
    }

    @Override
    public int getCooldown() {
        return 25 * 20;
    }

    @Override
    public List<EntityType<?>> getTypes() {
        return List.of(JJKEntities.AGITO.get());
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
    protected AgitoEntity summon(int index, LivingEntity owner) {
        return new AgitoEntity(owner);
    }


}
