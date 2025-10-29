package radon.jujutsu_kaisen.ability.shrine;


import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import radon.jujutsu_kaisen.ability.DomainExpansion;
import radon.jujutsu_kaisen.ability.IClosedDomain;
import radon.jujutsu_kaisen.ability.IOpenDomain;
import radon.jujutsu_kaisen.ability.registry.JJKAbilities;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.data.registry.JJKAttachmentTypes;
import radon.jujutsu_kaisen.entity.domain.ClosedDomainExpansionEntity;
import radon.jujutsu_kaisen.entity.domain.DomainExpansionEntity;
import radon.jujutsu_kaisen.entity.domain.MalevolentShrineEntity;
import radon.jujutsu_kaisen.entity.domain.OpenDomainExpansionEntity;
import radon.jujutsu_kaisen.util.HelperMethods;

public class MalevolentShrine extends DomainExpansion implements IOpenDomain {
    public static final int DELAY = 2 * 20;

    @Override
    public void onHitEntity(DomainExpansionEntity domain, LivingEntity owner, LivingEntity entity, boolean instant) {
        super.onHitEntity(domain, owner, entity, instant);

        if (!entity.getData(JJKAttachmentTypes.CLEAVED) && (instant || domain.getTime() >= DELAY)) {
            Cleave cleave = JJKAbilities.CLEAVE.get();
            cleave.performEntity(owner, entity, domain, instant);

            entity.setData(JJKAttachmentTypes.CLEAVED, true);
        }
    }

    @Override
    public void onHitBlock(DomainExpansionEntity domain, LivingEntity owner, BlockPos pos, boolean instant) {
        if (owner.level().isEmptyBlock(pos)) return;

        if (pos.getY() < domain.blockPosition().getY()) return;

        double probability = 0.0D;

        if (domain instanceof ClosedDomainExpansionEntity) {
            int radius = ConfigHolder.SERVER.domainRadius.getAsInt();
            double distance = Math.sqrt(domain.distanceToSqr(pos.getCenter()));
            probability = 1.0D - distance / radius;
        } else if (domain instanceof OpenDomainExpansionEntity open) {
            BlockPos center = domain.blockPosition();
            BlockPos relative = pos.subtract(center);

            double nx = (double) Math.abs(relative.getX()) / open.getWidth();
            double ny = (double) Math.abs(relative.getY()) / open.getHeight();
            double nz = (double) Math.abs(relative.getZ()) / open.getWidth();

            probability = 1.0D - (nx + ny + nz) / 3.0D;
        }

        probability /= 20;

        if (HelperMethods.RANDOM.nextDouble() < probability) {
            Dismantle dismantle = JJKAbilities.DISMANTLE.get();
            dismantle.performBlock(owner, domain, pos, false);
        }
    }


    @Override
    protected DomainExpansionEntity summon(LivingEntity owner) {
        MalevolentShrineEntity domain = new MalevolentShrineEntity(owner, this, this.getWidth(), this.getHeight());
        owner.level().addFreshEntity(domain);
        return domain;
    }

    @Override
    public int getWidth() {
        return 64;
    }

    @Override
    public int getHeight() {
        return 32;
    }
}
