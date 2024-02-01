package radon.jujutsu_kaisen.visual;

import net.minecraft.world.entity.LivingEntity;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.client.visual.ClientVisualHandler;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncVisualDataS2CPacket;

public class ServerVisualHandler {
    public static void sync(LivingEntity entity) {
        if (entity.level().isClientSide) return;

        ISorcererData cap = entity.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        ClientVisualHandler.ClientData data = new ClientVisualHandler.ClientData(cap.getToggled(), cap.getChanneled(), cap.getTraits(), JJKAbilities.getTechniques(entity), cap.getTechnique(), cap.getType(),
                cap.getExperience(), cap.getCursedEnergyColor());
        PacketHandler.broadcast(new SyncVisualDataS2CPacket(entity.getUUID(), data.serializeNBT()));
    }
}
