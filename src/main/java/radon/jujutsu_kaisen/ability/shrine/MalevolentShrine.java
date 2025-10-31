package radon.jujutsu_kaisen.ability.shrine;


import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import radon.jujutsu_kaisen.ability.DomainExpansion;
import radon.jujutsu_kaisen.ability.IOpenDomain;
import radon.jujutsu_kaisen.ability.registry.JJKAbilities;
import radon.jujutsu_kaisen.data.registry.JJKAttachmentTypes;
import radon.jujutsu_kaisen.entity.domain.DomainExpansionEntity;
import radon.jujutsu_kaisen.entity.domain.MalevolentShrineEntity;
import radon.jujutsu_kaisen.tags.JJKBlockTags;

public class MalevolentShrine extends DomainExpansion implements IOpenDomain {
    public static final int DELAY = 2 * 20;

    @Override
    public void onHitLiving(DomainExpansionEntity domain, LivingEntity owner, LivingEntity entity, boolean instant) {
        super.onHitLiving(domain, owner, entity, instant);

        if (!entity.getData(JJKAttachmentTypes.CLEAVED) && (instant || domain.getTime() >= DELAY)) {
            Cleave cleave = JJKAbilities.CLEAVE.get();
            cleave.performEntity(owner, entity, domain, instant);

            entity.setData(JJKAttachmentTypes.CLEAVED, true);
        }
    }

    @Override
    public void onHitNonLiving(DomainExpansionEntity domain, LivingEntity owner, BlockPos pos, boolean force, boolean instant) {
        boolean perform = false;

        if (force || domain.level().getBlockState(pos).is(JJKBlockTags.BARRIER)) {
            perform = (domain.getTime() + pos.asLong()) % 20 == 0;
        }

        if (!perform) return;

        Dismantle dismantle = JJKAbilities.DISMANTLE.get();
        dismantle.performBlock(owner, domain, pos, false);
    }

    @Override
    protected DomainExpansionEntity summon(LivingEntity owner) {
        MalevolentShrineEntity domain = new MalevolentShrineEntity(owner, this, this.getDiameter(), this.getHeight());
        owner.level().addFreshEntity(domain);
        return domain;
    }

    @Override
    public int getDiameter() {
        return 64;
    }

    @Override
    public int getHeight() {
        return 32;
    }
}
