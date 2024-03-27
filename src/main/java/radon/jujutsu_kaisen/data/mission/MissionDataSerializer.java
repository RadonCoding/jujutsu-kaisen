package radon.jujutsu_kaisen.data.mission;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.data.mimicry.IMimicryData;
import radon.jujutsu_kaisen.data.mimicry.MimicryData;

public class MissionDataSerializer implements IAttachmentSerializer<CompoundTag, IMissionData> {
    @Override
    public @NotNull IMissionData read(@NotNull IAttachmentHolder holder, @NotNull CompoundTag tag) {
        IMissionData data = new MissionData((Level) holder);
        data.deserializeNBT(tag);
        return data;
    }

    @Override
    public CompoundTag write(IMissionData attachment) {
        return attachment.serializeNBT();
    }
}