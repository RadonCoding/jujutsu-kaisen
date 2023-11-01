package radon.jujutsu_kaisen.network.packet.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.config.ConfigHolder;

import java.util.function.Supplier;

public class AddChantC2SPacket {
    private final ResourceLocation key;
    private final String chant;

    public AddChantC2SPacket(ResourceLocation key, String chant) {
        this.key = key;
        this.chant = chant;
    }

    public AddChantC2SPacket(FriendlyByteBuf buf) {
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

            if (this.chant.length() > ConfigHolder.SERVER.maximumChantLength.get()) return;

            Ability ability = JJKAbilities.getValue(this.key);

            if (ability == null || !ability.isScalable() || !ability.isTechnique()) return;

            ISorcererData cap = sender.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

            if (cap.getChants(ability).size() == ConfigHolder.SERVER.maximumChantCount.get() || this.chant.isEmpty() || this.chant.isBlank() || cap.hasChant(ability, this.chant))
                return;

            cap.addChant(ability, this.chant);
        });
        ctx.setPacketHandled(true);
    }
}