package radon.jujutsu_kaisen.network.packet.c2s;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.entity.base.CursedSpirit;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;

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

            player.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                Registry<EntityType<?>> registry = player.level.registryAccess().registryOrThrow(Registries.ENTITY_TYPE);
                EntityType<?> type = registry.get(this.key);

                if (type == null) return;

                if (!cap.hasCurse(registry, type)) return;

                if (type.create(player.level) instanceof CursedSpirit curse) {
                    Vec3 pos = player.position().subtract(player.getLookAngle()
                            .multiply(curse.getBbWidth(), 0.0D, curse.getBbWidth()));
                    curse.moveTo(pos.x(), pos.y(), pos.z(), player.getYRot(), player.getXRot());
                    curse.setTame(true);
                    curse.setOwner(player);
                    player.level.addFreshEntity(curse);

                    cap.addSummon(curse);

                    cap.removeCurse(registry, type);

                    PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(cap.serializeNBT()), player);
                }
            });
        });
        ctx.setPacketHandled(true);
    }
}