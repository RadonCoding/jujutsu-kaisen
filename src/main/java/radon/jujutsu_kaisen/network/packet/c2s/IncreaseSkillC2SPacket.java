package radon.jujutsu_kaisen.network.packet.c2s;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.stat.ISkillData;
import radon.jujutsu_kaisen.data.stat.Skill;
import radon.jujutsu_kaisen.util.SorcererUtil;

public record IncreaseSkillC2SPacket(Skill skill, int amount) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<IncreaseSkillC2SPacket> TYPE = new CustomPacketPayload.Type<>(new ResourceLocation(JujutsuKaisen.MOD_ID, "increase_skill_serverbound"));
    public static final StreamCodec<? super RegistryFriendlyByteBuf, IncreaseSkillC2SPacket> STREAM_CODEC = StreamCodec.composite(
            NeoForgeStreamCodecs.enumCodec(Skill.class),
            IncreaseSkillC2SPacket::skill,
            ByteBufCodecs.INT,
            IncreaseSkillC2SPacket::amount,
            IncreaseSkillC2SPacket::new
    );

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!(ctx.player() instanceof ServerPlayer sender)) return;

            if (!this.skill.isValid(sender)) return;

            IJujutsuCapability cap = sender.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap == null) return;

            ISorcererData sorcererData = cap.getSorcererData();

            ISkillData skillData = cap.getSkillData();

            int current = skillData.getSkill(this.skill);

            int max = SorcererUtil.getMaximumSkillLevel(sorcererData.getExperience(), current, amount);

            int real = max - current;

            if (real == 0) return;

            if (!sender.getAbilities().instabuild && sorcererData.getSkillPoints() < real) return;

            if (!sender.getAbilities().instabuild) {
                sorcererData.useSkillPoints(real);
            }
            skillData.increaseSkill(this.skill, real);
        });
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}