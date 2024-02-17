package radon.jujutsu_kaisen.data.mimicry;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.data.contract.ContractData;
import radon.jujutsu_kaisen.data.contract.IContractData;

public class MimicryDataSerializer implements IAttachmentSerializer<CompoundTag, IMimicryData> {
    @Override
    public @NotNull IMimicryData read(@NotNull IAttachmentHolder holder, @NotNull CompoundTag tag) {
        IMimicryData data = new MimicryData((LivingEntity) holder);
        data.deserializeNBT(tag);
        return data;
    }

    @Override
    public CompoundTag write(IMimicryData attachment) {
        return attachment.serializeNBT();
    }
}