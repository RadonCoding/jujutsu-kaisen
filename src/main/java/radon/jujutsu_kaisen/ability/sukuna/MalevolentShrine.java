package radon.jujutsu_kaisen.ability.sukuna;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.base.DomainExpansion;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.entity.MalevolentShrineEntity;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import radon.jujutsu_kaisen.sound.JJKSounds;

public class MalevolentShrine extends DomainExpansion implements DomainExpansion.IOpenDomain {
    @Override
    public int getDuration() {
        return 30 * 20;
    }

    @Override
    public void onHitEntity(DomainExpansionEntity domain, LivingEntity owner, Entity entity) {
        if (domain.getRandom().nextInt(5) == 0) {
            if (entity instanceof LivingEntity living) {
                Ability cleave = JJKAbilities.CLEAVE.get();

                if (cleave instanceof IDomainAttack attack) {
                    attack.perform(owner, domain, living);
                }
            }
        }
    }

    @Override
    public void onHitBlock(DomainExpansionEntity domain, LivingEntity owner, BlockPos pos) {
        if (owner.level instanceof ServerLevel level) {
            BlockState state = owner.level.getBlockState(pos);

            owner.level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), JJKSounds.SLASH.get(), SoundSource.MASTER, 0.05F, 1.0F);
            level.sendParticles(ParticleTypes.SWEEP_ATTACK, pos.getX(), pos.getY(), pos.getZ(),
                    0, 0.0D, 0.0D, 0.0D, 0.0D);

            if (state.getFluidState().isEmpty() && state.getBlock().defaultDestroyTime() > Block.INDESTRUCTIBLE) {
                owner.level.destroyBlock(pos, false);
            }
        }
    }

    @Override
    protected void createBarrier(LivingEntity owner) {
        owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
            int duration = this.getDuration();
            int width = this.getWidth();
            int height = this.getHeight();

            MalevolentShrineEntity domain = new MalevolentShrineEntity(owner, this, width, height, duration,
                    cap.getGrade().getPower() + (cap.hasTrait(Trait.STRONGEST) ? 1.0F : 0.0F));
            owner.level.addFreshEntity(domain);

            cap.setDomain(domain);
        });
    }

    @Override
    public int getWidth() {
        return 100;
    }

    @Override
    public int getHeight() {
        return 10;
    }
}
