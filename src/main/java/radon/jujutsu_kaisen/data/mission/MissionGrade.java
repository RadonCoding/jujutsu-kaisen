package radon.jujutsu_kaisen.data.mission;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import radon.jujutsu_kaisen.data.sorcerer.SorcererGrade;

public enum MissionGrade {
    D(0x7FFFFF),
    C(0x7EFF80),
    B(0xFEFF7F),
    A(0xFFBF7F),
    S(0xFF7F7E);

    private final int color;

    MissionGrade(int color) {
        this.color = color;
    }

    public int getColor() {
        return this.color;
    }

    public SorcererGrade toSorcererGrade() {
        return switch (this) {
            case D -> SorcererGrade.GRADE_4;
            case C -> SorcererGrade.GRADE_3;
            case B -> SorcererGrade.GRADE_2;
            case A -> SorcererGrade.GRADE_1;
            case S -> SorcererGrade.SPECIAL_GRADE;
        };
    }
}