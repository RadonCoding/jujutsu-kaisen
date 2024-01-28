package radon.jujutsu_kaisen.entity.idle_transfiguration;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.level.Level;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.idle_transfiguration.base.TransfiguredSoulEntity;

public class TransfiguredSoulNormalEntity extends TransfiguredSoulEntity {
    public TransfiguredSoulNormalEntity(EntityType<? extends TamableAnimal> pType, Level pLevel) {
        super(pType, pLevel);
    }

    public TransfiguredSoulNormalEntity(LivingEntity owner) {
        super(JJKEntities.TRANSFIGURED_SOUL_NORMAL.get(), owner);
    }
}
