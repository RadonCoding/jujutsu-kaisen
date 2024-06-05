package radon.jujutsu_kaisen.entity.ten_shadows;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.level.Level;
import radon.jujutsu_kaisen.ability.registry.JJKAbilities;
import radon.jujutsu_kaisen.ability.Summon;
import radon.jujutsu_kaisen.entity.registry.JJKEntities;

public class DivineDogWhiteEntity extends DivineDogEntity {
    public DivineDogWhiteEntity(EntityType<? extends TamableAnimal> pType, Level pLevel) {
        super(pType, pLevel);

        this.setVariant(Variant.WHITE);
    }

    public DivineDogWhiteEntity(LivingEntity owner, boolean ritual) {
        super(JJKEntities.DIVINE_DOG_WHITE.get(), owner, ritual);
    }

    @Override
    public Summon<?> getAbility() {
        return JJKAbilities.DIVINE_DOG_WHITE.get();
    }
}

