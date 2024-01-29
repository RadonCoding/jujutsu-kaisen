package radon.jujutsu_kaisen.network.packet.c2s;

import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.Pact;

import java.util.UUID;
import java.util.function.Supplier;

public class QuestionCreatePactC2SPacket {
    private final UUID identifier;
    private final Pact pact;

    public QuestionCreatePactC2SPacket(UUID identifier, Pact pact) {
        this.identifier = identifier;
        this.pact = pact;
    }

    public QuestionCreatePactC2SPacket(FriendlyByteBuf buf) {
        this(buf.readUUID(), buf.readEnum(Pact.class));
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUUID(this.identifier);
        buf.writeEnum(this.pact);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();

        ctx.enqueueWork(() -> {
            ServerPlayer sender = ctx.getSender();

            assert sender != null;

            ISorcererData cap = sender.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
            cap.createPactCreationRequest(this.identifier, this.pact);

            Player player = sender.serverLevel().getPlayerByUUID(this.identifier);

            if (player == sender) return;

            if (player != null) {
                Component accept = Component.translatable(String.format("chat.%s.pact_question_accept", JujutsuKaisen.MOD_ID))
                        .withStyle(Style.EMPTY.withColor(ChatFormatting.GREEN)
                                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/pactcreationaccept %s %s", sender.getName().getString(), this.pact.name())))
                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable(String.format("chat.%s.pact_question_accept", JujutsuKaisen.MOD_ID)))));
                Component decline = Component.translatable(String.format("chat.%s.pact_question_decline", JujutsuKaisen.MOD_ID))
                        .withStyle(Style.EMPTY.withColor(ChatFormatting.RED)
                                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/pactcreationdecline %s %s", sender.getName().getString(), this.pact.name())))
                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable(String.format("chat.%s.pact_question_decline", JujutsuKaisen.MOD_ID)))));

                Component message = Component.translatable(String.format("chat.%s.pact_question_create", JujutsuKaisen.MOD_ID), accept, decline,
                        this.pact.getName().getString().toLowerCase(), sender.getName());

                player.sendSystemMessage(message);
            }
        });
        ctx.setPacketHandled(true);
    }
}