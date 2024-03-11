package radon.jujutsu_kaisen.network.packet.s2c;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.AbilityHandler;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.client.event.ClientAbilityHandler;
import radon.jujutsu_kaisen.network.PacketHandler;

public class TriggerAbilityS2CPacket implements CustomPacketPayload {
    public static final ResourceLocation IDENTIFIER = new ResourceLocation(JujutsuKaisen.MOD_ID, "trigger_ability_clientbound");

    private final ResourceLocation key;

    public TriggerAbilityS2CPacket(ResourceLocation key) {
        this.key = key;
    }

    public TriggerAbilityS2CPacket(FriendlyByteBuf buf) {
        this(buf.readResourceLocation());
    }

    public void handle(PlayPayloadContext ctx) {
        ctx.workHandler().execute(() -> {
            Ability ability = JJKAbilities.getValue(this.key);

            if (ability == null) return;

            ClientAbilityHandler.trigger(ability);
        });
    }

    @Override
    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeResourceLocation(this.key);
    }

    @Override
    public @NotNull ResourceLocation id() {
        return IDENTIFIER;
    }
}