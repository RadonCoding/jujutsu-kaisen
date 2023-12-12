package radon.jujutsu_kaisen.network.packet.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
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

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();

        ctx.enqueueWork(() -> {
            ServerPlayer sender = ctx.getSender();

            assert sender != null;

            Ability ability = JJKAbilities.getValue(this.key);

            if (!(ability instanceof ITransformation transformation)) return;

            transformation.onRightClick(sender);
        });
        ctx.setPacketHandled(true);
    }
}