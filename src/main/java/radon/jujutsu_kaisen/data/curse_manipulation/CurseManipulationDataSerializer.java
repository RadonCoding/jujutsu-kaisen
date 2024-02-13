package radon.jujutsu_kaisen.data.curse_manipulation;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.data.projection_sorcery.IProjectionSorceryData;
import radon.jujutsu_kaisen.data.projection_sorcery.ProjectionSorceryData;

public class CurseManipulationDataSerializer implements IAttachmentSerializer<CompoundTag, ICurseManipulationData> {
    @Override
    public @NotNull ICurseManipulationData read(@NotNull IAttachmentHolder holder, @NotNull CompoundTag tag) {
        ICurseManipulationData data = new CurseManipulationData((LivingEntity) holder);
        data.deserializeNBT(tag);
        return data;
    }

    @Override
    public CompoundTag write(ICurseManipulationData attachment) {
        return attachment.serializeNBT();
    }
}