package radon.jujutsu_kaisen;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;
import radon.jujutsu_kaisen.ability.AbilityTriggerEvent;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.IImbuement;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.cursed_technique.JJKCursedTechniques;
import radon.jujutsu_kaisen.cursed_technique.base.ICursedTechnique;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.entity.projectile.ThrownChainProjectile;
import radon.jujutsu_kaisen.item.JJKItems;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;
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

            ItemStack stack = owner.getItemInHand(InteractionHand.MAIN_HAND);

            if (stack.isEmpty()) return;

            if (!(stack.getItem() instanceof SwordItem)) return;

            increaseImbuementAmount(stack, technique, amount);
        }

        @SubscribeEvent
        public static void onLivingAttack(LivingAttackEvent event) {
            DamageSource source = event.getSource();

            if (!(source.getEntity() instanceof LivingEntity attacker)) return;

            if (!attacker.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return;
            ISorcererData cap = attacker.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

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

            if (!HelperMethods.isMelee(source) && !(source.getDirectEntity() instanceof ThrownChainProjectile)) return;

            for (ItemStack stack : stacks) {
                for (ICursedTechnique technique : ImbuementHandler.getFullImbuements(stack)) {
                    Ability ability = technique.getImbuement();

                    if (!cap.isCooldownDone(ability)) continue;

                    ((IImbuement) ability).hit(attacker, victim);

                    if (attacker instanceof Player player && player.getAbilities().instabuild) continue;

                    if (ability.getRealCooldown(attacker) == 0) continue;

                    cap.addCooldown(ability);

                    if (attacker instanceof ServerPlayer player) {
                        PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(cap.serializeNBT()), player);
                    }
                }

                if (stack.is(JJKItems.MIMICRY_KATANA_BLACK.get()) || stack.is(JJKItems.MIMICRY_KATANA_WHITE.get())) {
                    stack.shrink(1);
                }
            }
        }
    }
}
