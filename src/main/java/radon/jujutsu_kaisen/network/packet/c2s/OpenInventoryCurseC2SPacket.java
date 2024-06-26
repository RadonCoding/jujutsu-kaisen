package radon.jujutsu_kaisen.network.packet.c2s;


import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.item.armor.InventoryCurseItem;
import radon.jujutsu_kaisen.util.CuriosUtil;

public class OpenInventoryCurseC2SPacket implements CustomPacketPayload {
    public static final OpenInventoryCurseC2SPacket INSTANCE = new OpenInventoryCurseC2SPacket();

    public static final CustomPacketPayload.Type<OpenInventoryCurseC2SPacket> TYPE = new CustomPacketPayload.Type<>(new ResourceLocation(JujutsuKaisen.MOD_ID, "open_inventory_curse_serverbound"));
    public static final StreamCodec<? super RegistryFriendlyByteBuf, OpenInventoryCurseC2SPacket> STREAM_CODEC = StreamCodec.unit(
            INSTANCE
    );

    private OpenInventoryCurseC2SPacket() {
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!(ctx.player() instanceof ServerPlayer sender)) return;

            if (sender.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof InventoryCurseItem item) {
                sender.openMenu(item);
            } else if (CuriosUtil.findSlot(sender, "bodyDL").getItem() instanceof InventoryCurseItem item) {
                sender.openMenu(item);
            }
        });
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}