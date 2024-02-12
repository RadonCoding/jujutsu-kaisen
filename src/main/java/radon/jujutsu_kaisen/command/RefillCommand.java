package radon.jujutsu_kaisen.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;

public class RefillCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralCommandNode<CommandSourceStack> node = dispatcher.register(Commands.literal("refill")
                .requires((player) -> player.hasPermission(2))
                .then(Commands.argument("player", EntityArgument.entity()).executes((ctx) ->
                        refill(EntityArgument.getPlayer(ctx, "player")))));

        dispatcher.register(Commands.literal("refill").requires((player) -> player.hasPermission(2)).redirect(node));
    }

    public static int refill(ServerPlayer player) {
        IJujutsuCapability jujutsuCap = player.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (jujutsuCap == null) return 0;

        ISorcererData data = jujutsuCap.getSorcererData();

        data.setEnergy(data.getMaxEnergy());

        PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(data.serializeNBT()), player);

        return 1;
    }
}
