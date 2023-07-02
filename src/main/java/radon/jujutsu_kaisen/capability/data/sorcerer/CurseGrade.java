package radon.jujutsu_kaisen.capability.data.sorcerer;

public enum CurseGrade {
    GRADE_4(10.0F),
    GRADE_3(25.0F),
    SEMI_GRADE_2(50.0F),
    GRADE_2(75.0F),
    SEMI_GRADE_1(100.0F),
    GRADE_1(125.0F),
    SPECIAL_GRADE_1(150.0F),
    SPECIAL_GRADE(300.0F);

    private final float experience;

    CurseGrade(float experience) {
        this.experience = experience;
    }

    public float getExperience() {
        return this.experience;
    }
}
