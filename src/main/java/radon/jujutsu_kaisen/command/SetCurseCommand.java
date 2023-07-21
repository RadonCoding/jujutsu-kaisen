package radon.jujutsu_kaisen.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;

public class SetCurseCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralCommandNode<CommandSourceStack> node = dispatcher.register(Commands.literal("setcurse")
                .requires((player) -> player.hasPermission(2))
                .then(Commands.argument("player", EntityArgument.entity()).then(Commands.argument("value", BoolArgumentType.bool()).executes((ctx) ->
                        setGrade(EntityArgument.getPlayer(ctx, "player"), BoolArgumentType.getBool(ctx, "value"))))));

        dispatcher.register(Commands.literal("setcurse").requires((player) -> player.hasPermission(2)).redirect(node));
    }

    public static int setGrade(ServerPlayer player, Boolean value) {
        player.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
            cap.setCurse(value);
            PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(cap.serializeNBT()), player);
        });
        return 1;
    }
}
