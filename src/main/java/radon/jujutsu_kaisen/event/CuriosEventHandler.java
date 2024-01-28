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
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.util.HelperMethods;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.event.CurioEquipEvent;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import java.util.Optional;

@Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CuriosEventHandler {
    @SubscribeEvent
    public static void onCuriosEquip(CurioEquipEvent event) {
        if (!JJKAbilities.hasTrait(event.getEntity(), Trait.PERFECT_BODY)) return;

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

        if (!JJKAbilities.hasTrait(attacker, Trait.PERFECT_BODY)) return;

        if (!HelperMethods.isMelee(source)) return;

        LazyOptional<ICuriosItemHandler> optional = CuriosApi.getCuriosInventory(attacker);

        if (!optional.isPresent()) return;

        ICuriosItemHandler inventory = optional.resolve().orElseThrow();

        Optional<SlotResult> rightHand = inventory.findCurio("right_hand", 0);
        Optional<SlotResult> leftHand = inventory.findCurio("left_hand", 0);

        ItemStack stack;

        if (attacker.getMainArm() == HumanoidArm.RIGHT) {
            stack = rightHand.isPresent() ? rightHand.get().stack() : ItemStack.EMPTY;
        } else {
            stack = leftHand.isPresent() ? leftHand.get().stack() : ItemStack.EMPTY;
        }

        event.setAmount(event.getAmount() + EnchantmentHelper.getDamageBonus(stack, victim.getMobType()));

        if (attacker instanceof Player player) {
            if (!attacker.level().isClientSide && !stack.isEmpty()) {
                ItemStack copy = stack.copy();
                stack.hurtEnemy(victim, player);

                if (stack.isEmpty()) {
                    ForgeEventFactory.onPlayerDestroyItem(player, copy, InteractionHand.MAIN_HAND);
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
