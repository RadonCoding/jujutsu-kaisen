package radon.jujutsu_kaisen.ability.misc;

import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.event.entity.living.LivingHurtEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.VeilHandler;
import radon.jujutsu_kaisen.ability.registry.JJKAbilities;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.Summon;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.registry.JJKEntities;
import radon.jujutsu_kaisen.entity.SimpleDomainEntity;
import radon.jujutsu_kaisen.entity.IBarrier;

import java.util.List;

public class SimpleDomain extends Summon<SimpleDomainEntity> {
    public SimpleDomain() {
        super(SimpleDomainEntity.class);
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        for (IBarrier barrier : VeilHandler.getBarriers((ServerLevel) owner.level(), owner.blockPosition())) {
            if (barrier.getOwner() == owner || !barrier.hasSureHitEffect()) continue;
            return true;
        }

        if (target == null || target.isDeadOrDying()) return false;
        if (!owner.hasLineOfSight(target)) return false;

        return JJKAbilities.QUICK_DRAW.get().isUnlocked(owner) && owner.distanceTo(target) <= SimpleDomainEntity.getRadius(owner);
    }

    @Override
    public boolean isTechnique() {
        return false;
    }

    @Override
    public List<EntityType<?>> getTypes() {
        return List.of(JJKEntities.SIMPLE_DOMAIN.get());
    }

    @Override
    public boolean isTenShadows() {
        return false;
    }

    @Override
    protected SimpleDomainEntity summon(LivingEntity owner) {
        return new SimpleDomainEntity(owner);
    }

    @Override
    public int getCooldown() {
        return 10 * 20;
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 0.1F;
    }

    @Override
    public MenuType getMenuType(LivingEntity owner) {
        return MenuType.DOMAIN;
    }

    @Override
    public boolean isDisplayed() {
        return false;
    }

    @Override
    public boolean isScalable(LivingEntity owner) {
        return false;
    }

    @Nullable
    @Override
    public Ability getParent(LivingEntity owner) {
        return JJKAbilities.CURSED_ENERGY_FLOW.get();
    }

    @Override
    public int getPointsCost() {
        return ConfigHolder.SERVER.simpleDomainCost.get();
    }

    @EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void onLivingHurt(LivingHurtEvent event) {
            if (!(event.getSource() instanceof JJKDamageSources.JujutsuDamageSource)) return;

            LivingEntity victim = event.getEntity();

            for (SimpleDomainEntity simple : victim.level().getEntitiesOfClass(SimpleDomainEntity.class, AABB.ofSize(victim.position(), 8.0D, 8.0D, 8.0D))) {
                if (victim.distanceTo(simple) < simple.getRadius()) {
                    float amount = event.getAmount();
                    float blocked = Math.min(simple.getHealth(), event.getAmount());
                    event.setAmount(amount - blocked);
                    simple.hurt(event.getSource(), blocked);
                }
            }
        }
    }
}