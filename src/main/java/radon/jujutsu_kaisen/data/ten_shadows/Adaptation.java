package radon.jujutsu_kaisen.data.ten_shadows;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.registry.JJKAbilities;

public class Adaptation {
    private final ResourceLocation key;

    @Nullable
    private Ability ability;

    public Adaptation(ResourceLocation key) {
        this.key = key;
    }

    public Adaptation(ResourceLocation key, @Nullable Ability ability) {
        this(key);

        this.ability = ability;
    }

    public Adaptation(CompoundTag nbt) {
        this.key = new ResourceLocation(nbt.getString("name"));

        if (nbt.contains("technique")) {
            this.ability = JJKAbilities.getValue(new ResourceLocation(nbt.getString("technique")));
        }
    }

    public ResourceLocation getKey() {
        return this.key;
    }

    @Nullable
    public Ability getAbility() {
        return this.ability;
    }

    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putString("ability", this.key.toString());

        if (this.ability != null) {
            nbt.putString("technique", JJKAbilities.getKey(this.ability).toString());
        }
        return nbt;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Adaptation other)) return false;

        if (this.ability != null && other.ability != null) {
            Ability.Classification first = this.ability.getClassification();
            Ability.Classification second = other.ability.getClassification();
            return this.ability == other.ability || (first != Ability.Classification.NONE && first == second);
        }
        return this.key == other.key;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;

        if (this.ability != null) {
            Ability.Classification classification = this.ability.getClassification();
            result = prime * result + (classification == Ability.Classification.NONE ? this.ability.hashCode() : classification.hashCode());
        }
        result = prime * result + this.key.hashCode();
        return result;
    }

    public enum Type {
        DAMAGE,
        COUNTER
    }
}
