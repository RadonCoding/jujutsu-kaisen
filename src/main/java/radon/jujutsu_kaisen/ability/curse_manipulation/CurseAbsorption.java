package radon.jujutsu_kaisen.ability.curse_manipulation;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.AbsorbedCurse;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.item.CursedSpiritOrbItem;
import radon.jujutsu_kaisen.item.JJKItems;
import radon.jujutsu_kaisen.util.EntityUtil;
import radon.jujutsu_kaisen.util.HelperMethods;

public class CurseAbsorption extends Ability implements Ability.IToggled {
    @Override
    public boolean isScalable(LivingEntity owner) {
        return false;
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        if (target == null) return false;
        if (!target.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return false;
        ISorcererData cap = target.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        return cap.getType() == JujutsuType.CURSE;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.TOGGLED;
    }

    private static boolean canAbsorb(LivingEntity owner, LivingEntity target) {
        if (!owner.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return false;
        if (!target.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return false;

        ISorcererData ownerCap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        ISorcererData targetCap = target.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        return (targetCap.getType() == JujutsuType.CURSE && (!(target instanceof TamableAnimal tamable) || !tamable.isTame())) &&
                (ownerCap.getExperience() / targetCap.getExperience() >= 2 || target.isDeadOrDying());
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
        if (!HelperMethods.isMelee(source)) return;

        if (!(source.getEntity() instanceof LivingEntity attacker)) return;

        if (!canAbsorb(attacker, victim)) return;

        if (!JJKAbilities.hasToggled(attacker, JJKAbilities.CURSE_ABSORPTION.get())) return;

        ISorcererData victimCap = victim.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        attacker.swing(InteractionHand.MAIN_HAND, true);

        ItemStack stack = new ItemStack(JJKItems.CURSED_SPIRIT_ORB.get());

        if (victim instanceof Player player) {
            CursedSpiritOrbItem.setAbsorbed(stack, new AbsorbedCurse(victim.getName(), victim.getType(), victimCap.serializeNBT(), player.getGameProfile()));
        } else {
            CursedSpiritOrbItem.setAbsorbed(stack, new AbsorbedCurse(victim.getName(), victim.getType(), victimCap.serializeNBT()));
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
    public static class CurseAbsorptionForgeEvents {
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
