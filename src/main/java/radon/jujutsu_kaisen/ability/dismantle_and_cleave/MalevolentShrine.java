package radon.jujutsu_kaisen.ability.dismantle_and_cleave;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.DomainExpansion;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.entity.MalevolentShrineEntity;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import radon.jujutsu_kaisen.util.HelperMethods;

public class MalevolentShrine extends DomainExpansion implements DomainExpansion.IOpenDomain {
    public static final int DELAY = 2 * 20;

    @Override
    public void onHitEntity(DomainExpansionEntity domain, LivingEntity owner, LivingEntity entity) {
        super.onHitEntity(domain, owner, entity);

        if (domain.getTime() >= DELAY) {
            Ability cleave = JJKAbilities.CLEAVE.get();
            ((IDomainAttack) cleave).perform(owner, domain, entity);
        }
    }

    @Override
    public void onHitBlock(DomainExpansionEntity domain, LivingEntity owner, BlockPos pos) {
        if (owner.level() instanceof ServerLevel) {
            BlockState state = owner.level().getBlockState(pos);

            owner.level().playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.MASTER,
                    1.0F, (1.0F + (HelperMethods.RANDOM.nextFloat() - HelperMethods.RANDOM.nextFloat()) * 0.2F) * 0.5F);

            if (owner.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
                if (state.getFluidState().isEmpty() && state.getBlock().defaultDestroyTime() > Block.INDESTRUCTIBLE) {
                    owner.level().setBlock(pos, Blocks.AIR.defaultBlockState(),
                            Block.UPDATE_ALL | Block.UPDATE_SUPPRESS_DROPS);

                    if (HelperMethods.RANDOM.nextInt(10) == 0) {
                        ((ServerLevel) owner.level()).sendParticles(ParticleTypes.EXPLOSION, pos.getX(), pos.getY(), pos.getZ(), 0,
                                0.0D, 0.0D, 0.0D, 0.0D);
                    }
                }
            }
        }
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
