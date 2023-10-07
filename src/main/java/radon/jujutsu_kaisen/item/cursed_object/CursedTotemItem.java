package radon.jujutsu_kaisen.item.cursed_object;

import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.item.base.CursedObjectItem;

public class CursedTotemItem extends CursedObjectItem {
    public CursedTotemItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public SorcererGrade getGrade() {
        return SorcererGrade.GRADE_1;
    }
}
