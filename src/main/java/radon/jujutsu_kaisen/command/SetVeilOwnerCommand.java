package radon.jujutsu_kaisen.command;

import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import radon.jujutsu_kaisen.block.entity.VeilRodBlockEntity;

public class SetVeilOwnerCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralCommandNode<CommandSourceStack> node = dispatcher.register(Commands.literal("jjksetveilowner")
                .requires((player) -> player.hasPermission(2))
                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                        .then(Commands.argument("owner", EntityArgument.entity())
                        .executes((ctx) -> setVeilOwner(BlockPosArgument.getBlockPos(ctx, "pos"), EntityArgument.getEntity(ctx, "owner"))))));

        dispatcher.register(Commands.literal("jjksetveilowner").requires((player) -> player.hasPermission(2)).redirect(node));
    }

    public static int setVeilOwner(BlockPos pos, Entity entity) {
        if (!(entity.level().getBlockEntity(pos) instanceof VeilRodBlockEntity be)) return 0;

        be.setOwnerUUID(entity.getUUID());

        return 1;
    }
}
