package radon.jujutsu_kaisen.ability.misc;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.VeilHandler;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.client.particle.CursedEnergyParticle;
import radon.jujutsu_kaisen.client.particle.ParticleColors;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import radon.jujutsu_kaisen.util.HelperMethods;

public class FallingBlossomEmotion extends Ability implements Ability.IToggled {
    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        for (DomainExpansionEntity domain : VeilHandler.getDomains((ServerLevel) owner.level(), owner.blockPosition())) {
            if (!domain.hasSureHitEffect() || !domain.checkSureHitEffect()) continue;
            return true;
        }
        return false;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.TOGGLED;
    }

    @Override
    public void run(LivingEntity owner) {
        if (!(owner.level() instanceof ServerLevel level)) return;

        owner.addEffect(new MobEffectInstance(JJKEffects.STUN.get(), 2, 0, false, false, false));

        for (int i = 0; i < 16; i++) {
            double x = owner.getX() + (HelperMethods.RANDOM.nextDouble() - 0.5D) * (owner.getBbWidth() * 1.25F) - owner.getLookAngle().scale(0.35D).x;
            double y = owner.getY() + HelperMethods.RANDOM.nextDouble() * (owner.getBbHeight());
            double z = owner.getZ() + (HelperMethods.RANDOM.nextDouble() - 0.5D) * (owner.getBbWidth() * 1.25F) - owner.getLookAngle().scale(0.35D).z;
            double speed = (owner.getBbHeight() * 0.1F) * HelperMethods.RANDOM.nextDouble();
            level.sendParticles(new CursedEnergyParticle.CursedEnergyParticleOptions(ParticleColors.FALLING_BLOSSOM_EMOTION, owner.getBbWidth() * 0.5F,
                    0.2F, 16), x, y, z, 0, 0.0D, speed, 0.0D, 1.0D);
        }

        for (Projectile projectile : owner.level().getEntitiesOfClass(Projectile.class, owner.getBoundingBox().inflate(1.0D))) {
            if (!(projectile.getOwner() instanceof LivingEntity living)) continue;
            if (!living.getCapability(SorcererDataHandler.INSTANCE).isPresent()) continue;

            ISorcererData cap = living.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

            DomainExpansionEntity domain = cap.getSummonByClass(DomainExpansionEntity.class);

            if (domain == null || !domain.isAffected(owner)) continue;

            if (projectile.getOwner() == living) {
                projectile.discard();
            }
        }
    }

    @Override
    public boolean isTechnique() {
        return false;
    }

    @Override
    public int getCooldown() {
        return 10 * 20;
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 1.0F;
    }

    @Override
    public MenuType getMenuType(LivingEntity owner) {
        return MenuType.DOMAIN;
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
    public Vec2 getDisplayCoordinates() {
        return new Vec2(3.0F, 5.0F);
    }

    @Override
    public int getPointsCost() {
        return ConfigHolder.SERVER.fallingBlossomEmotionCost.get();
    }

    @Override
    public void onEnabled(LivingEntity owner) {

    }

    @Override
    public void onDisabled(LivingEntity owner) {

    }

    @Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class FallingBlossomEmotionForgeEvents {
        @SubscribeEvent
        public static void onLivingHurt(LivingHurtEvent event) {
            if (!(event.getSource() instanceof JJKDamageSources.JujutsuDamageSource source)) return;

            LivingEntity victim = event.getEntity();

            if (!JJKAbilities.hasToggled(victim, JJKAbilities.FALLING_BLOSSOM_EMOTION.get())) return;

            if (!(source.getDirectEntity() instanceof DomainExpansionEntity)) return;

            event.setAmount(event.getAmount() * 0.5F);
        }
    }
}