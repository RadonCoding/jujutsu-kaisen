package radon.jujutsu_kaisen.data.chant;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.data.ability.AbilityData;
import radon.jujutsu_kaisen.data.ability.IAbilityData;

public class ChantDataSerializer implements IAttachmentSerializer<CompoundTag, IChantData> {
    @Override
    public @NotNull IChantData read(@NotNull IAttachmentHolder holder, @NotNull CompoundTag tag) {
        IChantData data = new ChantData();
        data.deserializeNBT(tag);
        return data;
    }

    @Override
    public CompoundTag write(IChantData attachment) {
        return attachment.serializeNBT();
    }
}