package radon.jujutsu_kaisen.command;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.server.command.EnumArgument;
import radon.jujutsu_kaisen.data.sorcerer.SorcererGrade;

public class SetGradeCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralCommandNode<CommandSourceStack> node = dispatcher.register(Commands.literal("jjksetgrade")
                .requires(player -> player.hasPermission(2))
                .then(Commands.argument("entity", EntityArgument.entity()).then(Commands.argument("grade", EnumArgument.enumArgument(SorcererGrade.class))
                        .executes(ctx -> setGrade(EntityArgument.getEntity(ctx, "entity"), ctx.getArgument("grade", SorcererGrade.class))))));

        dispatcher.register(Commands.literal("jjksetgrade").requires((player) -> player.hasPermission(2)).redirect(node));
    }

    public static int setGrade(Entity entity, SorcererGrade grade) {
        return SetExperienceCommand.setExperience(entity, grade.getRequiredExperience());
    }
}
