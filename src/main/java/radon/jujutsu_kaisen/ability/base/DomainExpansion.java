package radon.jujutsu_kaisen.ability.base;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.*;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.List;

public abstract class DomainExpansion extends Ability implements Ability.IToggled {
    public static final int BURNOUT = 30 * 20;

    @Override
    public boolean isChantable() {
        return false;
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        if (cap.hasToggled(this)) {
            if (target != null) {
                DomainExpansionEntity domain = cap.getDomain((ServerLevel) owner.level());
                return domain != null && domain.isInsideBarrier(target.blockPosition());
            }
        } else {
            if (target == null) return false;

            if (this instanceof DomainExpansion.IClosedDomain closed) {
                int radius = Math.round(closed.getRadius(owner));
                Vec3 direction = HelperMethods.getLookAngle(owner);
                Vec3 behind = owner.position().add(direction.scale(radius - DomainExpansionEntity.OFFSET));
                BlockPos center = BlockPos.containing(behind.x(), behind.y() - (double) (radius / 2), behind.z())
                        .offset(0, radius / 2, 0);
                BlockPos relative = target.blockPosition().subtract(center);

                if (relative.distSqr(Vec3i.ZERO) >= (radius - 1) * (radius - 1)) {
                    return false;
                }
            }

            if (this instanceof DomainExpansion.IOpenDomain open) {
                BlockPos relative = target.blockPosition().subtract(owner.blockPosition());

                if (relative.getY() > open.getHeight() || relative.distSqr(Vec3i.ZERO) >= open.getWidth() * open.getWidth()) {
                    return false;
                }
            }

            boolean result = owner.onGround() && cap.getType() == JujutsuType.CURSE || cap.hasTrait(Trait.REVERSE_CURSED_TECHNIQUE) ? owner.getHealth() / owner.getMaxHealth() < 0.8F :
                    owner.getHealth() / owner.getMaxHealth() < 0.3F || target.getHealth() > owner.getHealth() * 2;

            for (DomainExpansionEntity ignored : cap.getDomains((ServerLevel) owner.level())) {
                result = true;
                break;
            }

            Status status = this.getStatus(owner, true, false, false, false);

            if (result && (status == Status.DOMAIN_AMPLIFICATION)) {
                if (cap.hasToggled(JJKAbilities.DOMAIN_AMPLIFICATION.get())) {
                    cap.toggle(owner, JJKAbilities.DOMAIN_AMPLIFICATION.get());
                }
            }
            return result;
        }
        return false;
    }

    @Override
    public Status checkStatus(LivingEntity owner) {
        if (!owner.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return Status.FAILURE;
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        if (owner.level() instanceof ServerLevel level) {
            if (cap.getDomain(level) == null) return Status.FAILURE;
        }
        return  super.checkStatus(owner);
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.TOGGLED;
    }

    @Override
    public void onEnabled(LivingEntity owner) {
        this.createBarrier(owner);
    }

    @Override
    public void onDisabled(LivingEntity owner) {
        if (!owner.level().isClientSide) {
            owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                if (owner instanceof ServerPlayer player) {
                    PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(cap.serializeNBT()), player);
                }
            });
        }
    }

    @Override
    public void run(LivingEntity owner) {

    }

    @Override
    public float getCost(LivingEntity owner) {
        return 2.5F;
    }

    public void onHitEntity(DomainExpansionEntity domain, LivingEntity owner, LivingEntity entity) {
        MinecraftForge.EVENT_BUS.post(new LivingHitByDomainEvent(entity, this, owner));
    }

    public abstract void onHitBlock(DomainExpansionEntity domain, LivingEntity owner,  BlockPos pos);

    protected abstract void createBarrier(LivingEntity owner);

    @Override
    public boolean isTechnique() {
        return true;
    }

    @Override
    public final int getCooldown() {
        return 60 * 20;
    }

    @Override
    public boolean shouldLog() {
        return false;
    }

    @Override
    public MenuType getMenuType() {
        return MenuType.DOMAIN;
    }

    @Override
    public boolean isDisplayed(LivingEntity owner) {
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        CursedTechnique technique = cap.getTechnique();
        return technique != null && technique.getDomain() == this && super.isDisplayed(owner);
    }

    @Override
    public AbilityDisplayInfo getDisplay(LivingEntity owner) {
        Vec2 coordinates = this.getDisplayCoordinates();
        return new AbilityDisplayInfo("domain_expansion", coordinates.x, coordinates.y);
    }

    @Nullable
    @Override
    public Ability getParent(LivingEntity owner) {
        return JJKAbilities.SIMPLE_DOMAIN.get();
    }

    @Override
    public Vec2 getDisplayCoordinates() {
        return new Vec2(2.0F, 0.0F);
    }

    @Override
    public int getPointsCost() {
        return 500;
    }

    public interface IClosedDomain {
        default int getSize() {
            return 20;
        }
        default float getRadius(LivingEntity owner) {
            ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
            return this.getSize() * cap.getDomainSize();
        }
        List<Block> getBlocks();
        default List<Block> getFillBlocks() {
            return this.getBlocks();
        }
        default List<Block> getFloorBlocks() {
            return List.of();
        }
        default boolean canPlaceFloor(BlockGetter getter, BlockPos pos) {
            return true;
        }
        @Nullable
        default ParticleOptions getEnvironmentParticle() {
            return null;
        }
    }

    public interface IOpenDomain {
        int getWidth();
        int getHeight();
    }
}
