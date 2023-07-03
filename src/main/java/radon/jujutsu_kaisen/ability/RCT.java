package radon.jujutsu_kaisen.ability;

import net.minecraft.world.entity.LivingEntity;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;

import java.util.concurrent.atomic.AtomicBoolean;

public class RCT extends Ability implements Ability.IToggled {
    private static final float AMOUNT = 1.0F;
    private static final int DELAY = 20;

    @Override
    public ActivationType getActivationType() {
        return ActivationType.TOGGLED;
    }

    @Override
    public void run(LivingEntity owner) {
        owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
            if (owner.level.getGameTime() % (DELAY / (cap.getGrade().ordinal() + 1)) == 0) {
                owner.heal(AMOUNT);
            }              int burnout = cap.getBurnout();

            if (burnout > 0) {
                cap.setBurnout(--burnout);
            }
        });
    }

    @Override
    public boolean checkCost(LivingEntity owner) {
        AtomicBoolean result = new AtomicBoolean();

        owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap ->
                result.set(owner.getHealth() == owner.getMaxHealth() && cap.getBurnout() == 0));
        return result.get() || super.checkCost(owner);
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 10.0F;
    }

    @Override
    public void onEnabled(LivingEntity owner) {

    }

    @Override
    public void onDisabled(LivingEntity owner) {

    }
}
