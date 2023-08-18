package radon.jujutsu_kaisen.ability.ten_shadows;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.misc.Summon;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.ten_shadows.GreatSerpentEntity;

public class GreatSerpent extends Summon<GreatSerpentEntity> {
    public GreatSerpent() {
        super(GreatSerpentEntity.class);
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        if (JJKAbilities.hasToggled(owner, this)) {
            return target != null;
        }
        return target != null && owner.getHealth() / owner.getMaxHealth() <= 0.5F;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return this.isTamed(owner) ? ActivationType.TOGGLED : ActivationType.INSTANT;
    }

    public float getCost(LivingEntity owner) {
        return this.isTamed(owner) ? 0.25F : 50.0F;
    }

    @Override
    public int getCooldown() {
        return 15 * 20;
    }

    @Override
    public EntityType<GreatSerpentEntity> getType() {
        return JJKEntities.GREAT_SERPENT.get();
    }

    @Override
    protected GreatSerpentEntity summon(int index, LivingEntity owner) {
        return new GreatSerpentEntity(owner, this.isTamed(owner));
    }

    @Override
    public boolean canDie() {
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
