package radon.jujutsu_kaisen.network.packet.s2c;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.registry.JJKAbilities;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.client.ClientWrapper;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;

import java.util.HashSet;
import java.util.Set;

public record SyncAbilityDataS2CPacket(CompoundTag nbt) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SyncAbilityDataS2CPacket> TYPE = new CustomPacketPayload.Type<>(new ResourceLocation(JujutsuKaisen.MOD_ID, "sync_ability_data_clientbound"));
    public static final StreamCodec<? super RegistryFriendlyByteBuf, SyncAbilityDataS2CPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.COMPOUND_TAG,
            SyncAbilityDataS2CPacket::nbt,
            SyncAbilityDataS2CPacket::new
    );

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player player = ClientWrapper.getPlayer();

            if (player == null) return;

            IJujutsuCapability cap = player.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap == null) return;

            IAbilityData data = cap.getAbilityData();

            Set<Ability> newToggled = new HashSet<>();

            for (Tag tag : this.nbt.getList("toggled", Tag.TAG_STRING)) {
                newToggled.add(JJKAbilities.getValue(new ResourceLocation(tag.getAsString())));
            }

            Set<Ability> oldToggled = new HashSet<>(data.getToggled());
            oldToggled.removeAll(newToggled);

            for (Ability ability : oldToggled) {
                data.toggle(ability);
            }
            data.deserializeNBT(player.registryAccess(), this.nbt);
        });
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
