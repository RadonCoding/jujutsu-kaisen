package radon.jujutsu_kaisen.network.packet.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.world.entity.ai.behavior.declarative.Trigger;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.AbilityHandler;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.cursed_speech.ICursedSpeechData;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SetOverlayMessageS2CPacket;
import radon.jujutsu_kaisen.network.packet.s2c.TriggerAbilityS2CPacket;

import java.util.List;

public class TriggerAbilityC2SPacket implements CustomPacketPayload {
    public static final ResourceLocation IDENTIFIER = new ResourceLocation(JujutsuKaisen.MOD_ID, "trigger_ability_serverbound");

    private final ResourceLocation key;

    public TriggerAbilityC2SPacket(ResourceLocation key) {
        this.key = key;
    }

    public TriggerAbilityC2SPacket(FriendlyByteBuf buf) {
        this(buf.readResourceLocation());
    }

    public void handle(PlayPayloadContext ctx) {
        ctx.workHandler().execute(() -> {
            if (!(ctx.player().orElseThrow() instanceof ServerPlayer sender)) return;

            Ability ability = JJKAbilities.getValue(this.key);

            if (ability == null) return;

            Ability.Status status;

            if ((status = AbilityHandler.trigger(sender, ability)) == Ability.Status.SUCCESS) {
                PacketHandler.sendToClient(new TriggerAbilityS2CPacket(this.key), sender);
            } else {
                IJujutsuCapability cap = sender.getCapability(JujutsuCapabilityHandler.INSTANCE);

                if (cap == null) return;

                IAbilityData abilityData = cap.getAbilityData();
                ICursedSpeechData cursedSpeechData = cap.getCursedSpeechData();

                switch (status) {
                    case FAILURE ->
                            PacketHandler.sendToClient(new SetOverlayMessageS2CPacket(Component.translatable(String.format("ability.%s.fail.failure",
                                    JujutsuKaisen.MOD_ID)), false), sender);
                    case ENERGY ->
                            PacketHandler.sendToClient(new SetOverlayMessageS2CPacket(Component.translatable(String.format("ability.%s.fail.energy",
                                    JujutsuKaisen.MOD_ID)), false), sender);
                    case COOLDOWN ->
                            PacketHandler.sendToClient(new SetOverlayMessageS2CPacket(Component.translatable(String.format("ability.%s.fail.cooldown",
                                    JujutsuKaisen.MOD_ID), Math.max(1, abilityData.getRemainingCooldown(ability) / 20)), false), sender);
                    case THROAT ->
                            PacketHandler.sendToClient(new SetOverlayMessageS2CPacket(Component.translatable(String.format("ability.%s.fail.throat",
                                    JujutsuKaisen.MOD_ID), cursedSpeechData.getThroatDamage() / 20), false), sender);
                }
            }
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