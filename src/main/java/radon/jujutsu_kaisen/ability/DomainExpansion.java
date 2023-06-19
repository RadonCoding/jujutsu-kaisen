package radon.jujutsu_kaisen.ability;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import radon.jujutsu_kaisen.entity.DomainExpansionEntity;

public abstract class DomainExpansion extends Ability {
    public static final int BURNOUT = 10 * 20;

    @Override
    public ActivationType getActivationType() {
        return ActivationType.INSTANT;
    }

    protected abstract int getRadius();
    protected abstract int getDuration();
    protected abstract Block getBlock();

    public abstract void onHit(Entity entity);

    private void createBarrier(LivingEntity owner) {
        int radius = this.getRadius();
        int duration = this.getDuration();
        Block block = this.getBlock();

        DomainExpansionEntity domain = new DomainExpansionEntity(owner, this, block.defaultBlockState(), radius, duration);
        owner.level.addFreshEntity(domain);

        owner.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, duration, 3,
                false, false, false));
        owner.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, duration, 4,
                false, false, false));
    }

    @Override
    public void run(LivingEntity owner) {
        this.createBarrier(owner);
    }
}
