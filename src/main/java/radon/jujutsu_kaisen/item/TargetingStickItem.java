package radon.jujutsu_kaisen.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.Targeting;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.item.registry.JJKDataComponentTypes;
import radon.jujutsu_kaisen.network.PacketHandler;
import net.neoforged.neoforge.network.PacketDistributor;
import radon.jujutsu_kaisen.network.packet.s2c.SetOverlayMessageS2CPacket;

import java.lang.annotation.Target;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TargetingStickItem extends Item {
    public TargetingStickItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, @NotNull Player pPlayer, @NotNull InteractionHand pUsedHand) {
        if (pLevel.isClientSide) return super.use(pLevel, pPlayer, pUsedHand);

        ItemStack stack = pPlayer.getItemInHand(pUsedHand);

        if (!stack.has(JJKDataComponentTypes.ENTITY_UUID)) return super.use(pLevel, pPlayer, pUsedHand);

        stack.remove(JJKDataComponentTypes.ENTITY_UUID);

        PacketDistributor.sendToPlayer((ServerPlayer) pPlayer, new SetOverlayMessageS2CPacket(Component.translatable(String.format("chat.%s.targeting_stick.reset",
                JujutsuKaisen.MOD_ID)), false));

        return InteractionResultHolder.sidedSuccess(stack, pLevel.isClientSide);
    }

    @Override
    public boolean onLeftClickEntity(@NotNull ItemStack stack, @NotNull Player player, @NotNull Entity entity) {
        if (!(player.level() instanceof ServerLevel level)) return true;
        if (!(entity instanceof Mob second)) return true;

        UUID identifier = stack.get(JJKDataComponentTypes.ENTITY_UUID);

        if (identifier == null) {
            stack.set(JJKDataComponentTypes.ENTITY_UUID, entity.getUUID());

            PacketDistributor.sendToPlayer((ServerPlayer) player, new SetOverlayMessageS2CPacket(Component.translatable(String.format("chat.%s.targeting_stick.add",
                    JujutsuKaisen.MOD_ID)), false));

            return true;
        }

        if (!(level.getEntity(identifier) instanceof Mob first)) {
            stack.remove(JJKDataComponentTypes.ENTITY_UUID);
            return true;
        }

        if (first == second) return true;

        first.setTarget(second);
        second.setTarget(first);

        stack.remove(JJKDataComponentTypes.ENTITY_UUID);

        return true;
    }
}
