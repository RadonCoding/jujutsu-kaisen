package radon.jujutsu_kaisen.network.packet.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.BindingVow;

import java.util.function.Supplier;

public class RemoveChantC2SPacket {
    private final ResourceLocation key;
    private final String chant;

    public RemoveChantC2SPacket(ResourceLocation key, String chant) {
        this.key = key;
        this.chant = chant;
    }

    public RemoveChantC2SPacket(FriendlyByteBuf buf) {
        this(buf.readResourceLocation(), buf.readUtf());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeResourceLocation(this.key);
        buf.writeUtf(this.chant);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();

        ctx.enqueueWork(() -> {
            ServerPlayer sender = ctx.getSender();

            assert sender != null;

            Ability ability = JJKAbilities.getValue(this.key);

            if (ability == null) return;

            ISorcererData cap = sender.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
            cap.removeChant(ability, this.chant);
        });
        ctx.setPacketHandled(true);
    }
}