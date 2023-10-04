package radon.jujutsu_kaisen.network.packet.s2c;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.client.ClientWrapper;

import java.util.Set;
import java.util.function.Supplier;

public class SyncSorcererDataS2CPacket {
    private final CompoundTag nbt;

    public SyncSorcererDataS2CPacket(CompoundTag nbt) {
        this.nbt = nbt;
    }

    public SyncSorcererDataS2CPacket(FriendlyByteBuf buf) {
        this(buf.readNbt());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeNbt(this.nbt);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();

        ctx.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            Player player = ClientWrapper.getPlayer();

            assert player != null;

            player.getCapability(SorcererDataHandler.INSTANCE).ifPresent(oldCap -> {
                ISorcererData newCap = new SorcererData();
                newCap.deserializeNBT(this.nbt);

                Set<Ability> oldToggled = oldCap.getToggled();
                Set<Ability> newToggled = newCap.getToggled();

                oldToggled.removeAll(newToggled);

                for (Ability ability : oldToggled) {
                    oldCap.toggle(player, ability);
                }
                oldCap.deserializeNBT(this.nbt);
            });
        }));
        ctx.setPacketHandled(true);
    }
}
