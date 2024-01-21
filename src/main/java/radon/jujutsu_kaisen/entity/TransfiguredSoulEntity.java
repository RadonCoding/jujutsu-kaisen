package radon.jujutsu_kaisen.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.level.Level;
import radon.jujutsu_kaisen.ability.base.Summon;
import radon.jujutsu_kaisen.entity.base.SummonEntity;

public class TransfiguredSoulEntity extends SummonEntity {
    protected TransfiguredSoulEntity(EntityType<? extends TamableAnimal> pType, Level pLevel) {
        super(pType, pLevel);
    }

    @Override
    public Summon<?> getAbility() {
        return null;
    }
}
