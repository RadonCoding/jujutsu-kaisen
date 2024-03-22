package radon.jujutsu_kaisen.item.cursed_tool;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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
import net.neoforged.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.AbilityStopEvent;
import radon.jujutsu_kaisen.ability.AbilityTriggerEvent;
import radon.jujutsu_kaisen.ability.base.IAttack;
import radon.jujutsu_kaisen.ability.base.IChanneled;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.ICharged;
import radon.jujutsu_kaisen.ability.base.IDomainAttack;
import radon.jujutsu_kaisen.ability.base.IDurationable;
import radon.jujutsu_kaisen.ability.base.ITenShadowsAttack;
import radon.jujutsu_kaisen.ability.base.IToggled;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.cursed_technique.JJKCursedTechniques;
import radon.jujutsu_kaisen.cursed_technique.base.ICursedTechnique;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import radon.jujutsu_kaisen.util.CuriosUtil;

import java.util.*;

public class MimicryKatanaItem extends KatanaItem {
    public MimicryKatanaItem(Tier pTier, int pAttackDamageModifier, float pAttackSpeedModifier, Properties pProperties) {
        super(pTier, pAttackDamageModifier, pAttackSpeedModifier, pProperties);
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
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level pLevel, @NotNull List<Component> pTooltipComponents, @NotNull TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);

        ICursedTechnique technique = getTechnique(pStack);

        pTooltipComponents.add(Component.translatable(String.format("item.%s.mimicry_katana.technique", JujutsuKaisen.MOD_ID), technique.getName().copy().withStyle(ChatFormatting.DARK_PURPLE)));
    }

    public static ICursedTechnique getTechnique(ItemStack stack) {
        CompoundTag nbt = stack.getOrCreateTag();
        return JJKCursedTechniques.getValue(new ResourceLocation(nbt.getString("cursed_technique")));
    }

    public static void setTechnique(ItemStack stack, ICursedTechnique technique) {
        CompoundTag nbt = stack.getOrCreateTag();
        nbt.putString("cursed_technique", JJKCursedTechniques.getKey(technique).toString());
    }

    @Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void onAbilityTrigger(AbilityTriggerEvent.Post event) {
            LivingEntity owner = event.getEntity();

            Ability ability = event.getAbility();

            ICursedTechnique technique = JJKCursedTechniques.getTechnique(ability);

            if (technique == null) return;

            if (ability.getActivationType(owner) == Ability.ActivationType.INSTANT) {
                List<ItemStack> stacks = new ArrayList<>();
                stacks.add(owner.getItemInHand(InteractionHand.MAIN_HAND));
                stacks.addAll(CuriosUtil.findSlots(owner, owner.getMainArm() == HumanoidArm.RIGHT ? "right_hand" : "left_hand"));
                stacks.removeIf(ItemStack::isEmpty);

                for (ItemStack stack : stacks) {
                    if (!(stack.getItem() instanceof MimicryKatanaItem)) continue;

                    if (getTechnique(stack) == technique) {
                        stack.shrink(1);
                    }
                }
            }
        }

        @SubscribeEvent
        public static void onAbilityStop(AbilityStopEvent event) {
            Ability ability = event.getAbility();

            ICursedTechnique technique = JJKCursedTechniques.getTechnique(ability);

            if (technique == null) return;

            LivingEntity owner = event.getEntity();

            List<ItemStack> stacks = new ArrayList<>();
            stacks.add(owner.getItemInHand(InteractionHand.MAIN_HAND));
            stacks.addAll(CuriosUtil.findSlots(owner, owner.getMainArm() == HumanoidArm.RIGHT ? "right_hand" : "left_hand"));
            stacks.removeIf(ItemStack::isEmpty);

            for (ItemStack stack : stacks) {
                if (!(stack.getItem() instanceof MimicryKatanaItem)) continue;

                if (getTechnique(stack) == technique) {
                    stack.shrink(1);
                }
            }
        }
    }
}