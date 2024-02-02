package radon.jujutsu_kaisen.network.packet.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.NetworkEvent;
import radon.jujutsu_kaisen.ability.AbilityHandler;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.ITransformation;

import java.util.function.Supplier;

public class TransformationRightClickC2SPacket {
    private final ResourceLocation key;

    public TransformationRightClickC2SPacket(ResourceLocation key) {
        this.key = key;
    }

    public TransformationRightClickC2SPacket(FriendlyByteBuf buf) {
        this(buf.readResourceLocation());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeResourceLocation(this.key);
    }

    public void handle(NetworkEvent.Context ctx) {
        ctx.enqueueWork(() -> {
            ServerPlayer sender = ctx.getSender();

            if (sender == null) return;

            Ability ability = JJKAbilities.getValue(this.key);

            if (!(ability instanceof ITransformation transformation)) return;

            transformation.onRightClick(sender);
        });
        ctx.setPacketHandled(true);
    }
}