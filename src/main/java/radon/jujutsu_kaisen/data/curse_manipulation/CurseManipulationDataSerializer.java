package radon.jujutsu_kaisen.data.curse_manipulation;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.data.projection_sorcery.IProjectionSorceryData;
import radon.jujutsu_kaisen.data.projection_sorcery.ProjectionSorceryData;

import javax.annotation.Nullable;

public class CurseManipulationDataSerializer implements IAttachmentSerializer<CompoundTag, ICurseManipulationData> {
    @Override
    public @NotNull ICurseManipulationData read(@NotNull IAttachmentHolder holder, @NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        ICurseManipulationData data = new CurseManipulationData((LivingEntity) holder);
        data.deserializeNBT(provider, tag);
        return data;
    }

    @Override
    public @Nullable CompoundTag write(@NotNull ICurseManipulationData attachment, HolderLookup.@NotNull Provider provider) {
        return attachment.serializeNBT(provider);
    }
}