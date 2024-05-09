package radon.jujutsu_kaisen.entity.effect;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.entity.registry.JJKEntities;
import radon.jujutsu_kaisen.util.RotationUtil;

import javax.annotation.Nullable;
import java.util.UUID;

public class WoodShieldSegmentEntity extends WoodSegmentEntity {
    private static final int COUNT = 30;

    private boolean isDying;
    private int start;
    private WoodShieldSegmentEntity prevSegment;

    @Nullable
    private Vec3 pos;

    @Nullable
    private UUID ownerUUID;
    @Nullable
    private LivingEntity cachedOwner;

    public WoodShieldSegmentEntity(EntityType<?> pType, Level pLevel) {
        super(pType, pLevel);
    }

    public WoodShieldSegmentEntity(LivingEntity owner) {
        super(JJKEntities.WOOD_SHIELD_SEGMENT.get(), owner.level());

        this.setOwner(owner);
        this.pos = owner.position();

        this.setParent(this);

        this.moveTo(owner.getX(), owner.getY(), owner.getZ(), owner.getXRot(), owner.getYRot());
    }

    public WoodShieldSegmentEntity(WoodShieldSegmentEntity segment, float yawOffset, float pitchOffset) {
        super(segment, yawOffset, pitchOffset);

        this.setOwner(segment.getOwner());
        this.pos = segment.position();
    }

    public WoodShieldSegmentEntity(WoodShieldSegmentEntity segment, double offsetX, double offsetY, double offsetZ, float yawOffset, float pitchOffset) {
        super(segment, offsetX, offsetY, offsetZ, yawOffset, pitchOffset);

        this.setOwner(segment.getOwner());
        this.pos = segment.position();
    }

    public void setOwner(@Nullable LivingEntity pOwner) {
        if (pOwner != null) {
            this.ownerUUID = pOwner.getUUID();
            this.cachedOwner = pOwner;
        }
    }

    @Nullable
    public LivingEntity getOwner() {
        if (this.cachedOwner != null && !this.cachedOwner.isRemoved()) {
            return this.cachedOwner;
        } else if (this.ownerUUID != null && this.level() instanceof ServerLevel) {
            this.cachedOwner = (LivingEntity) ((ServerLevel) this.level()).getEntity(this.ownerUUID);
            return this.cachedOwner;
        } else {
            return null;
        }
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);

        if (this.pos != null) {
            pCompound.putDouble("pos_x", this.pos.x);
            pCompound.putDouble("pos_y", this.pos.y);
            pCompound.putDouble("pos_z", this.pos.z);
        }
        if (this.ownerUUID != null) {
            pCompound.putUUID("owner", this.ownerUUID);
        }
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);

        this.pos = new Vec3(pCompound.getDouble("pos_x"), pCompound.getDouble("pos_y"), pCompound.getDouble("pos_z"));

        if (pCompound.hasUUID("owner")) {
            this.ownerUUID = pCompound.getUUID("owner");
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide && this.isDying) {
            if (this.getTime() - this.start >= COUNT - this.getIndex()) {
                this.discard();
            }
            return;
        }

        LivingEntity owner = this.getOwner();

        if (!this.level().isClientSide && !this.isDying && (owner == null || owner.isRemoved() || !owner.isAlive())) {
            if (owner == null || owner.isRemoved()) {
                this.isDying = true;
                this.start = this.getTime();
            } else if (!owner.isAlive()) {
                this.discard();
            }
        } else if (owner != null) {
            if (this.pos != null) {
                if (!this.level().isClientSide && this.getIndex() == 0 && this.getTime() - 1 == 0) {
                    for (int i = 0; i < (int) Mth.clamp(owner.getBbWidth() * 5.0F, 6.0F, 22.0F); i++) {
                        Vec3 pos = new Vec3((this.random.nextDouble() - 0.5D) * owner.getBbWidth() * 2.5D, 0.0D, (this.random.nextDouble() - 0.5D) * owner.getBbWidth() * 2.5D);
                        float f = RotationUtil.getYaw(this.pos.subtract(this.position().add(pos)));
                        WoodShieldSegmentEntity segment = new WoodShieldSegmentEntity(this, pos.x, pos.y, pos.z, f + ((this.random.nextFloat() - 0.5F) * 160.0F), 80.0F);
                        segment.prevSegment = segment;
                        this.level().addFreshEntity(segment);
                    }
                }
                if (!this.level().isClientSide && this.getIndex() == 1 && this.getTime() > 0 && this.getTime() <= COUNT) {
                    float yaw = (this.random.nextFloat() - 0.5F) * 30.0F;
                    int i = this.prevSegment.getIndex();

                    if (i > 1) {
                        yaw = Mth.wrapDegrees(RotationUtil.getYaw(this.pos
                                .subtract(this.prevSegment.position())) - this.prevSegment.getYRot());
                        yaw /= owner.getBbWidth() + Math.max(4.4F - (float) i * 0.075F, 1.0F);
                    }
                    this.prevSegment = new WoodShieldSegmentEntity(this.prevSegment, yaw, -0.5F);
                    this.level().addFreshEntity(this.prevSegment);
                }
                owner.teleportTo(this.pos.x, this.pos.y, this.pos.z);
            }
        }
    }
}
