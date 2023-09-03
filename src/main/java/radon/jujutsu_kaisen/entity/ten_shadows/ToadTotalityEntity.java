package radon.jujutsu_kaisen.entity.ten_shadows;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.level.Level;
import radon.jujutsu_kaisen.entity.JJKEntities;

public class ToadTotalityEntity extends ToadEntity {
    public ToadTotalityEntity(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);

        this.setWings(true);
    }

    public ToadTotalityEntity(LivingEntity owner, boolean ritual) {
        super(JJKEntities.TOAD_TOTALITY.get(), owner, ritual);
    }
}