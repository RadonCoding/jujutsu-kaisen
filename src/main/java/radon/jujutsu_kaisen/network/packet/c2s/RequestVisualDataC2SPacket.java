package radon.jujutsu_kaisen.network.packet.c2s;

import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.client.visual.ClientVisualHandler;
import net.neoforged.neoforge.network.PacketDistributor;
import radon.jujutsu_kaisen.network.packet.s2c.ReceiveVisualDataS2CPacket;

import java.util.UUID;

public record RequestVisualDataC2SPacket(UUID src) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<RequestVisualDataC2SPacket> TYPE = new CustomPacketPayload.Type<>(new ResourceLocation(JujutsuKaisen.MOD_ID, "request_visual_data_serverbound"));
    public static final StreamCodec<? super RegistryFriendlyByteBuf, RequestVisualDataC2SPacket> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC,
            RequestVisualDataC2SPacket::src,
            RequestVisualDataC2SPacket::new
    );

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!(ctx.player() instanceof ServerPlayer sender)) return;

            if (!(sender.serverLevel().getEntity(this.src) instanceof LivingEntity target)) return;

            IJujutsuCapability cap = target.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap == null) return;

            ISorcererData sorcererData = cap.getSorcererData();
            IAbilityData abilityData = cap.getAbilityData();

            ClientVisualHandler.ClientData client = new ClientVisualHandler.ClientData(abilityData.getToggled(), abilityData.getChanneled(), sorcererData.getTraits(),
                    sorcererData.getActiveTechniques(), sorcererData.getTechnique(), sorcererData.getType(), sorcererData.getExperience(), sorcererData.getCursedEnergyColor());
            PacketDistributor.sendToPlayer(sender, new ReceiveVisualDataS2CPacket(this.src, client.serializeNBT()));
        });
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}