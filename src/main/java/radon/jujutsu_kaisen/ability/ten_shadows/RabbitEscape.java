package radon.jujutsu_kaisen.ability.ten_shadows;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.misc.Summon;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.ten_shadows.RabbitEscapeEntity;
import radon.jujutsu_kaisen.util.HelperMethods;

public class RabbitEscape extends Summon<RabbitEscapeEntity> {
    public RabbitEscape() {
        super(RabbitEscapeEntity.class);
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return target != null && HelperMethods.RANDOM.nextInt(3) == 0 && owner.getLastHurtByMobTimestamp() - owner.tickCount == 0;
    }

    @Override
    public EntityType<RabbitEscapeEntity> getType() {
        return JJKEntities.RABBIT_ESCAPE.get();
    }

    @Override
    protected int getCount() {
        return 64;
    }

    @Override
    public void run(LivingEntity owner) {
        super.run(owner);

        owner.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 3 * 20, 0, false, false, false));
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    @Override
    protected RabbitEscapeEntity summon(int index, LivingEntity owner) {
        return new RabbitEscapeEntity(owner);
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 50.0F;
    }

    @Override
    public boolean isTechnique() {
        return true;
    }
}
