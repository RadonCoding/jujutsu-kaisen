package radon.jujutsu_kaisen.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.util.PlayerUtil;

public class RerollCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralCommandNode<CommandSourceStack> node = dispatcher.register(Commands.literal("reroll")
                .requires((player) -> player.hasPermission(2))
                .then(Commands.argument("player", EntityArgument.entity()).executes((ctx) ->
                        reroll(EntityArgument.getPlayer(ctx, "player")))));

        dispatcher.register(Commands.literal("reroll").requires((player) -> player.hasPermission(2)).redirect(node));
    }

    public static int reroll(ServerPlayer player) {
        ISorcererData cap = player.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        PlayerUtil.removeAdvancement(player, "six_eyes");
        PlayerUtil.removeAdvancement(player, "heavenly_restriction");
        PlayerUtil.removeAdvancement(player, "vessel");

        cap.generate(player);

        return 1;
    }
}
