package radon.jujutsu_kaisen.data.ten_shadows;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.sorcerer.SorcererData;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;
import radon.jujutsu_kaisen.network.packet.s2c.SyncTenShadowsDataS2CPacket;

@Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TenShadowsDataProvider {
    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity owner = event.getEntity();

        if (!owner.hasData(JJKAttachmentTypes.TEN_SHADOWS)) return;

        ITenShadowsData data = owner.getData(JJKAttachmentTypes.TEN_SHADOWS);
        data.tick(owner);
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        Player original = event.getOriginal();
        Player clone = event.getEntity();

        if (!original.hasData(JJKAttachmentTypes.TEN_SHADOWS) || !clone.hasData(JJKAttachmentTypes.TEN_SHADOWS)) return;

        if (event.isWasDeath()) {
            ITenShadowsData data = original.getData(JJKAttachmentTypes.TEN_SHADOWS);

            if (!ConfigHolder.SERVER.realisticShikigami.get()) {
                data.revive(false);

                if (clone instanceof ServerPlayer player) {
                    PacketHandler.sendToClient(new SyncTenShadowsDataS2CPacket(data.serializeNBT()), player);
                }
            }
        }
    }

    public static class Serializer implements IAttachmentSerializer<CompoundTag, ITenShadowsData> {
        @Override
        public ITenShadowsData read(CompoundTag tag) {
            ITenShadowsData data = new TenShadowsData();
            data.deserializeNBT(tag);
            return data;
        }

        @Override
        public CompoundTag write(ITenShadowsData attachment) {
            return attachment.serializeNBT();
        }
    }
}