package radon.jujutsu_kaisen.network.packet.c2s;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.registry.JJKAbilities;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;

public record UnlockAbilityC2SPacket(ResourceLocation key) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<UnlockAbilityC2SPacket> TYPE = new CustomPacketPayload.Type<>(new ResourceLocation(JujutsuKaisen.MOD_ID, "unlock_ability_serverbound"));
    public static final StreamCodec<? super RegistryFriendlyByteBuf, UnlockAbilityC2SPacket> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC,
            UnlockAbilityC2SPacket::key,
            UnlockAbilityC2SPacket::new
    );

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!(ctx.player() instanceof ServerPlayer sender)) return;

            IJujutsuCapability cap = sender.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        ISorcererData data = cap.getSorcererData();

            Ability ability = JJKAbilities.getValue(this.key);

            if (ability == null) return;

            if (ability.canUnlock(sender)) {
                if (!sender.getAbilities().instabuild) {
                    data.useAbilityPoints(ability.getRealPointsCost(sender));
                }
                data.unlock(ability);
            }
        });
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}