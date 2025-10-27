package radon.jujutsu_kaisen.visual;


import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import radon.jujutsu_kaisen.client.visual.ClientVisualHandler;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.network.packet.s2c.SyncVisualDataS2CPacket;

public class ServerVisualHandler {
    public static void sync(LivingEntity entity) {
        if (entity.level().isClientSide) return;

        IJujutsuCapability cap = entity.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        ISorcererData sorcererData = cap.getSorcererData();
        IAbilityData abilityData = cap.getAbilityData();

        ClientVisualHandler.ClientData client = new ClientVisualHandler.ClientData(abilityData.getToggled(), abilityData.getChanneled(), sorcererData.getTraits(),
                sorcererData.getActiveTechniques(), sorcererData.getType(), sorcererData.getExperience(), sorcererData.getCursedEnergyColor());
        PacketDistributor.sendToAllPlayers(new SyncVisualDataS2CPacket(entity.getUUID(), client.serializeNBT()));
    }
}
