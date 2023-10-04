package radon.jujutsu_kaisen.capability.data.sorcerer;

import net.minecraft.network.chat.Component;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.config.ConfigHolder;


public enum SorcererGrade {
    GRADE_4,
    GRADE_3,
    SEMI_GRADE_2,
    GRADE_2,
    SEMI_GRADE_1,
    GRADE_1,
    SPECIAL_GRADE_1,
    SPECIAL_GRADE;

    public float getRequiredExperience() {
        return ConfigHolder.SERVER.getRequiredExperience().get(this);
    }

    public Component getName() {
        return Component.translatable(String.format("grade.%s.%s", JujutsuKaisen.MOD_ID, this.name().toLowerCase()));
    }
}
