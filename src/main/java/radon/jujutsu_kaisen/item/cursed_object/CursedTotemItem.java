package radon.jujutsu_kaisen.item.cursed_object;

import radon.jujutsu_kaisen.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.item.CursedObjectItem;

public class CursedTotemItem extends CursedObjectItem {
    public CursedTotemItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public SorcererGrade getGrade() {
        return SorcererGrade.GRADE_1;
    }
}
