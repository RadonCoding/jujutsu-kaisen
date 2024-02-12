package radon.jujutsu_kaisen.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.server.command.EnumArgument;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;
import radon.jujutsu_kaisen.data.sorcerer.Pact;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;

public class PactCreationAcceptCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralCommandNode<CommandSourceStack> node = dispatcher.register(Commands.literal("pactcreationaccept")
                .then(Commands.argument("player", EntityArgument.entity())
                        .then(Commands.argument("pact", EnumArgument.enumArgument(Pact.class))
                                .executes(ctx -> accept(ctx.getSource(), EntityArgument.getPlayer(ctx, "player"), ctx.getArgument("pact", Pact.class))))));

        dispatcher.register(Commands.literal("pactcreationaccept").redirect(node));
    }

    public static int accept(CommandSourceStack stack, ServerPlayer dst, Pact pact) {
        ServerPlayer src = stack.getPlayer();

        if (src == null) return 0;

        ISorcererData srcData = src.getData(JJKAttachmentTypes.SORCERER);
        ISorcererData dstData = dst.getData(JJKAttachmentTypes.SORCERER);

        if (srcData == null || dstData == null) return 0;

        if (dstData.hasRequestedPactCreation(src.getUUID(), pact)) {
            dstData.createPact(src.getUUID(), pact);
            srcData.createPact(dst.getUUID(), pact);

            dstData.removePactCreationRequest(src.getUUID(), pact);

            PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(dstData.serializeNBT()), dst);
            PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(srcData.serializeNBT()), src);

            src.sendSystemMessage(Component.translatable(String.format("chat.%s.pact_creation_accept", JujutsuKaisen.MOD_ID), pact.getName().getString().toLowerCase(), dst.getName()));
            dst.sendSystemMessage(Component.translatable(String.format("chat.%s.pact_creation_accept", JujutsuKaisen.MOD_ID), pact.getName().getString().toLowerCase(), src.getName()));
        } else {
            src.sendSystemMessage(Component.translatable(String.format("chat.%s.pact_failure_create", JujutsuKaisen.MOD_ID), dst.getName(), pact.getName().getString().toLowerCase()));
            return 0;
        }
        return 1;
    }
}
