package radon.jujutsu_kaisen.ability.misc;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.neoforged.neoforge.event.entity.living.LivingAttackEvent;
import net.neoforged.neoforge.event.entity.living.LivingHurtEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.VeilHandler;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.Summon;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.SimpleDomainEntity;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.List;

public class SimpleDomain extends Summon<SimpleDomainEntity> {
    public SimpleDomain() {
        super(SimpleDomainEntity.class);
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        for (DomainExpansionEntity domain : VeilHandler.getDomains((ServerLevel) owner.level(), owner.blockPosition())) {
            if (domain.getOwner() == owner || !domain.hasSureHitEffect()) continue;
            return true;
        }

        if (target == null || target.isDeadOrDying() || !owner.hasLineOfSight(target)) return false;

        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return false;

        ISorcererData data = cap.getSorcererData();

        return data.isUnlocked(JJKAbilities.QUICK_DRAW.get()) && owner.distanceTo(target) <= SimpleDomainEntity.getRadius(owner);
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
    public boolean display() {
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

    @Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void onLivingHurt(LivingHurtEvent event) {
            if (!(event.getSource() instanceof JJKDamageSources.JujutsuDamageSource)) return;

            LivingEntity victim = event.getEntity();

            for (SimpleDomainEntity simple : victim.level().getEntitiesOfClass(SimpleDomainEntity.class, AABB.ofSize(victim.position(), 8.0D, 8.0D, 8.0D))) {
                if (victim.distanceTo(simple) < simple.getRadius()) {
                    event.setAmount(event.getAmount() * 0.5F);
                    simple.hurt(event.getSource(), event.getAmount());
                }
            }
        }

        @SubscribeEvent
        public static void onLivingAttack(LivingAttackEvent event) {
            if (!(event.getSource().getDirectEntity() instanceof DomainExpansionEntity)) return;

            LivingEntity victim = event.getEntity();

            if (victim.level().isClientSide) return;

            IJujutsuCapability cap = victim.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap == null) return;

            ISorcererData sorcererData = cap.getSorcererData();
            IAbilityData abilityData = cap.getAbilityData();

            if (!abilityData.hasToggled(JJKAbilities.SIMPLE_DOMAIN.get())) return;

            SimpleDomainEntity domain = sorcererData.getSummonByClass(SimpleDomainEntity.class);

            if (domain != null) {
                domain.hurt(event.getSource(), event.getAmount());
                event.setCanceled(true);
            }
        }
    }
}