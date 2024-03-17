package radon.jujutsu_kaisen.network.packet.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.item.armor.InventoryCurseItem;
import radon.jujutsu_kaisen.util.CuriosUtil;

public class OpenInventoryCurseC2SPacket implements CustomPacketPayload {
    public static final ResourceLocation IDENTIFIER = new ResourceLocation(JujutsuKaisen.MOD_ID, "open_inventory_curse_serverbound");

    public OpenInventoryCurseC2SPacket() {
    }

    public OpenInventoryCurseC2SPacket(FriendlyByteBuf ignored) {
    }

    public void handle(PlayPayloadContext ctx) {
        ctx.workHandler().execute(() -> {
            if (!(ctx.player().orElseThrow() instanceof ServerPlayer sender)) return;

            if (sender.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof InventoryCurseItem item) {
                sender.openMenu(item);
            } else if (CuriosUtil.findSlot(sender, "body").getItem() instanceof InventoryCurseItem item) {
                sender.openMenu(item);
            }
        });
    }

    @Override
    public void write(@NotNull FriendlyByteBuf pBuffer) {

    }

    @Override
    public @NotNull ResourceLocation id() {
        return IDENTIFIER;
    }
}