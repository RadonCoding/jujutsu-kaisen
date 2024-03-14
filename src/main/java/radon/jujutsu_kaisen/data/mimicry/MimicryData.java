package radon.jujutsu_kaisen.data.mimicry;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.UnknownNullability;
import radon.jujutsu_kaisen.cursed_technique.JJKCursedTechniques;
import radon.jujutsu_kaisen.cursed_technique.base.ICursedTechnique;
import radon.jujutsu_kaisen.visual.ServerVisualHandler;

import javax.annotation.Nullable;
import java.util.LinkedHashSet;
import java.util.Set;

public class MimicryData implements IMimicryData {
    private final Set<ICursedTechnique> copied;
    private @Nullable ICursedTechnique currentCopied;

    private final LivingEntity owner;

    public MimicryData(LivingEntity owner) {
        this.owner = owner;

        this.copied = new LinkedHashSet<>();
    }

    @Override
    public void tick() {

    }

    @Override
    public void copy(ICursedTechnique technique) {
        this.copied.add(technique);
    }

    @Override
    public void uncopy(ICursedTechnique technique) {
        if (this.currentCopied == technique) {
            this.currentCopied = null;
            ServerVisualHandler.sync(this.owner);
        }
        this.copied.remove(technique);
    }

    @Override
    public boolean hasCopied(ICursedTechnique technique) {
        return this.copied.contains(technique);
    }

    @Override
    public Set<ICursedTechnique> getCopied() {
        return this.copied;
    }

    @Override
    public @Nullable ICursedTechnique getCurrentCopied() {
        return this.currentCopied;
    }

    @Override
    public void setCurrentCopied(@Nullable ICursedTechnique technique) {
        this.currentCopied = this.currentCopied == technique ? null : technique;
        ServerVisualHandler.sync(this.owner);
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();

        ListTag copiedTag = new ListTag();

        for (ICursedTechnique technique : this.copied) {
            copiedTag.add(StringTag.valueOf(JJKCursedTechniques.getKey(technique).toString()));
        }
        nbt.put("copied", copiedTag);

        if (this.currentCopied != null) {
            nbt.putString("current_copied", JJKCursedTechniques.getKey(this.currentCopied).toString());
        }
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.copied.clear();

        for (Tag tag : nbt.getList("copied", Tag.TAG_STRING)) {
            this.copied.add(JJKCursedTechniques.getValue(new ResourceLocation(tag.getAsString())));
        }

        if (nbt.contains("current_copied")) {
            this.currentCopied = JJKCursedTechniques.getValue(new ResourceLocation(nbt.getString("current_copied")));
        }
    }
}
