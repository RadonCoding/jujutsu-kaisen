package radon.jujutsu_kaisen.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.level.Level;

public class TransfiguredSoulSmallEntity extends TransfiguredSoulEntity {
    protected TransfiguredSoulSmallEntity(EntityType<? extends TamableAnimal> pType, Level pLevel) {
        super(pType, pLevel);
    }

    public TransfiguredSoulSmallEntity(LivingEntity owner) {
        super(JJKEntities.TRANSFIGURED_SOUL_SMALL.get(), owner);
    }
}
