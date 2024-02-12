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

        IJujutsuCapability jujutsuCap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (jujutsuCap == null) return;

        ISorcererData data = jujutsuCap.getSorcererData();
        
        data.useTransfiguredSouls(this.getSoulCost());
    }

    @Override
    public boolean isValid(LivingEntity owner) {
        IJujutsuCapability jujutsuCap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (jujutsuCap == null) return false;

        ISorcererData data = jujutsuCap.getSorcererData();

        if (data.getTransfiguredSouls() < this.getSoulCost()) return false;

        return super.isValid(owner);
    }
}
