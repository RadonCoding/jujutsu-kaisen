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
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.util.HelperMethods;

public class AddChantC2SPacket implements CustomPacketPayload {
    public static final ResourceLocation IDENTIFIER = new ResourceLocation(JujutsuKaisen.MOD_ID, "add_chant_serverbound");

    private final ResourceLocation key;
    private final String chant;

    public AddChantC2SPacket(ResourceLocation key, String chant) {
        this.key = key;
        this.chant = chant;
    }

    public AddChantC2SPacket(FriendlyByteBuf buf) {
        this(buf.readResourceLocation(), buf.readUtf());
    }

    public void handle(PlayPayloadContext ctx) {
        ctx.workHandler().execute(() -> {
            if (!(ctx.player().orElseThrow() instanceof ServerPlayer sender)) return;

            if (this.chant.length() > ConfigHolder.SERVER.maximumChantLength.get()) return;

            Ability ability = JJKAbilities.getValue(this.key);

            if (ability == null || !ability.isScalable(sender) || !ability.isChantable()) return;

            IJujutsuCapability cap = sender.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap == null) return;

            IChantData data = cap.getChantData();

            String text = this.chant.toLowerCase();

            if (!text.isEmpty() && !text.isBlank()) {
                for (String chant : data.getFirstChants(ability)) {
                    if (HelperMethods.strcmp(chant, text) < ConfigHolder.SERVER.chantSimilarityThreshold.get()) {
                        return;
                    }
                }
            }

            if (data.getFirstChants(ability).size() == ConfigHolder.SERVER.maximumChantCount.get() || text.isEmpty() || text.isBlank() || data.hasChant(ability, text))
                return;

            data.addChant(ability, text);
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