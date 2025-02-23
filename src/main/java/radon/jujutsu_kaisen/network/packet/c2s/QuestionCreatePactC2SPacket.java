package radon.jujutsu_kaisen.network.packet.c2s;


import net.minecraft.ChatFormatting;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.contract.IContractData;
import radon.jujutsu_kaisen.pact.JJKPacts;
import radon.jujutsu_kaisen.pact.Pact;

import java.util.UUID;

public record QuestionCreatePactC2SPacket(UUID identifier, Pact pact) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<QuestionCreatePactC2SPacket> TYPE = new CustomPacketPayload.Type<>(new ResourceLocation(JujutsuKaisen.MOD_ID, "question_create_pact_serverbound"));
    public static final StreamCodec<? super RegistryFriendlyByteBuf, QuestionCreatePactC2SPacket> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC,
            QuestionCreatePactC2SPacket::identifier,
            ByteBufCodecs.registry(JJKPacts.PACT_KEY),
            QuestionCreatePactC2SPacket::pact,
            QuestionCreatePactC2SPacket::new
    );

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!(ctx.player() instanceof ServerPlayer sender)) return;

            IJujutsuCapability cap = sender.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap == null) return;

            IContractData data = cap.getContractData();

            data.createPactCreationRequest(this.identifier, this.pact);

            Player player = sender.serverLevel().getPlayerByUUID(this.identifier);

            if (player == sender) return;

            if (player != null) {
                Component accept = Component.translatable(String.format("chat.%s.pact_question_accept", JujutsuKaisen.MOD_ID))
                        .withStyle(Style.EMPTY.withColor(ChatFormatting.GREEN)
                                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/jjkpactcreationaccept %s %s",
                                        sender.getName().getString(), JJKPacts.getKey(this.pact).toString())))
                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable(String.format("chat.%s.pact_question_accept", JujutsuKaisen.MOD_ID)))));
                Component decline = Component.translatable(String.format("chat.%s.pact_question_decline", JujutsuKaisen.MOD_ID))
                        .withStyle(Style.EMPTY.withColor(ChatFormatting.RED)
                                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/jjkpactcreationdecline %s %s",
                                        sender.getName().getString(), JJKPacts.getKey(this.pact).toString())))
                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable(String.format("chat.%s.pact_question_decline", JujutsuKaisen.MOD_ID)))));

                Component message = Component.translatable(String.format("chat.%s.pact_question_create", JujutsuKaisen.MOD_ID), accept, decline,
                        this.pact.getName(), sender.getName());

                player.sendSystemMessage(message);
            }
        });
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}