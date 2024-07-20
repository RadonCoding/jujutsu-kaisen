package radon.jujutsu_kaisen.data.curse_manipulation;


import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.ability.curse_manipulation.util.CurseManipulationUtil;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;
import radon.jujutsu_kaisen.cursed_technique.registry.JJKCursedTechniques;
import radon.jujutsu_kaisen.visual.ServerVisualHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class CurseManipulationData implements ICurseManipulationData {
    private final List<AbsorbedCurse> curses;
    private final LinkedHashSet<CursedTechnique> absorbed;
    private final LivingEntity owner;
    @Nullable
    private CursedTechnique currentAbsorbed;

    public CurseManipulationData(LivingEntity owner) {
        this.owner = owner;

        this.curses = new ArrayList<>();
        this.absorbed = new LinkedHashSet<>();
    }

    @Override
    public void tick() {

    }

    @Override
    public void absorb(@Nullable CursedTechnique technique) {
        this.absorbed.add(technique);
    }

    @Override
    public void absorb(Set<CursedTechnique> techniques) {
        this.absorbed.addAll(techniques);
    }

    @Override
    public void unabsorb(CursedTechnique technique) {
        this.absorbed.remove(technique);
        this.currentAbsorbed = null;
    }

    @Override
    public Set<CursedTechnique> getAbsorbed() {
        return this.absorbed;
    }

    @Override
    @Nullable
    public CursedTechnique getCurrentAbsorbed() {
        return this.currentAbsorbed;
    }

    @Override
    public void setCurrentAbsorbed(@Nullable CursedTechnique technique) {
        if (this.owner == null) return;

        this.currentAbsorbed = this.currentAbsorbed == technique ? null : technique;
        ServerVisualHandler.sync(this.owner);
    }

    @Override
    public void addCurse(@NotNull AbsorbedCurse curse) {
        this.curses.add(curse);
    }

    @Override
    public void removeCurse(AbsorbedCurse curse) {
        this.curses.remove(curse);
    }

    @Override
    public List<AbsorbedCurse> getCurses() {
        List<AbsorbedCurse> sorted = new ArrayList<>(this.curses);
        sorted.sort((o1, o2) -> (int) (CurseManipulationUtil.getCurseExperience(o2) - CurseManipulationUtil.getCurseExperience(o1)));
        return sorted;
    }

    @Override
    @Nullable
    public AbsorbedCurse getCurse(EntityType<?> type) {
        for (AbsorbedCurse curse : this.getCurses()) {
            if (curse.type() == type) return curse;
        }
        return null;
    }

    @Override
    public boolean hasCurse(EntityType<?> type) {
        for (AbsorbedCurse curse : this.getCurses()) {
            if (curse.type() == type) return true;
        }
        return false;
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag nbt = new CompoundTag();

        if (this.currentAbsorbed != null) {
            nbt.putString("current_absorbed", JJKCursedTechniques.getKey(this.currentAbsorbed).toString());
        }

        ListTag absorbedTag = new ListTag();

        for (CursedTechnique technique : this.absorbed) {
            absorbedTag.add(StringTag.valueOf(JJKCursedTechniques.getKey(technique).toString()));
        }
        nbt.put("absorbed", absorbedTag);

        ListTag cursesTag = new ListTag();

        for (AbsorbedCurse curse : this.curses) {
            cursesTag.add(AbsorbedCurse.CODEC.encode(curse, provider.createSerializationContext(NbtOps.INSTANCE),
                    new CompoundTag()).getOrThrow());
        }
        nbt.put("curses", cursesTag);

        return nbt;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, CompoundTag nbt) {
        if (nbt.contains("current_absorbed")) {
            this.currentAbsorbed = JJKCursedTechniques.getValue(new ResourceLocation(nbt.getString("current_absorbed")));
        }

        this.absorbed.clear();

        for (Tag tag : nbt.getList("absorbed", Tag.TAG_STRING)) {
            this.absorbed.add(JJKCursedTechniques.getValue(new ResourceLocation(tag.getAsString())));
        }

        this.curses.clear();

        for (Tag tag : nbt.getList("curses", Tag.TAG_COMPOUND)) {
            this.curses.add(AbsorbedCurse.CODEC.parse(provider.createSerializationContext(NbtOps.INSTANCE), tag).getOrThrow());
        }
    }
}
