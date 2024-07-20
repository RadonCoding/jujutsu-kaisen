package radon.jujutsu_kaisen.data.mission;


import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;

public enum MissionType implements StringRepresentable {
    EXORCISE_CURSES;

    public Component getTitle() {
        return Component.translatable(String.format("mission_type.%s.%s", JujutsuKaisen.MOD_ID, this.name().toLowerCase()));
    }

    public Component getDescription() {
        return Component.translatable(String.format("mission_type.%s.%s.description", JujutsuKaisen.MOD_ID, this.name().toLowerCase()));
    }

    @Override
    public @NotNull String getSerializedName() {
        return this.name();
    }
}
