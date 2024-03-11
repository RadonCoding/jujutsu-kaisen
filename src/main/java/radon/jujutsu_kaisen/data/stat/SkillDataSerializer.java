package radon.jujutsu_kaisen.data.stat;

import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.jetbrains.annotations.NotNull;

public class SkillDataSerializer implements IAttachmentSerializer<CompoundTag, ISkillData> {
    @Override
    public @NotNull ISkillData read(@NotNull IAttachmentHolder holder, @NotNull CompoundTag tag) {
        ISkillData data = new SkillData();
        data.deserializeNBT(tag);
        return data;
    }

    @Override
    public CompoundTag write(ISkillData attachment) {
        return attachment.serializeNBT();
    }
}