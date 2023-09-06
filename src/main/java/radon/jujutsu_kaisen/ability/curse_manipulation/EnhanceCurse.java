package radon.jujutsu_kaisen.ability.curse_manipulation;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.DisplayType;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.client.particle.ParticleColors;
import radon.jujutsu_kaisen.client.particle.VaporParticle;
import radon.jujutsu_kaisen.entity.base.CursedSpirit;
import radon.jujutsu_kaisen.util.HelperMethods;

public class EnhanceCurse extends Ability {
    private static final double RANGE = 5.0D;

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return false;
    }

    @Override
    public Ability.ActivationType getActivationType(LivingEntity owner) {
        return Ability.ActivationType.INSTANT;
    }

    private @Nullable Entity getTarget(LivingEntity owner) {
        if (HelperMethods.getLookAtHit(owner, RANGE) instanceof EntityHitResult hit) {
            return hit.getEntity();
        }
        return null;
    }

    @Override
    public void run(LivingEntity owner) {
        if (!(owner.level instanceof ServerLevel level)) return;

        if (this.getTarget(owner) instanceof CursedSpirit curse && curse.isTame() && curse.getOwner() == owner &&
                curse.getGrade().ordinal() < SorcererGrade.SPECIAL_GRADE.ordinal() && curse.getGrade().ordinal() + 1 <= JJKAbilities.getGrade(owner).ordinal()) {
            owner.swing(InteractionHand.MAIN_HAND);
            if (curse.isTame() && curse.getOwner() == owner) {
                curse.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                    cap.setGrade(SorcererGrade.values()[cap.getGrade().ordinal() + 1]);

                    for (int i = 0; i < 8; i++) {
                        level.sendParticles(new VaporParticle.VaporParticleOptions(ParticleColors.getCursedEnergyColor(curse), 1.5F, 0.5F, false, 1),
                                curse.getX() + (HelperMethods.RANDOM.nextGaussian() * 0.1D),
                                curse.getY() + HelperMethods.RANDOM.nextDouble(curse.getBbHeight()),
                                curse.getZ() + (HelperMethods.RANDOM.nextGaussian() * 0.1D),
                                0, 0.0D, HelperMethods.RANDOM.nextDouble(), 0.0D, 1.5D);
                    }
                });
            }
        }
    }

    @Override
    public Ability.Status checkTriggerable(LivingEntity owner) {
        if (!(this.getTarget(owner) instanceof CursedSpirit curse) || !curse.isTame() || curse.getOwner() != owner ||
                curse.getGrade().ordinal() == SorcererGrade.SPECIAL_GRADE.ordinal() && curse.getGrade().ordinal() + 1 > JJKAbilities.getGrade(owner).ordinal()) {
            return Ability.Status.FAILURE;
        }
        return super.checkTriggerable(owner);
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 100.0F;
    }

    @Override
    public boolean isTechnique() {
        return true;
    }

    @Override
    public DisplayType getDisplayType() {
        return DisplayType.SCROLL;
    }
}
