package radon.jujutsu_kaisen.network.packet.c2s;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.network.NetworkEvent;
import radon.jujutsu_kaisen.ability.JJKAbilities;

import java.util.function.Supplier;

public class CurseSummonC2SPacket {
    private final ResourceLocation key;

    public CurseSummonC2SPacket(ResourceLocation key) {
        this.key = key;
    }

    public CurseSummonC2SPacket(FriendlyByteBuf buf) {
        this(buf.readResourceLocation());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeResourceLocation(this.key);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();

        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();

            assert player != null;

            Registry<EntityType<?>> registry = player.level().registryAccess().registryOrThrow(Registries.ENTITY_TYPE);
            EntityType<?> type = registry.get(this.key);

            if (type != null) {
                JJKAbilities.summonCurse(player, type);
            }
        });
        ctx.setPacketHandled(true);
    }
}