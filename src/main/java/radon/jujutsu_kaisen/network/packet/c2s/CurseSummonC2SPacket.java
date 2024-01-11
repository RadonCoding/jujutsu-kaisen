package radon.jujutsu_kaisen.network.packet.c2s;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.network.NetworkEvent;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.AbsorbedCurse;

import java.util.function.Supplier;

public class CurseSummonC2SPacket {
    private final CompoundTag nbt;

    public CurseSummonC2SPacket(CompoundTag nbt) {
        this.nbt = nbt;
    }

    public CurseSummonC2SPacket(FriendlyByteBuf buf) {
        this(buf.readNbt());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeNbt(this.nbt);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();

        ctx.enqueueWork(() -> {
            ServerPlayer sender = ctx.getSender();

            assert sender != null;

            AbsorbedCurse curse = new AbsorbedCurse(this.nbt);
            JJKAbilities.summonCurse(sender, curse);
        });
        ctx.setPacketHandled(true);
    }
}