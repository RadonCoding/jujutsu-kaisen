package radon.jujutsu_kaisen.ability.scissor;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.living.LivingAttackEvent;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.IAttack;
import radon.jujutsu_kaisen.ability.base.IChanneled;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.ICharged;
import radon.jujutsu_kaisen.ability.base.IDomainAttack;
import radon.jujutsu_kaisen.ability.base.IDurationable;
import radon.jujutsu_kaisen.ability.base.ITenShadowsAttack;
import radon.jujutsu_kaisen.ability.base.IToggled;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.entity.SimpleDomainEntity;
import radon.jujutsu_kaisen.entity.curse.KuchisakeOnnaEntity;
import radon.jujutsu_kaisen.entity.effect.ScissorEntity;
import radon.jujutsu_kaisen.util.EntityUtil;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

import java.util.List;

public class Scissors extends Ability {
    public static final double RANGE = 16.0D;
    
    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return target != null && !target.isDeadOrDying() && owner.distanceTo(target) <= RANGE;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    private List<LivingEntity> getTargets(LivingEntity owner) {
        return EntityUtil.getEntities(LivingEntity.class, owner.level(), owner, AABB.ofSize(owner.position(), RANGE, RANGE, RANGE));
    }

    @Override
    public void run(LivingEntity owner) {
        List<LivingEntity> targets = this.getTargets(owner);

        for (LivingEntity target : targets) {
            for (int i = 0; i < HelperMethods.RANDOM.nextInt(4, 10); i++) {
                ScissorEntity scissor = new ScissorEntity(owner, this.getOutput(owner), target);
                owner.level().addFreshEntity(scissor);
            }

            if (target instanceof ServerPlayer player) {
                player.sendSystemMessage(Component.translatable(String.format("chat.%s.scissors", JujutsuKaisen.MOD_ID), owner.getName()));
            }
        }
    }

    @Override
    public Status isTriggerable(LivingEntity owner) {
        List<LivingEntity> targets = this.getTargets(owner);

        if (targets.isEmpty()) {
            return Status.FAILURE;
        }
        return super.isTriggerable(owner);
    }

    @Override
    public List<Ability> getRequirements() {
        return List.of(JJKAbilities.SIMPLE_DOMAIN.get());
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 100.0F;
    }

    @Override
    public int getCooldown() {
        return 5 * 20;
    }

    @Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void onLivingAttack(LivingAttackEvent event) {
            DamageSource source = event.getSource();

            if (!(source.getEntity() instanceof LivingEntity attacker)) return;

            LivingEntity victim = event.getEntity();

            if (victim.level().isClientSide) return;

            for (ScissorEntity scissor : victim.level().getEntitiesOfClass(ScissorEntity.class, AABB.ofSize(victim.position(), 16.0D, 16.0D, 16.0D))) {
                if (scissor.isActive()) continue;

                if (scissor.getVictim() == victim && scissor.getOwner() == attacker) {
                    event.setCanceled(true);
                    return;
                }
            }

            for (ScissorEntity scissor : attacker.level().getEntitiesOfClass(ScissorEntity.class, AABB.ofSize(attacker.position(), 16.0D, 16.0D, 16.0D))) {
                if (scissor.isActive()) continue;

                if (scissor.getVictim() == attacker && scissor.getOwner() == victim) {
                    event.setCanceled(true);
                    return;
                }
            }
        }
    }
}
