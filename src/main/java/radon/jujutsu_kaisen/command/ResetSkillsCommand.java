package radon.jujutsu_kaisen.command;

import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.world.entity.Entity;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.stat.ISkillData;
import radon.jujutsu_kaisen.network.PacketHandler;
import net.neoforged.neoforge.network.PacketDistributor;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSkillDataSC2Packet;
import radon.jujutsu_kaisen.util.PlayerUtil;

public class ResetSkillsCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralCommandNode<CommandSourceStack> node = dispatcher.register(Commands.literal("jjkresetskills")
                .requires((player) -> player.hasPermission(2))
                .then(Commands.argument("entity", EntityArgument.entity()).executes((ctx) ->
                        resetSkills(EntityArgument.getEntity(ctx, "entity")))));

        dispatcher.register(Commands.literal("jjkresetskills").requires((player) -> player.hasPermission(2)).redirect(node));
    }

    public static int resetSkills(Entity entity) {
        IJujutsuCapability cap = entity.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return 0;

        ISkillData data = cap.getSkillData();

        data.resetSkills();

        if (entity instanceof ServerPlayer player) {
            PacketDistributor.sendToPlayer(player, new SyncSkillDataSC2Packet(data.serializeNBT(player.registryAccess())));
        }
        return 1;
    }
}
