package radon.jujutsu_kaisen.network.packet.c2s;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.registry.JJKAbilities;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.mimicry.IMimicryData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.cursed_technique.registry.JJKCursedTechniques;
import radon.jujutsu_kaisen.cursed_technique.ICursedTechnique;

public record SetCopiedC2SPacket(ICursedTechnique technique) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SetCopiedC2SPacket> TYPE = new CustomPacketPayload.Type<>(new ResourceLocation(JujutsuKaisen.MOD_ID, "set_copied_serverbound"));
    public static final StreamCodec<? super RegistryFriendlyByteBuf, SetCopiedC2SPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.registry(JJKCursedTechniques.CURSED_TECHNIQUE_KEY),
            SetCopiedC2SPacket::technique,
            SetCopiedC2SPacket::new
    );

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!(ctx.player() instanceof ServerPlayer sender)) return;

            if (sender.isSpectator()) return;

            IJujutsuCapability cap = sender.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap == null) return;

            IAbilityData abilityData = cap.getAbilityData();
            IMimicryData mimicryData = cap.getMimicryData();

            if (abilityData.hasToggled(JJKAbilities.RIKA.get()) && mimicryData.hasCopied(this.technique)) {
                mimicryData.setCurrentCopied(this.technique);
            }
        });
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}