package radon.jujutsu_kaisen.ability.ten_shadows.summon;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Summon;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.ten_shadows.TranquilDeerEntity;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.List;

public class TranquilDeer extends Summon<TranquilDeerEntity> {
    public TranquilDeer() {
        super(TranquilDeerEntity.class);
    }

    @Override
    public boolean isScalable() {
        return false;
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        if (!this.isTamed(owner)) return false;

        if (JJKAbilities.hasToggled(owner, this)) {
            return target != null;
        }
        return target != null && HelperMethods.RANDOM.nextInt(10 / (JJKAbilities.hasToggled(owner, JJKAbilities.CHIMERA_SHADOW_GARDEN.get()) ? 2 : 1)) == 0;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return this.isTamed(owner) ? ActivationType.TOGGLED : ActivationType.INSTANT;
    }

    public float getCost(LivingEntity owner) {
        return this.isTamed(owner) ? 0.2F : 250.0F;
    }

    @Override
    public int getCooldown() {
        return 15 * 20;
    }

    @Override
    public List<EntityType<?>> getTypes() {
        return List.of(JJKEntities.TRANQUIL_DEER.get());
    }

    @Override
    protected TranquilDeerEntity summon(int index, LivingEntity owner) {
        return new TranquilDeerEntity(owner, this.isTamed(owner));
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
    protected boolean canTame() {
        return true;
    }

    @Override
    public boolean isTechnique() {
        return true;
    }
}
