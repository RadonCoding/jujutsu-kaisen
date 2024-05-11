package radon.jujutsu_kaisen.network.packet.s2c;

import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.registry.JJKAbilities;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.client.event.ClientAbilityHandler;

public record TriggerAbilityS2CPacket(Ability ability) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<TriggerAbilityS2CPacket> TYPE = new CustomPacketPayload.Type<>(new ResourceLocation(JujutsuKaisen.MOD_ID, "trigger_ability_clientbound"));
    public static final StreamCodec<? super RegistryFriendlyByteBuf, TriggerAbilityS2CPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.registry(JJKAbilities.ABILITY_KEY),
            TriggerAbilityS2CPacket::ability,
            TriggerAbilityS2CPacket::new
    );

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            ClientAbilityHandler.trigger(this.ability);
        });
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}