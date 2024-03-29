package radon.jujutsu_kaisen.network.packet.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.entity.base.ICommandable;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SetOverlayMessageS2CPacket;

import java.util.UUID;

public class CommandableTargetC2SPacket implements CustomPacketPayload {
    public static final ResourceLocation IDENTIFIER = new ResourceLocation(JujutsuKaisen.MOD_ID, "sync_sorcerer_data_serverbound");

    private final UUID target;

    public CommandableTargetC2SPacket(UUID target) {
        this.target = target;
    }

    public CommandableTargetC2SPacket(FriendlyByteBuf buf) {
        this(buf.readUUID());
    }

    public void handle(PlayPayloadContext ctx) {
        ctx.workHandler().execute(() -> {
            if (!(ctx.player().orElseThrow() instanceof ServerPlayer sender)) return;

            IJujutsuCapability cap = sender.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        ISorcererData data = cap.getSorcererData();

            ServerLevel level = sender.serverLevel();
            LivingEntity target = (LivingEntity) level.getEntity(this.target);

            if (target == null) return;

            for (Entity entity : data.getSummons()) {
                if (entity instanceof ICommandable commandable) {
                    if (target == entity || (target instanceof TamableAnimal tamable && tamable.isTame() && tamable.getOwner() == sender))
                        continue;

                    if (commandable.canChangeTarget()) {
                        commandable.changeTarget(target);

                        PacketHandler.sendToClient(new SetOverlayMessageS2CPacket(Component.translatable(String.format("chat.%s.set_target", JujutsuKaisen.MOD_ID),
                                entity.getName()), false), sender);
                    }
                }
            }
        });
    }

    @Override
    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeUUID(this.target);
    }

    @Override
    public ResourceLocation id() {
        return IDENTIFIER;
    }
}