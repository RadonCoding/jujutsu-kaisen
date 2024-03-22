package radon.jujutsu_kaisen.network.packet.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.AbilityHandler;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.IAttack;
import radon.jujutsu_kaisen.ability.base.IChanneled;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.ICharged;
import radon.jujutsu_kaisen.ability.base.IDomainAttack;
import radon.jujutsu_kaisen.ability.base.IDurationable;
import radon.jujutsu_kaisen.ability.base.ITenShadowsAttack;
import radon.jujutsu_kaisen.ability.base.IToggled;

public class UntriggerAbilityC2SPacket implements CustomPacketPayload {
    public static final ResourceLocation IDENTIFIER = new ResourceLocation(JujutsuKaisen.MOD_ID, "untrigger_ability_serverbound");

    private final ResourceLocation key;

    public UntriggerAbilityC2SPacket(ResourceLocation key) {
        this.key = key;
    }

    public UntriggerAbilityC2SPacket(FriendlyByteBuf buf) {
        this(buf.readResourceLocation());
    }

    public void handle(PlayPayloadContext ctx) {
        ctx.workHandler().execute(() -> {
            if (!(ctx.player().orElseThrow() instanceof ServerPlayer sender)) return;

            Ability ability = JJKAbilities.getValue(this.key);

            if (ability == null) return;

            AbilityHandler.untrigger(sender, ability);
        });
    }

    @Override
    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeResourceLocation(this.key);
    }

    @Override
    public @NotNull ResourceLocation id() {
        return IDENTIFIER;
    }
}