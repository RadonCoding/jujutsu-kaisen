package radon.jujutsu_kaisen.ability.idle_transfiguration;

import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.dismantle_and_cleave.Cleave;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.data.sorcerer.Trait;
import radon.jujutsu_kaisen.client.visual.ClientVisualHandler;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.item.JJKItems;
import radon.jujutsu_kaisen.util.DamageUtil;
import radon.jujutsu_kaisen.util.EntityUtil;
import radon.jujutsu_kaisen.util.RotationUtil;

public class IdleTransfiguration extends Ability implements Ability.IToggled, Ability.IAttack {
    public static final double RANGE = 32.0D;

    @Override
    public boolean isScalable(LivingEntity owner) {
        return false;
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return target != null;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return ActivationType.TOGGLED;

        ISorcererData data = cap.getSorcererData();
        return data.hasToggled(JJKAbilities.SELF_EMBODIMENT_OF_PERFECTION.get()) ? ActivationType.INSTANT : ActivationType.TOGGLED;
    }

    public static float calculateStrength(LivingEntity entity) {
        float strength = entity.getHealth();

        if (entity.level().isClientSide) {
            ClientVisualHandler.ClientData client = ClientVisualHandler.get(entity);

            if (client != null) {
                strength += client.experience * 0.1F;
            }
        } else {
            IJujutsuCapability cap = entity.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap != null) {
                ISorcererData data = cap.getSorcererData();
                strength += data.getExperience() * 0.1F;
            }
        }
        return strength;
    }

    public static @Nullable LivingEntity getTarget(LivingEntity owner) {
        LivingEntity result = null;

        if (RotationUtil.getLookAtHit(owner, RANGE) instanceof EntityHitResult hit && hit.getEntity() instanceof LivingEntity target) {
            result = target;
        }
        return result;
    }

    public static boolean checkSukuna(LivingEntity owner, LivingEntity target) {
        IJujutsuCapability ownercap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (ownercap == null) return false;

        ISorcererData ownerData = ownercap.getSorcererData();

        IJujutsuCapability targetcap = target.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (targetcap == null) return false;

        ISorcererData targetData = targetcap.getSorcererData();

        if (!targetData.hasTrait(Trait.VESSEL) || targetData.getFingers() == 0) return false;

        float experience = targetData.getFingers() * ((SorcererGrade.SPECIAL_GRADE.getRequiredExperience() * 4.0F) / 20);

        if (experience <= ownerData.getExperience()) return false;

        Cleave.perform(target, owner, null, JJKDamageSources.soulAttack(owner));

        return true;
    }

    private void run(LivingEntity owner, LivingEntity target) {
        if (checkSukuna(owner, target)) return;

        MobEffectInstance existing = target.getEffect(JJKEffects.TRANSFIGURED_SOUL.get());

        int amplifier = 0;

        if (existing != null) {
            amplifier = existing.getAmplifier() + 1;
        }

        MobEffectInstance instance = new MobEffectInstance(JJKEffects.TRANSFIGURED_SOUL.get(), 60 * 20, amplifier, false, true, true);
        target.addEffect(instance);

        if (!owner.level().isClientSide) {
            PacketDistributor.TRACKING_ENTITY.with(target).send(new ClientboundUpdateMobEffectPacket(target.getId(), instance));
        }

        float attackerStrength = IdleTransfiguration.calculateStrength(owner);
        float victimStrength = IdleTransfiguration.calculateStrength(target);

        int required = Math.round((victimStrength / attackerStrength) * 2);

        if (amplifier >= required) {
            if ((target instanceof Mob && !(target instanceof Monster)) || target instanceof Player) {
                absorb(owner, target);
            }
        }
    }

    @Override
    public void run(LivingEntity owner) {
        if (this.getActivationType(owner) == ActivationType.INSTANT) {
            owner.swing(InteractionHand.MAIN_HAND);

            if (owner.level().isClientSide) return;

            LivingEntity target = getTarget(owner);

            if (target == null) return;

            this.run(owner, target);
        }
    }

    @Override
    public Status isTriggerable(LivingEntity owner) {
        if (this.getActivationType(owner) == ActivationType.INSTANT) {
            LivingEntity target = getTarget(owner);

            if (target == null) {
                return Status.FAILURE;
            }
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
        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        ISorcererData data = cap.getSorcererData();

        if (data.hasToggled(JJKAbilities.SOUL_DECIMATION.get())) {
            data.toggle(JJKAbilities.SOUL_DECIMATION.get());
        }
    }

    @Override
    public void onDisabled(LivingEntity owner) {

    }

    public static void absorb(LivingEntity owner, LivingEntity target) {
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
