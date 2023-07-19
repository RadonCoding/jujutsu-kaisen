package radon.jujutsu_kaisen.network.packet.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import radon.jujutsu_kaisen.item.PistolItem;

import java.util.function.Supplier;

public class ShootPistolC2SPacket {
    private final float pitch;
    private final float yaw;

    public ShootPistolC2SPacket(float pitch, float yaw) {
        this.pitch = pitch;
        this.yaw = yaw;
    }

    public ShootPistolC2SPacket(FriendlyByteBuf buf) {
        this(buf.readFloat(), buf.readFloat());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeFloat(this.pitch);
        buf.writeFloat(this.yaw);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();

        ctx.enqueueWork(() -> {
            ServerPlayer sender = ctx.getSender();

            assert sender != null;

            sender.setYRot(Mth.wrapDegrees(this.yaw));
            sender.setXRot(Mth.clamp(this.pitch, -90.0F, 90.0F));
            
            ItemStack stack = sender.getMainHandItem();
            PistolItem.shoot(stack, sender);
        });
        ctx.setPacketHandled(true);
    }
}