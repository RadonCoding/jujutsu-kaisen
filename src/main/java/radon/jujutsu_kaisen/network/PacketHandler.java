package radon.jujutsu_kaisen.network;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.network.packet.c2s.*;
import radon.jujutsu_kaisen.network.packet.s2c.*;

@EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class PacketHandler {
    @SubscribeEvent
    public static void onRegisterPayloadHandlers(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(JujutsuKaisen.MOD_ID)
                .versioned("1.2.3");

        // Clientbound packets
        registrar.playToClient(SyncSorcererDataS2CPacket.TYPE, SyncSorcererDataS2CPacket.STREAM_CODEC, SyncSorcererDataS2CPacket::handle);
        registrar.playToClient(SyncAbilityDataS2CPacket.TYPE, SyncAbilityDataS2CPacket.STREAM_CODEC, SyncAbilityDataS2CPacket::handle);
        registrar.playToClient(SyncChantDataS2CPacket.TYPE, SyncChantDataS2CPacket.STREAM_CODEC, SyncChantDataS2CPacket::handle);
        registrar.playToClient(SyncContractDataS2CPacket.TYPE, SyncContractDataS2CPacket.STREAM_CODEC, SyncContractDataS2CPacket::handle);
        registrar.playToClient(SyncTenShadowsDataS2CPacket.TYPE, SyncTenShadowsDataS2CPacket.STREAM_CODEC, SyncTenShadowsDataS2CPacket::handle);
        registrar.playToClient(SyncCurseManipulationDataS2CPacket.TYPE, SyncCurseManipulationDataS2CPacket.STREAM_CODEC, SyncCurseManipulationDataS2CPacket::handle);
        registrar.playToClient(SyncProjectionSorceryDataS2CPacket.TYPE, SyncProjectionSorceryDataS2CPacket.STREAM_CODEC, SyncProjectionSorceryDataS2CPacket::handle);
        registrar.playToClient(SyncIdleTransfigurationDataS2CPacket.TYPE, SyncIdleTransfigurationDataS2CPacket.STREAM_CODEC, SyncIdleTransfigurationDataS2CPacket::handle);
        registrar.playToClient(SyncMimicryDataS2CPacket.TYPE, SyncMimicryDataS2CPacket.STREAM_CODEC, SyncMimicryDataS2CPacket::handle);
        registrar.playToClient(SyncCursedSpeechDataS2CPacket.TYPE, SyncCursedSpeechDataS2CPacket.STREAM_CODEC, SyncCursedSpeechDataS2CPacket::handle);
        registrar.playToClient(SyncSkillDataSC2Packet.TYPE, SyncSkillDataSC2Packet.STREAM_CODEC, SyncSkillDataSC2Packet::handle);
        registrar.playToClient(SyncMissionEntityDataS2CPacket.TYPE, SyncMissionEntityDataS2CPacket.STREAM_CODEC, SyncMissionEntityDataS2CPacket::handle);
        registrar.playToClient(CameraShakeS2CPacket.TYPE, CameraShakeS2CPacket.STREAM_CODEC, CameraShakeS2CPacket::handle);
        registrar.playToClient(SetOverlayMessageS2CPacket.TYPE, SetOverlayMessageS2CPacket.STREAM_CODEC, SetOverlayMessageS2CPacket::handle);
        registrar.playToClient(SyncVisualDataS2CPacket.TYPE, SyncVisualDataS2CPacket.STREAM_CODEC, SyncVisualDataS2CPacket::handle);
        registrar.playToClient(ReceiveVisualDataS2CPacket.TYPE, ReceiveVisualDataS2CPacket.STREAM_CODEC, ReceiveVisualDataS2CPacket::handle);
        registrar.playToClient(ScreenFlashS2CPacket.TYPE, ScreenFlashS2CPacket.STREAM_CODEC, ScreenFlashS2CPacket::handle);
        registrar.playToClient(SyncMouthS2CPacket.TYPE, SyncMouthS2CPacket.STREAM_CODEC, SyncMouthS2CPacket::handle);
        registrar.playToClient(AddChantS2CPacket.TYPE, AddChantS2CPacket.STREAM_CODEC, AddChantS2CPacket::handle);
        registrar.playToClient(RemoveChantS2CPacket.TYPE, RemoveChantS2CPacket.STREAM_CODEC, RemoveChantS2CPacket::handle);
        registrar.playToClient(TriggerAbilityS2CPacket.TYPE, TriggerAbilityS2CPacket.STREAM_CODEC, TriggerAbilityS2CPacket::handle);
        registrar.playToClient(OpenMissionScreenS2CPacket.TYPE, OpenMissionScreenS2CPacket.STREAM_CODEC, OpenMissionScreenS2CPacket::handle);
        registrar.playToClient(SyncMissionLevelDataS2CPacket.TYPE, SyncMissionLevelDataS2CPacket.STREAM_CODEC, SyncMissionLevelDataS2CPacket::handle);
        registrar.playToClient(SyncMissionS2CPacket.TYPE, SyncMissionS2CPacket.STREAM_CODEC, SyncMissionS2CPacket::handle);
        registrar.playToClient(AddDimensionS2CPacket.TYPE, AddDimensionS2CPacket.STREAM_CODEC, AddDimensionS2CPacket::handle);
        registrar.playToClient(RemoveDimensionS2CPacket.TYPE, RemoveDimensionS2CPacket.STREAM_CODEC, RemoveDimensionS2CPacket::handle);
        registrar.playToClient(SyncDomainDataS2CPacket.TYPE, SyncDomainDataS2CPacket.STREAM_CODEC, SyncDomainDataS2CPacket::handle);
        registrar.playToClient(UpdateDomainInfoS2CPacket.TYPE, UpdateDomainInfoS2CPacket.STREAM_CODEC, UpdateDomainInfoS2CPacket::handle);
        registrar.playToClient(RemoveDomainInfoS2CPacket.TYPE, RemoveDomainInfoS2CPacket.STREAM_CODEC, RemoveDomainInfoS2CPacket::handle);

        // Serverbound packets
        registrar.playToServer(TriggerAbilityC2SPacket.TYPE, TriggerAbilityC2SPacket.STREAM_CODEC, TriggerAbilityC2SPacket::handle);
        registrar.playToServer(OpenInventoryCurseC2SPacket.TYPE, OpenInventoryCurseC2SPacket.STREAM_CODEC, OpenInventoryCurseC2SPacket::handle);
        registrar.playToServer(CommandableTargetC2SPacket.TYPE, CommandableTargetC2SPacket.STREAM_CODEC, CommandableTargetC2SPacket::handle);
        registrar.playToServer(JumpInputListenerC2SPacket.TYPE, JumpInputListenerC2SPacket.STREAM_CODEC, JumpInputListenerC2SPacket::handle);
        registrar.playToServer(RightClickInputListenerC2SPacket.TYPE, RightClickInputListenerC2SPacket.STREAM_CODEC, RightClickInputListenerC2SPacket::handle);
        registrar.playToServer(SetVeilSizeC2SPacket.TYPE, SetVeilSizeC2SPacket.STREAM_CODEC, SetVeilSizeC2SPacket::handle);
        registrar.playToServer(RequestBountyCostC2SPacket.TYPE, RequestBountyCostC2SPacket.STREAM_CODEC, RequestBountyCostC2SPacket::handle);
        registrar.playToServer(SetTojiBountyC2SPacket.TYPE, SetTojiBountyC2SPacket.STREAM_CODEC, SetTojiBountyC2SPacket::handle);
        registrar.playToServer(ScissorsAnswerC2SPacket.TYPE, ScissorsAnswerC2SPacket.STREAM_CODEC, ScissorsAnswerC2SPacket::handle);
        registrar.playToServer(ShadowInventoryTakeC2SPacket.TYPE, ShadowInventoryTakeC2SPacket.STREAM_CODEC, ShadowInventoryTakeC2SPacket::handle);
        registrar.playToServer(CurseSummonC2SPacket.TYPE, CurseSummonC2SPacket.STREAM_CODEC, CurseSummonC2SPacket::handle);
        registrar.playToServer(SetCopiedC2SPacket.TYPE, SetCopiedC2SPacket.STREAM_CODEC, SetCopiedC2SPacket::handle);
        registrar.playToServer(SetAbsorbedC2SPacket.TYPE, SetAbsorbedC2SPacket.STREAM_CODEC, SetAbsorbedC2SPacket::handle);
        registrar.playToServer(SetAdditionalC2SPacket.TYPE, SetAdditionalC2SPacket.STREAM_CODEC, SetAdditionalC2SPacket::handle);
        registrar.playToServer(RequestVisualDataC2SPacket.TYPE, RequestVisualDataC2SPacket.STREAM_CODEC, RequestVisualDataC2SPacket::handle);
        registrar.playToServer(QuestionCreatePactC2SPacket.TYPE, QuestionCreatePactC2SPacket.STREAM_CODEC, QuestionCreatePactC2SPacket::handle);
        registrar.playToServer(AddBindingVowC2SPacket.TYPE, AddBindingVowC2SPacket.STREAM_CODEC, AddBindingVowC2SPacket::handle);
        registrar.playToServer(RemoveBindingVowC2SPacket.TYPE, RemoveBindingVowC2SPacket.STREAM_CODEC, RemoveBindingVowC2SPacket::handle);
        registrar.playToServer(UnlockAbilityC2SPacket.TYPE, UnlockAbilityC2SPacket.STREAM_CODEC, UnlockAbilityC2SPacket::handle);
        registrar.playToServer(AddChantC2SPacket.TYPE, AddChantC2SPacket.STREAM_CODEC, AddChantC2SPacket::handle);
        registrar.playToServer(RemoveChantC2SPacket.TYPE, RemoveChantC2SPacket.STREAM_CODEC, RemoveChantC2SPacket::handle);
        registrar.playToServer(ChangeOutputC2SPacket.TYPE, ChangeOutputC2SPacket.STREAM_CODEC, ChangeOutputC2SPacket::handle);
        registrar.playToServer(UncopyC2SPacket.TYPE, UncopyC2SPacket.STREAM_CODEC, UncopyC2SPacket::handle);
        registrar.playToServer(RemoveAdditionalC2SPacket.TYPE, RemoveAdditionalC2SPacket.STREAM_CODEC, RemoveAdditionalC2SPacket::handle);
        registrar.playToServer(QuestionRemovePactC2SPacket.TYPE, QuestionRemovePactC2SPacket.STREAM_CODEC, QuestionRemovePactC2SPacket::handle);
        registrar.playToServer(NyoiStaffSummonLightningC2SPacket.TYPE, NyoiStaffSummonLightningC2SPacket.STREAM_CODEC, NyoiStaffSummonLightningC2SPacket::handle);
        registrar.playToServer(SetCursedEnergyColorC2SPacket.TYPE, SetCursedEnergyColorC2SPacket.STREAM_CODEC, SetCursedEnergyColorC2SPacket::handle);
        registrar.playToServer(UntriggerAbilityC2SPacket.TYPE, UntriggerAbilityC2SPacket.STREAM_CODEC, UntriggerAbilityC2SPacket::handle);
        registrar.playToServer(ClearChantsS2CPacket.TYPE, ClearChantsS2CPacket.STREAM_CODEC, ClearChantsS2CPacket::handle);
        registrar.playToServer(TransformationRightClickC2SPacket.TYPE, TransformationRightClickC2SPacket.STREAM_CODEC, TransformationRightClickC2SPacket::handle);
        registrar.playToServer(IncreaseSkillC2SPacket.TYPE, IncreaseSkillC2SPacket.STREAM_CODEC, IncreaseSkillC2SPacket::handle);
        registrar.playToServer(SearchForMissionsC2SPacket.TYPE, SearchForMissionsC2SPacket.STREAM_CODEC, SearchForMissionsC2SPacket::handle);
        registrar.playToServer(AcceptMissionC2SPacket.TYPE, AcceptMissionC2SPacket.STREAM_CODEC, AcceptMissionC2SPacket::handle);
    }
}
