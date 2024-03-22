package radon.jujutsu_kaisen.data.ten_shadows;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.IAttack;
import radon.jujutsu_kaisen.ability.base.IChanneled;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.ICharged;
import radon.jujutsu_kaisen.ability.base.IDomainAttack;
import radon.jujutsu_kaisen.ability.base.IDurationable;
import radon.jujutsu_kaisen.ability.base.ITenShadowsAttack;
import radon.jujutsu_kaisen.ability.base.IToggled;

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

        if (nbt.contains("ability")) {
            this.ability = JJKAbilities.getValue(new ResourceLocation(nbt.getString("ability")));
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
        nbt.putString("key", this.key.toString());

        if (this.ability != null) {
            nbt.putString("ability", JJKAbilities.getKey(this.ability).toString());
        }
        return nbt;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Adaptation other)) {
            return false;
        }

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
