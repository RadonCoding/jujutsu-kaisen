package radon.jujutsu_kaisen.ability.ten_shadows;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.misc.Summon;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.ten_shadows.DivineDogEntity;

public class DivineDogs extends Summon<DivineDogEntity> {
    public DivineDogs() {
        super(DivineDogEntity.class);
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return owner.getHealth() / owner.getMaxHealth() <= 0.9F;
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 0.25F;
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
    protected int getCount() {
        return 2;
    }

    @Override
    protected DivineDogEntity summon(int index, LivingEntity owner) {
        return new DivineDogEntity(owner, index == 0 ? DivineDogEntity.Variant.WHITE : DivineDogEntity.Variant.BLACK, false);
    }

    @Override
    public EntityType<DivineDogEntity> getType() {
        return JJKEntities.DIVINE_DOG.get();
    }

    @Override
    public boolean canDie() {
        return true;
    }
}
