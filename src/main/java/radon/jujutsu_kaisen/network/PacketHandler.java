package radon.jujutsu_kaisen.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.network.packet.c2s.*;
import radon.jujutsu_kaisen.network.packet.s2c.*;

public class PacketHandler {
    private static SimpleChannel INSTANCE;

    private static int packetId = 0;

    private static int id() {
        return packetId++;
    }

    public static void register() {
        INSTANCE = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(JujutsuKaisen.MOD_ID, "main"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(x -> true)
                .serverAcceptedVersions(x -> true)
                .simpleChannel();
        INSTANCE.messageBuilder(SyncSorcererDataS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(SyncSorcererDataS2CPacket::new)
                .encoder(SyncSorcererDataS2CPacket::encode)
                .consumerMainThread(SyncSorcererDataS2CPacket::handle)
                .add();
        INSTANCE.messageBuilder(SyncTenShadowsDataS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(SyncTenShadowsDataS2CPacket::new)
                .encoder(SyncTenShadowsDataS2CPacket::encode)
                .consumerMainThread(SyncTenShadowsDataS2CPacket::handle)
                .add();
        INSTANCE.messageBuilder(TriggerAbilityC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(TriggerAbilityC2SPacket::new)
                .encoder(TriggerAbilityC2SPacket::encode)
                .consumerMainThread(TriggerAbilityC2SPacket::handle)
                .add();
        INSTANCE.messageBuilder(OpenInventoryCurseC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(OpenInventoryCurseC2SPacket::new)
                .encoder(OpenInventoryCurseC2SPacket::encode)
                .consumerMainThread(OpenInventoryCurseC2SPacket::handle)
                .add();
        INSTANCE.messageBuilder(CameraShakeS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(CameraShakeS2CPacket::new)
                .encoder(CameraShakeS2CPacket::encode)
                .consumerMainThread(CameraShakeS2CPacket::handle)
                .add();
        INSTANCE.messageBuilder(CommandableTargetC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(CommandableTargetC2SPacket::new)
                .encoder(CommandableTargetC2SPacket::encode)
                .consumerMainThread(CommandableTargetC2SPacket::handle)
                .add();
        INSTANCE.messageBuilder(SetOverlayMessageS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(SetOverlayMessageS2CPacket::new)
                .encoder(SetOverlayMessageS2CPacket::encode)
                .consumerMainThread(SetOverlayMessageS2CPacket::handle)
                .add();
        INSTANCE.messageBuilder(JumpInputListenerC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(JumpInputListenerC2SPacket::new)
                .encoder(JumpInputListenerC2SPacket::encode)
                .consumerMainThread(JumpInputListenerC2SPacket::handle)
                .add();
        INSTANCE.messageBuilder(RightClickInputListenerC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(RightClickInputListenerC2SPacket::new)
                .encoder(RightClickInputListenerC2SPacket::encode)
                .consumerMainThread(RightClickInputListenerC2SPacket::handle)
                .add();
        INSTANCE.messageBuilder(SetSizeC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(SetSizeC2SPacket::new)
                .encoder(SetSizeC2SPacket::encode)
                .consumerMainThread(SetSizeC2SPacket::handle)
                .add();
        INSTANCE.messageBuilder(SetFrequencyS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(SetFrequencyS2CPacket::new)
                .encoder(SetFrequencyS2CPacket::encode)
                .consumerMainThread(SetFrequencyS2CPacket::handle)
                .add();
        INSTANCE.messageBuilder(SetCostS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(SetCostS2CPacket::new)
                .encoder(SetCostS2CPacket::encode)
                .consumerMainThread(SetCostS2CPacket::handle)
                .add();
        INSTANCE.messageBuilder(RequestCostC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(RequestCostC2SPacket::new)
                .encoder(RequestCostC2SPacket::encode)
                .consumerMainThread(RequestCostC2SPacket::handle)
                .add();
        INSTANCE.messageBuilder(SetTojiBountyC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(SetTojiBountyC2SPacket::new)
                .encoder(SetTojiBountyC2SPacket::encode)
                .consumerMainThread(SetTojiBountyC2SPacket::handle)
                .add();
        INSTANCE.messageBuilder(KuchisakeOnnaAnswerC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(KuchisakeOnnaAnswerC2SPacket::new)
                .encoder(KuchisakeOnnaAnswerC2SPacket::encode)
                .consumerMainThread(KuchisakeOnnaAnswerC2SPacket::handle)
                .add();
        INSTANCE.messageBuilder(ShadowInventoryTakeC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(ShadowInventoryTakeC2SPacket::new)
                .encoder(ShadowInventoryTakeC2SPacket::encode)
                .consumerMainThread(ShadowInventoryTakeC2SPacket::handle)
                .add();
        INSTANCE.messageBuilder(CurseSummonC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(CurseSummonC2SPacket::new)
                .encoder(CurseSummonC2SPacket::encode)
                .consumerMainThread(CurseSummonC2SPacket::handle)
                .add();
        INSTANCE.messageBuilder(SetAdditionalC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(SetAdditionalC2SPacket::new)
                .encoder(SetAdditionalC2SPacket::encode)
                .consumerMainThread(SetAdditionalC2SPacket::handle)
                .add();
        INSTANCE.messageBuilder(SetAbsorbedC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(SetAbsorbedC2SPacket::new)
                .encoder(SetAbsorbedC2SPacket::encode)
                .consumerMainThread(SetAbsorbedC2SPacket::handle)
                .add();
        INSTANCE.messageBuilder(RequestVisualDataC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(RequestVisualDataC2SPacket::new)
                .encoder(RequestVisualDataC2SPacket::encode)
                .consumerMainThread(RequestVisualDataC2SPacket::handle)
                .add();
        INSTANCE.messageBuilder(ReceiveVisualDataS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(ReceiveVisualDataS2CPacket::new)
                .encoder(ReceiveVisualDataS2CPacket::encode)
                .consumerMainThread(ReceiveVisualDataS2CPacket::handle)
                .add();
        INSTANCE.messageBuilder(SetDomainSizeC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(SetDomainSizeC2SPacket::new)
                .encoder(SetDomainSizeC2SPacket::encode)
                .consumerMainThread(SetDomainSizeC2SPacket::handle)
                .add();
        INSTANCE.messageBuilder(ScreenFlashS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(ScreenFlashS2CPacket::new)
                .encoder(ScreenFlashS2CPacket::encode)
                .consumerMainThread(ScreenFlashS2CPacket::handle)
                .add();
        INSTANCE.messageBuilder(QuestionCreatePactC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(QuestionCreatePactC2SPacket::new)
                .encoder(QuestionCreatePactC2SPacket::encode)
                .consumerMainThread(QuestionCreatePactC2SPacket::handle)
                .add();
        INSTANCE.messageBuilder(AddBindingVowC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(AddBindingVowC2SPacket::new)
                .encoder(AddBindingVowC2SPacket::encode)
                .consumerMainThread(AddBindingVowC2SPacket::handle)
                .add();
        INSTANCE.messageBuilder(RemoveBindingVowC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(RemoveBindingVowC2SPacket::new)
                .encoder(RemoveBindingVowC2SPacket::encode)
                .consumerMainThread(RemoveBindingVowC2SPacket::handle)
                .add();
        INSTANCE.messageBuilder(UnlockAbilityC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(UnlockAbilityC2SPacket::new)
                .encoder(UnlockAbilityC2SPacket::encode)
                .consumerMainThread(UnlockAbilityC2SPacket::handle)
                .add();
        INSTANCE.messageBuilder(AddChantC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(AddChantC2SPacket::new)
                .encoder(AddChantC2SPacket::encode)
                .consumerMainThread(AddChantC2SPacket::handle)
                .add();
        INSTANCE.messageBuilder(RemoveChantC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(RemoveChantC2SPacket::new)
                .encoder(RemoveChantC2SPacket::encode)
                .consumerMainThread(RemoveChantC2SPacket::handle)
                .add();
        INSTANCE.messageBuilder(ChangeOutputC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(ChangeOutputC2SPacket::new)
                .encoder(ChangeOutputC2SPacket::encode)
                .consumerMainThread(ChangeOutputC2SPacket::handle)
                .add();
        INSTANCE.messageBuilder(UncopyAbilityC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(UncopyAbilityC2SPacket::new)
                .encoder(UncopyAbilityC2SPacket::encode)
                .consumerMainThread(UncopyAbilityC2SPacket::handle)
                .add();
        INSTANCE.messageBuilder(SyncVisualDataS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(SyncVisualDataS2CPacket::new)
                .encoder(SyncVisualDataS2CPacket::encode)
                .consumerMainThread(SyncVisualDataS2CPacket::handle)
                .add();
        INSTANCE.messageBuilder(SyncMouthS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(SyncMouthS2CPacket::new)
                .encoder(SyncMouthS2CPacket::encode)
                .consumerMainThread(SyncMouthS2CPacket::handle)
                .add();
        INSTANCE.messageBuilder(TransformationRightClickC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(TransformationRightClickC2SPacket::new)
                .encoder(TransformationRightClickC2SPacket::encode)
                .consumerMainThread(TransformationRightClickC2SPacket::handle)
                .add();
        INSTANCE.messageBuilder(QuestionRemovePactC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(QuestionRemovePactC2SPacket::new)
                .encoder(QuestionRemovePactC2SPacket::encode)
                .consumerMainThread(QuestionRemovePactC2SPacket::handle)
                .add();
        INSTANCE.messageBuilder(NyoiStaffSummonLightningC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(NyoiStaffSummonLightningC2SPacket::new)
                .encoder(NyoiStaffSummonLightningC2SPacket::encode)
                .consumerMainThread(NyoiStaffSummonLightningC2SPacket::handle)
                .add();
        INSTANCE.messageBuilder(SetCursedEnergyColorC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(SetCursedEnergyColorC2SPacket::new)
                .encoder(SetCursedEnergyColorC2SPacket::encode)
                .consumerMainThread(SetCursedEnergyColorC2SPacket::handle)
                .add();
        INSTANCE.messageBuilder(UntriggerAbilityC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(UntriggerAbilityC2SPacket::new)
                .encoder(UntriggerAbilityC2SPacket::encode)
                .consumerMainThread(UntriggerAbilityC2SPacket::handle)
                .add();
        INSTANCE.messageBuilder(AddChantS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(AddChantS2CPacket::new)
                .encoder(AddChantS2CPacket::encode)
                .consumerMainThread(AddChantS2CPacket::handle)
                .add();
        INSTANCE.messageBuilder(RemoveChantS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(RemoveChantS2CPacket::new)
                .encoder(RemoveChantS2CPacket::encode)
                .consumerMainThread(RemoveChantS2CPacket::handle)
                .add();
        INSTANCE.messageBuilder(ClearChantsC2SPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(ClearChantsC2SPacket::new)
                .encoder(ClearChantsC2SPacket::encode)
                .consumerMainThread(ClearChantsC2SPacket::handle)
                .add();
    }

    public static <MSG> void broadcast(MSG message) {
        INSTANCE.send(PacketDistributor.ALL.noArg(), message);
    }

    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }

    public static <MSG> void sendToClient(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    public static <MSG> void sendTracking(MSG message, Entity entity) {
        INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), message);
    }
}
