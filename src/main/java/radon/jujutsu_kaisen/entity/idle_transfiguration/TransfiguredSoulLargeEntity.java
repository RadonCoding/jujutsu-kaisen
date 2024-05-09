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
import radon.jujutsu_kaisen.entity.sorcerer.base.SorcererEntity;

public class TransfiguredSoulLargeEntity extends TransfiguredSoulVariantEntity {
    public TransfiguredSoulLargeEntity(EntityType<? extends TamableAnimal> pType, Level pLevel) {
        super(pType, pLevel);
    }

    public TransfiguredSoulLargeEntity(LivingEntity owner) {
        super(JJKEntities.TRANSFIGURED_SOUL_LARGE.get(), owner);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return SorcererEntity.createAttributes()
                .add(Attributes.MAX_HEALTH, 5 * 20.0F)
                .add(Attributes.ATTACK_DAMAGE, 2 * 2.0D);
    }

    @Override
    public Summon<?> getAbility() {
        return JJKAbilities.TRANSFIGURED_SOUL_LARGE.get();
    }
}
