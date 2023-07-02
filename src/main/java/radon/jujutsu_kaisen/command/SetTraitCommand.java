package radon.jujutsu_kaisen.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.server.command.EnumArgument;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.SyncSorcererDataS2CPacket;

public class SetTraitCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralCommandNode<CommandSourceStack> node = dispatcher.register(Commands.literal("settrait")
                .requires((player) -> player.hasPermission(2))
                .then(Commands.argument("player", EntityArgument.entity()).then(Commands.argument("trait", EnumArgument.enumArgument(Trait.class)).executes((ctx) ->
                        setTrait(EntityArgument.getPlayer(ctx, "player"), ctx.getArgument("trait", Trait.class))))));

        dispatcher.register(Commands.literal("settrait").requires((player) -> player.hasPermission(2)).redirect(node));
    }

    public static int setTrait(ServerPlayer player, Trait trait) {
        player.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
            cap.setTrait(trait);

            if (trait == Trait.HEAVENLY_RESTRICTION) {
                cap.setTechnique(CursedTechnique.NONE);
            }
            PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(cap.serializeNBT()), player);
        });
        return 1;
    }
}
