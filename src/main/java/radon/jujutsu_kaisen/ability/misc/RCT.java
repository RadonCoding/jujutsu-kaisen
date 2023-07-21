package radon.jujutsu_kaisen.ability.misc;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class RCT extends Ability {
    private static final float AMOUNT = 0.2F;

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        AtomicBoolean result = new AtomicBoolean();

        owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap ->
                result.set((owner.getHealth() / owner.getMaxHealth()) < 0.75F || cap.hasTrait(Trait.SIX_EYES) && cap.getBurnout() > 0));
        return result.get();
    }

    @Override
    public ActivationType getActivationType() {
        return ActivationType.CHANNELED;
    }

    @Override
    public void run(LivingEntity owner) {
        owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
            owner.heal(AMOUNT * cap.getGrade().getPower());

            int burnout = cap.getBurnout();

            if (burnout > 0) {
                cap.setBurnout(--burnout);
            }
        });
    }

    @Override
    public float getCost(LivingEntity owner) {
        AtomicReference<Float> result = new AtomicReference<>(0.0F);

        owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
            if ((cap.hasTrait(Trait.SIX_EYES) && cap.getBurnout() > 0) || owner.getHealth() < owner.getMaxHealth()) {
                result.set(10.0F);
            }
        });
        return result.get();
    }

    @Override
    public boolean isDisplayed() {
        return false;
    }
}
