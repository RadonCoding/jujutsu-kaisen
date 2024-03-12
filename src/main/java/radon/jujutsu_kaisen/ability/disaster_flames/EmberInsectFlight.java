package radon.jujutsu_kaisen.ability.disaster_flames;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.Summon;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.entity.EmberInsectFlightEntity;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.List;

public class EmberInsectFlight extends Summon<EmberInsectFlightEntity> {
    public EmberInsectFlight() {
        super(EmberInsectFlightEntity.class);
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return false;

        IAbilityData data = cap.getAbilityData();

        if (data.hasToggled(this)) {
            return owner.getFeetBlockState().getCollisionShape(owner.level(), owner.blockPosition()).isEmpty() && HelperMethods.RANDOM.nextInt(5) != 0;
        }
        return owner.fallDistance > 2.0F && !owner.isInFluidType();
    }

    @Override
    public void run(LivingEntity owner) {
        super.run(owner);

        owner.resetFallDistance();
    }

    @Override
    public List<EntityType<?>> getTypes() {
        return List.of(JJKEntities.EMBER_INSECT_FLIGHT.get());
    }

    @Override
    public boolean isTenShadows() {
        return false;
    }

    @Override
    protected EmberInsectFlightEntity summon(LivingEntity owner) {
        return new EmberInsectFlightEntity(owner);
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 0.1F;
    }

    @Override
    public boolean display() {
        return false;
    }
}
