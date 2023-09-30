package radon.jujutsu_kaisen.network.packet.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.network.CustomPayloadEvent;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.AbilityHandler;
import radon.jujutsu_kaisen.ability.JJKAbilities;

public class TriggerAbilityC2SPacket {
    private final ResourceLocation key;

    public TriggerAbilityC2SPacket(ResourceLocation key) {
        this.key = key;
    }

    public TriggerAbilityC2SPacket(FriendlyByteBuf buf) {
        this(buf.readResourceLocation());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeResourceLocation(this.key);
    }

    public void handle(CustomPayloadEvent.Context ctx) {
        Ability ability = JJKAbilities.getValue(this.key);

        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            assert player != null;
            AbilityHandler.trigger(player, ability);
        });
        ctx.setPacketHandled(true);
    }
}