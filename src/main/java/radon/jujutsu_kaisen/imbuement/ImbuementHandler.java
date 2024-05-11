package radon.jujutsu_kaisen.imbuement;

import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingAttackEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.AbilityTriggerEvent;
import radon.jujutsu_kaisen.ability.registry.JJKAbilities;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.IImbuement;
import radon.jujutsu_kaisen.cursed_technique.registry.JJKCursedTechniques;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.entity.projectile.ThrownChainProjectile;
import net.neoforged.neoforge.network.PacketDistributor;
import radon.jujutsu_kaisen.item.registry.JJKDataComponentTypes;
import radon.jujutsu_kaisen.network.packet.s2c.SyncAbilityDataS2CPacket;
import radon.jujutsu_kaisen.util.CuriosUtil;
import radon.jujutsu_kaisen.util.DamageUtil;

import java.util.*;

public class ImbuementHandler {
    public static Set<Ability> getFullImbuements(ItemStack stack) {
        Map<Ability, Integer> amounts = getImbuementAmounts(stack);

        Iterator<Map.Entry<Ability, Integer>> iter = amounts.entrySet().iterator();

        while (iter.hasNext()) {
            Map.Entry<Ability, Integer> entry = iter.next();

            Ability technique = entry.getKey();

            int amount = getImbuementAmount(stack, technique);

            if (amount < ConfigHolder.SERVER.requiredImbuementAmount.get()) iter.remove();
        }
        return amounts.keySet();
    }

    public static Map<Ability, Integer> getImbuementAmounts(ItemStack stack) {
        Map<Ability, Integer> amounts = new HashMap<>();

        for (DeferredHolder<Ability, ? extends Ability> entry : JJKAbilities.ABILITIES.getEntries()) {
            Ability ability = entry.get();

            int amount = getImbuementAmount(stack, ability);

            if (amount == 0) continue;

            amounts.put(ability, amount);
        }
        return amounts;
    }

    public static int getImbuementAmount(ItemStack stack, Ability ability) {
        Map<Ability, Integer> imbuements = stack.get(JJKDataComponentTypes.IMBUEMENTS);

        if (imbuements == null) return 0;

        return imbuements.get(ability);
    }

    public static void increaseImbuementAmount(ItemStack stack, Ability ability, int amount) {
        if (!stack.has(JJKDataComponentTypes.IMBUEMENTS)) stack.set(JJKDataComponentTypes.IMBUEMENTS, new HashMap<>());

        Map<Ability, Integer> imbuements = stack.get(JJKDataComponentTypes.IMBUEMENTS);

        if (imbuements == null) return;

        imbuements.put(ability, Math.min(ConfigHolder.SERVER.requiredImbuementAmount.get(),
                imbuements.getOrDefault(ability, 0) + amount));
    }

    @EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void onAbilityTrigger(AbilityTriggerEvent.Post event) {
            LivingEntity owner = event.getEntity();

            Ability ability = event.getAbility();

            CursedTechnique technique = JJKCursedTechniques.getTechnique(ability);

            if (technique == null) return;

            if (ability instanceof IImbuement && ability.isTechnique() && ability.getCost(owner) > 0.0F) {
                int amount = Math.round(Math.max(1, ability.getCost(owner) / 100.0F));

                ItemStack held = owner.getItemInHand(InteractionHand.MAIN_HAND);

                if (held.isEmpty()) return;

                if (!(held.getItem() instanceof SwordItem)) return;

                increaseImbuementAmount(held, ability, amount);
            }
        }

        @SubscribeEvent
        public static void onLivingAttack(LivingAttackEvent event) {
            DamageSource source = event.getSource();

            if (!(source.getEntity() instanceof LivingEntity attacker)) return;

            IJujutsuCapability cap = attacker.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap == null) return;

            IAbilityData data = cap.getAbilityData();

            LivingEntity victim = event.getEntity();

            if (victim.level().isClientSide) return;

            List<ItemStack> stacks = new ArrayList<>();

            if (source.getDirectEntity() instanceof ThrownChainProjectile chain) {
                stacks.add(chain.getStack());
            } else {
                stacks.add(attacker.getItemInHand(InteractionHand.MAIN_HAND));
                stacks.addAll(CuriosUtil.findSlots(attacker, attacker.getMainArm() == HumanoidArm.RIGHT ? "right_hand" : "left_hand"));
            }
            stacks.removeIf(ItemStack::isEmpty);

            if (!DamageUtil.isMelee(source) && !(source.getDirectEntity() instanceof ThrownChainProjectile)) return;

            for (ItemStack stack : stacks) {
                for (Ability ability : ImbuementHandler.getFullImbuements(stack)) {
                    if (!data.isCooldownDone(ability)) continue;

                    ((IImbuement) ability).hit(attacker, victim);

                    if (attacker instanceof Player player && player.getAbilities().instabuild) continue;

                    if (ability.getRealCooldown(attacker) == 0) continue;

                    data.addCooldown(ability);

                    if (attacker instanceof ServerPlayer player) {
                        PacketDistributor.sendToPlayer(player, new SyncAbilityDataS2CPacket(data.serializeNBT(player.registryAccess())));
                    }
                }
            }
        }
    }
}
