package radon.jujutsu_kaisen.ability.idle_transfiguration.base;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import radon.jujutsu_kaisen.ability.base.Summon;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;

public abstract class TransfiguredSoul<T extends Entity> extends Summon<T> implements ITransfiguredSoul {
    public TransfiguredSoul(Class<T> clazz) {
        super(clazz);
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    @Override
    public void run(LivingEntity owner) {
        super.run(owner);

        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        ISorcererData data = cap.getSorcererData();
        data.useTransfiguredSouls(this.getSoulCost());
    }

    @Override
    public boolean isValid(LivingEntity owner) {
        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return false;

        ISorcererData data = cap.getSorcererData();

        if (data.getTransfiguredSouls() < this.getSoulCost()) return false;

        return super.isValid(owner);
    }
}
