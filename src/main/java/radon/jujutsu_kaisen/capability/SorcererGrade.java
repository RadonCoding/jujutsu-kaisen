package radon.jujutsu_kaisen.capability;

import net.minecraft.network.chat.Component;
import radon.jujutsu_kaisen.JujutsuKaisen;

public enum SorcererGrade {
    UNRANKED(0.0F, 1.0F),
    GRADE_4(10.0F, 1.15F),
    GRADE_3(100.0F, 1.3F),
    SEMI_GRADE_2(300.0F, 1.45F),
    GRADE_2(500.0F, 1.6F),
    SEMI_GRADE_1(1000.0F, 1.75F),
    GRADE_1(1500.0F, 1.9F),
    SPECIAL_GRADE_1(2000.0F, 2.05F),
    SPECIAL_GRADE(2500.0F, 2.2F);

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

    public static SorcererGrade getGrade(float experience) {
        SorcererGrade result = null;

        for (SorcererGrade rank : SorcererGrade.values()) {
            if (rank.getRequiredExperience() > experience) {
                break;
            }
            result = rank;
        }
        return result;
    }
}
