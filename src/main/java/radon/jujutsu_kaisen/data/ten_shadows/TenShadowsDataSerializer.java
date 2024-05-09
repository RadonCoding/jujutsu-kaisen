package radon.jujutsu_kaisen.data.ten_shadows;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.data.curse_manipulation.CurseManipulationData;
import radon.jujutsu_kaisen.data.curse_manipulation.ICurseManipulationData;

import javax.annotation.Nullable;

public class TenShadowsDataSerializer implements IAttachmentSerializer<CompoundTag, ITenShadowsData> {
    @Override
    public @NotNull ITenShadowsData read(@NotNull IAttachmentHolder holder, @NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        ITenShadowsData data = new TenShadowsData((LivingEntity) holder);
        data.deserializeNBT(provider, tag);
        return data;
    }

    @Override
    public @Nullable CompoundTag write(@NotNull ITenShadowsData attachment, HolderLookup.@NotNull Provider provider) {
        return attachment.serializeNBT(provider);
    }
}