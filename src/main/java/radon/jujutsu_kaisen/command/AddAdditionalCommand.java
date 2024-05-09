package radon.jujutsu_kaisen.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import radon.jujutsu_kaisen.command.argument.CursedTechniqueArgument;
import radon.jujutsu_kaisen.cursed_technique.ICursedTechnique;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import net.neoforged.neoforge.network.PacketDistributor;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;

public class AddAdditionalCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralCommandNode<CommandSourceStack> node = dispatcher.register(Commands.literal("jjkaddadditional")
                .requires((player) -> player.hasPermission(2))
                .then(Commands.argument("entity", EntityArgument.entity()).then(Commands.argument("technique", CursedTechniqueArgument.cursedTechnique())
                        .executes(ctx -> addAdditional(EntityArgument.getEntity(ctx, "entity"), CursedTechniqueArgument.getTechnique(ctx, "technique"))))));

        dispatcher.register(Commands.literal("jjkaddadditional").requires((player) -> player.hasPermission(2)).redirect(node));
    }

    public static int addAdditional(Entity entity, ICursedTechnique technique) {
        IJujutsuCapability cap = entity.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return 0;

        ISorcererData data = cap.getSorcererData();

        data.addAdditional(technique);

        if (entity instanceof ServerPlayer player) {
            PacketDistributor.sendToPlayer(player, new SyncSorcererDataS2CPacket(data.serializeNBT(player.registryAccess())));
        }
        return 1;
    }
}
