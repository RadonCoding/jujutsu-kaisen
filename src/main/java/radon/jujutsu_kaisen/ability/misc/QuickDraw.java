package radon.jujutsu_kaisen.ability.misc;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.projectile.*;
import net.minecraft.world.phys.Vec2;
import net.neoforged.neoforge.event.entity.living.LivingAttackEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.entity.SimpleDomainEntity;

public class QuickDraw extends Ability implements Ability.IToggled {
    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return false;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.TOGGLED;
    }

    private static void attack(LivingEntity owner, Entity entity) {
        if (entity instanceof AbstractArrow || entity instanceof ThrowableItemProjectile) {
            owner.lookAt(EntityAnchorArgument.Anchor.EYES, entity.position().add(0.0D, entity.getBbHeight() / 2.0F, 0.0D));
            owner.swing(InteractionHand.MAIN_HAND, true);
            entity.discard();
        } else if (entity instanceof LivingEntity) {
            if (entity.invulnerableTime > 0) return;

            owner.lookAt(EntityAnchorArgument.Anchor.EYES, entity.position().add(0.0D, entity.getBbHeight() / 2.0F, 0.0D));
        }
    }

    @Override
    public void run(LivingEntity owner) {
        if (owner.level().isClientSide) return;

        owner.addEffect(new MobEffectInstance(JJKEffects.STUN.get(), 2, 0, false, false, false));

        ISorcererData data = owner.getData(JJKAttachmentTypes.SORCERER);
        

        if (data.hasToggled(JJKAbilities.SIMPLE_DOMAIN.get())) {
            SimpleDomainEntity domain = data.getSummonByClass(SimpleDomainEntity.class);

            if (domain == null) return;

            for (Entity entity : owner.level().getEntities(owner, domain.getBoundingBox())) {
                if (entity == domain || entity.distanceTo(domain) > domain.getRadius()) continue;

                attack(owner, entity);
            }
        }
    }

    @Override
    public void onEnabled(LivingEntity owner) {

    }

    @Override
    public void onDisabled(LivingEntity owner) {

    }

    @Override
    public int getCooldown() {
        return 5 * 20;
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 0;
    }

    @Override
    public boolean isValid(LivingEntity owner) {
        ISorcererData data = owner.getData(JJKAttachmentTypes.SORCERER);

        if (data == null) return false;

        return (data.hasToggled(JJKAbilities.SIMPLE_DOMAIN.get()) || data.hasToggled(JJKAbilities.FALLING_BLOSSOM_EMOTION.get())) && super.isValid(owner);
    }

    @Nullable
    @Override
    public Ability getParent(LivingEntity owner) {
        return JJKAbilities.SIMPLE_DOMAIN.get();
    }

    @Override
    public int getPointsCost() {
        return ConfigHolder.SERVER.quickDrawCost.get();
    }

    @Override
    public Vec2 getDisplayCoordinates() {
        return new Vec2(4.0F, 4.0F);
    }

    @Override
    public boolean isTechnique() {
        return false;
    }

    @Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void onLivingAttack(LivingAttackEvent event) {
            LivingEntity victim = event.getEntity();

            ISorcererData data = victim.getData(JJKAttachmentTypes.SORCERER);

            if (victim.level().isClientSide || !data.hasToggled(JJKAbilities.QUICK_DRAW.get()) ||
                    !data.hasToggled(JJKAbilities.FALLING_BLOSSOM_EMOTION.get())) return;

            Entity attacker = event.getSource().getDirectEntity();

            QuickDraw.attack(victim, attacker);
        }
    }
}
