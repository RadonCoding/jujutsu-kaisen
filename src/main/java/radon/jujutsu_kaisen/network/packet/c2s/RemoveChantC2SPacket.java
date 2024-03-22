package radon.jujutsu_kaisen.network.packet.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
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
import radon.jujutsu_kaisen.data.chant.IChantData;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;

public class RemoveChantC2SPacket implements CustomPacketPayload {
    public static final ResourceLocation IDENTIFIER = new ResourceLocation(JujutsuKaisen.MOD_ID, "remove_chant_serverbound");

    private final ResourceLocation key;
    private final String chant;

    public RemoveChantC2SPacket(ResourceLocation key, String chant) {
        this.key = key;
        this.chant = chant;
    }

    public RemoveChantC2SPacket(FriendlyByteBuf buf) {
        this(buf.readResourceLocation(), buf.readUtf());
    }

    public void handle(PlayPayloadContext ctx) {
        ctx.workHandler().execute(() -> {
            if (!(ctx.player().orElseThrow() instanceof ServerPlayer sender)) return;

            Ability ability = JJKAbilities.getValue(this.key);

            if (ability == null) return;

            IJujutsuCapability cap = sender.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap == null) return;

            IChantData data = cap.getChantData();

            data.removeChant(ability, this.chant);
        });
    }

    @Override
    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeResourceLocation(this.key);
        pBuffer.writeUtf(this.chant);
    }

    @Override
    public @NotNull ResourceLocation id() {
        return IDENTIFIER;
    }
}