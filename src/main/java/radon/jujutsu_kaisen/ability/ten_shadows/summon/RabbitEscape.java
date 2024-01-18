package radon.jujutsu_kaisen.ability.ten_shadows.summon;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.Summon;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.ten_shadows.RabbitEscapeEntity;

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
        return target != null && !target.isDeadOrDying() && owner.tickCount - owner.getLastHurtByMobTimestamp() < 20;
    }

    @Override
    public List<EntityType<?>> getTypes() {
        return List.of(JJKEntities.RABBIT_ESCAPE.get());
    }

    @Override
    public void run(LivingEntity owner) {
        super.run(owner);

        owner.addEffect(new MobEffectInstance(JJKEffects.INVISIBILITY.get(), 3 * 20, 0, false, false, false));
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
