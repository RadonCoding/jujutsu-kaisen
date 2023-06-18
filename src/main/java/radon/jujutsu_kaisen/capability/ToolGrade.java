package radon.jujutsu_kaisen.capability;

import net.minecraft.network.chat.Component;
import radon.jujutsu_kaisen.JujutsuKaisen;

public enum ToolGrade {
    GRADE_4,
    GRADE_3,
    SEMI_GRADE_2,
    GRADE_2,
    SEMI_GRADE_1,
    GRADE_1,
    SPECIAL_GRADE_1,
    SPECIAL_GRADE;

    public Component getComponent() {
        return Component.translatable(String.format("grade.%s.%s", JujutsuKaisen.MOD_ID, this.name().toLowerCase()));
    }
}
