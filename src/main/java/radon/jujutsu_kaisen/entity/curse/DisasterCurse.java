package radon.jujutsu_kaisen.entity.curse;


import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.level.Level;
import radon.jujutsu_kaisen.data.sorcerer.SorcererGrade;

public abstract class DisasterCurse extends CursedSpirit {
    protected DisasterCurse(EntityType<? extends TamableAnimal> pType, Level pLevel) {
        super(pType, pLevel);
    }

    @Override
    public boolean hasMeleeAttack() {
        return true;
    }

    @Override
    public boolean hasArms() {
        return true;
    }

    @Override
    public boolean canJump() {
        return true;
    }

    @Override
    public float getExperience() {
        return SorcererGrade.SPECIAL_GRADE.getRequiredExperience() * 2.0F;
    }
}
