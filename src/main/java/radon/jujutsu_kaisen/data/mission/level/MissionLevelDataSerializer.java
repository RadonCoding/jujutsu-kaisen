package radon.jujutsu_kaisen.data.mission.level;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.jetbrains.annotations.NotNull;

public class MissionLevelDataSerializer implements IAttachmentSerializer<CompoundTag, IMissionLevelData> {
    @Override
    public @NotNull IMissionLevelData read(@NotNull IAttachmentHolder holder, @NotNull CompoundTag tag) {
        IMissionLevelData data = new MissionLevelData((Level) holder);
        data.deserializeNBT(tag);
        return data;
    }

    @Override
    public CompoundTag write(IMissionLevelData attachment) {
        return attachment.serializeNBT();
    }
}