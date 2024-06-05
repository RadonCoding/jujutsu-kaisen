package radon.jujutsu_kaisen.command;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.command.argument.PactArgument;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.contract.IContractData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.pact.Pact;

public class PactRemovalDeclineCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralCommandNode<CommandSourceStack> node = dispatcher.register(Commands.literal("jjkpactremovaldecline")
                .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("pact", PactArgument.pact())
                                .executes(ctx -> decline(ctx.getSource(), EntityArgument.getPlayer(ctx, "player"), PactArgument.getPact(ctx, "pact"))))));

        dispatcher.register(Commands.literal("jjkpactremovaldecline").redirect(node));
    }

    public static int decline(CommandSourceStack stack, ServerPlayer dst, Pact pact) {
        ServerPlayer src = stack.getPlayer();

        if (src == null) return 0;

        IJujutsuCapability cap = dst.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return 0;

        IContractData data = cap.getContractData();

        if (data.hasRequestedPactRemoval(src.getUUID(), pact)) {
            data.removePactRemovalRequest(src.getUUID(), pact);

            dst.sendSystemMessage(Component.translatable(String.format("chat.%s.pact_removal_decline", JujutsuKaisen.MOD_ID), src.getName(), pact.getName().getString().toLowerCase()));
        } else {
            src.sendSystemMessage(Component.translatable(String.format("chat.%s.pact_removal_decline", JujutsuKaisen.MOD_ID), dst.getName(), pact.getName().getString().toLowerCase()));
            return 0;
        }
        return 1;
    }
}
