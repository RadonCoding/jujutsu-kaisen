package radon.jujutsu_kaisen.network.packet.c2s;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.binding_vow.BindingVow;
import radon.jujutsu_kaisen.binding_vow.JJKBindingVows;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.contract.IContractData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;

public record AddBindingVowC2SPacket(BindingVow vow) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<AddBindingVowC2SPacket> TYPE = new CustomPacketPayload.Type<>(new ResourceLocation(JujutsuKaisen.MOD_ID, "add_binding_vow_serverbound"));
    public static final StreamCodec<? super RegistryFriendlyByteBuf, AddBindingVowC2SPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.registry(JJKBindingVows.BINDING_VOW_KEY),
            AddBindingVowC2SPacket::vow,
            AddBindingVowC2SPacket::new
    );

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!(ctx.player() instanceof ServerPlayer sender)) return;

            IJujutsuCapability cap = sender.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap == null) return;

            IContractData data = cap.getContractData();

            if (!data.isCooldownDone(this.vow)) return;

            data.addBindingVow(this.vow);
            data.addBindingVowCooldown(this.vow);
        });
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}