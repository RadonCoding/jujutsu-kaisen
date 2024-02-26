package radon.jujutsu_kaisen.data.cursed_speech;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.data.curse_manipulation.CurseManipulationData;

public class CursedSpeechDataSerializer implements IAttachmentSerializer<CompoundTag, ICursedSpeechData> {
    @Override
    public @NotNull ICursedSpeechData read(@NotNull IAttachmentHolder holder, @NotNull CompoundTag tag) {
        ICursedSpeechData data = new CursedSpeechData();
        data.deserializeNBT(tag);
        return data;
    }

    @Override
    public CompoundTag write(ICursedSpeechData attachment) {
        return attachment.serializeNBT();
    }
}