package radon.jujutsu_kaisen.data.mission.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.jetbrains.annotations.NotNull;

public class MissionEntityDataSerializer implements IAttachmentSerializer<CompoundTag, IMissionEntityData> {
    @Override
    public @NotNull IMissionEntityData read(@NotNull IAttachmentHolder holder, @NotNull CompoundTag tag) {
        IMissionEntityData data = new MissionEntityData((LivingEntity) holder);
        data.deserializeNBT(tag);
        return data;
    }

    @Override
    public CompoundTag write(IMissionEntityData attachment) {
        return attachment.serializeNBT();
    }
}