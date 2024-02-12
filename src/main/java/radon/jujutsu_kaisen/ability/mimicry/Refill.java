package radon.jujutsu_kaisen.ability.mimicry;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;
import radon.jujutsu_kaisen.entity.curse.RikaEntity;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;

public class Refill extends Ability {
    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return false;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    @Override
    public void run(LivingEntity owner) {
        if (owner.level().isClientSide) return;

        ISorcererData ownerData = owner.getData(JJKAttachmentTypes.SORCERER);

        if (ownerData == null) return;

        float amount = ownerData.getMaxEnergy() - ownerData.getEnergy();

        RikaEntity rika = ownerData.getSummonByClass(RikaEntity.class);

        if (rika == null) return;

        ISorcererData summonData = rika.getData(JJKAttachmentTypes.SORCERER);

        if (summonData.getEnergy() > amount && ownerData.getEnergy() < ownerData.getMaxEnergy()) {
            ownerData.addEnergy(amount);
            summonData.useEnergy(amount);

            if (owner instanceof ServerPlayer player) {
                PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(ownerData.serializeNBT()), player);
            }
        }
    }

    @Override
    public boolean isValid(LivingEntity owner) {
        ISorcererData data = owner.getData(JJKAttachmentTypes.SORCERER);

        if (data == null) return false;

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
