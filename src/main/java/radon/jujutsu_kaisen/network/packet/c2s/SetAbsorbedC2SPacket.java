package radon.jujutsu_kaisen.network.packet.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.curse_manipulation.ICurseManipulationData;
import radon.jujutsu_kaisen.cursed_technique.JJKCursedTechniques;
import radon.jujutsu_kaisen.cursed_technique.base.ICursedTechnique;

public class SetAbsorbedC2SPacket implements CustomPacketPayload {
    public static final ResourceLocation IDENTIFIER = new ResourceLocation(JujutsuKaisen.MOD_ID, "set_absorbed_serverbound");

    private final ICursedTechnique technique;

    public SetAbsorbedC2SPacket(ICursedTechnique technique) {
        this.technique = technique;
    }

    public SetAbsorbedC2SPacket(FriendlyByteBuf buf) {
        this(JJKCursedTechniques.getValue(buf.readResourceLocation()));
    }

    public void handle(PlayPayloadContext ctx) {
        ctx.workHandler().execute(() -> {
            if (!(ctx.player().orElseThrow() instanceof ServerPlayer sender)) return;

            IJujutsuCapability cap = sender.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap == null) return;

            ICurseManipulationData data = cap.getCurseManipulationData();

            if (!JJKAbilities.hasActiveTechnique(sender, JJKCursedTechniques.CURSE_MANIPULATION.get())) return;

            if (!data.getAbsorbed().contains(this.technique)) return;

            data.setCurrentAbsorbed(this.technique);
        });
    }

    @Override
    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeResourceLocation(JJKCursedTechniques.getKey(this.technique));
    }

    @Override
    public ResourceLocation id() {
        return IDENTIFIER;
    }
}