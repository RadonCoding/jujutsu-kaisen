package radon.jujutsu_kaisen.network.packet.s2c;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.sorcerer.SorcererData;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;
import radon.jujutsu_kaisen.client.ClientWrapper;

import java.util.Set;

public class SyncSorcererDataS2CPacket implements CustomPacketPayload {
    public static final ResourceLocation IDENTIFIER = new ResourceLocation(JujutsuKaisen.MOD_ID, "sync_sorcerer_data_clientbound");
    
    private final CompoundTag nbt;

    public SyncSorcererDataS2CPacket(CompoundTag nbt) {
        this.nbt = nbt;
    }

    public SyncSorcererDataS2CPacket(FriendlyByteBuf buf) {
        this(buf.readNbt());
    }

    public void handle(PlayPayloadContext ctx) {
        ctx.workHandler().execute(() -> {
            Player player = ClientWrapper.getPlayer();

            if (player == null) return;

            ISorcererData old = player.getData(JJKAttachmentTypes.SORCERER);

            ISorcererData tmp = new SorcererData(null);
            tmp.deserializeNBT(this.nbt);

            Set<Ability> oldToggled = old.getToggled();
            Set<Ability> newToggled = tmp.getToggled();

            oldToggled.removeAll(newToggled);

            for (Ability ability : oldToggled) {
                old.toggle(ability);
            }
            old.deserializeNBT(this.nbt);
        });
    }

    @Override
    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeNbt(this.nbt);
    }

    @Override
    public @NotNull ResourceLocation id() {
        return IDENTIFIER;
    }
}
