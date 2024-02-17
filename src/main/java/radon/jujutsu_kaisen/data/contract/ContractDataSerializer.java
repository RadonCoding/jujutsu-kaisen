package radon.jujutsu_kaisen.data.contract;

import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.data.chant.ChantData;
import radon.jujutsu_kaisen.data.chant.IChantData;

public class ContractDataSerializer implements IAttachmentSerializer<CompoundTag, IContractData> {
    @Override
    public @NotNull IContractData read(@NotNull IAttachmentHolder holder, @NotNull CompoundTag tag) {
        IContractData data = new ContractData();
        data.deserializeNBT(tag);
        return data;
    }

    @Override
    public CompoundTag write(IContractData attachment) {
        return attachment.serializeNBT();
    }
}