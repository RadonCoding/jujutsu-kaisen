package radon.jujutsu_kaisen.network.packet.c2s;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.cursed_technique.registry.JJKCursedTechniques;
import radon.jujutsu_kaisen.ability.curse_manipulation.util.CurseManipulationUtil;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;

public record CurseSummonC2SPacket(int index) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<CurseSummonC2SPacket> TYPE = new CustomPacketPayload.Type<>(new ResourceLocation(JujutsuKaisen.MOD_ID, "curse_summon_serverbound"));
    public static final StreamCodec<? super RegistryFriendlyByteBuf, CurseSummonC2SPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            CurseSummonC2SPacket::index,
            CurseSummonC2SPacket::new
    );

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!(ctx.player() instanceof ServerPlayer sender)) return;

            if (sender.isSpectator()) return;

            IJujutsuCapability cap = sender.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap == null) return;

            ISorcererData data = cap.getSorcererData();

            if (data.hasActiveTechnique(JJKCursedTechniques.CURSE_MANIPULATION.get())) {
                CurseManipulationUtil.summonCurse(sender, this.index, true);
            }
        });
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}