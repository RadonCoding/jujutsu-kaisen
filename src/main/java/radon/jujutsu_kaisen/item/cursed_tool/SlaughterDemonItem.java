package radon.jujutsu_kaisen.item.cursed_tool;

import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.world.item.Tier;
import radon.jujutsu_kaisen.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.item.CursedToolItem;

public class SlaughterDemonItem extends CursedToolItem {
    public SlaughterDemonItem(Tier pTier, Properties pProperties) {
        super(pTier, pProperties);
    }

    @Override
    public SorcererGrade getGrade() {
        return SorcererGrade.GRADE_4;
    }
}
