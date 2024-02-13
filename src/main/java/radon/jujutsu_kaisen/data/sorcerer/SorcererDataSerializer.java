package radon.jujutsu_kaisen.data.sorcerer;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.data.curse_manipulation.CurseManipulationData;
import radon.jujutsu_kaisen.data.curse_manipulation.ICurseManipulationData;

public class SorcererDataSerializer implements IAttachmentSerializer<CompoundTag, ISorcererData> {
    @Override
    public @NotNull ISorcererData read(@NotNull IAttachmentHolder holder, @NotNull CompoundTag tag) {
        ISorcererData data = new SorcererData((LivingEntity) holder);
        data.deserializeNBT(tag);
        return data;
    }

    @Override
    public CompoundTag write(ISorcererData attachment) {
        return attachment.serializeNBT();
    }
}