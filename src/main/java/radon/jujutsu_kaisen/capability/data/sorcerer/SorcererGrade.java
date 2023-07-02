package radon.jujutsu_kaisen.capability.data.sorcerer;

import net.minecraft.network.chat.Component;
import radon.jujutsu_kaisen.JujutsuKaisen;

public enum SorcererGrade {
    GRADE_4(0.0F, 1.0F),
    GRADE_3(100.0F, 1.15F),
    SEMI_GRADE_2(300.0F, 1.3F),
    GRADE_2(500.0F, 1.45F),
    SEMI_GRADE_1(1000.0F, 1.6F),
    GRADE_1(1500.0F, 1.75F),
    SPECIAL_GRADE_1(2000.0F, 1.9F),
    SPECIAL_GRADE(2500.0F, 2.05F);

    private final float required;
    private final float power;

    SorcererGrade(float required, float power) {
        this.required = required;
        this.power = power;
    }

    public float getRequiredExperience() {
        return this.required;
    }

    public Component getComponent() {
        return Component.translatable(String.format("grade.%s.%s", JujutsuKaisen.MOD_ID, this.name().toLowerCase()));
    }

    public float getPower() {
        return this.power;
    }
}
