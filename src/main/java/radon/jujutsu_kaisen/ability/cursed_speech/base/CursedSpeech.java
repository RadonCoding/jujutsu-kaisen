package radon.jujutsu_kaisen.ability.cursed_speech.base;

import net.minecraft.world.entity.LivingEntity;
import radon.jujutsu_kaisen.ability.base.IAttack;
import radon.jujutsu_kaisen.ability.base.IChanneled;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.ICharged;
import radon.jujutsu_kaisen.ability.base.IDomainAttack;
import radon.jujutsu_kaisen.ability.base.IDurationable;
import radon.jujutsu_kaisen.ability.base.ITenShadowsAttack;
import radon.jujutsu_kaisen.ability.base.IToggled;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.cursed_speech.ICursedSpeechData;

public abstract class CursedSpeech extends Ability implements ICursedSpeech {
    @Override
    public Status isTriggerable(LivingEntity owner) {
        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return Status.FAILURE;

        ICursedSpeechData data = cap.getCursedSpeechData();

        if (data.isThroatDamaged()) {
            return Status.THROAT;
        }
        return super.isTriggerable(owner);
    }

    @Override
    public void run(LivingEntity owner) {
        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        ICursedSpeechData data = cap.getCursedSpeechData();
        data.hurtThroat(this.getThroatDamage());
    }
}
