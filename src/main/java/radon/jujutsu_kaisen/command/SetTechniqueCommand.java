package radon.jujutsu_kaisen.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;
import radon.jujutsu_kaisen.cursed_technique.base.ICursedTechnique;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;

public class SetTechniqueCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralCommandNode<CommandSourceStack> node = dispatcher.register(Commands.literal("settechnique")
                .requires((player) -> player.hasPermission(2))
                .then(Commands.argument("player", EntityArgument.entity()).then(Commands.argument("technique", CursedTechniqueArgument.cursedTechnique())
                        .executes((ctx) -> setTechnique(EntityArgument.getPlayer(ctx, "player"), CursedTechniqueArgument.getTechnique(ctx, "technique"))))));

        dispatcher.register(Commands.literal("settechnique").requires((player) -> player.hasPermission(2)).redirect(node));
    }

    public static int setTechnique(ServerPlayer player, ICursedTechnique technique) {
        ISorcererData data = player.getData(JJKAttachmentTypes.SORCERER);

        if (data == null) return 0;

        data.setTechnique(technique);
        data.clearToggled();
        data.channel(null);

        PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(data.serializeNBT()), player);

        return 1;
    }
}
