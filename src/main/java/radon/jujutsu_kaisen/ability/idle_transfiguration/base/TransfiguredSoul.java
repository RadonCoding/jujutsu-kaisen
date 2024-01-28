package radon.jujutsu_kaisen.ability.idle_transfiguration.base;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.ITransfiguredSoul;
import radon.jujutsu_kaisen.ability.base.Summon;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;

public abstract class TransfiguredSoul<T extends Entity> extends Summon<T> implements ITransfiguredSoul {
    public TransfiguredSoul(Class<T> clazz) {
        super(clazz);
    }

    @Override
    public void onEnabled(LivingEntity owner) {
        super.onEnabled(owner);

        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        cap.useTransfiguredSouls(this.getSoulCost());
    }

    @Override
    public boolean isValid(LivingEntity owner) {
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        if (cap.getTransfiguredSouls() < this.getSoulCost()) return false;

        return super.isValid(owner);
    }
}
