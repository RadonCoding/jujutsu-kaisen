package radon.jujutsu_kaisen.ability.ai.rika;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.entity.effect.PureLoveBeam;
import radon.jujutsu_kaisen.entity.curse.RikaEntity;
import radon.jujutsu_kaisen.util.HelperMethods;

public class ShootPureLove extends Ability {
    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return ((RikaEntity) owner).isOpen() || (target != null && owner.hasLineOfSight(target) && HelperMethods.RANDOM.nextInt(10) == 0);
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    @Override
    public void run(LivingEntity owner) {
        PureLoveBeam beam = new PureLoveBeam(owner, (float) ((owner.yHeadRot + 90.0F) * Math.PI / 180.0F), (float) (-owner.getXRot() * Math.PI / 180.0F));
        owner.level.addFreshEntity(beam);
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
