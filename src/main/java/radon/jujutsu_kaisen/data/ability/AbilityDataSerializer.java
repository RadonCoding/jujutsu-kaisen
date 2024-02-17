package radon.jujutsu_kaisen.data.ability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.sorcerer.SorcererData;

public class AbilityDataSerializer implements IAttachmentSerializer<CompoundTag, IAbilityData> {
    @Override
    public @NotNull IAbilityData read(@NotNull IAttachmentHolder holder, @NotNull CompoundTag tag) {
        IAbilityData data = new AbilityData((LivingEntity) holder);
        data.deserializeNBT(tag);
        return data;
    }

    @Override
    public CompoundTag write(IAbilityData attachment) {
        return attachment.serializeNBT();
    }
}