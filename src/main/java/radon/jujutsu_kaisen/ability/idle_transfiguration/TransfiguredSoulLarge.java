package radon.jujutsu_kaisen.ability.idle_transfiguration;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.TransfiguredSoul;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.entity.idle_transfiguration.TransfiguredSoulLargeEntity;
import radon.jujutsu_kaisen.util.HelperMethods;

public class TransfiguredSoulLarge extends TransfiguredSoul {
    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        if (target == null) return false;

        return HelperMethods.RANDOM.nextInt(5) == 0;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    @Override
    public void run(LivingEntity owner) {
        owner.swing(InteractionHand.MAIN_HAND);

        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        TransfiguredSoulLargeEntity soul = new TransfiguredSoulLargeEntity(owner);
        owner.level().addFreshEntity(soul);

        cap.addSummon(soul);
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 100.0F;
    }

    @Override
    public int getSoulCost() {
        return 1;
    }
}
