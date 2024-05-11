package radon.jujutsu_kaisen.util;

import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import radon.jujutsu_kaisen.block.JJKBlocks;
import radon.jujutsu_kaisen.block.entity.VeilBlockEntity;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.data.sorcerer.Trait;
import radon.jujutsu_kaisen.item.veil.modifier.Modifier;
import radon.jujutsu_kaisen.item.veil.modifier.PlayerModifier;

import java.util.List;
import java.util.UUID;

public class VeilUtil {
    public static boolean isAllowed(Entity entity, UUID ownerUUID, List<Modifier> modifiers) {
        if (entity.getUUID().equals(ownerUUID)) return true;

        IJujutsuCapability cap = entity.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap != null) {
            ISorcererData data = cap.getSorcererData();

            if (data.hasTrait(Trait.HEAVENLY_RESTRICTION_BODY)) return true;
        }

        if (entity instanceof Player player) {
            for (Modifier modifier : modifiers) {
                if (modifier.getAction() != Modifier.Action.ALLOW || modifier.getType() != Modifier.Type.PLAYER)
                    continue;

                Component name = player.getDisplayName();

                if (name == null) continue;

                if (((PlayerModifier) modifier).getName().equals(name.getString())) {
                    return true;
                }
            }
        }

        if (cap != null) {
            ISorcererData data = cap.getSorcererData();

            for (Modifier modifier : modifiers) {
                if (modifier.getAction() == Modifier.Action.ALLOW) {
                    if (modifier.getType() == Modifier.Type.CURSE && data.getType() == JujutsuType.CURSE) return true;
                    if (modifier.getType() == Modifier.Type.SORCERER && data.getType() == JujutsuType.SORCERER) return true;
                } else if (modifier.getAction() == Modifier.Action.DENY) {
                    if (modifier.getType() == Modifier.Type.CURSE && data.getType() == JujutsuType.CURSE) return false;
                    if (modifier.getType() == Modifier.Type.SORCERER && data.getType() == JujutsuType.SORCERER) return false;
                }
            }
        }
        return true;
    }

    public static boolean canDamage(List<Modifier> modifiers) {
        for (Modifier modifier : modifiers) {
            if (modifier.getType() == Modifier.Type.VIOLENCE && modifier.getAction() == Modifier.Action.DENY) return false;
        }
        return true;
    }

    public static boolean canDestroy(Entity entity, BlockPos target, UUID parentUUID, List<Modifier> modifiers) {
        if (entity.level().getBlockState(target).is(JJKBlocks.VEIL_ROD)) return true;

        // If the block is a veil block and the block is owned by the barrier
        if (entity.level().getBlockEntity(target) instanceof VeilBlockEntity be && be.getParentUUID() != null && be.getParentUUID().equals(parentUUID)) return true;

        for (Modifier modifier : modifiers) {
            if (modifier.getType() == Modifier.Type.GRIEFING && modifier.getAction() == Modifier.Action.DENY) return false;
        }
        return true;
    }
}
