package radon.jujutsu_kaisen.block.property;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public class LongProperty extends Property<Long> {
    private final ImmutableSet<Long> values;
    private final long min;
    private final long max;

    protected LongProperty(String pName, long pMin, long pMax) {
        super(pName, Long.class);

        if (pMin < 0) {
            throw new IllegalArgumentException("Min value of " + pName + " must be 0 or greater");
        } else if (pMax <= pMin) {
            throw new IllegalArgumentException("Max value of " + pName + " must be greater than min (" + pMin + ")");
        } else {
            this.min = pMin;
            this.max = pMax;
            Set<Long> set = Sets.newHashSet();

            for (long i = pMin; i <= pMax; ++i) {
                set.add(i);
            }
            this.values = ImmutableSet.copyOf(set);
        }
    }

    @Override
    public @NotNull Collection<Long> getPossibleValues() {
        return this.values;
    }

    @Override
    public boolean equals(Object pOther) {
        if (this == pOther) {
            return true;
        } else {
            if (pOther instanceof LongProperty Longproperty && super.equals(pOther)) {
                return this.values.equals(Longproperty.values);
            }
            return false;
        }
    }

    @Override
    public int generateHashCode() {
        return 31 * super.generateHashCode() + this.values.hashCode();
    }

    public static LongProperty create(String pName, long pMin, long pMax) {
        return new LongProperty(pName, pMin, pMax);
    }

    @Override
    public @NotNull Optional<Long> getValue(@NotNull String pValue) {
        try {
            long parsed = Long.parseLong(pValue);
            return parsed >= this.min && parsed <= this.max ? Optional.of(parsed) : Optional.empty();
        } catch (NumberFormatException numberformatexception) {
            return Optional.empty();
        }
    }

    @Override
    public @NotNull String getName(Long pValue) {
        return pValue.toString();
    }
}
