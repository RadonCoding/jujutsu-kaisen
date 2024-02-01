package radon.jujutsu_kaisen;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;
import radon.jujutsu_kaisen.ability.AbilityHandler;
import radon.jujutsu_kaisen.ability.AbilityTriggerEvent;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.IImbuement;
import radon.jujutsu_kaisen.ability.base.Imbuement;
import radon.jujutsu_kaisen.capability.data.sorcerer.cursed_technique.JJKCursedTechniques;
import radon.jujutsu_kaisen.capability.data.sorcerer.cursed_technique.base.ICursedTechnique;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.config.ServerConfig;
import radon.jujutsu_kaisen.entity.projectile.ThrownChainProjectile;
import radon.jujutsu_kaisen.util.CuriosUtil;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.*;

public class ImbuementHandler {
    public static Set<ICursedTechnique> getFullImbuements(ItemStack stack) {
       Map<ICursedTechnique, Integer> amounts = getImbuementAmounts(stack);

        Iterator<Map.Entry<ICursedTechnique, Integer>> iter = amounts.entrySet().iterator();

        while (iter.hasNext()) {
            Map.Entry<ICursedTechnique, Integer> entry = iter.next();

            ICursedTechnique technique = entry.getKey();

            int amount = getImbuementAmount(stack, technique);

            if (amount < ConfigHolder.SERVER.requiredImbuementAmount.get()) iter.remove();
        }
        return amounts.keySet();
    }

    public static Map<ICursedTechnique, Integer> getImbuementAmounts(ItemStack stack) {
        Map<ICursedTechnique, Integer> amounts = new HashMap<>();

        for (RegistryObject<ICursedTechnique> entry : JJKCursedTechniques.CURSED_TECHNIQUES.getEntries()) {
            ICursedTechnique technique = entry.get();

            int amount = getImbuementAmount(stack, technique);

            if (amount == 0) continue;

            amounts.put(technique, amount);
        }
        return amounts;
    }

    public static int getImbuementAmount(ItemStack stack, ICursedTechnique technique) {
        CompoundTag nbt = stack.getTag();

        if (nbt == null) return 0;

        if (!nbt.contains(JujutsuKaisen.MOD_ID)) return 0;

        CompoundTag mod = nbt.getCompound(JujutsuKaisen.MOD_ID);

        if (!mod.contains("imbuements")) return 0;

        CompoundTag imbuements = mod.getCompound("imbuements");
        return imbuements.getInt(JJKCursedTechniques.getKey(technique).toString());
    }

    public static void increaseImbuementAmount(ItemStack stack, ICursedTechnique technique, int amount) {
        CompoundTag nbt = stack.getOrCreateTag();

        if (!nbt.contains(JujutsuKaisen.MOD_ID)) {
            nbt.put(JujutsuKaisen.MOD_ID, new CompoundTag());
        }
        CompoundTag mod = nbt.getCompound(JujutsuKaisen.MOD_ID);

        if (!mod.contains("imbuements")) {
            mod.put("imbuements", new CompoundTag());
        }
        CompoundTag imbuements = mod.getCompound("imbuements");

        int imbuement = imbuements.getInt(JJKCursedTechniques.getKey(technique).toString());

        imbuements.putInt(JJKCursedTechniques.getKey(technique).toString(), Math.min(ConfigHolder.SERVER.requiredImbuementAmount.get(), imbuement + amount));
    }

    public static void setFullyImbued(ItemStack stack, ICursedTechnique technique) {
        CompoundTag nbt = stack.getOrCreateTag();

        if (!nbt.contains(JujutsuKaisen.MOD_ID)) {
            nbt.put(JujutsuKaisen.MOD_ID, new CompoundTag());
        }

        CompoundTag mod = nbt.getCompound(JujutsuKaisen.MOD_ID);

        if (!mod.contains("imbuements")) {
            mod.put("imbuements", new CompoundTag());
        }
        CompoundTag imbuements = mod.getCompound("imbuements");
        imbuements.putInt(JJKCursedTechniques.getKey(technique).toString(), ConfigHolder.SERVER.requiredImbuementAmount.get());
    }

    @Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ImbuemenetEventHandlerForgeEvents {
        @SubscribeEvent
        public static void onAbilityTrigger(AbilityTriggerEvent.Post event) {
            LivingEntity owner = event.getEntity();

            Ability ability = event.getAbility();

            if (!ability.isTechnique() || ability.getCost(owner) == 0.0F) return;

            int amount = Math.round(Math.max(1, ability.getCost(owner) / 100.0F));

            ICursedTechnique technique = JJKCursedTechniques.getTechnique(ability);

            if (technique == null) return;

            List<ItemStack> stacks = new ArrayList<>();

            if (!owner.getItemInHand(InteractionHand.MAIN_HAND).isEmpty()) {
                stacks.add(owner.getItemInHand(InteractionHand.MAIN_HAND));
            }
            if (!owner.getItemInHand(InteractionHand.OFF_HAND).isEmpty()) {
                stacks.add(owner.getItemInHand(InteractionHand.OFF_HAND));
            }
            stacks.addAll(CuriosUtil.findSlots(owner, "right_hand"));
            stacks.addAll(CuriosUtil.findSlots(owner, "left_hand"));

            for (ItemStack stack : stacks) {
                if (!(stack.getItem() instanceof SwordItem)) continue;
                increaseImbuementAmount(stack, technique, amount);
                break;
            }
        }

        @SubscribeEvent
        public static void onLivingAttack(LivingAttackEvent event) {
            DamageSource source = event.getSource();

            if (!(source.getEntity() instanceof LivingEntity attacker)) return;

            LivingEntity victim = event.getEntity();

            if (victim.level().isClientSide) return;

            List<ItemStack> stacks = new ArrayList<>();

            if (source.getDirectEntity() instanceof ThrownChainProjectile chain) {
                stacks.add(chain.getStack());
            } else {
                stacks.add(attacker.getItemInHand(InteractionHand.MAIN_HAND));
                stacks.addAll(CuriosUtil.findSlots(attacker, attacker.getMainArm() == HumanoidArm.RIGHT ? "right_hand" : "left_hand")
                        .stream().toList());
            }

            if (!HelperMethods.isMelee(source) && !(source.getDirectEntity() instanceof ThrownChainProjectile)) return;

            for (ItemStack stack : stacks) {
                for (ICursedTechnique technique : ImbuementHandler.getFullImbuements(stack)) {
                    IImbuement imbuement = (IImbuement) technique.getImbuement();
                    imbuement.hit(attacker, victim);
                }
            }
        }
    }
}
