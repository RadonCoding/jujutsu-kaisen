package radon.jujutsu_kaisen.item.cursed_object;


import radon.jujutsu_kaisen.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.item.CursedObjectItem;

public class CursedCompassItem extends CursedObjectItem {
    public CursedCompassItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public SorcererGrade getGrade() {
        return SorcererGrade.GRADE_3;
    }
}
