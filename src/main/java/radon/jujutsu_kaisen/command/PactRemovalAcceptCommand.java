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
import radon.jujutsu_kaisen.command.argument.PactArgument;
import radon.jujutsu_kaisen.data.contract.IContractData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.network.PacketHandler;
import net.neoforged.neoforge.network.PacketDistributor;
import radon.jujutsu_kaisen.network.packet.s2c.SyncContractDataS2CPacket;
import radon.jujutsu_kaisen.pact.Pact;

public class PactRemovalAcceptCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralCommandNode<CommandSourceStack> node = dispatcher.register(Commands.literal("jjkpactremovalaccept")
                .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("pact", PactArgument.pact())
                                .executes(ctx -> accept(ctx.getSource(), EntityArgument.getPlayer(ctx, "player"), PactArgument.getPact(ctx, "pact"))))));

        dispatcher.register(Commands.literal("jjkpactremovalaccept").redirect(node));
    }

    public static int accept(CommandSourceStack stack, ServerPlayer dst, Pact pact) {
        ServerPlayer src = stack.getPlayer();

        if (src == null) return 0;

        IJujutsuCapability srcCap = src.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (srcCap == null) return 0;

        IContractData srcData = srcCap.getContractData();

        IJujutsuCapability dstCap = dst.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (dstCap == null) return 0;

        IContractData dstData = dstCap.getContractData();

        if (srcData == null || dstData == null) return 0;

        if (dstData.hasRequestedPactRemoval(src.getUUID(), pact)) {
            dstData.removePact(src.getUUID(), pact);
            srcData.removePact(dst.getUUID(), pact);

            dstData.removePactRemovalRequest(src.getUUID(), pact);

            PacketDistributor.sendToPlayer(dst, new SyncContractDataS2CPacket(dstData.serializeNBT(dst.registryAccess())));
            PacketDistributor.sendToPlayer(src, new SyncContractDataS2CPacket(srcData.serializeNBT(src.registryAccess())));

            src.sendSystemMessage(Component.translatable(String.format("chat.%s.pact_accept_remove", JujutsuKaisen.MOD_ID), pact.getName().getString().toLowerCase(), dst.getName()));
            dst.sendSystemMessage(Component.translatable(String.format("chat.%s.pact_accept_remove", JujutsuKaisen.MOD_ID), pact.getName().getString().toLowerCase(), src.getName()));
        } else {
            src.sendSystemMessage(Component.translatable(String.format("chat.%s.pact_failure_remove", JujutsuKaisen.MOD_ID), dst.getName(), pact.getName().getString().toLowerCase()));
            return 0;
        }
        return 1;
    }
}
