package radon.jujutsu_kaisen.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.ability.base.DomainExpansion;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.List;

public abstract class OpenDomainExpansionEntity extends DomainExpansionEntity {
    private static final EntityDataAccessor<Integer> DATA_WIDTH = SynchedEntityData.defineId(OpenDomainExpansionEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_HEIGHT = SynchedEntityData.defineId(OpenDomainExpansionEntity.class, EntityDataSerializers.INT);

    private static final float STRENGTH = 500.0F;

    public OpenDomainExpansionEntity(EntityType<? extends Mob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public OpenDomainExpansionEntity(EntityType<? extends Mob> pEntityType, LivingEntity owner, DomainExpansion ability, int width, int height) {
        super(pEntityType, owner, ability);

        Vec3 pos = owner.position()
                .subtract(HelperMethods.getLookAngle(owner)
                .multiply(this.getBbWidth(), 0.0D, this.getBbWidth()));
        this.moveTo(pos.x(), pos.y(), pos.z(), owner.getXRot(), owner.getYRot());

        this.entityData.set(DATA_WIDTH, width);
        this.entityData.set(DATA_HEIGHT, height);

        owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
            AttributeInstance attribute = this.getAttribute(Attributes.MAX_HEALTH);

            if (attribute != null) {
                attribute.setBaseValue(STRENGTH * cap.getAbilityPower(owner));
                this.setHealth(this.getMaxHealth());
            }
        });
    }

    @Override
    protected boolean isAffected(BlockPos pos) {
        for (DomainExpansionEntity domain : this.getDomains()) {
            if (domain.isInsideBarrier(pos)) return false;
        }
        return super.isAffected(pos);
    }

    protected int getWidth() {
        return this.entityData.get(DATA_WIDTH);
    }

    protected int getHeight() {
        return this.entityData.get(DATA_HEIGHT);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();

        this.entityData.define(DATA_WIDTH, 0);
        this.entityData.define(DATA_HEIGHT, 0);
    }

    @Override
    public void warn() {
        LivingEntity owner = this.getOwner();

        if (owner != null) {
            AABB bounds = this.getBounds();

            for (Entity entity : this.level().getEntities(this, bounds, this::isAffected)) {
                entity.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> cap.onInsideDomain(this));
            }
        }
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);

        pCompound.putInt("width", this.getWidth());
        pCompound.putInt("height", this.getHeight());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);

        this.entityData.set(DATA_WIDTH, pCompound.getInt("width"));
        this.entityData.set(DATA_HEIGHT, pCompound.getInt("height"));
    }

    protected List<DomainExpansionEntity> getDomains() {
        List<DomainExpansionEntity> domains = HelperMethods.getEntityCollisionsOfClass(DomainExpansionEntity.class, this.level(), this.getBounds());
        domains.removeIf(domain -> domain.is(this));
        return domains;
    }

    protected void doSureHitEffect(@NotNull LivingEntity owner) {
        AABB bounds = this.getBounds();

        for (Entity entity : this.level().getEntities(this, bounds, this::isAffected)) {
            if (entity instanceof LivingEntity living) {
                this.ability.onHitEntity(this, owner, living);
            }
        }
    }

    @Override
    public boolean checkSureHitEffect() {
        for (DomainExpansionEntity domain : this.getDomains()) {
            if (domain instanceof ClosedDomainExpansionEntity closed && !closed.isInsideBarrier(this.blockPosition())) continue;

            if (this.shouldCollapse(domain.getStrength())) {
                this.discard();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();

        if (this.checkSureHitEffect()) {
            this.warn();
        }
    }

    @Override
    public void onRemovedFromWorld() {
        if (!this.level().isClientSide) {
            LivingEntity owner = this.getOwner();

            if (owner != null) {
                owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                    cap.setBurnout(DomainExpansion.BURNOUT);

                    if (owner instanceof ServerPlayer player) {
                        PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(cap.serializeNBT()), player);
                    }
                });
            }
        }
        super.onRemovedFromWorld();
    }

    @Override
    public void tick() {
        super.tick();

        LivingEntity owner = this.getOwner();

        if (owner != null) {
            if (!this.level().isClientSide) {
                if (this.checkSureHitEffect()) {
                    this.warn();

                    this.doSureHitEffect(owner);
                }
            }
        }
    }
}
