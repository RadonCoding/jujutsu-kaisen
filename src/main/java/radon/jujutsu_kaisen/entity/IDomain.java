package radon.jujutsu_kaisen.entity;


import net.minecraft.world.entity.LivingEntity;

public interface IDomain extends IBarrier {
    void setInstant(boolean instant);

    boolean isInstant();

    default float getStrength() {
        return IBarrier.super.getStrength();
    }

    void performAttack();

    boolean canAttack();
}
