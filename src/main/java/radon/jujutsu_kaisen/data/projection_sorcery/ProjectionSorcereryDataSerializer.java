package radon.jujutsu_kaisen.data.projection_sorcery;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.jetbrains.annotations.NotNull;

public class ProjectionSorcereryDataSerializer implements IAttachmentSerializer<CompoundTag, IProjectionSorceryData> {
    @Override
    public @NotNull IProjectionSorceryData read(@NotNull IAttachmentHolder holder, @NotNull CompoundTag tag) {
        IProjectionSorceryData data = new ProjectionSorceryData((LivingEntity) holder);
        data.deserializeNBT(tag);
        return data;
    }

    @Override
    public CompoundTag write(IProjectionSorceryData attachment) {
        return attachment.serializeNBT();
    }
}