package radon.jujutsu_kaisen.data.curse_manipulation;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import radon.jujutsu_kaisen.ability.curse_manipulation.util.CurseManipulationUtil;
import radon.jujutsu_kaisen.data.sorcerer.AbsorbedCurse;
import radon.jujutsu_kaisen.visual.ServerVisualHandler;
import radon.jujutsu_kaisen.cursed_technique.JJKCursedTechniques;
import radon.jujutsu_kaisen.cursed_technique.base.ICursedTechnique;

import javax.annotation.Nullable;
import java.util.*;

public class CurseManipulationData implements ICurseManipulationData {
    private final List<AbsorbedCurse> curses;
    private final Set<ICursedTechnique> absorbed;
    private @Nullable ICursedTechnique currentAbsorbed;

    private LivingEntity owner;

    public CurseManipulationData() {
        this.curses = new ArrayList<>();
        this.absorbed = new LinkedHashSet<>();
    }

    @Override
    public void init(LivingEntity owner) {
        this.owner = owner;
    }

    @Override
    public void tick(LivingEntity owner) {
        if (this.owner == null) {
            this.owner = owner;
        }
    }

    @Override
    public void absorb(@Nullable ICursedTechnique technique) {
        this.absorbed.add(technique);
    }

    @Override
    public void unabsorb(ICursedTechnique technique) {
        this.absorbed.remove(technique);
        this.currentAbsorbed = null;
    }

    @Override
    public Set<ICursedTechnique> getAbsorbed() {
        return this.absorbed;
    }

    @Override
    public void setCurrentAbsorbed(@Nullable ICursedTechnique technique) {
        this.currentAbsorbed = this.currentAbsorbed == technique ? null : technique;
        ServerVisualHandler.sync(this.owner);
    }

    @Override
    public @Nullable ICursedTechnique getCurrentAbsorbed() {
        return this.currentAbsorbed;
    }

    @Override
    public void addCurse(AbsorbedCurse curse) {
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
    public @Nullable AbsorbedCurse getCurse(EntityType<?> type) {
        for (AbsorbedCurse curse : this.getCurses()) {
            if (curse.getType() == type) return curse;
        }
        return null;
    }

    @Override
    public boolean hasCurse(EntityType<?> type) {
        for (AbsorbedCurse curse : this.getCurses()) {
            if (curse.getType() == type) return true;
        }
        return false;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();

        if (this.currentAbsorbed != null) {
            nbt.putString("current_absorbed", JJKCursedTechniques.getKey(this.currentAbsorbed).toString());
        }

        ListTag absorbedTag = new ListTag();

        for (ICursedTechnique technique : this.absorbed) {
            absorbedTag.add(StringTag.valueOf(JJKCursedTechniques.getKey(technique).toString()));
        }
        nbt.put("absorbed", absorbedTag);

        ListTag cursesTag = new ListTag();

        for (AbsorbedCurse curse : this.curses) {
            cursesTag.add(curse.serializeNBT());
        }
        nbt.put("curses", cursesTag);

        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (nbt.contains("current_absorbed")) {
            this.currentAbsorbed = JJKCursedTechniques.getValue(new ResourceLocation(nbt.getString("current_absorbed")));
        }

        this.absorbed.clear();

        for (Tag tag : nbt.getList("absorbed", Tag.TAG_STRING)) {
            this.absorbed.add(JJKCursedTechniques.getValue(new ResourceLocation(tag.getAsString())));
        }

        this.curses.clear();

        for (Tag key : nbt.getList("curses", Tag.TAG_COMPOUND)) {
            CompoundTag curse = (CompoundTag) key;
            this.curses.add(new AbsorbedCurse(curse));
        }
    }
}
