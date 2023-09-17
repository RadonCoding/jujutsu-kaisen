package radon.jujutsu_kaisen.entity.effect;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.util.HelperMethods;

import javax.annotation.Nullable;
import java.util.UUID;

public class WoodShieldEntity extends WoodSegmentEntity {
    private boolean isDying;
    private int start;
    private WoodShieldEntity prevSegment;
    private Vec3 pos;

    @Nullable
    private UUID ownerUUID;
    @Nullable
    private LivingEntity cachedOwner;

    public WoodShieldEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public WoodShieldEntity(LivingEntity owner) {
        super(JJKEntities.WOOD_SHIELD.get(), owner.level);

        this.setOwner(owner);
        this.pos = owner.position();

        this.setParent(this);

        this.moveTo(owner.getX(), owner.getY(), owner.getZ(), owner.getXRot(), owner.getYRot());
    }

    public WoodShieldEntity(WoodShieldEntity segment, float yawOffset, float pitchOffset) {
        super(segment, yawOffset, pitchOffset);

        this.setOwner(segment.getOwner());
        this.pos = segment.pos;
    }

    public WoodShieldEntity(WoodShieldEntity segment, double offsetX, double offsetY, double offsetZ, float yawOffset, float pitchOffset) {
        super(segment, offsetX, offsetY, offsetZ, yawOffset, pitchOffset);

        this.setOwner(segment.getOwner());
        this.pos = segment.pos;
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
        } else if (this.ownerUUID != null && this.level instanceof ServerLevel) {
            this.cachedOwner = (LivingEntity) ((ServerLevel) this.level).getEntity(this.ownerUUID);
            return this.cachedOwner;
        } else {
            return null;
        }
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);

        pCompound.putDouble("pos_x", this.pos.x());
        pCompound.putDouble("pos_y", this.pos.y());
        pCompound.putDouble("pos_z", this.pos.z());

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

        LivingEntity owner = this.getOwner();

        if (!this.level.isClientSide && (owner == null || owner.isRemoved() || !owner.isAlive())) {
            this.discard();
        } else if (owner != null) {
            if (!this.isDying && !JJKAbilities.hasToggled(owner, JJKAbilities.WOOD_SHIELD.get())) {
                this.isDying = true;
                this.start = this.tickCount;
            }

            int count = 30;

            if ((!this.isDying || this.tickCount - this.start < count - this.getIndex())) {
                if (!this.level.isClientSide && this.getIndex() == 0 && this.tickCount == 1) {
                    for (int i = 0; i < (int) Mth.clamp(owner.getBbWidth() * 5.0F, 6.0F, 22.0F); i++) {
                        Vec3 pos = new Vec3((this.random.nextDouble() - 0.5D) * owner.getBbWidth() * 2.5D, 0.0D, (this.random.nextDouble() - 0.5D) * owner.getBbWidth() * 2.5D);
                        float f = HelperMethods.getYaw(this.pos.subtract(this.position().add(pos)));
                        WoodShieldEntity segment = new WoodShieldEntity(this, pos.x(), pos.y(), pos.z(), f + ((this.random.nextFloat() - 0.5F) * 160.0F), 80.0F);
                        segment.prevSegment = segment;
                        this.level.addFreshEntity(segment);
                    }
                }
                if (!this.level.isClientSide && this.getIndex() == 1 && this.tickCount > 1 && this.tickCount <= count) {
                    float yaw = (this.random.nextFloat() - 0.5F) * 30.0F;
                    int i = this.prevSegment.getIndex();

                    if (i > 1) {
                        yaw = Mth.wrapDegrees(HelperMethods.getYaw(this.pos
                                .subtract(this.prevSegment.position())) - this.prevSegment.getYRot());
                        yaw /= owner.getBbWidth() + Math.max(4.4F - (float) i * 0.075F, 1.0F);
                    }
                    this.prevSegment = new WoodShieldEntity(this.prevSegment, yaw, -0.5F);
                    this.level.addFreshEntity(this.prevSegment);
                }
                if (this.pos != null) {
                    owner.moveTo(this.pos.x(), this.pos.y(), this.pos.z());
                }
            } else if (!this.level.isClientSide) {
                this.discard();
            }
        }
    }
}
