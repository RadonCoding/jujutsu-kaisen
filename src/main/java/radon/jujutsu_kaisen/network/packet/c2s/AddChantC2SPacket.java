package radon.jujutsu_kaisen.network.packet.c2s;

import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.registry.JJKAbilities;
import radon.jujutsu_kaisen.data.chant.IChantData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.util.HelperMethods;

public record AddChantC2SPacket(Ability ability, String chant) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<AddChantC2SPacket> TYPE = new CustomPacketPayload.Type<>(new ResourceLocation(JujutsuKaisen.MOD_ID, "add_chant_serverbound"));
    public static final StreamCodec<? super RegistryFriendlyByteBuf, AddChantC2SPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.registry(JJKAbilities.ABILITY_KEY),
            AddChantC2SPacket::ability,
            ByteBufCodecs.STRING_UTF8,
            AddChantC2SPacket::chant,
            AddChantC2SPacket::new
    );

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!(ctx.player() instanceof ServerPlayer sender)) return;

            if (this.chant.length() > ConfigHolder.SERVER.maximumChantLength.get()) return;

            if (!this.ability.isScalable(sender) || !this.ability.isChantable()) return;

            IJujutsuCapability cap = sender.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap == null) return;

            IChantData data = cap.getChantData();

            String text = this.chant.toLowerCase();

            if (!text.isEmpty() && !text.isBlank()) {
                for (String chant : data.getFirstChants(this.ability)) {
                    if (HelperMethods.strcmp(chant, text) < ConfigHolder.SERVER.chantSimilarityThreshold.get()) {
                        return;
                    }
                }
            }

            if (data.getFirstChants(this.ability).size() == ConfigHolder.SERVER.maximumChantCount.get() || text.isEmpty() || text.isBlank() || data.hasChant(this.ability, text))
                return;

            data.addChant(this.ability, text);
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}