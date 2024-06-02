package radon.jujutsu_kaisen.ability.idle_transfiguration;

import radon.jujutsu_kaisen.ability.IClosedDomain;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.DomainExpansion;
import radon.jujutsu_kaisen.block.JJKBlocks;
import radon.jujutsu_kaisen.effect.registry.JJKEffects;
import radon.jujutsu_kaisen.entity.domain.base.ClosedDomainExpansionEntity;
import radon.jujutsu_kaisen.entity.domain.SelfEmbodimentOfPerfectionEntity;
import radon.jujutsu_kaisen.entity.DomainExpansionEntity;
import radon.jujutsu_kaisen.util.RotationUtil;

import java.util.List;

public class SelfEmbodimentOfPerfection extends DomainExpansion implements IClosedDomain {
    @Override
    public void onHitEntity(DomainExpansionEntity domain, LivingEntity owner, LivingEntity entity, boolean instant) {
        super.onHitEntity(domain, owner, entity, instant);

        if (IdleTransfiguration.checkSukuna(owner, entity)) return;

        float attackerStrength = IdleTransfiguration.calculateStrength(owner);
        float victimStrength = IdleTransfiguration.calculateStrength(entity);

        int required = Math.round((victimStrength / attackerStrength) * 2);

        MobEffectInstance instance = new MobEffectInstance(JJKEffects.TRANSFIGURED_SOUL, Math.round(20 * 20 * (instant ? 0.5F : 1.0F)),
                required, false, true, true);
        entity.addEffect(instance);
    }

    @Override
    protected DomainExpansionEntity summon(LivingEntity owner) {
        ClosedDomainExpansionEntity domain = new ClosedDomainExpansionEntity(owner, this);
        owner.level().addFreshEntity(domain);

        SelfEmbodimentOfPerfectionEntity center = new SelfEmbodimentOfPerfectionEntity(domain);

        Vec3 pos = owner.position()
                .add(owner.getUpVector(1.0F).scale(center.getBbHeight() / 2.0F))
                .subtract(RotationUtil.calculateViewVector(0.0F, owner.getYRot())
                        .multiply(center.getBbWidth() / 2.0F, 0.0D, center.getBbWidth() / 2.0F));
        center.moveTo(pos.x, pos.y, pos.z, 180.0F - RotationUtil.getTargetAdjustedYRot(owner), 0.0F);

        owner.level().addFreshEntity(center);

        return domain;
    }

    @Override
    public List<Block> getBlocks() {
        return List.of(JJKBlocks.DOMAIN_TRANSPARENT.get());
    }
}
