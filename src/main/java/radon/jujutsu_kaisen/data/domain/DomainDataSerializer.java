package radon.jujutsu_kaisen.data.domain;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class DomainDataSerializer implements IAttachmentSerializer<CompoundTag, IDomainData> {
    @Override
    public @NotNull IDomainData read(@NotNull IAttachmentHolder holder, @NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        IDomainData data = new DomainData((Level) holder);
        data.deserializeNBT(provider, tag);
        return data;
    }

    @Override
    @Nullable
    public CompoundTag write(@NotNull IDomainData attachment, HolderLookup.@NotNull Provider provider) {
        return attachment.serializeNBT(provider);
    }
}