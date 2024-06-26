package radon.jujutsu_kaisen.data.contract;


import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.binding_vow.BindingVow;
import radon.jujutsu_kaisen.binding_vow.JJKBindingVows;
import radon.jujutsu_kaisen.pact.JJKPacts;
import radon.jujutsu_kaisen.pact.Pact;

import java.util.*;

public class ContractData implements IContractData {
    private final Map<UUID, Set<Pact>> acceptedPacts;
    private final Map<UUID, Set<Pact>> requestedPactsCreations;
    private final Map<UUID, Integer> createRequestExpirations;
    private final Map<UUID, Set<Pact>> requestedPactsRemovals;
    private final Set<BindingVow> bindingVows;
    private final Map<BindingVow, Integer> bindingVowCooldowns;

    public ContractData() {
        this.acceptedPacts = new HashMap<>();
        this.requestedPactsCreations = new HashMap<>();
        this.createRequestExpirations = new HashMap<>();
        this.requestedPactsRemovals = new HashMap<>();
        this.bindingVows = new HashSet<>();
        this.bindingVowCooldowns = new HashMap<>();
    }

    private void updateRequestExpirations() {
        Iterator<Map.Entry<UUID, Integer>> iter = this.createRequestExpirations.entrySet().iterator();

        while (iter.hasNext()) {
            Map.Entry<UUID, Integer> entry = iter.next();

            int remaining = entry.getValue();

            if (remaining > 0) {
                this.createRequestExpirations.put(entry.getKey(), --remaining);
            } else {
                iter.remove();
                this.requestedPactsCreations.remove(entry.getKey());
            }
        }
    }

    private void updateBindingVowCooldowns() {
        Iterator<Map.Entry<BindingVow, Integer>> iter = this.bindingVowCooldowns.entrySet().iterator();

        while (iter.hasNext()) {
            Map.Entry<BindingVow, Integer> entry = iter.next();

            int remaining = entry.getValue();

            if (remaining > 0) {
                this.bindingVowCooldowns.put(entry.getKey(), --remaining);
            } else {
                iter.remove();
                this.bindingVowCooldowns.remove(entry.getKey());
            }
        }
    }


    @Override
    public void tick() {
        this.updateRequestExpirations();
        this.updateBindingVowCooldowns();
    }

    @Override
    public void createPact(UUID recipient, Pact pact) {
        if (!this.acceptedPacts.containsKey(recipient)) {
            this.acceptedPacts.put(recipient, new HashSet<>());
        }
        this.acceptedPacts.get(recipient).add(pact);
    }

    @Override
    public boolean hasPact(UUID recipient, Pact pact) {
        return this.acceptedPacts.getOrDefault(recipient, Set.of()).contains(pact);
    }

    @Override
    public void removePact(UUID recipient, Pact pact) {
        this.acceptedPacts.get(recipient).remove(pact);

        if (this.acceptedPacts.get(recipient).isEmpty()) {
            this.acceptedPacts.remove(recipient);
        }
    }

    @Override
    public void createPactCreationRequest(UUID recipient, Pact pact) {
        if (!this.requestedPactsCreations.containsKey(recipient)) {
            this.requestedPactsCreations.put(recipient, new HashSet<>());
        }
        this.requestedPactsCreations.get(recipient).add(pact);
        this.createRequestExpirations.put(recipient, 30 * 20);
    }

    @Override
    public void createPactRemovalRequest(UUID recipient, Pact pact) {
        if (!this.requestedPactsRemovals.containsKey(recipient)) {
            this.requestedPactsRemovals.put(recipient, new HashSet<>());
        }
        this.requestedPactsRemovals.get(recipient).add(pact);
    }

    @Override
    public void removePactCreationRequest(UUID recipient, Pact pact) {
        this.requestedPactsCreations.getOrDefault(recipient, new HashSet<>()).remove(pact);
        this.createRequestExpirations.remove(recipient);
    }

    @Override
    public void removePactRemovalRequest(UUID recipient, Pact pact) {
        this.requestedPactsRemovals.getOrDefault(recipient, new HashSet<>()).remove(pact);
    }

    @Override
    public boolean hasRequestedPactCreation(UUID recipient, Pact pact) {
        return this.requestedPactsCreations.getOrDefault(recipient, Set.of()).contains(pact);
    }

    @Override
    public boolean hasRequestedPactRemoval(UUID recipient, Pact pact) {
        return this.requestedPactsRemovals.getOrDefault(recipient, Set.of()).contains(pact);
    }

    @Override
    public void addBindingVow(BindingVow vow) {
        this.bindingVows.add(vow);
    }

    @Override
    public void removeBindingVow(BindingVow vow) {
        this.bindingVows.remove(vow);
    }

    @Override
    public boolean hasBindingVow(BindingVow vow) {
        return this.bindingVows.contains(vow);
    }

    @Override
    public void addBindingVowCooldown(BindingVow vow) {
        this.bindingVowCooldowns.put(vow, 20 * 60 * 30);
    }

    @Override
    public int getRemainingCooldown(BindingVow vow) {
        return this.bindingVowCooldowns.get(vow);
    }

    @Override
    public boolean isCooldownDone(BindingVow vow) {
        return !this.bindingVowCooldowns.containsKey(vow);
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag nbt = new CompoundTag();

        ListTag acceptedPactsTag = new ListTag();

        for (Map.Entry<UUID, Set<Pact>> entry : this.acceptedPacts.entrySet()) {
            CompoundTag data = new CompoundTag();
            data.putUUID("recipient", entry.getKey());

            ListTag pacts = new ListTag();

            for (Pact pact : entry.getValue()) {
                pacts.add(StringTag.valueOf(JJKPacts.getKey(pact).toString()));
            }
            data.put("entries", pacts);

            acceptedPactsTag.add(data);
        }
        nbt.put("accepted_pacts", acceptedPactsTag);

        ListTag bindingVowsTag = new ListTag();

        for (BindingVow vow : this.bindingVows) {
            bindingVowsTag.add(StringTag.valueOf(JJKBindingVows.getKey(vow).toString()));
        }
        nbt.put("binding_vows", bindingVowsTag);

        ListTag bindingVowCooldownsTag = new ListTag();

        for (Map.Entry<BindingVow, Integer> entry : this.bindingVowCooldowns.entrySet()) {
            CompoundTag data = new CompoundTag();
            data.putString("vow", JJKBindingVows.getKey(entry.getKey()).toString());
            data.putInt("cooldown", entry.getValue());
        }
        nbt.put("binding_vow_cooldowns", bindingVowCooldownsTag);

        return nbt;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, CompoundTag nbt) {
        this.acceptedPacts.clear();

        for (Tag key : nbt.getList("accepted_pacts", Tag.TAG_COMPOUND)) {
            CompoundTag data = (CompoundTag) key;

            Set<Pact> pacts = new HashSet<>();

            for (Tag entry : data.getList("entries", Tag.TAG_STRING)) {
                pacts.add(JJKPacts.getValue(new ResourceLocation(entry.getAsString())));
            }
            this.acceptedPacts.put(data.getUUID("recipient"), pacts);
        }

        this.bindingVows.clear();

        for (Tag key : nbt.getList("binding_vows", Tag.TAG_STRING)) {
            this.bindingVows.add(JJKBindingVows.getValue(new ResourceLocation(key.getAsString())));
        }

        this.bindingVowCooldowns.clear();

        for (Tag entry : nbt.getList("binding_vow_cooldowns", Tag.TAG_COMPOUND)) {
            CompoundTag data = (CompoundTag) entry;
            this.bindingVowCooldowns.put(JJKBindingVows.getValue(new ResourceLocation(data.getString("vow"))), data.getInt("cooldown"));
        }
    }
}
