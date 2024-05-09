package radon.jujutsu_kaisen.ability.ten_shadows.summon;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.Summon;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.effect.registry.JJKEffects;
import radon.jujutsu_kaisen.entity.registry.JJKEntities;
import radon.jujutsu_kaisen.entity.ten_shadows.RabbitEscapeEntity;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.List;

public class RabbitEscape extends Summon<RabbitEscapeEntity> {
    public RabbitEscape() {
        super(RabbitEscapeEntity.class);
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
    public List<EntityType<?>> getTypes() {
        return List.of(JJKEntities.RABBIT_ESCAPE.get());
    }

    @Override
    public void run(LivingEntity owner) {
        super.run(owner);

        owner.addEffect(new MobEffectInstance(JJKEffects.INVISIBILITY, 3 * 20, 0, false, false, false));
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return this.isTamed(owner) ? ActivationType.TOGGLED : ActivationType.INSTANT;
    }

    @Override
    protected RabbitEscapeEntity summon(LivingEntity owner) {
        return new RabbitEscapeEntity(owner, this.isTamed(owner));
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
    public float getCost(LivingEntity owner) {
        return this.isTamed(owner) ? 0.1F : 10.0F;
    }
}
