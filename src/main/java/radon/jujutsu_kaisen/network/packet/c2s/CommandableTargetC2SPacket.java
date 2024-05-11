package radon.jujutsu_kaisen.network.packet.c2s;

import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.entity.ICommandable;
import net.neoforged.neoforge.network.PacketDistributor;
import radon.jujutsu_kaisen.network.packet.s2c.SetOverlayMessageS2CPacket;

import java.util.UUID;

public record CommandableTargetC2SPacket(UUID target) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<CommandableTargetC2SPacket> TYPE = new CustomPacketPayload.Type<>(new ResourceLocation(JujutsuKaisen.MOD_ID, "sync_sorcerer_data_serverbound"));
    public static final StreamCodec<? super RegistryFriendlyByteBuf, CommandableTargetC2SPacket> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC,
            CommandableTargetC2SPacket::target,
            CommandableTargetC2SPacket::new
    );

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!(ctx.player() instanceof ServerPlayer sender)) return;

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

                        PacketDistributor.sendToPlayer(sender, new SetOverlayMessageS2CPacket(Component.translatable(String.format("chat.%s.set_target", JujutsuKaisen.MOD_ID),
                                entity.getName()), false));
                    }
                }
            }
        });
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}