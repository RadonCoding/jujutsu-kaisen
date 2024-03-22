package radon.jujutsu_kaisen.ability.curse_manipulation;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.base.IAttack;
import radon.jujutsu_kaisen.ability.base.IChanneled;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.ICharged;
import radon.jujutsu_kaisen.ability.base.IDomainAttack;
import radon.jujutsu_kaisen.ability.base.IDurationable;
import radon.jujutsu_kaisen.ability.base.ITenShadowsAttack;
import radon.jujutsu_kaisen.ability.base.IToggled;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.curse_manipulation.AbsorbedCurse;
import radon.jujutsu_kaisen.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.item.CursedSpiritOrbItem;
import radon.jujutsu_kaisen.item.JJKItems;
import radon.jujutsu_kaisen.util.DamageUtil;
import radon.jujutsu_kaisen.util.EntityUtil;

public class CurseAbsorption extends Ability implements IToggled {
    @Override
    public boolean isScalable(LivingEntity owner) {
        return false;
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        if (target == null) return false;

        IJujutsuCapability cap = target.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return false;

        ISorcererData data = cap.getSorcererData();

        return data.getType() == JujutsuType.CURSE;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.TOGGLED;
    }

    private static boolean canAbsorb(LivingEntity owner, LivingEntity target) {
        if (target.isRemoved()) return false;

        IJujutsuCapability ownerCap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (ownerCap == null) return false;

        ISorcererData ownerData = ownerCap.getSorcererData();

        IJujutsuCapability targetCap = target.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (targetCap == null) return false;

        ISorcererData targetData = targetCap.getSorcererData();

        if (ownerData == null || targetData == null) return false;

        return (targetData.getType() == JujutsuType.CURSE && (!(target instanceof TamableAnimal tamable) || !tamable.isTame())) &&
                (ownerData.getExperience() / targetData.getExperience() >= 2 || target.isDeadOrDying());
    }

    @Override
    public void run(LivingEntity owner) {

    }

    @Override
    public float getCost(LivingEntity owner) {
        return 0;
    }

    @Override
    public void onEnabled(LivingEntity owner) {

    }

    @Override
    public void onDisabled(LivingEntity owner) {

    }

    private static void check(LivingEntity victim, DamageSource source) {
        if (victim.level().isClientSide) return;

        if (!DamageUtil.isMelee(source)) return;

        if (!(source.getEntity() instanceof LivingEntity attacker)) return;

        if (!canAbsorb(attacker, victim)) return;

        IJujutsuCapability attackerCap = attacker.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (attackerCap == null) return;

        IAbilityData attackerData = attackerCap.getAbilityData();

        if (!attackerData.hasToggled(JJKAbilities.CURSE_ABSORPTION.get())) return;

        IJujutsuCapability victimCap = victim.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (victimCap == null) return;

        ISorcererData victimData = victimCap.getSorcererData();

        attacker.swing(InteractionHand.MAIN_HAND, true);

        ItemStack stack = new ItemStack(JJKItems.CURSED_SPIRIT_ORB.get());

        if (victim instanceof Player player) {
            CursedSpiritOrbItem.setAbsorbed(stack, new AbsorbedCurse(victim.getName(), victim.getType(), victimData.serializeNBT(), player.getGameProfile()));
        } else {
            CursedSpiritOrbItem.setAbsorbed(stack, new AbsorbedCurse(victim.getName(), victim.getType(), victimData.serializeNBT()));
        }

        if (attacker instanceof Player player) {
            player.addItem(stack);
        } else {
            attacker.setItemSlot(EquipmentSlot.MAINHAND, stack);
        }
        EntityUtil.makePoofParticles(victim);

        if (!(victim instanceof Player)) {
            victim.discard();
        } else {
            victim.kill();
        }
    }

    @Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void onLivingDamage(LivingDamageEvent event) {
            check(event.getEntity(), event.getSource());
        }

        @SubscribeEvent
        public static void onLivingDeath(LivingDeathEvent event) {
            check(event.getEntity(), event.getSource());
        }
    }
}
