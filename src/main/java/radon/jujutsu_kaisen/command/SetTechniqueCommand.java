package radon.jujutsu_kaisen.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.cursed_technique.base.ICursedTechnique;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncAbilityDataS2CPacket;
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
        IJujutsuCapability cap = player.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return 0;

        ISorcererData sorcererData = cap.getSorcererData();
        IAbilityData abilityData = cap.getAbilityData();

        sorcererData.setTechnique(technique);

        abilityData.clearToggled();
        abilityData.channel(null);

        PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(sorcererData.serializeNBT()), player);
        PacketHandler.sendToClient(new SyncAbilityDataS2CPacket(sorcererData.serializeNBT()), player);

        return 1;
    }
}
