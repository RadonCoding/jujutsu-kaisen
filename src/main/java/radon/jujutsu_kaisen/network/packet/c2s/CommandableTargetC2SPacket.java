package radon.jujutsu_kaisen.network.packet.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkEvent;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.entity.base.ICommandable;
import radon.jujutsu_kaisen.network.PacketHandler;

import java.util.UUID;
import java.util.function.Supplier;

public class CommandableTargetC2SPacket {
    private final UUID target;

    public CommandableTargetC2SPacket(UUID target) {
        this.target = target;
    }

    public CommandableTargetC2SPacket(FriendlyByteBuf buf) {
        this(buf.readUUID());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUUID(this.target);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();

        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();

            assert player != null;

            player.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                ServerLevel level = player.getLevel();
                LivingEntity target = (LivingEntity) level.getEntity(this.target);

                if (target == null) return;

                for (Entity summon : cap.getSummons(level)) {
                    if (summon instanceof ICommandable commandable) {
                        if (target == summon) continue;

                        if (commandable.changeTarget(target)) {
                            PacketHandler.sendToClient(new SetOverlayMessageS2CPacket(Component.translatable(String.format("chat.%s.set_target", JujutsuKaisen.MOD_ID),
                                    summon.getName()), false), player);
                        }
                    }
                }
            });
        });
        ctx.setPacketHandled(true);
    }
}