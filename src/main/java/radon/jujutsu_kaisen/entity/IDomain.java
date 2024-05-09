package radon.jujutsu_kaisen.entity;

import radon.jujutsu_kaisen.ability.DomainExpansion;

public interface IDomain extends IBarrier {
    float getScale();

    default float getStrength() {
        return IBarrier.super.getStrength();
    }
}
