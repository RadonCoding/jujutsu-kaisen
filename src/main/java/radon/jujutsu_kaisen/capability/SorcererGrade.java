package radon.jujutsu_kaisen.capability;

public enum SorcererGrade {
    UNRANKED(0.0F),
    GRADE_4(10.0F),
    GRADE_3(50.0F),
    SEMI_GRADE_2(100.0F),
    GRADE_2(150.0F),
    SEMI_GRADE_1(250.0F),
    GRADE_1(300.0F),
    SPECIAL_GRADE_1(500.0F),
    SPECIAL_GRADE(1000.0F);

    private final float experience;

    SorcererGrade(float experience) {
        this.experience = experience;
    }

    public float getExperience() {
        return this.experience;
    }

    public static SorcererGrade getGrade(float experience) {
        SorcererGrade result = null;

        for (SorcererGrade rank : SorcererGrade.values()) {
            if (rank.getExperience() > experience) {
                break;
            }
            result = rank;
        }
        return result;
    }
}
