package radon.jujutsu_kaisen.event;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.entity.living.LivingHurtEvent;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.Trait;
import radon.jujutsu_kaisen.util.DamageUtil;
import radon.jujutsu_kaisen.util.HelperMethods;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.event.CurioCanEquipEvent;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import java.util.Optional;

@EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class CuriosEventHandler {
    @SubscribeEvent
    public static void onCuriosEquip(CurioCanEquipEvent event) {
        LivingEntity entity = event.getEntity();

        IJujutsuCapability cap = entity.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        ISorcererData data = cap.getSorcererData();

        if (!data.hasTrait(Trait.PERFECT_BODY)) return;

        if (event.getSlotContext().identifier().equals("right_hand") || event.getSlotContext().identifier().equals("left_hand")) {
            event.setResult(Event.Result.ALLOW);
        }
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        LivingEntity victim = event.getEntity();

        if (victim.level().isClientSide) return;

        DamageSource source = event.getSource();

        if (!(source.getEntity() instanceof LivingEntity attacker)) return;

        IJujutsuCapability cap = attacker.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        ISorcererData data = cap.getSorcererData();

        if (!data.hasTrait(Trait.PERFECT_BODY)) return;

        if (!DamageUtil.isMelee(source)) return;

        Optional<ICuriosItemHandler> optional = CuriosApi.getCuriosInventory(attacker);

        if (optional.isEmpty()) return;

        ICuriosItemHandler inventory = optional.get();

        Optional<SlotResult> rightHand = inventory.findCurio("right_hand", 0);
        Optional<SlotResult> leftHand = inventory.findCurio("left_hand", 0);

        ItemStack stack;

        if (attacker.getMainArm() == HumanoidArm.RIGHT) {
            stack = rightHand.isPresent() ? rightHand.get().stack() : ItemStack.EMPTY;
        } else {
            stack = leftHand.isPresent() ? leftHand.get().stack() : ItemStack.EMPTY;
        }

        event.setAmount(event.getAmount() + EnchantmentHelper.getDamageBonus(stack, victim.getType()));

        if (attacker instanceof Player player) {
            if (!attacker.level().isClientSide && !stack.isEmpty()) {
                ItemStack copy = stack.copy();
                stack.hurtEnemy(victim, player);

                if (stack.isEmpty()) {
                    EventHooks.onPlayerDestroyItem(player, copy, InteractionHand.MAIN_HAND);
                    player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
                }
            }
        }

        if (victim instanceof Player player) {
            ItemStack using = player.isUsingItem() ? player.getUseItem() : ItemStack.EMPTY;

            if (!stack.isEmpty() && !using.isEmpty() && stack.getItem() instanceof AxeItem && using.is(Items.SHIELD)) {
                float f = 0.25F + (float) EnchantmentHelper.getBlockEfficiency(attacker) * 0.05F;

                if (HelperMethods.RANDOM.nextFloat() < f) {
                    player.getCooldowns().addCooldown(Items.SHIELD, 100);
                    attacker.level().broadcastEntityEvent(player, (byte) 30);
                }
            }
        }
    }
}
