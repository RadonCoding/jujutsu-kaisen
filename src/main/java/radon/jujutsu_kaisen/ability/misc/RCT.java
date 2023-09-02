package radon.jujutsu_kaisen.ability.misc;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.DisplayType;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class RCT extends Ability implements Ability.IChannelened {
    public static final float AMOUNT = 0.2F;
    public static final float COST = 5.0F;

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        AtomicBoolean result = new AtomicBoolean();

        owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap ->
                result.set(owner.getHealth() < owner.getMaxHealth() || ((cap.hasTrait(Trait.SIX_EYES) || cap.hasTrait(Trait.STRONGEST)) && cap.getBurnout() > 0)));
        return result.get();
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.CHANNELED;
    }

    @Override
    public void run(LivingEntity owner) {
        owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
            owner.heal(AMOUNT * cap.getGrade().getPower());

            if (cap.hasTrait(Trait.SIX_EYES) || cap.hasTrait(Trait.STRONGEST)) {
                int burnout = cap.getBurnout();

                if (burnout > 0) {
                    cap.setBurnout(Math.max(0, burnout - 10));
                }
            }
        });
    }

    @Override
    public float getCost(LivingEntity owner) {
        AtomicReference<Float> result = new AtomicReference<>(0.0F);

        owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
            if (owner.getHealth() < owner.getMaxHealth() || ((cap.hasTrait(Trait.SIX_EYES) || cap.hasTrait(Trait.STRONGEST)) && cap.getBurnout() > 0)) {
                result.set(COST);
            }
        });
        return result.get();
    }

    @Override
    public DisplayType getDisplayType() {
        return DisplayType.NONE;
    }

    @Override
    public List<Trait> getRequirements() {
        return List.of(Trait.REVERSE_CURSED_TECHNIQUE);
    }

    @Override
    public boolean isUnlocked(LivingEntity owner) {
        return JJKAbilities.getType(owner) != JujutsuType.CURSE && super.isUnlocked(owner);
    }

    @Override
    public void onRelease(LivingEntity owner, int charge) {

    }
}
