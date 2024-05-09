package radon.jujutsu_kaisen.network.packet.c2s;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.AbilityHandler;
import radon.jujutsu_kaisen.ability.registry.JJKAbilities;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.cursed_speech.ICursedSpeechData;
import net.neoforged.neoforge.network.PacketDistributor;
import radon.jujutsu_kaisen.network.packet.s2c.SetOverlayMessageS2CPacket;
import radon.jujutsu_kaisen.network.packet.s2c.TriggerAbilityS2CPacket;

public record TriggerAbilityC2SPacket(Ability ability) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<TriggerAbilityC2SPacket> TYPE = new CustomPacketPayload.Type<>(new ResourceLocation(JujutsuKaisen.MOD_ID, "trigger_ability_serverbound"));
    public static final StreamCodec<? super RegistryFriendlyByteBuf, TriggerAbilityC2SPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.registry(JJKAbilities.ABILITY_KEY),
            TriggerAbilityC2SPacket::ability,
            TriggerAbilityC2SPacket::new
    );

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!(ctx.player() instanceof ServerPlayer sender)) return;

            Ability.Status status;

            if ((status = AbilityHandler.trigger(sender, this.ability)) == Ability.Status.SUCCESS) {
                PacketDistributor.sendToPlayer(sender, new TriggerAbilityS2CPacket(this.ability));
            } else {
                IJujutsuCapability cap = sender.getCapability(JujutsuCapabilityHandler.INSTANCE);

                if (cap == null) return;

                IAbilityData abilityData = cap.getAbilityData();
                ICursedSpeechData cursedSpeechData = cap.getCursedSpeechData();

                switch (status) {
                    case FAILURE ->
                            PacketDistributor.sendToPlayer(sender, new SetOverlayMessageS2CPacket(Component.translatable(String.format("technique.%s.fail.failure",
                                    JujutsuKaisen.MOD_ID)), false));
                    case ENERGY ->
                            PacketDistributor.sendToPlayer(sender, new SetOverlayMessageS2CPacket(Component.translatable(String.format("technique.%s.fail.energy",
                                    JujutsuKaisen.MOD_ID)), false));
                    case COOLDOWN ->
                            PacketDistributor.sendToPlayer(sender, new SetOverlayMessageS2CPacket(Component.translatable(String.format("technique.%s.fail.cooldown",
                                    JujutsuKaisen.MOD_ID), Math.max(1, abilityData.getRemainingCooldown(ability) / 20)), false));
                    case THROAT ->
                            PacketDistributor.sendToPlayer(sender, new SetOverlayMessageS2CPacket(Component.translatable(String.format("technique.%s.fail.throat",
                                    JujutsuKaisen.MOD_ID), cursedSpeechData.getThroatDamage() / 20), false));
                }
            }
        });
    }
    
    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}