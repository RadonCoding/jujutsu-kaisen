package radon.jujutsu_kaisen.ability.shrine;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.DomainExpansion;
import radon.jujutsu_kaisen.ability.IDomainAttack;
import radon.jujutsu_kaisen.ability.registry.JJKAbilities;
import radon.jujutsu_kaisen.entity.domain.MalevolentShrineEntity;
import radon.jujutsu_kaisen.entity.DomainExpansionEntity;
import radon.jujutsu_kaisen.entity.domain.base.ClosedDomainExpansionEntity;
import radon.jujutsu_kaisen.entity.domain.base.OpenDomainExpansionEntity;
import radon.jujutsu_kaisen.util.HelperMethods;

public class MalevolentShrine extends DomainExpansion implements DomainExpansion.IOpenDomain {
    public static final int DELAY = 2 * 20;
    private static final int INTERVAL = 10;

    @Override
    public void onHitEntity(DomainExpansionEntity domain, LivingEntity owner, LivingEntity entity, boolean instant) {
        super.onHitEntity(domain, owner, entity, instant);

        if (instant || domain.getTime() == DELAY || (domain.level().getGameTime() % INTERVAL == 0 && domain.getTime() >= DELAY)) {
            Cleave cleave = JJKAbilities.CLEAVE.get();
            cleave.performEntity(owner, entity, domain, instant);
        }
    }

    @Override
    public void onHitBlock(DomainExpansionEntity domain, LivingEntity owner, BlockPos pos, boolean instant) {
        int radius = 0;

        if (domain instanceof ClosedDomainExpansionEntity closed) {
            radius = closed.getRadius();
        }

        if (domain instanceof OpenDomainExpansionEntity open) {
            radius = open.getWidth() * open.getHeight();
        }

        if (HelperMethods.RANDOM.nextInt(radius * 10) != 0) return;

        Dismantle dismantle = JJKAbilities.DISMANTLE.get();
        dismantle.performBlock(owner, domain, pos, false);
    }

    @Override
    protected DomainExpansionEntity createBarrier(LivingEntity owner) {
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
