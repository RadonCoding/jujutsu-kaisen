package radon.jujutsu_kaisen.ability.base;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class DomainExpansion extends Ability implements Ability.IToggled {
    public static final int BURNOUT = 30 * 20;

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        AtomicBoolean result = new AtomicBoolean();

        if (JJKAbilities.hasToggled(owner, this)) {
            result.set(target != null);
        } else {
            if (!owner.isOnGround() || target == null) return false;

            double distance = owner.distanceTo(target);

            if (this instanceof DomainExpansion.IClosedDomain closed) {
                if (distance >= closed.getRadius() / 2.0F) {
                    return false;
                }
            }

            if (this instanceof DomainExpansion.IOpenDomain open) {
                if (distance >= open.getWidth() / 2.0F) {
                    return false;
                }
            }

            owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                result.set(owner.getHealth() / owner.getMaxHealth() < 0.75F || cap.getEnergy() - this.getCost(owner) < (cap.getMaxEnergy() / 2) || target.getHealth() > owner.getHealth() * 2);

                for (DomainExpansionEntity ignored : cap.getDomains((ServerLevel) owner.level)) {
                    result.set(true);
                }

                Status status = this.getStatus(owner, true, false, false, false);

                if (result.get() && (status == Status.SIMPLE_DOMAIN || status == Status.DOMAIN_AMPLIFICATION)) {
                    if (cap.hasToggled(JJKAbilities.SIMPLE_DOMAIN.get())) {
                        cap.toggle(owner, JJKAbilities.SIMPLE_DOMAIN.get());
                    }
                    if (cap.hasToggled(JJKAbilities.DOMAIN_AMPLIFICATION.get())) {
                        cap.toggle(owner, JJKAbilities.DOMAIN_AMPLIFICATION.get());
                    }
                }
            });
        }
        return result.get();
    }

    @Override
    public ActivationType getActivationType() {
        return ActivationType.TOGGLED;
    }

    @Override
    public void onEnabled(LivingEntity owner) {
        this.createBarrier(owner);
    }

    @Override
    public void onDisabled(LivingEntity owner) {

    }

    @Override
    public void run(LivingEntity owner) {

    }

    @Override
    public float getCost(LivingEntity owner) {
        return 1000.0F / this.getDuration();
    }

    public abstract void onHitEntity(DomainExpansionEntity domain, LivingEntity owner, Entity entity);
    public abstract void onHitBlock(DomainExpansionEntity domain, LivingEntity owner,  BlockPos pos);

    protected abstract void createBarrier(LivingEntity owner);

    public boolean bypass() {
        return false;
    }

    @Override
    public boolean isTechnique() {
        return true;
    }

    @Override
    public final int getCooldown() {
        return 60 * 20;
    }

    @Override
    public boolean shouldLog() {
        return false;
    }

    public interface IClosedDomain {
        int getRadius();
        List<Block> getBlocks();
    }

    public interface IOpenDomain {
        int getWidth();
        int getHeight();
    }
}
