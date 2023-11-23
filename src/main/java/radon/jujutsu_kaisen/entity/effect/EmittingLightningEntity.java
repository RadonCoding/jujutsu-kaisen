package radon.jujutsu_kaisen.entity.effect;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.util.HelperMethods;

public class EmittingLightningEntity extends LightningEntity {
    public EmittingLightningEntity(EntityType<? extends Projectile> pType, Level pLevel) {
        super(pType, pLevel);
    }

    public EmittingLightningEntity(LivingEntity owner, float power) {
        super(JJKEntities.EMITTING_LIGHTNING.get(), owner, power);
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();

        if (this.getOwner() instanceof LivingEntity owner) {
            float yaw = this.isEmitting() ? (HelperMethods.RANDOM.nextFloat() - 0.5F) * 360.0F : owner.getYRot();
            float pitch = this.isEmitting() ? (HelperMethods.RANDOM.nextFloat() - 0.5F) * 360.0F : owner.getXRot();

            this.setYaw((float) ((yaw + 90.0F) * Math.PI / 180.0D));
            this.setPitch((float) (-pitch * Math.PI / 180.0D));

            this.setPos(owner.position().add(0.0D, owner.getBbHeight() / 2.0F, 0.0D));
        }
        this.calculateEndPos();
        this.checkCollisions(new Vec3(this.getX(), this.getY(), this.getZ()), new Vec3(this.endPosX, this.endPosY, this.endPosZ));
    }

    @Override
    protected boolean isEmitting() {
        return true;
    }

    @Override
    protected float getDamage() {
        return 10.0F;
    }

    @Override
    public void tick() {
        super.tick();

        Entity owner = this.getOwner();

        if (owner == null) return;

        this.setPos(owner.getX(), owner.getY() + (owner.getBbHeight() / 2.0F), owner.getZ());
    }
}
