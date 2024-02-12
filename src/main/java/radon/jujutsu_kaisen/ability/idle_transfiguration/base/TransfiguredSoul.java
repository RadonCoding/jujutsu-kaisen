package radon.jujutsu_kaisen.ability.idle_transfiguration.base;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import radon.jujutsu_kaisen.ability.base.Summon;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;

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

        ISorcererData data = owner.getData(JJKAttachmentTypes.SORCERER);
        
        data.useTransfiguredSouls(this.getSoulCost());
    }

    @Override
    public boolean isValid(LivingEntity owner) {
        ISorcererData data = owner.getData(JJKAttachmentTypes.SORCERER);
        
        if (data == null) return false;

        if (data.getTransfiguredSouls() < this.getSoulCost()) return false;

        return super.isValid(owner);
    }
}
