package radon.jujutsu_kaisen.ability.base;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.DisplayType;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.LivingHitByDomainEvent;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class DomainExpansion extends Ability implements Ability.IToggled {
    public static final int BURNOUT = 30 * 20;

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        AtomicBoolean result = new AtomicBoolean();

        if (JJKAbilities.hasToggled(owner, this)) {
            if (target != null) {
                result.set(true);

                if (this instanceof IOpenDomain) {
                    owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                        DomainExpansionEntity domain = cap.getDomain((ServerLevel) owner.level);
                        result.set(domain != null && domain.isInsideBarrier(target.blockPosition()));
                    });
                }
            }
        } else {
            if (target == null) return false;

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
                result.set(cap.getType() == JujutsuType.CURSE || cap.hasTrait(Trait.REVERSE_CURSED_TECHNIQUE) ? owner.getHealth() / owner.getMaxHealth() < 0.75F :
                        owner.getHealth() / owner.getMaxHealth() < 0.25F || cap.getEnergy() - this.getCost(owner) < (cap.getMaxEnergy() / 2) ||
                        target.getHealth() > owner.getHealth() * 2);

                for (DomainExpansionEntity ignored : cap.getDomains((ServerLevel) owner.level)) {
                    result.set(true);
                }

                Status status = this.getStatus(owner, true, false, false, false);

                if (result.get() && (status == Status.SIMPLE_DOMAIN || status == Status.DOMAIN_AMPLIFICATION)) {
                    if (cap.hasToggled(JJKAbilities.SIMPLE_DOMAIN.get())) {
                        cap.toggle(owner, JJKAbilities.SIMPLE_DOMAIN.get());
                    }
                }
            });
        }
        return result.get();
    }

    @Override
    public Status checkStatus(LivingEntity owner) {
        AtomicBoolean result = new AtomicBoolean();

        if (owner.level instanceof ServerLevel level) {
            owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                if (cap.getDomain(level) == null) {
                    result.set(true);
                }
            });
        }
        return result.get() ? Status.FAILURE : super.checkStatus(owner);
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
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
        return 2.5F;
    }

    public void onHitEntity(DomainExpansionEntity domain, LivingEntity owner, LivingEntity entity) {
        MinecraftForge.EVENT_BUS.post(new LivingHitByDomainEvent(entity, this));
    }

    public abstract void onHitBlock(DomainExpansionEntity domain, LivingEntity owner,  BlockPos pos);

    protected abstract void createBarrier(LivingEntity owner);

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

    @Override
    public DisplayType getDisplayType() {
        return DisplayType.DOMAIN;
    }

    @Override
    public List<Trait> getRequirements() {
        return List.of(Trait.DOMAIN_EXPANSION);
    }

    public interface IClosedDomain {
        int getRadius();
        List<Block> getBlocks();
        default List<Block> getFillBlocks() {
            return this.getBlocks();
        }
    }

    public interface IOpenDomain {
        int getWidth();
        int getHeight();
    }
}
