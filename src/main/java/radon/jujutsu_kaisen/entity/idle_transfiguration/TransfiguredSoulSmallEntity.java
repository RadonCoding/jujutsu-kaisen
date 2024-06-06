package radon.jujutsu_kaisen.entity.idle_transfiguration;


import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import radon.jujutsu_kaisen.ability.registry.JJKAbilities;
import radon.jujutsu_kaisen.ability.Summon;
import radon.jujutsu_kaisen.entity.registry.JJKEntities;
import radon.jujutsu_kaisen.entity.idle_transfiguration.base.TransfiguredSoulVariantEntity;
import radon.jujutsu_kaisen.entity.sorcerer.SorcererEntity;

public class TransfiguredSoulSmallEntity extends TransfiguredSoulVariantEntity {
    public TransfiguredSoulSmallEntity(EntityType<? extends TamableAnimal> pType, Level pLevel) {
        super(pType, pLevel);
    }

    public TransfiguredSoulSmallEntity(LivingEntity owner) {
        super(JJKEntities.TRANSFIGURED_SOUL_SMALL.get(), owner);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return SorcererEntity.createAttributes()
                .add(Attributes.MAX_HEALTH, 10.0F)
                .add(Attributes.ATTACK_DAMAGE, 1.0D);
    }

    @Override
    public Summon<?> getAbility() {
        return JJKAbilities.TRANSFIGURED_SOUL_SMALL.get();
    }
}
