package radon.jujutsu_kaisen.util;

import radon.jujutsu_kaisen.data.sorcerer.SorcererGrade;

public class SorcererUtil {
    public static SorcererGrade getGrade(float experience) {
        SorcererGrade result = SorcererGrade.GRADE_4;

        for (SorcererGrade grade : SorcererGrade.values()) {
            if (experience < grade.getRequiredExperience()) break;

            result = grade;
        }
        return result;
    }

    public static float getPower(float experience) {
        return 1.0F + experience / 1500.0F;
    }
}
