package radon.jujutsu_kaisen.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import radon.jujutsu_kaisen.capability.data.ten_shadows.ITenShadowsData;
import radon.jujutsu_kaisen.capability.data.ten_shadows.TenShadowsDataHandler;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncTenShadowsDataS2CPacket;

public class ResetSummonsCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralCommandNode<CommandSourceStack> node = dispatcher.register(Commands.literal("resetsummons")
                .requires((player) -> player.hasPermission(2))
                .then(Commands.argument("player", EntityArgument.entity()).executes((ctx) ->
                        reset(EntityArgument.getPlayer(ctx, "player")))));

        dispatcher.register(Commands.literal("resetsummons").requires((player) -> player.hasPermission(2)).redirect(node));
    }

    public static int reset(ServerPlayer player) {
        ITenShadowsData cap = player.getCapability(TenShadowsDataHandler.INSTANCE).resolve().orElseThrow();
        cap.revive(true);
        PacketHandler.sendToClient(new SyncTenShadowsDataS2CPacket(cap.serializeNBT()), player);
        return 1;
    }
}
