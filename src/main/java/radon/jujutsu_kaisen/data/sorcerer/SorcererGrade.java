package radon.jujutsu_kaisen.data.sorcerer;

import com.mojang.serialization.Codec;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.config.ConfigHolder;


public enum SorcererGrade implements StringRepresentable {
    GRADE_4(0.0F),
    GRADE_3(50.0F),
    SEMI_GRADE_2(100.0F),
    GRADE_2(200.0F),
    SEMI_GRADE_1(400.0F),
    GRADE_1(800.0F),
    SPECIAL_GRADE_1(1600.0F),
    SPECIAL_GRADE(3200.0F);

    public static final Codec<SorcererGrade> CODEC = StringRepresentable.fromEnum(SorcererGrade::values);

    private final float required;

    SorcererGrade(float required) {
        this.required = required;
    }

    public float getRequiredExperience() {
        return this.required;
    }

    public Component getName() {
        return Component.translatable(String.format("grade.%s.%s", JujutsuKaisen.MOD_ID, this.name().toLowerCase()));
    }

    @Override
    public @NotNull String getSerializedName() {
        return this.name();
    }
}
