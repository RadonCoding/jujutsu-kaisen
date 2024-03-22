package radon.jujutsu_kaisen.entity.projectile;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.ExplosionHandler;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.HollowPurpleExplosion;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.projectile.base.JujutsuProjectile;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncAbilityDataS2CPacket;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;
import radon.jujutsu_kaisen.sound.JJKSounds;
import radon.jujutsu_kaisen.util.EntityUtil;
import radon.jujutsu_kaisen.util.RotationUtil;

public class RedProjectile extends JujutsuProjectile {
    private static final double LAUNCH_POWER = 10.0D;
    private static final float EXPLOSIVE_POWER = 5.0F;
    private static final float MAX_EXPLOSION = 15.0F;
    public static final int DELAY = 20;
    private static final int DURATION = 3 * 20;
    private static final float SPEED = 5.0F;
    private static final float DAMAGE = 20.0F;

    private boolean chanted;

    public RedProjectile(EntityType<? extends Projectile> pType, Level level) {
        super(pType, level);
    }

    public RedProjectile(LivingEntity owner, float power, boolean chanted) {
        super(JJKEntities.RED.get(), owner.level(), owner, power);

        this.chanted = chanted;

        Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);
        EntityUtil.offset(this, look, new Vec3(owner.getX(), owner.getEyeY() - (this.getBbHeight() / 2.0F), owner.getZ()).add(look));
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);

        pCompound.putBoolean("chanted", this.chanted);
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);

        this.chanted = pCompound.getBoolean("chanted");
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult pResult) {
        super.onHitEntity(pResult);

        if (this.level().isClientSide) return;

        Entity entity = pResult.getEntity();

        if (!(this.getOwner() instanceof LivingEntity owner)) return;

        if (entity == owner) return;

        float factor = 1.0F - (((float) this.getTime() - DELAY) / DURATION);

        if (entity.hurt(JJKDamageSources.indirectJujutsuAttack(this, owner, JJKAbilities.RED.get()), DAMAGE * factor * this.getPower())) {
            entity.setDeltaMovement(RotationUtil.getTargetAdjustedLookAngle(this).multiply(1.0D, 0.25D, 1.0D).scale(LAUNCH_POWER));
            entity.hurtMarked = true;
        }
    }

    @Override
    protected void onHit(@NotNull HitResult pResult) {
        super.onHit(pResult);

        if (this.level().isClientSide) return;

        this.playSound(JJKSounds.RED_EXPLOSION.get(), 3.0F, 1.0F);

        if (this.getOwner() instanceof LivingEntity owner) {
            Vec3 offset = new Vec3(this.getX(), this.getY() + (this.getBbHeight() / 2.0F), this.getZ());
            ExplosionHandler.spawn(this.level().dimension(), offset, Math.min(MAX_EXPLOSION, EXPLOSIVE_POWER * this.getPower()), 2 * 20, this.getPower() * 0.1F, owner,
                    JJKDamageSources.indirectJujutsuAttack(this, owner, JJKAbilities.RED.get()), false);
        }
        this.discard();
    }

    @Override
    public void tick() {
        super.tick();

        if (this.getOwner() instanceof LivingEntity owner) {
            if (this.getTime() < DELAY) {
                if (!owner.isAlive()) {
                    this.discard();
                } else {
                    if (this.getTime() % 5 == 0) {
                        owner.swing(InteractionHand.MAIN_HAND);
                    }
                    Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);
                    EntityUtil.offset(this, look, new Vec3(owner.getX(), owner.getEyeY() - (this.getBbHeight() / 2.0F), owner.getZ()).add(look));
                }
            } else if (this.getTime() >= DURATION) {
                this.discard();
            } else if (this.getTime() >= DELAY) {
                if (!this.level().isClientSide) {
                    if (this.chanted) {
                        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

                        if (cap == null) return;

                        IAbilityData data = cap.getAbilityData();

                        for (BlueProjectile blue : this.level().getEntitiesOfClass(BlueProjectile.class, this.getBoundingBox().expandTowards(this.getDeltaMovement()))) {
                            if (!(owner instanceof Player player) || !player.getAbilities().instabuild) {
                                if (JJKAbilities.HOLLOW_PURPLE.get().getStatus(owner) != Ability.Status.SUCCESS) {
                                    continue;
                                }
                                data.addCooldown(JJKAbilities.HOLLOW_PURPLE.get());
                            }

                            if (owner instanceof ServerPlayer player) {
                                PacketHandler.sendToClient(new SyncAbilityDataS2CPacket(data.serializeNBT()), player);
                            }
                            HollowPurpleExplosion explosion = new HollowPurpleExplosion(owner, this.getPower(), blue.position().add(0.0D, blue.getBbHeight() / 2.0F, 0.0D));
                            this.level().addFreshEntity(explosion);

                            blue.discard();
                            this.discard();

                            break;
                        }
                    }
                }

                if (this.getTime() == DELAY) {
                    this.setDeltaMovement(RotationUtil.getTargetAdjustedLookAngle(owner).scale(SPEED));
                }
            }
        }
    }
}
