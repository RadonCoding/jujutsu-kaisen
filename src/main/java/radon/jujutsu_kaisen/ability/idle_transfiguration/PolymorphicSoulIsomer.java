package radon.jujutsu_kaisen.ability.idle_transfiguration;

import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.idle_transfiguration.base.TransfiguredSoul;
import radon.jujutsu_kaisen.entity.registry.JJKEntities;
import radon.jujutsu_kaisen.entity.idle_transfiguration.PolymorphicSoulIsomerEntity;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.List;

public class PolymorphicSoulIsomer extends TransfiguredSoul<PolymorphicSoulIsomerEntity> {
    public PolymorphicSoulIsomer() {
        super(PolymorphicSoulIsomerEntity.class);
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        if (target == null) return false;

        return HelperMethods.RANDOM.nextInt(40) == 0;
    }

    @Override
    public List<EntityType<?>> getTypes() {
        return List.of(JJKEntities.POLYMORPHIC_SOUL_ISOMER.get());
    }

    @Override
    public boolean isTenShadows() {
        return false;
    }

    @Override
    protected PolymorphicSoulIsomerEntity summon(LivingEntity owner) {
        return new PolymorphicSoulIsomerEntity(owner);
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 50.0F;
    }

    @Override
    public int getSoulCost() {
        return 5;
    }
}