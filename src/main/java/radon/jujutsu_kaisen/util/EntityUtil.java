package radon.jujutsu_kaisen.util;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.misc.RCT1;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;

import java.util.UUID;

public class EntityUtil {

    public static void convertTo(LivingEntity src, LivingEntity dst, boolean transferInventory, boolean kill) {
        if (!src.isRemoved()) {
            dst.copyPosition(src);

            if (src.hasCustomName()) {
                dst.setCustomName(src.getCustomName());
                dst.setCustomNameVisible(src.isCustomNameVisible());
            }

            dst.setInvulnerable(src.isInvulnerable());

            if (transferInventory) {
                for (EquipmentSlot slot : EquipmentSlot.values()) {
                    ItemStack stack = src.getItemBySlot(slot);

                    if (!stack.isEmpty()) {
                        dst.setItemSlot(slot, stack.copy());
                    }
                }
            }

            src.level().addFreshEntity(dst);

            if (src.isPassenger()) {
                Entity vehicle = src.getVehicle();
                src.stopRiding();

                if (vehicle != null) {
                    vehicle.startRiding(vehicle, true);
                }
            }

            if (kill) {
                if (src instanceof Player) {
                    src.kill();
                } else {
                    src.discard();
                }
            }
        }
    }

    @Nullable
    public static RCT1 getRCTTier(LivingEntity owner) {
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        if (cap.isUnlocked(JJKAbilities.RCT3.get())) return JJKAbilities.RCT3.get();
        if (cap.isUnlocked(JJKAbilities.RCT2.get())) return JJKAbilities.RCT2.get();
        if (cap.isUnlocked(JJKAbilities.RCT1.get())) return JJKAbilities.RCT1.get();

        return null;
    }

    public static boolean applyModifier(LivingEntity owner, Attribute attribute, UUID identifier, String name, double amount, AttributeModifier.Operation operation) {
        AttributeInstance instance = owner.getAttribute(attribute);
        AttributeModifier modifier = new AttributeModifier(identifier, name, amount, operation);

        if (instance != null) {
            AttributeModifier existing = instance.getModifier(identifier);

            if (existing != null) {
                if (existing.getAmount() != amount) {
                    instance.removeModifier(identifier);
                    instance.addTransientModifier(modifier);
                    return true;
                }
            } else {
                instance.addTransientModifier(modifier);
                return true;
            }
        }
        return false;
    }

    public static void removeModifier(LivingEntity owner, Attribute attribute, UUID identifier) {
        AttributeInstance instance = owner.getAttribute(attribute);

        if (instance != null) {
            instance.removeModifier(identifier);
        }
    }
}
