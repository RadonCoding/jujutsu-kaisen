package radon.jujutsu_kaisen.ability.misc;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.DisplayType;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;

import java.util.concurrent.atomic.AtomicReference;

public class Heal extends Ability implements Ability.IChannelened {
    private static final float AMOUNT = 0.2F;

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return owner.getHealth() < owner.getMaxHealth();
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.CHANNELED;
    }

    @Override
    public void run(LivingEntity owner) {
        owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap ->
                owner.heal(AMOUNT * cap.getGrade().getPower()));
    }

    @Override
    public float getCost(LivingEntity owner) {
        AtomicReference<Float> result = new AtomicReference<>(0.0F);

        owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
            if (owner.getHealth() < owner.getMaxHealth()) {
                result.set(2.5F);
            }
        });
        return result.get();
    }

    @Override
    public DisplayType getDisplayType() {
        return DisplayType.NONE;
    }

    @Override
    public void onRelease(LivingEntity owner, int charge) {

    }
}
