package radon.jujutsu_kaisen.network.packet.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.sorcerer.BindingVow;

public class RemoveBindingVowC2SPacket implements CustomPacketPayload {
    public static final ResourceLocation IDENTIFIER = new ResourceLocation(JujutsuKaisen.MOD_ID, "remove_binding_vow_serverbound");

    private final BindingVow vow;

    public RemoveBindingVowC2SPacket(BindingVow vow) {
        this.vow = vow;
    }

    public RemoveBindingVowC2SPacket(FriendlyByteBuf buf) {
        this(buf.readEnum(BindingVow.class));
    }

    public void handle(PlayPayloadContext ctx) {
        ctx.workHandler().submitAsync(() -> {
            if (!(ctx.player().orElseThrow() instanceof ServerPlayer sender)) return;

            ISorcererData data = sender.getData(JJKAttachmentTypes.SORCERER);

            if (!data.isCooldownDone(this.vow)) return;

            data.removeBindingVow(this.vow);
            data.addBindingVowCooldown(this.vow);
        });
    }

    @Override
    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeEnum(this.vow);
    }

    @Override
    public @NotNull ResourceLocation id() {
        return IDENTIFIER;
    }
}