package radon.jujutsu_kaisen.ability;


import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JJKConstants;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.VeilHandler;
import radon.jujutsu_kaisen.ability.event.LivingHitByDomainEvent;
import radon.jujutsu_kaisen.ability.registry.JJKAbilities;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;
import radon.jujutsu_kaisen.data.DataProvider;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.domain.IDomainData;
import radon.jujutsu_kaisen.data.registry.JJKAttachmentTypes;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.entity.DomainExpansionEntity;
import radon.jujutsu_kaisen.entity.IBarrier;
import radon.jujutsu_kaisen.entity.IDomain;
import radon.jujutsu_kaisen.entity.domain.base.ClosedDomainExpansionEntity;
import radon.jujutsu_kaisen.network.packet.s2c.SyncAbilityDataS2CPacket;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;
import radon.jujutsu_kaisen.util.RotationUtil;

import java.util.Optional;

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
            if (this instanceof IClosedDomain) {
                float yaw = RotationUtil.getTargetAdjustedYRot(owner);
                Vec3 direction = RotationUtil.calculateViewVector(0.0F, yaw);
                Vec3 behind = owner.position().subtract(0.0D, ClosedDomainExpansionEntity.RADIUS, 0.0D)
                        .add(direction.scale(ClosedDomainExpansionEntity.RADIUS - DomainExpansionEntity.OFFSET));
                BlockPos center = BlockPos.containing(behind.x, behind.y, behind.z)
                        .offset(0, ClosedDomainExpansionEntity.RADIUS, 0);
                BlockPos relative = target.blockPosition().subtract(center);

                if (relative.distSqr(Vec3i.ZERO) >= (ClosedDomainExpansionEntity.RADIUS - 1) * (ClosedDomainExpansionEntity.RADIUS - 1)) {
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

        ISorcererData sorcererData = cap.getSorcererData();

        if (sorcererData.hasSummonOfClass(DomainExpansionEntity.class)) {
            Optional<IDomainData> domainData = DataProvider.getDataIfPresent(owner.level(), JJKAttachmentTypes.DOMAIN);

            if (domainData.isPresent() && domainData.get().hasDomain(owner.getUUID())) return Status.FAILURE;
        }

        return super.isTriggerable(owner);
    }

    @Override
    public Status isStillUsable(LivingEntity owner) {
        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return Status.FAILURE;

        ISorcererData sorcererData = cap.getSorcererData();

        if (!sorcererData.hasSummonOfClass(DomainExpansionEntity.class)) {
            Optional<IDomainData> domainData = DataProvider.getDataIfPresent(owner.level(), JJKAttachmentTypes.DOMAIN);

            if (domainData.isEmpty() || !domainData.get().hasDomain(owner.getUUID())) return Status.FAILURE;
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

        DomainExpansionEntity domain = this.summon(owner);

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
            PacketDistributor.sendToPlayer(player, new SyncAbilityDataS2CPacket(abilityData.serializeNBT(player.registryAccess())));
            PacketDistributor.sendToPlayer(player, new SyncSorcererDataS2CPacket(sorcererData.serializeNBT(player.registryAccess())));
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

    public void onHitBlock(DomainExpansionEntity domain, LivingEntity owner, BlockPos pos, boolean instant) {
    }

    protected abstract DomainExpansionEntity summon(LivingEntity owner);

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
        CursedTechnique technique = data.getTechnique();
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
}
