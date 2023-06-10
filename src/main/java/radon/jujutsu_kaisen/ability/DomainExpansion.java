package radon.jujutsu_kaisen.ability;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;

public abstract class DomainExpansion extends Ability {
    @Override
    public ActivationType getActivationType() {
        return ActivationType.INSTANT;
    }

    protected abstract int getRadius();
    protected abstract int getDuration();
    protected abstract Block getBlock();

    protected abstract void onHit(LivingEntity owner, Entity entity);

    private void createBarrier(LivingEntity owner) {
        /*int radius = this.getRadius();
        int duration = this.getDuration();
        Block block = this.getBlock();

        // Spawn the domain expansion which renders a sphere which is only visual
        // It also applies the domain effects and prevents entities from escaping
        DomainExpansionEntity domain = new DomainExpansionEntity(owner, radius, duration, block);
        owner.level.addEntity(domain);

        // Some example effects
        // owner.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, duration, 4));
        // owner.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, duration, 4));
        // owner.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, duration, 4));
        // owner.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, duration, 4));
        */
    }

    @Override
    public void run(LivingEntity owner) {
        this.createBarrier(owner);
    }
}
