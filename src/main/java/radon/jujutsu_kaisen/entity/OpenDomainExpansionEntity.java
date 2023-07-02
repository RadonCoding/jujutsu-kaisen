package radon.jujutsu_kaisen.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
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
import radon.jujutsu_kaisen.ability.DomainExpansion;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import radon.jujutsu_kaisen.entity.base.SorcererEntity;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class OpenDomainExpansionEntity extends DomainExpansionEntity {
    private static final float STRENGTH = 1000.0F;

    private DomainExpansion ability;

    private int width;
    private int height;
    private int duration;

    public OpenDomainExpansionEntity(EntityType<? extends Mob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public AABB getBounds() {
        return new AABB(this.getX() - this.width, this.getY() - 1.0D, this.getZ() - this.width,
                this.getX() + this.width, this.getY() + this.height - 1.0D, this.getZ() + this.width);
    }

    public OpenDomainExpansionEntity(EntityType<? extends Mob> pEntityType, LivingEntity owner, DomainExpansion ability, float strength, int width, int height, int duration) {
        super(pEntityType, owner, strength);

        Vec3 pos = owner.position()
                .subtract(owner.getLookAngle()
                .multiply(this.getBbWidth(), 0.0D, this.getBbWidth()));
        this.moveTo(pos.x(), pos.y(), pos.z());

        this.ability = ability;

        this.width = width;
        this.height = height;
        this.duration = duration;

        owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
            AttributeInstance attribute = this.getAttribute(Attributes.MAX_HEALTH);

            if (attribute != null) {
                attribute.setBaseValue(STRENGTH * cap.getGrade().getPower());
                this.setHealth(this.getMaxHealth());
            }
        });
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();

        for (SorcererEntity sorcerer : this.level.getEntitiesOfClass(SorcererEntity.class, this.getBounds())) {
            sorcerer.onInsideDomain(this);
        }
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);

        pCompound.putString("ability", JJKAbilities.getKey(this.ability).toString());
        pCompound.putInt("width", this.width);
        pCompound.putInt("height", this.height);
        pCompound.putInt("duration", this.duration);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);

        this.ability = (DomainExpansion) JJKAbilities.getValue(new ResourceLocation(pCompound.getString("ability")));
        this.width = pCompound.getInt("width");
        this.height = pCompound.getInt("height");
        this.duration = pCompound.getInt("duration");
    }

    private <T extends Entity> List<T> getEntitiesInside(Class<T> clazz) {
        return this.level.getEntitiesOfClass(clazz, this.getBounds());
    }

    private void doSureHitEffect(LivingEntity owner) {
        AABB bounds = this.getBounds();

        for (Entity entity : this.level.getEntities(owner, bounds)) {
            if (entity == this) continue;

            if (entity instanceof SorcererEntity sorcerer) {
                sorcerer.onInsideDomain(this);
            }
            this.ability.onHitEntity(this, owner, entity);
        }

        for (BlockPos pos : BlockPos.randomBetweenClosed(this.random, 50,
                (int) bounds.minX, (int) bounds.minY, (int)  bounds.minZ, (int)
                        bounds.maxX, (int)  bounds.maxY, (int) bounds.maxZ)) {
            this.ability.onHitBlock(this, owner, pos);
        }
    }

    private boolean checkSureHitEffect(LivingEntity owner) {
        AtomicBoolean result = new AtomicBoolean(true);

        List<ClosedDomainExpansionEntity> domains = this.getEntitiesInside(ClosedDomainExpansionEntity.class);

        owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
            for (ClosedDomainExpansionEntity domain : domains) {
                if (this.getBoundingBox().intersects(domain.getBounds()) && domain.getStrength() >= this.getStrength()) {
                    result.set(false);
                }
            }
        });
        return result.get();
    }

    @Override
    public void tick() {
        Entity entity = this.getOwner();

        if (this.level.isClientSide || (entity == null || !entity.isRemoved()) && this.level.hasChunkAt(this.blockPosition())) {
            super.tick();

            if (!this.level.isClientSide && entity instanceof LivingEntity owner) {
                if (this.checkSureHitEffect(owner)) {
                    this.doSureHitEffect(owner);
                }

                if (this.duration-- == 0) {
                    this.discard();
                }
            }
        } else {
            this.discard();
        }
    }
}
