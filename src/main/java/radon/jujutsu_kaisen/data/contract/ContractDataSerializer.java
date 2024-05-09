package radon.jujutsu_kaisen.data.contract;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.data.chant.ChantData;
import radon.jujutsu_kaisen.data.chant.IChantData;

import javax.annotation.Nullable;

public class ContractDataSerializer implements IAttachmentSerializer<CompoundTag, IContractData> {
    @Override
    public @NotNull IContractData read(@NotNull IAttachmentHolder holder, @NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        IContractData data = new ContractData();
        data.deserializeNBT(provider, tag);
        return data;
    }

    @Override
    public @Nullable CompoundTag write(@NotNull IContractData attachment, HolderLookup.@NotNull Provider provider) {
        return attachment.serializeNBT(provider);
    }
}