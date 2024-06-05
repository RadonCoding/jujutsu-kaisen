package radon.jujutsu_kaisen.item.veil.modifier;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.ArrayList;
import java.util.List;

public class ModifierUtils {
    private static final int MAX_MODIFIERS = 8;

    public static Modifier resolve(CompoundTag nbt) {
        Modifier.Type type = Modifier.Type.values()[nbt.getInt("type")];

        return switch (type) {
            case NONE -> new Modifier(nbt);
            case PLAYER -> new PlayerModifier(nbt);
            case COLOR -> new ColorModifier(nbt);
            case TRANSPARENT -> new TransparentModifier(nbt);
            case CURSE -> new CurseModifier(nbt);
            case SORCERER -> new SorcererModifier(nbt);
            case GRIEFING -> new GriefingModifier(nbt);
            case VIOLENCE -> new ViolenceModifier(nbt);
        };
    }

    public static List<Modifier> getModifiers(CompoundTag nbt) {
        if (!nbt.contains("modifiers")) return List.of();

        ListTag modifiersTag = nbt.getList("modifiers", Tag.TAG_COMPOUND);
        return deserialize(modifiersTag);
    }

    public static void setModifier(CompoundTag nbt, int index, Modifier modifier) {
        if (!nbt.contains("modifiers")) {
            ListTag modifiersTag = new ListTag();

            for (int i = 0; i < MAX_MODIFIERS; i++) {
                modifiersTag.add(new Modifier(Modifier.Type.NONE, Modifier.Action.NONE).serializeNBT());
            }
            nbt.put("modifiers", modifiersTag);
        }
        ListTag modifiersTag = nbt.getList("modifiers", Tag.TAG_COMPOUND);
        modifiersTag.set(index, modifier.serializeNBT());
    }

    public static List<Modifier> deserialize(ListTag modifiersTag) {
        List<Modifier> modifiers = new ArrayList<>();

        for (Tag tag : modifiersTag) {
            CompoundTag modifier = (CompoundTag) tag;
            modifiers.add(resolve(modifier));
        }
        return modifiers;
    }

    public static ListTag serialize(List<Modifier> modifiers) {
        ListTag modifiersTag = new ListTag();

        for (Modifier modifier : modifiers) {
            modifiersTag.add(modifier.serializeNBT());
        }
        return modifiersTag;
    }
}
