package radon.jujutsu_kaisen.network.packet.c2s;

import net.minecraft.network.RegistryFriendlyByteBuf;
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

public record UntriggerAbilityC2SPacket(Ability ability) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<UntriggerAbilityC2SPacket> TYPE = new CustomPacketPayload.Type<>(new ResourceLocation(JujutsuKaisen.MOD_ID, "untrigger_ability_serverbound"));
    public static final StreamCodec<? super RegistryFriendlyByteBuf, UntriggerAbilityC2SPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.registry(JJKAbilities.ABILITY_KEY),
            UntriggerAbilityC2SPacket::ability,
            UntriggerAbilityC2SPacket::new
    );

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!(ctx.player() instanceof ServerPlayer sender)) return;

            AbilityHandler.untrigger(sender, this.ability);
        });
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}