package radon.jujutsu_kaisen.network.packet.s2c;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.IAttack;
import radon.jujutsu_kaisen.ability.base.IChanneled;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.ICharged;
import radon.jujutsu_kaisen.ability.base.IDomainAttack;
import radon.jujutsu_kaisen.ability.base.IDurationable;
import radon.jujutsu_kaisen.ability.base.ITenShadowsAttack;
import radon.jujutsu_kaisen.ability.base.IToggled;
import radon.jujutsu_kaisen.client.ClientWrapper;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.projection_sorcery.IProjectionSorceryData;

import java.util.HashSet;
import java.util.Set;

public class SyncAbilityDataS2CPacket implements CustomPacketPayload {
    public static final ResourceLocation IDENTIFIER = new ResourceLocation(JujutsuKaisen.MOD_ID, "sync_ability_data_clientbound");

    private final CompoundTag nbt;

    public SyncAbilityDataS2CPacket(CompoundTag nbt) {
        this.nbt = nbt;
    }

    public SyncAbilityDataS2CPacket(FriendlyByteBuf buf) {
        this(buf.readNbt());
    }

    public void handle(PlayPayloadContext ctx) {
        ctx.workHandler().execute(() -> {
            Player player = ClientWrapper.getPlayer();

            if (player == null) return;

            IJujutsuCapability cap = player.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap == null) return;

            IAbilityData data = cap.getAbilityData();

            Set<Ability> newToggled = new HashSet<>();

            for (Tag key : this.nbt.getList("toggled", Tag.TAG_STRING)) {
                newToggled.add(JJKAbilities.getValue(new ResourceLocation(key.getAsString())));
            }

            Set<Ability> oldToggled = new HashSet<>(data.getToggled());
            oldToggled.removeAll(newToggled);

            for (Ability ability : oldToggled) {
                data.toggle(ability);
            }
            data.deserializeNBT(this.nbt);
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
