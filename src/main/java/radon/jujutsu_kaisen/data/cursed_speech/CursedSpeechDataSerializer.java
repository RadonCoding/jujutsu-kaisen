package radon.jujutsu_kaisen.data.cursed_speech;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.data.curse_manipulation.CurseManipulationData;

import javax.annotation.Nullable;

public class CursedSpeechDataSerializer implements IAttachmentSerializer<CompoundTag, ICursedSpeechData> {
    @Override
    public @NotNull ICursedSpeechData read(@NotNull IAttachmentHolder holder, @NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        ICursedSpeechData data = new CursedSpeechData();
        data.deserializeNBT(provider, tag);
        return data;
    }

    @Override
    public @Nullable CompoundTag write(@NotNull ICursedSpeechData attachment, HolderLookup.@NotNull Provider provider) {
        return attachment.serializeNBT(provider);
    }
}