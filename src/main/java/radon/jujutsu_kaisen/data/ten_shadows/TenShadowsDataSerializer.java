package radon.jujutsu_kaisen.data.ten_shadows;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.data.curse_manipulation.CurseManipulationData;
import radon.jujutsu_kaisen.data.curse_manipulation.ICurseManipulationData;

public class TenShadowsDataSerializer implements IAttachmentSerializer<CompoundTag, ITenShadowsData> {
    @Override
    public @NotNull ITenShadowsData read(@NotNull IAttachmentHolder holder, @NotNull CompoundTag tag) {
        ITenShadowsData data = new TenShadowsData((LivingEntity) holder);
        data.deserializeNBT(tag);
        return data;
    }

    @Override
    public CompoundTag write(ITenShadowsData attachment) {
        return attachment.serializeNBT();
    }
}