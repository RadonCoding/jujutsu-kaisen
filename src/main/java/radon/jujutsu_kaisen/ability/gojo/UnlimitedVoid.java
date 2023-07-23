package radon.jujutsu_kaisen.ability.gojo;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import radon.jujutsu_kaisen.ability.base.DomainExpansion;
import radon.jujutsu_kaisen.block.JJKBlocks;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.entity.ClosedDomainExpansionEntity;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;

import java.util.List;

public class UnlimitedVoid extends DomainExpansion implements DomainExpansion.IClosedDomain {
    @Override
    public int getRadius() {
        return 20;
    }

    @Override
    public int getDuration() {
        return 30 * 20;
    }

    @Override
    public List<Block> getBlocks() {
        return List.of(JJKBlocks.UNLIMITED_VOID.get());
    }

    @Override
    public void onHitEntity(DomainExpansionEntity domain, LivingEntity owner, Entity entity) {
        int duration = this.getDuration() - domain.getTime();

        if (entity instanceof LivingEntity living) {
            if (!living.hasEffect(JJKEffects.UNLIMITED_VOID.get())) {
                living.addEffect(new MobEffectInstance(JJKEffects.UNLIMITED_VOID.get(), duration, 0, false, false, false));
                living.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, duration, 4));
            }
        }
    }

    @Override
    public void onHitBlock(DomainExpansionEntity domain, LivingEntity owner, BlockPos pos) {

    }

    @Override
    protected void createBarrier(LivingEntity owner) {
        owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
            int duration = this.getDuration();
            int radius = this.getRadius();
            List<Block> blocks = this.getBlocks();

            ClosedDomainExpansionEntity domain = new ClosedDomainExpansionEntity(owner, this, blocks, radius, duration);
            owner.level.addFreshEntity(domain);
        });
    }

    @Override
    public boolean bypass() {
        return true;
    }
}
