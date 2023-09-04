package radon.jujutsu_kaisen.entity.ten_shadows;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.level.Level;
import radon.jujutsu_kaisen.entity.JJKEntities;

public class DivineDogWhiteEntity extends DivineDogEntity {
    public DivineDogWhiteEntity(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public DivineDogWhiteEntity(LivingEntity owner, boolean ritual) {
        super(JJKEntities.DIVINE_DOG_WHITE.get(), owner, ritual, Variant.WHITE);
    }
}

