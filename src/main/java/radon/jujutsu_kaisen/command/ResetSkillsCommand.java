package radon.jujutsu_kaisen.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.repository.Pack;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.stat.ISkillData;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSkillDataSC2Packet;
import radon.jujutsu_kaisen.util.PlayerUtil;

public class ResetSkillsCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralCommandNode<CommandSourceStack> node = dispatcher.register(Commands.literal("resetskills")
                .requires((player) -> player.hasPermission(2))
                .then(Commands.argument("player", EntityArgument.entity()).executes((ctx) ->
                        resetSkills(EntityArgument.getPlayer(ctx, "player")))));

        dispatcher.register(Commands.literal("resetskills").requires((player) -> player.hasPermission(2)).redirect(node));
    }

    public static int resetSkills(ServerPlayer player) {
        IJujutsuCapability cap = player.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return 0;

        ISkillData data = cap.getSkillData();

        data.resetSkills();

        PacketHandler.sendToClient(new SyncSkillDataSC2Packet(data.serializeNBT()), player);

        return 1;
    }
}
