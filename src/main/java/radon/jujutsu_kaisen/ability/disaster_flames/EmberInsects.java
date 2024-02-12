package radon.jujutsu_kaisen.ability.disaster_flames;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.entity.projectile.EmberInsectProjectile;
import radon.jujutsu_kaisen.util.HelperMethods;

public class EmberInsects extends Ability {
    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return HelperMethods.RANDOM.nextInt(5) == 0 && target != null && owner.hasLineOfSight(target);
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    @Override
    public void run(LivingEntity owner) {
        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        ISorcererData data = cap.getSorcererData();

        for (int i = 0; i < 12; i++) {
            int delay = i * 2;

            data.delayTickEvent(() -> {
                owner.swing(InteractionHand.MAIN_HAND);

                EmberInsectProjectile insect = new EmberInsectProjectile(owner, this.getPower(owner),
                        HelperMethods.RANDOM.nextFloat() * (HelperMethods.RANDOM.nextBoolean() ? 1 : -1),
                        HelperMethods.RANDOM.nextFloat() * (HelperMethods.RANDOM.nextBoolean() ? 1 : -1));
                owner.level().addFreshEntity(insect);
            }, delay);
        }
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 50.0F;
    }

    @Override
    public int getCooldown() {
        return 5 * 20;
    }
}
