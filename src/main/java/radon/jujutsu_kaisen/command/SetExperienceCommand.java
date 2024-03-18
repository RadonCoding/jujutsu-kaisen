package radon.jujutsu_kaisen.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;

public class SetExperienceCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralCommandNode<CommandSourceStack> node = dispatcher.register(Commands.literal("jjksetexperience")
                .requires((player) -> player.hasPermission(2))
                .then(Commands.argument("entity", EntityArgument.entity()).then(Commands.argument("experience", FloatArgumentType.floatArg())
                        .executes(ctx -> setExperience(EntityArgument.getEntity(ctx, "entity"), FloatArgumentType.getFloat(ctx, "experience"))))));

        dispatcher.register(Commands.literal("jjksetexperience").requires((player) -> player.hasPermission(2)).redirect(node));
    }

    public static int setExperience(Entity entity, float experience) {
        IJujutsuCapability cap = entity.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return 0;

        ISorcererData data = cap.getSorcererData();

        data.setExperience(experience);

        int abilityPoints = Math.round(data.getExperience() / ConfigHolder.SERVER.abilityPointInterval.get().floatValue());
        int skillPoints = Math.round(data.getExperience() / ConfigHolder.SERVER.skillPointInterval.get().floatValue());

        data.setAbilityPoints(abilityPoints);
        data.setSkillPoints(skillPoints);

        if (entity instanceof ServerPlayer player) {
            PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(data.serializeNBT()), player);
        }
        return 1;
    }
}
