package radon.jujutsu_kaisen.data.mission;

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
}