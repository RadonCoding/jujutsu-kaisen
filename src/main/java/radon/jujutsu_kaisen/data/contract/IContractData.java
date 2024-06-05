package radon.jujutsu_kaisen.data.contract;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import radon.jujutsu_kaisen.binding_vow.BindingVow;
import radon.jujutsu_kaisen.pact.Pact;

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
