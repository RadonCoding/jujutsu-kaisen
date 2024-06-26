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
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;
import radon.jujutsu_kaisen.cursed_technique.registry.JJKCursedTechniques;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.curse_manipulation.ICurseManipulationData;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;

public record SetAbsorbedC2SPacket(CursedTechnique technique) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SetAbsorbedC2SPacket> TYPE = new CustomPacketPayload.Type<>(new ResourceLocation(JujutsuKaisen.MOD_ID, "set_absorbed_serverbound"));
    public static final StreamCodec<? super RegistryFriendlyByteBuf, SetAbsorbedC2SPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.registry(JJKCursedTechniques.CURSED_TECHNIQUE_KEY),
            SetAbsorbedC2SPacket::technique,
            SetAbsorbedC2SPacket::new
    );

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!(ctx.player() instanceof ServerPlayer sender)) return;

            if (sender.isSpectator()) return;

            IJujutsuCapability cap = sender.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap == null) return;

            ISorcererData sorcererData = cap.getSorcererData();

            if (!sorcererData.hasActiveTechnique(JJKCursedTechniques.CURSE_MANIPULATION.get())) return;

            ICurseManipulationData curseManipulationData = cap.getCurseManipulationData();

            if (!curseManipulationData.getAbsorbed().contains(this.technique)) return;

            curseManipulationData.setCurrentAbsorbed(this.technique);
        });
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}