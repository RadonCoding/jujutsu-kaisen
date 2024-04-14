package radon.jujutsu_kaisen.entity.base;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.PartEntity;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public abstract class JJKPartEntity<T extends Entity> extends PartEntity<T> {
    private EntityDimensions size;

    protected int newPosRotationIncrements;
    protected double interpTargetX;
    protected double interpTargetY;
    protected double interpTargetZ;
    protected double interpTargetYaw;
    protected double interpTargetPitch;
    public float renderYawOffset;
    public float prevRenderYawOffset;

    public JJKPartEntity(T parent) {
        super(parent);

        this.setPos(parent.getX(), parent.getY(), parent.getZ());
    }

    @Override
    public boolean shouldRender(double pX, double pY, double pZ) {
        return this.getParent().shouldRender(pX, pY, pZ) && super.shouldRender(pX, pY, pZ);
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Nullable
    public ItemStack getPickResult() {
        return this.getParent().getPickResult();
    }

    @Override
    public boolean hurt(@NotNull DamageSource pSource, float pAmount) {
        return !this.isInvulnerableTo(pSource) && this.getParent().hurt(pSource, pAmount);
    }

    @Override
    public boolean isInvulnerableTo(DamageSource pSource) {
        return pSource.getEntity() == this.getParent() || super.isInvulnerableTo(pSource);
    }

    @Override
    public @NotNull EntityDimensions getDimensions(@NotNull Pose pPose) {
        return this.size;
    }

    public boolean shouldBeSaved() {
        return false;
    }

    protected void setSize(EntityDimensions size) {
        this.size = size;
        this.refreshDimensions();
    }

    @Override
    public void recreateFromPacket(@NotNull ClientboundAddEntityPacket packet) {
        super.recreateFromPacket(packet);

        JJKPartEntity.assignPartIDs(this);
    }

    public final void updateLastPos() {
        this.moveTo(this.getX(), this.getY(), this.getZ());
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();
        this.tickCount++;
    }

    @Override
    public void tick() {
        this.updateLastPos();

        super.tick();

        if (this.newPosRotationIncrements > 0) {
            double d0 = this.getX() + (this.interpTargetX - this.getX()) / (double) this.newPosRotationIncrements;
            double d2 = this.getY() + (this.interpTargetY - this.getY()) / (double) this.newPosRotationIncrements;
            double d4 = this.getZ() + (this.interpTargetZ - this.getZ()) / (double) this.newPosRotationIncrements;
            double d6 = Mth.wrapDegrees(this.interpTargetYaw - (double) this.getYRot());
            this.setYRot((float) ((double) this.getYRot() + d6 / (double) this.newPosRotationIncrements));
            this.setXRot((float) ((double) this.getXRot() + (this.interpTargetPitch - (double) this.getXRot()) / (double) this.newPosRotationIncrements));
            --this.newPosRotationIncrements;
            this.setPos(d0, d2, d4);
            this.setRot(this.getYRot(), this.getXRot());
        }
        while (getYRot() - this.yRotO < -180F) this.yRotO -= 360F;
        while (getYRot() - this.yRotO >= 180F) this.yRotO += 360F;

        while (this.renderYawOffset - this.prevRenderYawOffset < -180F) this.prevRenderYawOffset -= 360F;
        while (this.renderYawOffset - this.prevRenderYawOffset >= 180F) this.prevRenderYawOffset += 360F;

        while (getXRot() - this.xRotO < -180F) this.xRotO -= 360F;
        while (getXRot() - this.xRotO >= 180F) this.xRotO += 360F;
    }

    public abstract ResourceLocation getRenderer();

    @Override
    public void setId(int pId) {
        super.setId(pId + 1);
    }

    @Override
    public boolean isCurrentlyGlowing() {
        return this.getParent().isCurrentlyGlowing();
    }

    @Override
    public boolean isInvisible() {
        return this.getParent().isInvisible();
    }

    public static void assignPartIDs(Entity parent) {
        PartEntity<?>[] parts = parent.getParts();

        if (parts == null) return;

        for (int i = 0, length = parts.length; i < length; i++) {
            PartEntity<?> part = parts[i];
            part.setId(parent.getId() + i);
        }
    }

    @Override
    public void setRot(float pYRot, float pXRot) {
        super.setRot(pYRot, pXRot);
    }

    @Override
    protected void defineSynchedData() {

    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag pCompound) {

    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag pCompound) {

    }
}
