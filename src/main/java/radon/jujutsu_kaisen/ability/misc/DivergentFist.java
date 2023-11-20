package radon.jujutsu_kaisen.ability.misc;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.AbilityDisplayInfo;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.client.particle.CursedEnergyParticle;
import radon.jujutsu_kaisen.client.particle.ParticleColors;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.util.HelperMethods;

public class DivergentFist extends Ability {
    private static final float DAMAGE = 10.0F;
    private static final double RANGE = 3.0D;
    private static final double LAUNCH_POWER = 5.0D;

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return target != null;
    }

    @Override
    public boolean isTechnique() {
        return false;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    private @Nullable Entity getTarget(LivingEntity owner) {
        if (HelperMethods.getLookAtHit(owner, RANGE) instanceof EntityHitResult hit) {
            return hit.getEntity();
        }
        return null;
    }

    @Override
    public Vec2 getDisplayCoordinates() {
        return new Vec2(0.0F, 1.0F);
    }

    @Override
    public AbilityDisplayInfo getDisplay(LivingEntity owner) {
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        Vec2 coordinates = this.getDisplayCoordinates();
        return new AbilityDisplayInfo(String.format("%s_%s", JJKAbilities.getKey(this).getPath(), cap.getType().name().toLowerCase()), coordinates.x, coordinates.y);
    }

    @Override
    public boolean isScalable() {
        return true;
    }

    @Override
    public int getPointsCost() {
        return ConfigHolder.SERVER.divergentFistCost.get();
    }

    @Override
    public void run(LivingEntity owner) {
        if (owner.level().isClientSide) return;

        Entity target = this.getTarget(owner);

        if (target != null) {
            ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

            owner.swing(InteractionHand.MAIN_HAND);

            if (owner instanceof Player player) {
                player.attack(target);
            } else {
                owner.doHurtTarget(target);
            }
            target.invulnerableTime = 0;

            Vec3 look = owner.getLookAngle();

            float power = this.getPower(owner);

            cap.delayTickEvent(() -> {
                owner.swing(InteractionHand.MAIN_HAND, true);

                if (owner instanceof Player player) {
                    player.attack(target);
                } else {
                    owner.doHurtTarget(target);
                }
                target.invulnerableTime = 0;

                Vec3 pos = target.position().add(0.0D, target.getBbHeight() / 2.0F, 0.0D);
                ((ServerLevel) target.level()).sendParticles(ParticleTypes.EXPLOSION, pos.x(), pos.y(), pos.z(), 0, 1.0D, 0.0D, 0.0D, 1.0D);
                target.level().playSound(null, pos.x(), pos.y(), pos.z(), SoundEvents.GENERIC_EXPLODE, SoundSource.MASTER, 1.0F, 1.0F);

                target.setDeltaMovement(look.scale(LAUNCH_POWER));
                target.hurtMarked = true;

                for (int i = 0; i < 96; i++) {
                    double theta = HelperMethods.RANDOM.nextDouble() * 2 * Math.PI;
                    double phi = HelperMethods.RANDOM.nextDouble() * Math.PI;
                    double r = HelperMethods.RANDOM.nextDouble() * 0.8D;
                    double x = r * Math.sin(phi) * Math.cos(theta);
                    double y = r * Math.sin(phi) * Math.sin(theta);
                    double z = r * Math.cos(phi);
                    Vec3 speed = look.add(x, y, z);
                    Vec3 offset = pos.add(look);
                    ((ServerLevel) target.level()).sendParticles(new CursedEnergyParticle.CursedEnergyParticleOptions(ParticleColors.getCursedEnergyColor(owner), owner.getBbWidth(),
                            0.2F, 8), offset.x(), offset.y(), offset.z(), 0, speed.x(), speed.y(), speed.z(), 1.0D);
                }
                target.hurt(JJKDamageSources.jujutsuAttack(owner, this), DAMAGE * power);
            }, 5);
        }
    }

    @Override
    public Status checkTriggerable(LivingEntity owner) {
        Entity target = this.getTarget(owner);

        if (target == null) {
            return Status.FAILURE;
        }
        return super.checkTriggerable(owner);
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 50.0F;
    }

    @Override
    public int getCooldown() {
        return 10 * 20;
    }

    @Override
    public MenuType getMenuType() {
        return MenuType.SCROLL;
    }
}
