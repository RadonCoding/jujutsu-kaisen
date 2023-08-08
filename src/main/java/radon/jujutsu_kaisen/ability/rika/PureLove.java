package radon.jujutsu_kaisen.ability.rika;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.entity.PureLoveBeam;
import radon.jujutsu_kaisen.entity.curse.RikaEntity;

public class PureLove extends Ability {
    private static final int DURATION = 10 * 20;

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return ((RikaEntity) owner).isOpen();
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    @Override
    public void run(LivingEntity owner) {
        PureLoveBeam beam = new PureLoveBeam(owner, (float) ((owner.yHeadRot + 90.0F) * Math.PI / 180.0F), (float) (-owner.getXRot() * Math.PI / 180.0F), DURATION);
        owner.level.addFreshEntity(beam);
    }

    @Override
    public Status checkTriggerable(LivingEntity owner) {
        if (!((RikaEntity) owner).isOpen()) return Status.FAILURE;
        return super.checkTriggerable(owner);
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 500.0F;
    }

    @Override
    public int getCooldown() {
        return 30 * 20;
    }

    @Override
    public Classification getClassification() {
        return Classification.PURE_LOVE;
    }
}
