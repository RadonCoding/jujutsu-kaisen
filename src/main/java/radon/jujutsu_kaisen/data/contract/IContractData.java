package radon.jujutsu_kaisen.data.contract;

import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;

import java.util.UUID;

public interface IContractData extends INBTSerializable<CompoundTag> {
    void tick();

    void createPact(UUID recipient, Pact pact);

    boolean hasPact(UUID recipient, Pact pact);

    void removePact(UUID recipient, Pact pact);

    void createPactCreationRequest(UUID recipient, Pact pact);

    void createPactRemovalRequest(UUID recipient, Pact pact);

    void removePactCreationRequest(UUID recipient, Pact pact);

    void removePactRemovalRequest(UUID recipient, Pact pact);

    boolean hasRequestedPactCreation(UUID recipient, Pact pact);

    boolean hasRequestedPactRemoval(UUID recipient, Pact pact);

    void addBindingVow(BindingVow vow);

    void removeBindingVow(BindingVow vow);

    boolean hasBindingVow(BindingVow vow);

    void addBindingVowCooldown(BindingVow vow);

    int getRemainingCooldown(BindingVow vow);

    boolean isCooldownDone(BindingVow vow);
}
