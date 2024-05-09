package radon.jujutsu_kaisen.data.stat;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class SkillDataSerializer implements IAttachmentSerializer<CompoundTag, ISkillData> {
    @Override
    public @NotNull ISkillData read(@NotNull IAttachmentHolder holder, @NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        ISkillData data = new SkillData();
        data.deserializeNBT(provider, tag);
        return data;
    }

    @Override
    public @Nullable CompoundTag write(@NotNull ISkillData attachment, HolderLookup.@NotNull Provider provider) {
        return attachment.serializeNBT(provider);
    }
}