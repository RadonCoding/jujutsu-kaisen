package radon.jujutsu_kaisen.ability.limitless;

import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import radon.jujutsu_kaisen.ability.DomainExpansion;
import radon.jujutsu_kaisen.block.JJKBlocks;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.effect.registry.JJKEffects;
import radon.jujutsu_kaisen.entity.domain.base.ClosedDomainExpansionEntity;
import radon.jujutsu_kaisen.entity.DomainExpansionEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;

import java.util.List;

public class UnlimitedVoid extends DomainExpansion implements DomainExpansion.IClosedDomain {
    @Override
    public void onHitEntity(DomainExpansionEntity domain, LivingEntity owner, LivingEntity entity, boolean instant) {
        super.onHitEntity(domain, owner, entity, instant);

        IJujutsuCapability cap = entity.getCapability(JujutsuCapabilityHandler.INSTANCE);

        boolean human = cap == null;

        entity.addEffect(new MobEffectInstance(JJKEffects.UNLIMITED_VOID, Math.round((human ? 60 : 20) * 20 * (instant ? 0.5F : 1.0F)),
                0, false, false, false));
        entity.addEffect(new MobEffectInstance(JJKEffects.STUN, Math.round((human ? 60 : 20) * (instant ? 0.5F : 1.0F)),
                1, false, false, false));
        entity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, Math.round((human ? 60 : 20) * (instant ? 0.5F : 1.0F)),
                4, false, false, false));

        if (domain.getTime() % 20 == 0) {
            if (!human) {
                ISorcererData data = cap.getSorcererData();

                data.increaseBrainDamage();

                if (entity instanceof ServerPlayer player) {
                    PacketDistributor.sendToPlayer(player, new SyncSorcererDataS2CPacket(data.serializeNBT(player.registryAccess())));
                }
            }
        }
    }

    @Override
    protected DomainExpansionEntity createBarrier(LivingEntity owner) {
        ClosedDomainExpansionEntity domain = new ClosedDomainExpansionEntity(owner, this);
        owner.level().addFreshEntity(domain);

        return domain;
    }

    @Override
    public List<Block> getBlocks() {
        return List.of(JJKBlocks.UNLIMITED_VOID.get());
    }
}
