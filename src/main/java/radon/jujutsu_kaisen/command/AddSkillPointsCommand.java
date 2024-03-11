package radon.jujutsu_kaisen.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;

public class AddSkillPointsCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralCommandNode<CommandSourceStack> node = dispatcher.register(Commands.literal("addskillpoints")
                .requires((player) -> player.hasPermission(2))
                .then(Commands.argument("player", EntityArgument.entity()).then(Commands.argument("points", IntegerArgumentType.integer())
                        .executes(ctx -> addSkillPoints(EntityArgument.getPlayer(ctx, "player"), IntegerArgumentType.getInteger(ctx, "points"))))));

        dispatcher.register(Commands.literal("addskillpoints").requires((player) -> player.hasPermission(2)).redirect(node));
    }

    public static int addSkillPoints(ServerPlayer player, int points) {
        IJujutsuCapability cap = player.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return 0;

        ISorcererData data = cap.getSorcererData();

        data.addSkillPoints(points);

        PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(data.serializeNBT()), player);

        return 1;
    }
}
