package radon.jujutsu_kaisen.ability.curse_manipulation;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.world.entity.LivingEntity;
import radon.jujutsu_kaisen.ability.base.Imbuement;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;

public class CurseManipulationImbuement extends Imbuement {
    @Override
    public void hit(ItemStack stack, LivingEntity owner, LivingEntity target) {
        if (!target.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return;
        ISorcererData cap = target.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        cap.useEnergy(10.0F);

        if (target instanceof ServerPlayer player) {
            PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(cap.serializeNBT()), player);
        }
    }
}

