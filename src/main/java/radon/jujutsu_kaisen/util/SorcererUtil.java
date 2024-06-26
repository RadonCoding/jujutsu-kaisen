package radon.jujutsu_kaisen.util;


import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.data.sorcerer.SorcererGrade;

public class SorcererUtil {
    public static int getMaximumSkillLevel(float experience, int current, int amount) {
        int maxLevelForExperience = Math.round(experience * 0.01F);
        return Math.min(ConfigHolder.SERVER.maximumSkillLevel.get(), Math.min(maxLevelForExperience, current + amount));
    }

    public static SorcererGrade getGrade(float experience) {
        SorcererGrade result = SorcererGrade.GRADE_4;

        for (SorcererGrade grade : SorcererGrade.values()) {
            if (experience < grade.getRequiredExperience()) break;

            result = grade;
        }
        return result;
    }
}
