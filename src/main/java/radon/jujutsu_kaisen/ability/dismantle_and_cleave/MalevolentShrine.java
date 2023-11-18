package radon.jujutsu_kaisen.ability.dismantle_and_cleave;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.DomainExpansion;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.entity.MalevolentShrineEntity;
import radon.jujutsu_kaisen.entity.OpenDomainExpansionEntity;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import radon.jujutsu_kaisen.util.HelperMethods;

public class MalevolentShrine extends DomainExpansion implements DomainExpansion.IOpenDomain {
    public static final int DELAY = 2 * 20;
    private static final int INTERVAL = 5;

    @Override
    public void onHitEntity(DomainExpansionEntity domain, LivingEntity owner, LivingEntity entity, boolean instant) {
        super.onHitEntity(domain, owner, entity, instant);

        if (instant || domain.getTime() == DELAY || (domain.level().getGameTime() % INTERVAL == 0 && domain.getTime() >= DELAY)) {
            Ability cleave = JJKAbilities.CLEAVE.get();
            ((IDomainAttack) cleave).performEntity(owner, domain, entity);
        }
    }

    @Override
    public void onHitBlock(DomainExpansionEntity domain, LivingEntity owner, BlockPos pos) {
        Ability dismantle = JJKAbilities.DISMANTLE.get();
        ((IDomainAttack) dismantle).performBlock(owner, domain, pos);
    }

    @Override
    protected void createBarrier(LivingEntity owner) {
        owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
            int width = Math.round(this.getWidth() * cap.getDomainSize());
            int height = Math.round(this.getHeight() * cap.getDomainSize());

            MalevolentShrineEntity domain = new MalevolentShrineEntity(owner, this, width, height);
            owner.level().addFreshEntity(domain);

            cap.setDomain(domain);
        });
    }

    @Override
    public int getWidth() {
        return 64;
    }

    @Override
    public int getHeight() {
        return 16;
    }
}
