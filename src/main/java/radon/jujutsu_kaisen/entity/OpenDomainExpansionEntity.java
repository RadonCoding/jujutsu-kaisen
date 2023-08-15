package radon.jujutsu_kaisen.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
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

public class OpenDomainExpansionEntity extends DomainExpansionEntity {
    private static final float STRENGTH = 1000.0F;

    private int width;
    private int height;

    public OpenDomainExpansionEntity(EntityType<? extends Mob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public OpenDomainExpansionEntity(EntityType<? extends Mob> pEntityType, LivingEntity owner, DomainExpansion ability, int width, int height, float strength) {
        super(pEntityType, owner, ability, strength);

        Vec3 pos = owner.position()
                .subtract(owner.getLookAngle()
                .multiply(this.getBbWidth(), 0.0D, this.getBbWidth()));
        this.moveTo(pos.x(), pos.y(), pos.z());

        this.width = width;
        this.height = height;

        owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
            AttributeInstance attribute = this.getAttribute(Attributes.MAX_HEALTH);

            if (attribute != null) {
                attribute.setBaseValue(STRENGTH * cap.getGrade().getPower());
                this.setHealth(this.getMaxHealth());
            }
        });
    }

    @Override
    public AABB getBounds() {
        return new AABB(this.getX() - (double) (this.width / 2), this.getY() - 1.0D, this.getZ() - (double) (this.width / 2),
                this.getX() + (double) (this.width / 2), this.getY() + this.height - 1.0D, this.getZ() + (double) (this.width / 2));
    }

    @Override
    public boolean isInsideBarrier(Entity entity) {
        AABB bounds = this.getBounds();
        return bounds.intersects(entity.getBoundingBox());
    }

    @Override
    public void warn() {
        LivingEntity owner = this.getOwner();

        if (owner != null) {
            AABB bounds = this.getBounds();

            for (Entity entity : this.level.getEntities(this, bounds, this::isAffected)) {
                entity.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> cap.onInsideDomain(this));
            }
        }
        this.warned = true;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);

        pCompound.putInt("width", this.width);
        pCompound.putInt("height", this.height);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);

        this.width = pCompound.getInt("width");
        this.height = pCompound.getInt("height");
    }

    private List<ClosedDomainExpansionEntity> getClosedDomainsInside() {
        return HelperMethods.getEntityCollisionsOfClass(ClosedDomainExpansionEntity.class, this.level, this.getBoundingBox());
    }

    private void doSureHitEffect(@NotNull LivingEntity owner) {
        AABB bounds = this.getBounds();

        for (Entity entity : this.level.getEntities(this, bounds, this::isAffected)) {
            if (entity instanceof LivingEntity living) {
                this.ability.onHitEntity(this, owner, living);
            }
        }

        for (BlockPos pos : BlockPos.randomBetweenClosed(this.random, this.width * this.height / 4,
                (int) bounds.minX, (int) bounds.minY, (int) bounds.minZ, (int)
                        bounds.maxX, (int) bounds.maxY, (int) bounds.maxZ)) {
            this.ability.onHitBlock(this, owner, pos);
        }
    }

    private boolean checkSureHitEffect() {
        List<ClosedDomainExpansionEntity> domains = this.getClosedDomainsInside();

        for (ClosedDomainExpansionEntity domain : domains) {
            if (!domain.isInsideBarrier(this)) continue;

            if (domain.getStrength() > this.getStrength()) {
                this.discard();
                return false;
            } else if (domain.getStrength() == this.getStrength()) {
                return false;
            }
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
        if (!this.level.isClientSide) {
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
            if (!this.level.isClientSide) {
                if (this.checkSureHitEffect()) {
                    if (!this.warned || this.getTime() % 5 == 0) {
                        this.warn();
                    }
                    this.doSureHitEffect(owner);
                }
            }
        }
    }
}
