package radon.jujutsu_kaisen.visual;

import net.minecraft.world.entity.LivingEntity;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.client.visual.ClientVisualHandler;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncVisualDataS2CPacket;

public class ServerVisualHandler {
    public static void sync(LivingEntity entity) {
        if (entity.level().isClientSide) return;

        IJujutsuCapability cap = entity.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        ISorcererData data = cap.getSorcererData();

        ClientVisualHandler.ClientData client = new ClientVisualHandler.ClientData(data.getToggled(), data.getChanneled(), data.getTraits(), data.getActiveTechniques(), data.getTechnique(), data.getType(),
                data.getExperience(), data.getCursedEnergyColor());
        PacketHandler.broadcast(new SyncVisualDataS2CPacket(entity.getUUID(), client.serializeNBT()));
    }
}
