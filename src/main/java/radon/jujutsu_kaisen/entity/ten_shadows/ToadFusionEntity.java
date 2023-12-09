package radon.jujutsu_kaisen.entity.ten_shadows;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.level.Level;
import radon.jujutsu_kaisen.entity.JJKEntities;

public class ToadFusionEntity extends ToadEntity {
    public ToadFusionEntity(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public ToadFusionEntity(LivingEntity owner, boolean ritual) {
        super(JJKEntities.TOAD_FUSION.get(), owner, true, ritual);
    }

    @Override
    public boolean hasWings() {
        return true;
    }
}