package radon.jujutsu_kaisen.network.packet.c2s;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.entity.ConnectedLightningEntity;
import radon.jujutsu_kaisen.entity.NyoiStaffEntity;

import java.util.UUID;

public record NyoiStaffSummonLightningC2SPacket(UUID identifier) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<NyoiStaffSummonLightningC2SPacket> TYPE = new CustomPacketPayload.Type<>(new ResourceLocation(JujutsuKaisen.MOD_ID, "nyoi_staff_summon_lightning_serverbound"));
    public static final StreamCodec<? super RegistryFriendlyByteBuf, NyoiStaffSummonLightningC2SPacket> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC,
            NyoiStaffSummonLightningC2SPacket::identifier,
            NyoiStaffSummonLightningC2SPacket::new
    );

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!(ctx.player() instanceof ServerPlayer sender)) return;

            if (!(sender.serverLevel().getEntity(this.identifier) instanceof NyoiStaffEntity staff)) return;
            if (!staff.isCharged() || staff.getOwner() != sender) return;

            IJujutsuCapability cap = sender.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        ISorcererData data = cap.getSorcererData();

            sender.level().addFreshEntity(new ConnectedLightningEntity(sender, data.getAbilityOutput(), sender.position().add(0.0D, sender.getBbHeight() / 2, 0.0D),
                    staff.position()));

            staff.setCharged(false);
        });
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}