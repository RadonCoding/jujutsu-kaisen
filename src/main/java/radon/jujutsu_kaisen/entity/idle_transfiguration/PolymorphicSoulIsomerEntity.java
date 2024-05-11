package radon.jujutsu_kaisen.entity.idle_transfiguration;

import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import radon.jujutsu_kaisen.ability.registry.JJKAbilities;
import radon.jujutsu_kaisen.ability.Summon;
import radon.jujutsu_kaisen.entity.registry.JJKEntities;
import radon.jujutsu_kaisen.entity.sorcerer.base.SorcererEntity;
import radon.jujutsu_kaisen.entity.idle_transfiguration.base.TransfiguredSoulEntity;

public class PolymorphicSoulIsomerEntity extends TransfiguredSoulEntity {
    public PolymorphicSoulIsomerEntity(EntityType<? extends TamableAnimal> pType, Level pLevel) {
        super(pType, pLevel);
    }

    public PolymorphicSoulIsomerEntity(LivingEntity owner) {
        super(JJKEntities.POLYMORPHIC_SOUL_ISOMER.get(), owner);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return SorcererEntity.createAttributes()
                .add(Attributes.MAX_HEALTH, 1.0F)
                .add(Attributes.ATTACK_DAMAGE, 5 * 2.0D);
    }

    @Override
    public Summon<?> getAbility() {
        return JJKAbilities.POLYMORPHIC_SOUL_ISOMER.get();
    }
}
