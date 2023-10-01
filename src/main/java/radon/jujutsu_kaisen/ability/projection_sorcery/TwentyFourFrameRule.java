package radon.jujutsu_kaisen.ability.projection_sorcery;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.DisplayType;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.effect.ProjectionFrameEntity;
import radon.jujutsu_kaisen.util.HelperMethods;

public class TwentyFourFrameRule extends Ability {
    private static final double RANGE = 3.0D;
    private static final float DAMAGE = 15.0F;

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return false;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    private @Nullable LivingEntity getTarget(LivingEntity owner) {
        if (HelperMethods.getLookAtHit(owner, RANGE) instanceof EntityHitResult hit) {
            return hit.getEntity() instanceof LivingEntity target ? target : null;
        }
        return null;
    }

    @Override
    public void run(LivingEntity owner) {
        owner.swing(InteractionHand.MAIN_HAND);

        LivingEntity target = this.getTarget(owner);

        if (target == null) return;

        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        cap.addSpeedStack();

        if (JJKAbilities.hasToggled(target, JJKAbilities.INFINITY.get())) return;

        owner.level().addFreshEntity(new ProjectionFrameEntity(owner, target));
    }

    @Override
    public Status checkTriggerable(LivingEntity owner) {
        LivingEntity target = this.getTarget(owner);

        if (target == null) {
            return Status.FAILURE;
        }
        return super.checkTriggerable(owner);
    }

    @Override
    public int getCooldown() {
        return 10 * 20;
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 100.0F;
    }

    @Override
    public DisplayType getDisplayType() {
        return DisplayType.SCROLL;
    }

    @Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void onLivingHurt(LivingHurtEvent event) {
            DamageSource source = event.getSource();
            if (!(source.getEntity() instanceof LivingEntity attacker)) return;

            LivingEntity victim = event.getEntity();

            for (ProjectionFrameEntity frame : victim.level().getEntitiesOfClass(ProjectionFrameEntity.class, AABB.ofSize(victim.position(),
                    8.0D, 8.0D, 8.0D))) {
                if (frame.getVictim() != victim) continue;

                Vec3 center = new Vec3(frame.getX(), frame.getY(), frame.getZ());
                frame.level().addParticle(ParticleTypes.EXPLOSION, center.x(), center.y(), center.z(), 1.0D, 0.0D, 0.0D);
                frame.level().addParticle(ParticleTypes.EXPLOSION_EMITTER, center.x(), center.y(), center.z(), 1.0D, 0.0D, 0.0D);

                frame.level().playSound(null, frame.getX(), frame.getY(), frame.getZ(), SoundEvents.GLASS_BREAK, SoundSource.MASTER, 1.0F, 1.0F);
                frame.discard();

                LivingEntity owner = frame.getOwner();

                if (owner != null) {
                    ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
                    victim.hurt(JJKDamageSources.indirectJujutsuAttack(frame, attacker, JJKAbilities.PROJECTION_SORCERY.get()), DAMAGE * cap.getGrade().getRealPower(owner));
                }
            }
        }
    }
}
