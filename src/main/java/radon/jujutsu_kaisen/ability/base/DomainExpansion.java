package radon.jujutsu_kaisen.ability.base;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JJKConstants;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.VeilHandler;
import radon.jujutsu_kaisen.ability.AbilityHandler;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.event.LivingHitByDomainEvent;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.cursed_technique.base.ICursedTechnique;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import radon.jujutsu_kaisen.entity.base.IBarrier;
import radon.jujutsu_kaisen.entity.base.IDomain;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncAbilityDataS2CPacket;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;
import radon.jujutsu_kaisen.util.RotationUtil;

import java.util.List;

public abstract class DomainExpansion extends Ability implements IToggled {
    public static final int BURNOUT = 10 * 20;

    @Override
    public boolean isScalable(LivingEntity owner) {
        return false;
    }

    @Override
    protected boolean isNotDisabledFromDA(LivingEntity owner) {
        return true;
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        if (target == null || target.isDeadOrDying()) return false;

        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return false;

        ISorcererData sorcererData = cap.getSorcererData();
        IAbilityData abilityData = cap.getAbilityData();

        if (abilityData.hasToggled(this)) {
            DomainExpansionEntity domain = sorcererData.getSummonByClass(DomainExpansionEntity.class);

            if (domain == null) return false;

            return domain.isInsideBarrier(target.blockPosition());
        } else {
            if (this instanceof DomainExpansion.IClosedDomain closed) {
                int radius = Math.round(closed.getRadius(owner));

                float yaw = RotationUtil.getTargetAdjustedYRot(owner);
                Vec3 direction = RotationUtil.calculateViewVector(0.0F, yaw);
                Vec3 behind = owner.position().subtract(0.0D, radius, 0.0D).add(direction.scale(radius - DomainExpansionEntity.OFFSET));
                BlockPos center = BlockPos.containing(behind.x, behind.y, behind.z).offset(0, radius, 0);
                BlockPos relative = target.blockPosition().subtract(center);

                if (relative.distSqr(Vec3i.ZERO) >= (radius - 1) * (radius - 1)) {
                    return false;
                }
            }

            boolean result = owner.onGround() && sorcererData.getType() == JujutsuType.CURSE || JJKAbilities.RCT1.get().isUnlocked(owner) ?
                    owner.getHealth() / owner.getMaxHealth() < 0.8F : owner.getHealth() / owner.getMaxHealth() < 0.3F || target.getHealth() > owner.getHealth() * 2;

            if (!result) {
                for (IBarrier barrier : VeilHandler.getBarriers((ServerLevel) owner.level(), owner.blockPosition())) {
                    if (!(barrier instanceof IDomain)) continue;

                    result = true;
                    break;
                }
            }

            Status status = this.getStatus(owner);

            if (result && status == Status.SUCCESS) {
                if (abilityData.hasToggled(JJKAbilities.DOMAIN_AMPLIFICATION.get())) {
                    AbilityHandler.trigger(owner, JJKAbilities.DOMAIN_AMPLIFICATION.get());
                }
            }
            return result;
        }
    }

    public static float getStrength(LivingEntity owner, boolean instant) {
        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return 0.0F;

        ISorcererData data = cap.getSorcererData();

        return getStrength(instant, data.getDomainSize());
    }

    public static float getStrength(boolean instant, float size) {
        return ((ConfigHolder.SERVER.maximumDomainSize.get().floatValue() + 0.1F) - size) * (instant ? 0.5F : 1.0F);
    }

    @Override
    public boolean isValid(LivingEntity owner) {
        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return false;

        ISorcererData data = cap.getSorcererData();
        return data.getBrainDamage() < JJKConstants.MAX_BRAIN_DAMAGE && super.isValid(owner);
    }

    @Override
    public Status isTriggerable(LivingEntity owner) {
        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return Status.FAILURE;

        ISorcererData data = cap.getSorcererData();

        if (data.hasSummonOfClass(DomainExpansionEntity.class)) return Status.FAILURE;

        return super.isTriggerable(owner);
    }

    @Override
    public Status isStillUsable(LivingEntity owner) {
        if (!owner.level().isClientSide) {
            IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap == null) return Status.FAILURE;

            ISorcererData data = cap.getSorcererData();

            if (!data.hasSummonOfClass(DomainExpansionEntity.class)) {
                return Status.FAILURE;
            }
        }
        return super.isStillUsable(owner);
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.TOGGLED;
    }

    @Override
    public void onEnabled(LivingEntity owner) {
        if (owner.level().isClientSide) return;

        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        ISorcererData data = cap.getSorcererData();

        DomainExpansionEntity domain = this.createBarrier(owner);

        data.addSummon(domain);
    }

    @Override
    public void onDisabled(LivingEntity owner) {
        if (owner.level().isClientSide) return;

        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        ISorcererData sorcererData = cap.getSorcererData();
        IAbilityData abilityData = cap.getAbilityData();

        sorcererData.unsummonByClass(DomainExpansionEntity.class);

        if (owner instanceof ServerPlayer player) {
            PacketHandler.sendToClient(new SyncAbilityDataS2CPacket(abilityData.serializeNBT()), player);
            PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(sorcererData.serializeNBT()), player);
        }
    }

    @Override
    public void run(LivingEntity owner) {

    }

    @Override
    public float getCost(LivingEntity owner) {
        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return 0.0F;

        IAbilityData data = cap.getAbilityData();

        return data.hasToggled(this) ? 2.0F : 1000.0F;
    }

    public void onHitEntity(DomainExpansionEntity domain, LivingEntity owner, LivingEntity entity, boolean instant) {
        NeoForge.EVENT_BUS.post(new LivingHitByDomainEvent(entity, this, owner));
    }

    public abstract void onHitBlock(DomainExpansionEntity domain, LivingEntity owner, BlockPos pos);

    protected abstract DomainExpansionEntity createBarrier(LivingEntity owner);

    @Override
    public boolean shouldLog(LivingEntity owner) {
        return false;
    }

    @Override
    public MenuType getMenuType(LivingEntity owner) {
        return MenuType.DOMAIN;
    }

    @Override
    public boolean isDisplayed(LivingEntity owner) {
        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return false;

        ISorcererData data = cap.getSorcererData();
        ICursedTechnique technique = data.getTechnique();
        return technique != null && technique.getDomain() == this && super.isDisplayed(owner);
    }

    @Override
    public ResourceLocation getIcon(LivingEntity owner) {
        return new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/ability/domain_expansion.png");
    }

    @Nullable
    @Override
    public Ability getParent(LivingEntity owner) {
        return JJKAbilities.CURSED_ENERGY_FLOW.get();
    }

    @Override
    public int getPointsCost() {
        return ConfigHolder.SERVER.domainExpansionCost.get();
    }

    public interface IClosedDomain {
        default int getSize() {
            return 20;
        }

        default float getRadius(LivingEntity owner) {
            IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap == null) return 0.0F;

            ISorcererData data = cap.getSorcererData();
            return this.getSize() * data.getDomainSize();
        }

        List<Block> getBlocks();

        default List<Block> getFillBlocks() {
            return this.getBlocks();
        }

        default List<Block> getFloorBlocks() {
            return List.of();
        }

        default List<Block> getDecorationBlocks() {
            return List.of();
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
