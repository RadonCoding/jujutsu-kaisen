package radon.jujutsu_kaisen.network.packet.c2s;

import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.menu.BountyMenu;
import radon.jujutsu_kaisen.network.PacketHandler;
import net.neoforged.neoforge.network.PacketDistributor;
import radon.jujutsu_kaisen.util.SorcererUtil;

import java.nio.charset.Charset;

public record RequestBountyCostC2SPacket(String name) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<RequestBountyCostC2SPacket> TYPE = new CustomPacketPayload.Type<>(new ResourceLocation(JujutsuKaisen.MOD_ID, "request_bounty_cost_serverbound"));
    public static final StreamCodec<? super RegistryFriendlyByteBuf, RequestBountyCostC2SPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            RequestBountyCostC2SPacket::name,
            RequestBountyCostC2SPacket::new
    );

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!(ctx.player() instanceof ServerPlayer sender)) return;

            ServerPlayer target = sender.server.getPlayerList().getPlayerByName(String.valueOf(this.name));

            if (target == null) return;

            IJujutsuCapability cap = target.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap == null) return;

            ISorcererData data = cap.getSorcererData();

            int cost = (Mth.floor(64 * ((float) (SorcererUtil.getGrade(data.getExperience()).ordinal() + 1) / SorcererGrade.values().length)));

            if (sender.containerMenu instanceof BountyMenu menu) {
                menu.setCost(cost);
            }
        });
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}