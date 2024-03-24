package radon.jujutsu_kaisen.entity.ten_shadows;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.level.Level;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Summon;
import radon.jujutsu_kaisen.entity.JJKEntities;

public class DivineDogBlackEntity extends DivineDogEntity {
    public DivineDogBlackEntity(EntityType<? extends TamableAnimal> pType, Level pLevel) {
        super(pType, pLevel);

        this.setVariant(Variant.BLACK);
    }

    public DivineDogBlackEntity(LivingEntity owner, boolean ritual) {
        super(JJKEntities.DIVINE_DOG_BLACK.get(), owner, ritual);
    }

    @Override
    public Summon<?> getAbility() {
        return JJKAbilities.DIVINE_DOG_BLACK.get();
    }
}
