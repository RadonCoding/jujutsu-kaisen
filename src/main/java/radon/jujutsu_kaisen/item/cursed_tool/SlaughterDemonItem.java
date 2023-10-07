package radon.jujutsu_kaisen.item.cursed_tool;

import net.minecraft.world.item.Tier;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.item.base.CursedToolItem;

public class SlaughterDemonItem extends CursedToolItem {
    public SlaughterDemonItem(Tier pTier, int pAttackDamageModifier, float pAttackSpeedModifier, Properties pProperties) {
        super(pTier, pAttackDamageModifier, pAttackSpeedModifier, pProperties);
    }

    @Override
    protected SorcererGrade getGrade() {
        return SorcererGrade.GRADE_4;
    }
}
