package radon.jujutsu_kaisen.ability.mimicry;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.registry.JJKAbilities;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.entity.curse.RikaEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;

public class Refill extends Ability {
    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return false;
    }

    @Override
    public boolean isScalable(LivingEntity owner) {
        return false;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    @Override
    public void run(LivingEntity owner) {
        if (owner.level().isClientSide) return;

        IJujutsuCapability ownerCap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (ownerCap == null) return;

        ISorcererData ownerData = ownerCap.getSorcererData();

        float amount = ownerData.getMaxEnergy() - ownerData.getEnergy();

        RikaEntity rika = ownerData.getSummonByClass(RikaEntity.class);

        if (rika == null) return;

        IJujutsuCapability rikacap = rika.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (rikacap == null) return;

        ISorcererData rikaData = rikacap.getSorcererData();

        if (rikaData.getEnergy() > amount && ownerData.getEnergy() < ownerData.getMaxEnergy()) {
            ownerData.addEnergy(amount);
            rikaData.useEnergy(amount);

            if (owner instanceof ServerPlayer player) {
                PacketDistributor.sendToPlayer(player, new SyncSorcererDataS2CPacket(ownerData.serializeNBT(player.registryAccess())));
            }
        }
    }

    @Override
    public boolean isValid(LivingEntity owner) {
        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return false;

        IAbilityData data = cap.getAbilityData();
        return data.hasToggled(JJKAbilities.RIKA.get()) && super.isValid(owner);
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 0;
    }

    @Override
    public int getCooldown() {
        return 60 * 20;
    }
}
