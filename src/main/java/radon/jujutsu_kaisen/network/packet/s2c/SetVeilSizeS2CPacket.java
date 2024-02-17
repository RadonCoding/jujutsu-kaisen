package radon.jujutsu_kaisen.network.packet.s2c;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.ClientWrapper;
import radon.jujutsu_kaisen.menu.VeilRodMenu;

public class SetVeilSizeS2CPacket implements CustomPacketPayload {
    public static final ResourceLocation IDENTIFIER = new ResourceLocation(JujutsuKaisen.MOD_ID, "set_veil_size_clientbound");

    private final int size;

    public SetVeilSizeS2CPacket(int size) {
        this.size = size;
    }

    public SetVeilSizeS2CPacket(FriendlyByteBuf buf) {
        this(buf.readInt());
    }

    public void handle(PlayPayloadContext ctx) {
        ctx.workHandler().execute(() -> {
            Player player = ClientWrapper.getPlayer();

            if (player == null) return;

            if (!(player.containerMenu instanceof VeilRodMenu menu)) return;

            menu.setSize(this.size);
        });
    }

    @Override
    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeInt(this.size);
    }

    @Override
    public @NotNull ResourceLocation id() {
        return IDENTIFIER;
    }
}