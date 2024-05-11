package radon.jujutsu_kaisen.item.cursed_tool;

import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.AbilityStopEvent;
import radon.jujutsu_kaisen.ability.AbilityTriggerEvent;
import radon.jujutsu_kaisen.cursed_technique.registry.JJKCursedTechniques;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.entity.DomainExpansionEntity;
import radon.jujutsu_kaisen.item.registry.JJKDataComponentTypes;
import radon.jujutsu_kaisen.util.CuriosUtil;

import java.util.*;

public class MimicryKatanaItem extends KatanaItem {
    public MimicryKatanaItem(Tier pTier, Properties pProperties) {
        super(pTier, pProperties);
    }

    @Override
    public boolean onDroppedByPlayer(@NotNull ItemStack item, @NotNull Player player) {
        return false;
    }

    @Override
    public void inventoryTick(@NotNull ItemStack pStack, @NotNull Level pLevel, @NotNull Entity pEntity, int pSlotId, boolean pIsSelected) {
        super.inventoryTick(pStack, pLevel, pEntity, pSlotId, pIsSelected);

        IJujutsuCapability cap = pEntity.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        ISorcererData data = cap.getSorcererData();

        if (!data.hasSummonOfClass(DomainExpansionEntity.class)) {
            pStack.shrink(1);
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @NotNull TooltipContext pContext, @NotNull List<Component> pTooltipComponents, @NotNull TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pContext, pTooltipComponents, pIsAdvanced);

        CursedTechnique technique = pStack.get(JJKDataComponentTypes.CURSED_TECHNIQUE);

        if (technique == null) return;

        pTooltipComponents.add(Component.translatable(String.format("item.%s.mimicry_katana.technique", JujutsuKaisen.MOD_ID), technique.getName().copy().withStyle(ChatFormatting.DARK_PURPLE)));
    }

    @EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void onAbilityTrigger(AbilityTriggerEvent.Post event) {
            LivingEntity owner = event.getEntity();

            Ability ability = event.getAbility();

            CursedTechnique technique = JJKCursedTechniques.getTechnique(ability);

            if (technique == null) return;

            if (ability.getActivationType(owner) == Ability.ActivationType.INSTANT) {
                List<ItemStack> stacks = new ArrayList<>();
                stacks.add(owner.getItemInHand(InteractionHand.MAIN_HAND));
                stacks.addAll(CuriosUtil.findSlots(owner, owner.getMainArm() == HumanoidArm.RIGHT ? "right_hand" : "left_hand"));
                stacks.removeIf(ItemStack::isEmpty);

                for (ItemStack stack : stacks) {
                    if (!(stack.getItem() instanceof MimicryKatanaItem)) continue;

                    if (stack.get(JJKDataComponentTypes.CURSED_TECHNIQUE) == technique) {
                        stack.shrink(1);
                    }
                }
            }
        }

        @SubscribeEvent
        public static void onAbilityStop(AbilityStopEvent event) {
            Ability ability = event.getAbility();

            CursedTechnique technique = JJKCursedTechniques.getTechnique(ability);

            if (technique == null) return;

            LivingEntity owner = event.getEntity();

            List<ItemStack> stacks = new ArrayList<>();
            stacks.add(owner.getItemInHand(InteractionHand.MAIN_HAND));
            stacks.addAll(CuriosUtil.findSlots(owner, owner.getMainArm() == HumanoidArm.RIGHT ? "right_hand" : "left_hand"));
            stacks.removeIf(ItemStack::isEmpty);

            for (ItemStack stack : stacks) {
                if (!(stack.getItem() instanceof MimicryKatanaItem)) continue;

                if (stack.get(JJKDataComponentTypes.CURSED_TECHNIQUE) == technique) {
                    stack.shrink(1);
                }
            }
        }
    }
}