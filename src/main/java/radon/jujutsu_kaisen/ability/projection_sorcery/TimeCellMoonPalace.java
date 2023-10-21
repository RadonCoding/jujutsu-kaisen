package radon.jujutsu_kaisen.ability.projection_sorcery;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.DomainExpansion;
import radon.jujutsu_kaisen.block.JJKBlocks;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.entity.ClosedDomainExpansionEntity;
import radon.jujutsu_kaisen.entity.TimeCellMoonPalaceEntity;
import radon.jujutsu_kaisen.entity.base.DomainExpansionCenterEntity;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import radon.jujutsu_kaisen.entity.projectile.FilmGaugeProjectile;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.List;

public class TimeCellMoonPalace extends DomainExpansion implements DomainExpansion.IClosedDomain {
    @Override
    public @Nullable ParticleOptions getEnvironmentParticle() {
        return ParticleTypes.WHITE_ASH;
    }

    @Override
    public void onHitEntity(DomainExpansionEntity domain, LivingEntity owner, LivingEntity entity) {
        super.onHitEntity(domain, owner, entity);

        if (entity.hasEffect(JJKEffects.TWENTY_FOUR_FRAME_RULE.get())) return;

        if (owner.level().getGameTime() % 20 == 0) {
            DomainExpansionCenterEntity center = domain.getDomainCenter();

            if (center == null) return;

            owner.level().addFreshEntity(new FilmGaugeProjectile(owner, this.getPower(owner), entity, center));
        }
    }

    @Override
    public void onHitBlock(DomainExpansionEntity domain, LivingEntity owner, BlockPos pos) {

    }

    @Override
    protected void createBarrier(LivingEntity owner) {
        owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
            int radius = Math.round(this.getRadius(owner));

            ClosedDomainExpansionEntity domain = new ClosedDomainExpansionEntity(owner, this, radius);
            owner.level().addFreshEntity(domain);

            cap.setDomain(domain);

            TimeCellMoonPalaceEntity entity = new TimeCellMoonPalaceEntity(domain);
            Vec3 pos = owner.position()
                    .subtract(HelperMethods.getLookAngle(owner).multiply(entity.getBbWidth(), 0.0D, entity.getBbWidth()));
            entity.moveTo(pos.x(), pos.y(), pos.z(), owner.getYRot(), owner.getXRot());

            Vec3 look = HelperMethods.getLookAngle(owner);
            double d0 = look.horizontalDistance();
            entity.setYRot((float) (Mth.atan2(look.x(), look.z()) * (double) (180.0F / (float) Math.PI)));
            entity.setXRot((float) (Mth.atan2(look.y(), d0) * (double) (180.0F / (float) Math.PI)));

            owner.level().addFreshEntity(entity);
        });
    }

    @Override
    public List<Block> getBlocks() {
        return List.of(JJKBlocks.UNLIMITED_VOID.get());
    }
}
