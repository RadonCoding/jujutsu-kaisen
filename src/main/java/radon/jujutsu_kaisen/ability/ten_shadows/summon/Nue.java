package radon.jujutsu_kaisen.ability.ten_shadows.summon;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.Summon;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.entity.registry.JJKEntities;
import radon.jujutsu_kaisen.entity.ten_shadows.NueEntity;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.List;

public class Nue extends Summon<NueEntity> {
    public Nue() {
        super(NueEntity.class);
    }

    @Override
    public boolean isScalable(LivingEntity owner) {
        return false;
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        if (!this.isTamed(owner)) return false;
        if (target == null || target.isDeadOrDying()) return false;

        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return false;

        IAbilityData data = cap.getAbilityData();

        if (data.hasToggled(this)) {
            return owner.level().getGameTime() % 20 != 0 || HelperMethods.RANDOM.nextInt(20) != 0;
        }
        return owner.level().getGameTime() % 20 == 0 && HelperMethods.RANDOM.nextInt(20) == 0;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return this.isTamed(owner) ? ActivationType.TOGGLED : ActivationType.INSTANT;
    }

    @Override
    public float getCost(LivingEntity owner) {
        return this.isTamed(owner) ? 0.15F : 100.0F;
    }

    @Override
    public List<EntityType<?>> getTypes() {
        return List.of(JJKEntities.NUE.get());
    }

    @Override
    protected NueEntity summon(LivingEntity owner) {
        return new NueEntity(owner, this.isTamed(owner));
    }

    @Override
    public boolean canDie() {
        return true;
    }

    @Override
    public boolean isTenShadows() {
        return true;
    }

    @Override
    protected boolean canTame() {
        return true;
    }

    @Override
    public int getCooldown() {
        return 15 * 20;
    }
}
