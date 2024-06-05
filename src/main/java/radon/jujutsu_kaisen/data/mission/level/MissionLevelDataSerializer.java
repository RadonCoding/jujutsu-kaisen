package radon.jujutsu_kaisen.data.mission.level;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class MissionLevelDataSerializer implements IAttachmentSerializer<CompoundTag, IMissionLevelData> {
    @Override
    public @NotNull IMissionLevelData read(@NotNull IAttachmentHolder holder, @NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        IMissionLevelData data = new MissionLevelData((Level) holder);
        data.deserializeNBT(provider, tag);
        return data;
    }

    @Override
    @Nullable
    public CompoundTag write(@NotNull IMissionLevelData attachment, HolderLookup.@NotNull Provider provider) {
        return attachment.serializeNBT(provider);
    }
}