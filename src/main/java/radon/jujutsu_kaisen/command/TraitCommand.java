package radon.jujutsu_kaisen.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.server.command.EnumArgument;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.Trait;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;

public class TraitCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralCommandNode<CommandSourceStack> node = dispatcher.register(Commands.literal("trait")
                .requires((player) -> player.hasPermission(2))
                .then(Commands.literal("add").then(Commands.argument("player", EntityArgument.entity()).then(Commands.argument("trait", EnumArgument.enumArgument(Trait.class)).executes((ctx) ->
                        addTrait(EntityArgument.getPlayer(ctx, "player"), ctx.getArgument("trait", Trait.class))))))
                .then(Commands.literal("remove").then(Commands.argument("player", EntityArgument.entity()).then(Commands.argument("trait", EnumArgument.enumArgument(Trait.class)).executes((ctx) ->
                        removeTrait(EntityArgument.getPlayer(ctx, "player"), ctx.getArgument("trait", Trait.class)))))));

        dispatcher.register(Commands.literal("trait").requires((player) -> player.hasPermission(2)).redirect(node));
    }

    public static int addTrait(ServerPlayer player, Trait trait) {
        IJujutsuCapability cap = player.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return  0;

        ISorcererData data = cap.getSorcererData();

        data.addTrait(trait);

        PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(data.serializeNBT()), player);

        return 1;
    }

    public static int removeTrait(ServerPlayer player, Trait trait) {
        IJujutsuCapability cap = player.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return 0;

        ISorcererData data = cap.getSorcererData();

        data.removeTrait(trait);

        PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(data.serializeNBT()), player);

        return 1;
    }
}
