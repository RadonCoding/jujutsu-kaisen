package radon.jujutsu_kaisen.data.sorcerer;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;
import radon.jujutsu_kaisen.data.projection_sorcery.IProjectionSorceryData;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;

@Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SorcererDataProvider {
    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity owner = event.getEntity();

        if (!owner.hasData(JJKAttachmentTypes.SORCERER)) return;

        ISorcererData data = owner.getData(JJKAttachmentTypes.SORCERER);
        data.tick(owner);
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        Player original = event.getOriginal();
        Player clone = event.getEntity();

        if (!original.hasData(JJKAttachmentTypes.SORCERER) || !clone.hasData(JJKAttachmentTypes.SORCERER)) return;

        if (event.isWasDeath()) {
            ISorcererData data = original.getData(JJKAttachmentTypes.SORCERER);
            data.setEnergy(data.getMaxEnergy());
            data.clearToggled();
            data.resetCooldowns();
            data.resetBrainDamage();
            data.resetBurnout();
            data.resetExtraEnergy();
            data.resetBlackFlash();
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();

        ISorcererData data = player.getData(JJKAttachmentTypes.SORCERER);
        data.init(player);

        if (player.level().isClientSide) return;

        PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(data.serializeNBT()), (ServerPlayer) player);
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        ISorcererData data = player.getData(JJKAttachmentTypes.SORCERER);
        PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(data.serializeNBT()), player);
    }

    public static class Serializer implements IAttachmentSerializer<CompoundTag, ISorcererData> {
        @Override
        public ISorcererData read(CompoundTag tag) {
            ISorcererData data = new SorcererData();
            data.deserializeNBT(tag);
            return data;
        }

        @Override
        public CompoundTag write(ISorcererData attachment) {
            return attachment.serializeNBT();
        }
    }
}