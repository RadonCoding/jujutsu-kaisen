package radon.jujutsu_kaisen.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.JujutsuAbilities;

import java.util.HashSet;
import java.util.Set;

public class SorcererData implements ISorcererData {
    private boolean initialized;
    private CursedTechnique technique;
    private SpecialTrait trait;
    private SorcererGrade grade;

    private float energy;

    private final Set<Ability> toggled;

    public SorcererData() {
        this.grade = SorcererGrade.UNRANKED;
        this.toggled = new HashSet<>();
    }

    public void tick(LivingEntity entity, boolean isClientSide) {
        if (isClientSide) {
            for (Ability toggled : this.toggled) {
                toggled.runClient(entity);
            }
        } else {
            for (Ability toggled : this.toggled) {
                toggled.runServer(entity);
            }
        }
    }

    public CursedTechnique getTechnique() {
        return this.technique;
    }

    @Override
    public SorcererGrade getGrade() {
        return this.grade;
    }

    public void toggleAbility(Ability ability) {
        if (this.toggled.contains(ability)) {
            this.toggled.remove(ability);
        } else {
            this.toggled.add(ability);
        }
    }

    public boolean isInitialized() {
        return this.initialized;
    }

    public void generate() {
        this.initialized = true;

        this.technique = CursedTechnique.GOJO;

        /*if (HelperMethods.RANDOM.nextInt(10) == 0) {
            this.trait = SpecialTrait.HEAVENLY_RESTRICTION;
        } else {
            if (HelperMethods.RANDOM.nextInt(10) == 0) {
                this.trait = SpecialTrait.SIX_EYES;
            }
            this.technique = HelperMethods.randomEnum(CursedTechnique.class);
        }*/
    }

    public boolean hasToggled(Ability ability) {
        return this.toggled.contains(ability);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putBoolean("initialized", this.initialized);

        if (this.technique != null) {
            nbt.putInt("technique", this.technique.ordinal());
        }
        if (this.trait != null) {
            nbt.putInt("trait", this.trait.ordinal());
        }
        nbt.putInt("grade", this.grade.ordinal());
        nbt.putFloat("energy", this.energy);

        ListTag toggledTag = new ListTag();

        for (Ability ability : this.toggled) {
            toggledTag.add(StringTag.valueOf(JujutsuAbilities.getKey(ability).toString()));
        }
        nbt.put("toggled", toggledTag);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.initialized = nbt.getBoolean("initialized");

        if (nbt.contains("technique")) {
            this.technique = CursedTechnique.values()[nbt.getInt("technique")];
        }
        if (nbt.contains("trait")) {
            this.trait = SpecialTrait.values()[nbt.getInt("trait")];
        }
        this.grade = SorcererGrade.values()[nbt.getInt("grade")];
        this.energy = nbt.getFloat("energy");

        for (Tag key : nbt.getList("toggled", Tag.TAG_STRING)) {
            this.toggled.add(JujutsuAbilities.getValue(new ResourceLocation(key.getAsString())));
        }
    }
}
