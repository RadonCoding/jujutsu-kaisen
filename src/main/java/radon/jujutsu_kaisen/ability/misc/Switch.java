package radon.jujutsu_kaisen.ability.misc;

import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.registry.JJKAbilities;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.data.sorcerer.Trait;
import radon.jujutsu_kaisen.entity.sorcerer.SukunaEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import radon.jujutsu_kaisen.network.packet.s2c.SyncAbilityDataS2CPacket;
import radon.jujutsu_kaisen.util.EntityUtil;

public class Switch extends Ability {
    @Override
    public boolean isScalable(LivingEntity owner) {
        return false;
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        if (target == null || target.isDeadOrDying()) return false;
        if (!owner.hasLineOfSight(target)) return false;

        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return false;

        ISorcererData data = cap.getSorcererData();

        return data.getType() == JujutsuType.CURSE || JJKAbilities.RCT1.get().isUnlocked(owner) ? owner.getHealth() / owner.getMaxHealth() < 0.8F :
                owner.getHealth() / owner.getMaxHealth() < 0.3F;
    }

    @Override
    public boolean isUsableInSpectator() {
        return true;
    }

    @Override
    public boolean isTechnique() {
        return false;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    @Override
    public void run(LivingEntity owner) {
        if (owner.level().isClientSide) return;

        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        ISorcererData sorcererData = cap.getSorcererData();
        IAbilityData abilityData = cap.getAbilityData();

        SukunaEntity sukuna;

        if ((sukuna = sorcererData.getSummonByClass(SukunaEntity.class)) != null) {
            sorcererData.removeSummon(sukuna);
            sukuna.discard();
        } else {
            sukuna = new SukunaEntity(owner, sorcererData.getFingers(), true);
            EntityUtil.convertTo(owner, sukuna, true, false);

            sorcererData.addSummon(sukuna);

            abilityData.clear();

            if (owner instanceof ServerPlayer player) {
                PacketDistributor.sendToPlayer(player, new SyncAbilityDataS2CPacket(abilityData.serializeNBT(player.registryAccess())));
            }
        }
    }

    @Override
    public boolean isValid(LivingEntity owner) {
        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return false;

        ISorcererData data = cap.getSorcererData();

        return data.hasTrait(Trait.VESSEL) && data.getFingers() > 0 && super.isValid(owner);
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 0;
    }

    @Override
    public int getCooldown() {
        return 30 * 20;
    }
}
