package radon.jujutsu_kaisen.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.server.command.EnumArgument;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.Pact;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;

public class PactAcceptCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralCommandNode<CommandSourceStack> node = dispatcher.register(Commands.literal("pactaccept")
                .then(Commands.argument("player", EntityArgument.entity())
                        .then(Commands.argument("pact", EnumArgument.enumArgument(Pact.class))
                                .executes(ctx -> accept(ctx.getSource(), EntityArgument.getPlayer(ctx, "player"), ctx.getArgument("pact", Pact.class))))));

        dispatcher.register(Commands.literal("pactaccept").redirect(node));
    }

    public static int accept(CommandSourceStack stack, ServerPlayer dst, Pact pact) {
        ServerPlayer src = stack.getPlayer();

        if (src == null) return 0;

        ISorcererData dstCap = dst.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        ISorcererData srcCap = src.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        if (dstCap.hasRequestedPact(src.getUUID(), pact)) {
            dstCap.createPact(src.getUUID(), pact);
            srcCap.createPact(dst.getUUID(), pact);

            dstCap.removePactRequest(src.getUUID(), pact);

            PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(dstCap.serializeNBT()), dst);
            PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(srcCap.serializeNBT()), src);

            src.sendSystemMessage(Component.translatable(String.format("chat.%s.pact_accept", JujutsuKaisen.MOD_ID), pact.getName().getString().toLowerCase(), dst.getName()));
            dst.sendSystemMessage(Component.translatable(String.format("chat.%s.pact_accept", JujutsuKaisen.MOD_ID), pact.getName().getString().toLowerCase(), src.getName()));
        } else {
            src.sendSystemMessage(Component.translatable(String.format("chat.%s.pact_failure", JujutsuKaisen.MOD_ID), dst.getName(), pact.getName().getString().toLowerCase()));
            return 0;
        }
        return 1;
    }
}
