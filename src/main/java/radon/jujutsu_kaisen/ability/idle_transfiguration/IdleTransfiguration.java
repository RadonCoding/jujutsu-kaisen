package radon.jujutsu_kaisen.ability.idle_transfiguration;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.ITransformation;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.AbsorbedCurse;
import radon.jujutsu_kaisen.capability.data.sorcerer.TenShadowsMode;
import radon.jujutsu_kaisen.client.visual.ClientVisualHandler;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.TransfiguredSoulEntity;
import radon.jujutsu_kaisen.item.CursedSpiritOrbItem;
import radon.jujutsu_kaisen.item.JJKItems;
import radon.jujutsu_kaisen.item.TransfiguredSoulItem;
import radon.jujutsu_kaisen.util.EntityUtil;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.ArrayList;

public class IdleTransfiguration extends Ability implements Ability.IToggled, Ability.IAttack {
    @Override
    public boolean isScalable(LivingEntity owner) {
        return false;
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return false;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.TOGGLED;
    }

    public static float calculateStrength(LivingEntity entity) {
        float strength = entity.getHealth();

        if (entity.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
            if (entity.level().isClientSide) {
                ClientVisualHandler.ClientData data = ClientVisualHandler.get(entity);

                if (data != null) {
                    strength += data.experience * 0.1F;
                }
            } else {
                ISorcererData cap = entity.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
                strength += cap.getExperience() * 0.1F;
            }
        }
        return strength;
    }

    @Override
    public void run(LivingEntity owner) {

    }

    @Override
    public Status isTriggerable(LivingEntity owner) {
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        if (cap.hasToggled(JJKAbilities.SOUL_DECIMATION.get())) {
            cap.toggle(JJKAbilities.SOUL_DECIMATION.get());
        }
        return super.isTriggerable(owner);
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 10.0F;
    }

    @Override
    public int getCooldown() {
        return 15 * 20;
    }

    @Override
    public void onEnabled(LivingEntity owner) {

    }

    @Override
    public void onDisabled(LivingEntity owner) {

    }

    @Override
    public boolean attack(DamageSource source, LivingEntity owner, LivingEntity target) {
        if (owner.level().isClientSide) return false;
        if (!HelperMethods.isMelee(source)) return false;
        if (!owner.getMainHandItem().isEmpty()) return false;

        MobEffectInstance existing = target.getEffect(JJKEffects.TRANSFIGURED_SOUL.get());

        int amplifier = 0;

        if (existing != null) {
            amplifier = existing.getAmplifier() + 1;
        }

        float attackerStrength = IdleTransfiguration.calculateStrength(owner);
        float victimStrength = IdleTransfiguration.calculateStrength(target);

        int required = Math.round((victimStrength / attackerStrength) * 2);

        if (amplifier >= required) {
            ItemStack stack = new ItemStack(JJKItems.TRANSFIGURED_SOUL.get());

            if (owner instanceof Player player) {
                player.addItem(stack);
            } else {
                owner.setItemSlot(EquipmentSlot.MAINHAND, stack);
            }

            EntityUtil.makePoofParticles(target);

            if (!(target instanceof Player)) {
                target.discard();
            } else {
                target.kill();
            }
        } else {
            MobEffectInstance instance = new MobEffectInstance(JJKEffects.TRANSFIGURED_SOUL.get(), 60 * 20, amplifier, false, true, true);
            target.addEffect(instance);

            if (!owner.level().isClientSide) {
                PacketDistributor.TRACKING_ENTITY.with(() -> target).send(new ClientboundUpdateMobEffectPacket(target.getId(), instance));
            }
        }
        return true;
    }
}
