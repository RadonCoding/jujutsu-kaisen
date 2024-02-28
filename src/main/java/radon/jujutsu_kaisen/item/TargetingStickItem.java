package radon.jujutsu_kaisen.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
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
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SetOverlayMessageS2CPacket;

import java.lang.annotation.Target;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TargetingStickItem extends Item {
    public TargetingStickItem(Properties pProperties) {
        super(pProperties);
    }

    private static Optional<UUID> getEntity(ItemStack stack) {
        CompoundTag nbt = stack.getTag();

        if (nbt == null) return Optional.empty();

        return Optional.of(nbt.getUUID("entity"));
    }

    private static void setEntity(ItemStack stack, UUID identifier) {
        CompoundTag nbt = stack.getOrCreateTag();
        nbt.putUUID("entity", identifier);
    }

    private static void resetEntity(ItemStack stack) {
        CompoundTag nbt = stack.getOrCreateTag();
        nbt.remove("entity");
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, @NotNull Player pPlayer, @NotNull InteractionHand pUsedHand) {
        if (!pLevel.isClientSide) {
            ItemStack stack = pPlayer.getItemInHand(pUsedHand);
            Optional<UUID> entity = getEntity(stack);

            if (entity.isPresent()) {
                resetEntity(stack);

                PacketHandler.sendToClient(new SetOverlayMessageS2CPacket(Component.translatable(String.format("chat.%s.targeting_stick.reset",
                        JujutsuKaisen.MOD_ID)), false), (ServerPlayer) pPlayer);
            }
        }
        return super.use(pLevel, pPlayer, pUsedHand);
    }

    @Override
    public boolean onLeftClickEntity(@NotNull ItemStack stack, @NotNull Player player, @NotNull Entity entity) {
        if (!(player.level() instanceof ServerLevel level)) return true;
        if (!(entity instanceof Mob second)) return true;

        Optional<UUID> stored = getEntity(stack);

        if (stored.isPresent()) {
            if (!(level.getEntity(stored.get()) instanceof Mob first)) {
                resetEntity(stack);
                return true;
            }

            if (first == second) return true;

            first.setTarget(second);
            second.setTarget(first);

            resetEntity(stack);

            return true;
        }

        setEntity(stack, entity.getUUID());

        PacketHandler.sendToClient(new SetOverlayMessageS2CPacket(Component.translatable(String.format("chat.%s.targeting_stick.add",
                JujutsuKaisen.MOD_ID)), false), (ServerPlayer) player);

        return true;
    }
}
