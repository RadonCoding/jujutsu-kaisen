package radon.jujutsu_kaisen.network.packet.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.entity.ConnectedLightningEntity;
import radon.jujutsu_kaisen.entity.NyoiStaffEntity;

import java.util.UUID;

public class NyoiStaffSummonLightningC2SPacket implements CustomPacketPayload {
    public static final ResourceLocation IDENTIFIER = new ResourceLocation(JujutsuKaisen.MOD_ID, "nyoi_staff_summon_lightning_serverbound");

    private final UUID identifier;

    public NyoiStaffSummonLightningC2SPacket(UUID identifier) {
        this.identifier = identifier;
    }

    public NyoiStaffSummonLightningC2SPacket(FriendlyByteBuf buf) {
        this(buf.readUUID());
    }

    public void handle(PlayPayloadContext ctx) {
        ctx.workHandler().execute(() -> {
            if (!(ctx.player().orElseThrow() instanceof ServerPlayer sender)) return;

            if (!(sender.serverLevel().getEntity(this.identifier) instanceof NyoiStaffEntity staff)) return;
            if (!staff.isCharged() || staff.getOwner() != sender) return;

            IJujutsuCapability cap = sender.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        ISorcererData data = cap.getSorcererData();

            sender.level().addFreshEntity(new ConnectedLightningEntity(sender, data.getAbilityOutput(), sender.position().add(0.0D, sender.getBbHeight() / 2.0F, 0.0D),
                    staff.position()));

            staff.setCharged(false);
        });
    }

    @Override
    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeUUID(this.identifier);
    }

    @Override
    public ResourceLocation id() {
        return IDENTIFIER;
    }
}