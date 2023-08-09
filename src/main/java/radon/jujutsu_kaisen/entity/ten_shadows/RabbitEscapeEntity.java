package radon.jujutsu_kaisen.entity.ten_shadows;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.entity.JJKEntities;

public class RabbitEscapeEntity extends Rabbit {
    private static final int DURATION = 3 * 20;

    public RabbitEscapeEntity(EntityType<? extends Rabbit> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public RabbitEscapeEntity(LivingEntity owner) {
        super(JJKEntities.RABBIT_ESCAPE.get(), owner.level);

        Vec3 pos = owner.position()
                .subtract(owner.getLookAngle()
                        .multiply(this.getBbWidth(), 0.0D, this.getBbWidth()));
        this.setPos(pos.x(), pos.y(), pos.z());

        Vec3 look = owner.getLookAngle();

        this.setDeltaMovement(look.x() * (this.random.nextDouble() * 2.0D + 1.0D) + (this.random.nextDouble() * (this.random.nextBoolean() ? 1 : -1) * 0.5D),
                look.y() * (this.random.nextDouble() * 4.0D + 2.0D) + (this.random.nextDouble() * (this.random.nextBoolean() ? 1 : -1) * 0.5D),
                look.z() * (this.random.nextDouble() * 2.0D + 1.0D) + (this.random.nextDouble() * (this.random.nextBoolean() ? 1 : -1) * 0.5D));

        this.yHeadRot = this.getYRot();
        this.yHeadRotO = this.yHeadRot;
    }

    @Override
    protected void doPush(@NotNull Entity p_20971_) {

    }

    @Override
    public void tick() {
        super.tick();

        if (this.tickCount >= DURATION) {
            this.discard();
        } else {
            Vec3 movement = this.getDeltaMovement();
            double dx = movement.x();
            double dy = movement.y();
            double dz = movement.z();

            double pitch = -Math.asin(dy);
            double yaw = -Math.atan2(dx, dz);

            this.setRot((float) Math.toDegrees(yaw), (float) Math.toDegrees(pitch));
        }
    }

    @Override
    public @NotNull Variant getVariant() {
        return Variant.WHITE;
    }

    public static @NotNull AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 3.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D);
    }
}
