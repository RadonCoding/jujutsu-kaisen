package radon.jujutsu_kaisen.item;

import net.minecraft.world.item.Tier;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.item.base.CursedToolItem;

public class InvertedSpearOfHeavenItem extends CursedToolItem {
    public InvertedSpearOfHeavenItem(Tier pTier, int pAttackDamageModifier, float pAttackSpeedModifier, Properties pProperties) {
        super(pTier, pAttackDamageModifier, pAttackSpeedModifier, pProperties);
    }

    @Override
    protected SorcererGrade getGrade() {
        return SorcererGrade.SPECIAL_GRADE;
    }
}
