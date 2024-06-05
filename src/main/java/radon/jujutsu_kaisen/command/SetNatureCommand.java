package radon.jujutsu_kaisen.command;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.server.command.EnumArgument;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.sorcerer.CursedEnergyNature;
import net.neoforged.neoforge.network.PacketDistributor;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;

public class SetNatureCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralCommandNode<CommandSourceStack> node = dispatcher.register(Commands.literal("jjksetnature")
                .requires((player) -> player.hasPermission(2))
                .then(Commands.argument("entity", EntityArgument.entity()).then(Commands.argument("nature", EnumArgument.enumArgument(CursedEnergyNature.class)).executes((ctx) ->
                        setType(EntityArgument.getEntity(ctx, "entity"), ctx.getArgument("nature", CursedEnergyNature.class))))));

        dispatcher.register(Commands.literal("jjksetnature").requires((player) -> player.hasPermission(2)).redirect(node));
    }

    public static int setType(Entity entity, CursedEnergyNature nature) {
        IJujutsuCapability cap = entity.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return 0;

        ISorcererData data = cap.getSorcererData();

        data.setNature(nature);

        if (entity instanceof ServerPlayer player) {
            PacketDistributor.sendToPlayer(player, new SyncSorcererDataS2CPacket(data.serializeNBT(player.registryAccess())));
        }
        return 1;
    }
}
