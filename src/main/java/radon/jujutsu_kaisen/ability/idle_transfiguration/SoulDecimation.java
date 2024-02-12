package radon.jujutsu_kaisen.ability.idle_transfiguration;

import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.entity.idle_transfiguration.base.TransfiguredSoulEntity;
import radon.jujutsu_kaisen.util.DamageUtil;

public class SoulDecimation extends Ability implements Ability.IToggled, Ability.IAttack {
    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return target != null;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return ActivationType.TOGGLED;

        ISorcererData data = cap.getSorcererData();

        if (data == null) return ActivationType.TOGGLED;

        return data.hasToggled(JJKAbilities.SELF_EMBODIMENT_OF_PERFECTION.get()) ? ActivationType.INSTANT : ActivationType.TOGGLED;
    }

    @Override
    public boolean isScalable(LivingEntity owner) {
        return false;
    }

    private void run(LivingEntity owner, LivingEntity target) {
        if (IdleTransfiguration.checkSukuna(owner, target)) return;

        MobEffectInstance existing = target.getEffect(JJKEffects.TRANSFIGURED_SOUL.get());

        int amplifier = 0;

        if (existing != null) {
            amplifier = existing.getAmplifier() + 2;
        }

        float attackerStrength = IdleTransfiguration.calculateStrength(owner);
        float victimStrength = IdleTransfiguration.calculateStrength(target);

        int required = Math.round((victimStrength / attackerStrength) * 2);

        if (target instanceof TransfiguredSoulEntity || amplifier >= required) {
            target.hurt(JJKDamageSources.soulAttack(owner), target.getMaxHealth());
        } else {
            MobEffectInstance instance = new MobEffectInstance(JJKEffects.TRANSFIGURED_SOUL.get(), 60 * 20, amplifier, false, true, true);
            target.addEffect(instance);

            if (!owner.level().isClientSide) {
                PacketDistributor.TRACKING_ENTITY.with(target).send(new ClientboundUpdateMobEffectPacket(target.getId(), instance));
            }
        }
    }

    @Override
    public void run(LivingEntity owner) {
        if (this.getActivationType(owner) == ActivationType.INSTANT) {
            owner.swing(InteractionHand.MAIN_HAND);

            if (owner.level().isClientSide) return;

            LivingEntity target = IdleTransfiguration.getTarget(owner);

            if (target == null) return;

            this.run(owner, target);
        }
    }

    @Override
    public Status isTriggerable(LivingEntity owner) {
        if (this.getActivationType(owner) == ActivationType.INSTANT) {
            LivingEntity target = IdleTransfiguration.getTarget(owner);

            if (target == null) {
                return Status.FAILURE;
            }
        }
        return super.isTriggerable(owner);
    }

    @Override
    public float getCost(LivingEntity owner) {
        return JJKAbilities.IDLE_TRANSFIGURATION.get().getCost(owner) * 2;
    }

    @Override
    public void onEnabled(LivingEntity owner) {
        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        ISorcererData data = cap.getSorcererData();
        

        if (data.hasToggled(JJKAbilities.IDLE_TRANSFIGURATION.get())) {
            data.toggle(JJKAbilities.IDLE_TRANSFIGURATION.get());
        }
    }

    @Override
    public void onDisabled(LivingEntity owner) {

    }

    @Override
    public boolean attack(DamageSource source, LivingEntity owner, LivingEntity target) {
        if (owner.level().isClientSide) return false;
        if (!DamageUtil.isMelee(source)) return false;
        if (!owner.getMainHandItem().isEmpty()) return false;

        this.run(owner, target);

        return true;
    }

    @Override
    public MenuType getMenuType(LivingEntity owner) {
        return this.getActivationType(owner) == ActivationType.INSTANT ? MenuType.MELEE : MenuType.RADIAL;
    }
}
