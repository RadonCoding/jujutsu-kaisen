package radon.jujutsu_kaisen.network.packet.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.stat.ISkillData;
import radon.jujutsu_kaisen.data.stat.Skill;

public class IncreaseSkillC2SPacket implements CustomPacketPayload {
    public static final ResourceLocation IDENTIFIER = new ResourceLocation(JujutsuKaisen.MOD_ID, "increase_skill_serverbound");

    private final Skill skill;
    private final int amount;

    public IncreaseSkillC2SPacket(Skill key, int amount) {
        this.skill = key;
        this.amount = amount;
    }

    public IncreaseSkillC2SPacket(FriendlyByteBuf buf) {
        this(buf.readEnum(Skill.class), buf.readInt());
    }

    public void handle(PlayPayloadContext ctx) {
        ctx.workHandler().execute(() -> {
            if (!(ctx.player().orElseThrow() instanceof ServerPlayer sender)) return;

            IJujutsuCapability cap = sender.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap == null) return;

            ISorcererData sorcererData = cap.getSorcererData();

            ISkillData skillData = cap.getSkillData();

            int current = skillData.getSkill(this.skill);

            if (current >= ConfigHolder.SERVER.maximumSkillLevel.get()) return;

            int real = Math.min(ConfigHolder.SERVER.maximumSkillLevel.get(), current + this.amount) - current;

            if (!sender.getAbilities().instabuild && sorcererData.getSkillPoints() < real) return;

            if (!sender.getAbilities().instabuild) {
                sorcererData.useSkillPoints(real);
            }
            skillData.increaseSkill(this.skill, real);
        });
    }

    @Override
    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeEnum(this.skill);
        pBuffer.writeInt(this.amount);
    }

    @Override
    public @NotNull ResourceLocation id() {
        return IDENTIFIER;
    }
}