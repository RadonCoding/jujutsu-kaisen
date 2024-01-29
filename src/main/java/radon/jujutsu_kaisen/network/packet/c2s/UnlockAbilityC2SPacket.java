package radon.jujutsu_kaisen.network.packet.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;

import java.util.function.Supplier;

public class UnlockAbilityC2SPacket {
    private final ResourceLocation key;

    public UnlockAbilityC2SPacket(ResourceLocation key) {
        this.key = key;
    }

    public UnlockAbilityC2SPacket(FriendlyByteBuf buf) {
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

            ISorcererData cap = sender.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

            Ability ability = JJKAbilities.getValue(this.key);

            if (ability == null) return;

            if (ability.canUnlock(sender)) {
                if (!sender.getAbilities().instabuild) {
                    cap.usePoints(ability.getRealPointsCost(sender));
                }
                cap.unlock(ability);
            }
        });
        ctx.setPacketHandled(true);
    }
}