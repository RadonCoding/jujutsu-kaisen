package radon.jujutsu_kaisen.ability.misc;

import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.AbilityDisplayInfo;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.List;

public class DivergentFist extends Ability {
    private static final float EXPLOSIVE_POWER = 1.0F;
    private static final double LAUNCH_POWER = 2.5D;
    private static final double RANGE = 3.0D;

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return target != null;
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
    public boolean isChantable() {
        return false;
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

            owner.swing(InteractionHand.MAIN_HAND, true);

            if (owner instanceof Player player) {
                player.attack(target);
            } else {
                owner.doHurtTarget(target);
            }
            target.invulnerableTime = 0;

            cap.delayTickEvent(() -> {
                owner.swing(InteractionHand.MAIN_HAND, true);

                if (owner instanceof Player player) {
                    player.attack(target);
                } else {
                    owner.doHurtTarget(target);
                }
                target.invulnerableTime = 0;

                float radius = EXPLOSIVE_POWER * this.getPower(owner);

                Vec3 explosionPos = target.position().add(0.0D, target.getBbHeight() / 2.0F, 0.0D);

                float f2 = radius * 2.0F;
                int k1 = Mth.floor(explosionPos.x() - (double) f2 - 1.0D);
                int l1 = Mth.floor(explosionPos.x() + (double) f2 + 1.0D);
                int i2 = Mth.floor(explosionPos.y() - (double) f2 - 1.0D);
                int i1 = Mth.floor(explosionPos.y() + (double) f2 + 1.0D);
                int j2 = Mth.floor(explosionPos.z() - (double) f2 - 1.0D);
                int j1 = Mth.floor(explosionPos.z() + (double) f2 + 1.0D);
                List<Entity> entities = owner.level().getEntities(owner, new AABB(k1, i2, j2, l1, i1, j1));

                for (Entity entity : entities) {
                    if (!entity.ignoreExplosion()) {
                        double d12 = Math.sqrt(entity.distanceToSqr(explosionPos)) / (double) f2;

                        if (d12 <= 1.0D) {
                            double d5 = entity.getX() - explosionPos.x();
                            double d7 = (entity instanceof PrimedTnt ? entity.getY() : entity.getEyeY()) - explosionPos.y();
                            double d9 = entity.getZ() - explosionPos.z();
                            double d13 = Math.sqrt(d5 * d5 + d7 * d7 + d9 * d9);

                            if (d13 != 0.0D) {
                                d5 /= d13;
                                d7 /= d13;
                                d9 /= d13;
                                double d14 = Explosion.getSeenPercent(explosionPos, entity);
                                double d10 = (1.0D - d12) * d14;
                                double d11;

                                if (entity instanceof LivingEntity living) {
                                    d11 = ProtectionEnchantment.getExplosionKnockbackAfterDampener(living, d10);
                                } else {
                                    d11 = d10;
                                }

                                d5 *= d11;
                                d7 *= d11;
                                d9 *= d11;
                                Vec3 vec31 = new Vec3(d5, d7, d9);
                                entity.setDeltaMovement(entity.getDeltaMovement().add(vec31.scale(LAUNCH_POWER)));
                                entity.hurtMarked = true;
                            }
                        }
                    }
                }
                owner.level().explode(owner, JJKDamageSources.indirectJujutsuAttack(owner, owner, this), null, explosionPos, radius, false,
                        Level.ExplosionInteraction.NONE);
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
