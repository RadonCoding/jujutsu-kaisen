package radon.jujutsu_kaisen.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.network.packet.c2s.*;
import radon.jujutsu_kaisen.network.packet.s2c.*;

@Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class PacketHandler {
    @SubscribeEvent
    public static void onRegisterPayloadHandler(RegisterPayloadHandlerEvent event) {
        IPayloadRegistrar registrar = event.registrar(JujutsuKaisen.MOD_ID)
                .versioned("1.2.3");

        // Clietbound packets
        registrar.configuration(SyncSorcererDataS2CPacket.IDENTIFIER, SyncSorcererDataS2CPacket::new, SyncSorcererDataS2CPacket::handle);
        registrar.configuration(SyncTenShadowsDataS2CPacket.IDENTIFIER, SyncTenShadowsDataS2CPacket::new, SyncTenShadowsDataS2CPacket::handle);
        registrar.configuration(SyncProjectionSorceryDataS2CPacket.IDENTIFIER, SyncProjectionSorceryDataS2CPacket::new, SyncProjectionSorceryDataS2CPacket::handle);
        registrar.configuration(SyncCurseManipulationDataS2CPacket.IDENTIFIER, SyncCurseManipulationDataS2CPacket::new, SyncCurseManipulationDataS2CPacket::handle);
        registrar.configuration(CameraShakeS2CPacket.IDENTIFIER, CameraShakeS2CPacket::new, CameraShakeS2CPacket::handle);
        registrar.configuration(SetOverlayMessageS2CPacket.IDENTIFIER, SetOverlayMessageS2CPacket::new, SetOverlayMessageS2CPacket::handle);
        registrar.configuration(SetCostS2CPacket.IDENTIFIER, SetCostS2CPacket::new, SetCostS2CPacket::handle);
        registrar.configuration(ReceiveVisualDataS2CPacket.IDENTIFIER, ReceiveVisualDataS2CPacket::new, ReceiveVisualDataS2CPacket::handle);
        registrar.configuration(ScreenFlashS2CPacket.IDENTIFIER, ScreenFlashS2CPacket::new, ScreenFlashS2CPacket::handle);
        registrar.configuration(SyncMouthS2CPacket.IDENTIFIER, SyncMouthS2CPacket::new, SyncMouthS2CPacket::handle);
        registrar.configuration(AddChantS2CPacket.IDENTIFIER, AddChantS2CPacket::new, AddChantS2CPacket::handle);
        registrar.configuration(RemoveChantS2CPacket.IDENTIFIER, RemoveChantS2CPacket::new, RemoveChantS2CPacket::handle);

        // Serverbound packets
        registrar.configuration(TriggerAbilityC2SPacket.IDENTIFIER, TriggerAbilityC2SPacket::new, TriggerAbilityC2SPacket::handle);
        registrar.configuration(OpenInventoryCurseC2SPacket.IDENTIFIER, OpenInventoryCurseC2SPacket::new, OpenInventoryCurseC2SPacket::handle);
        registrar.configuration(CommandableTargetC2SPacket.IDENTIFIER, CommandableTargetC2SPacket::new, CommandableTargetC2SPacket::handle);
        registrar.configuration(JumpInputListenerC2SPacket.IDENTIFIER, JumpInputListenerC2SPacket::new, JumpInputListenerC2SPacket::handle);
        registrar.configuration(RightClickInputListenerC2SPacket.IDENTIFIER, RightClickInputListenerC2SPacket::new, RightClickInputListenerC2SPacket::handle);
        registrar.configuration(SetSizeC2SPacket.IDENTIFIER, SetSizeC2SPacket::new, SetSizeC2SPacket::handle);
        registrar.configuration(RequestCostC2SPacket.IDENTIFIER, RequestCostC2SPacket::new, RequestCostC2SPacket::handle);
        registrar.configuration(SetTojiBountyC2SPacket.IDENTIFIER, SetTojiBountyC2SPacket::new, SetTojiBountyC2SPacket::handle);
        registrar.configuration(KuchisakeOnnaAnswerC2SPacket.IDENTIFIER, KuchisakeOnnaAnswerC2SPacket::new, KuchisakeOnnaAnswerC2SPacket::handle);
        registrar.configuration(ShadowInventoryTakeC2SPacket.IDENTIFIER, ShadowInventoryTakeC2SPacket::new, ShadowInventoryTakeC2SPacket::handle);
        registrar.configuration(CurseSummonC2SPacket.IDENTIFIER, CurseSummonC2SPacket::new, CurseSummonC2SPacket::handle);
        registrar.configuration(SetCopiedC2SPacket.IDENTIFIER, SetCopiedC2SPacket::new, SetCopiedC2SPacket::handle);
        registrar.configuration(SetAbsorbedC2SPacket.IDENTIFIER, SetAbsorbedC2SPacket::new, SetAbsorbedC2SPacket::handle);
        registrar.configuration(RequestVisualDataC2SPacket.IDENTIFIER, RequestVisualDataC2SPacket::new, RequestVisualDataC2SPacket::handle);
        registrar.configuration(SetDomainSizeC2SPacket.IDENTIFIER, SetDomainSizeC2SPacket::new, SetDomainSizeC2SPacket::handle);
        registrar.configuration(QuestionCreatePactC2SPacket.IDENTIFIER, QuestionCreatePactC2SPacket::new, QuestionCreatePactC2SPacket::handle);
        registrar.configuration(AddBindingVowC2SPacket.IDENTIFIER, AddBindingVowC2SPacket::new, AddBindingVowC2SPacket::handle);
        registrar.configuration(RemoveBindingVowC2SPacket.IDENTIFIER, RemoveBindingVowC2SPacket::new, RemoveBindingVowC2SPacket::handle);
        registrar.configuration(UnlockAbilityC2SPacket.IDENTIFIER, UnlockAbilityC2SPacket::new, UnlockAbilityC2SPacket::handle);
        registrar.configuration(AddChantC2SPacket.IDENTIFIER, AddChantC2SPacket::new, AddChantC2SPacket::handle);
        registrar.configuration(RemoveChantC2SPacket.IDENTIFIER, RemoveChantC2SPacket::new, RemoveChantC2SPacket::handle);
        registrar.configuration(ChangeOutputC2SPacket.IDENTIFIER, ChangeOutputC2SPacket::new, ChangeOutputC2SPacket::handle);
        registrar.configuration(UncopyAbilityC2SPacket.IDENTIFIER, UncopyAbilityC2SPacket::new, UncopyAbilityC2SPacket::handle);
        registrar.configuration(QuestionRemovePactC2SPacket.IDENTIFIER, QuestionRemovePactC2SPacket::new, QuestionRemovePactC2SPacket::handle);
        registrar.configuration(NyoiStaffSummonLightningC2SPacket.IDENTIFIER, NyoiStaffSummonLightningC2SPacket::new, NyoiStaffSummonLightningC2SPacket::handle);
        registrar.configuration(SetCursedEnergyColorC2SPacket.IDENTIFIER, SetCursedEnergyColorC2SPacket::new, SetCursedEnergyColorC2SPacket::handle);
        registrar.configuration(UntriggerAbilityC2SPacket.IDENTIFIER, UntriggerAbilityC2SPacket::new, UntriggerAbilityC2SPacket::handle);
        registrar.configuration(ClearChantsC2SPacket.IDENTIFIER, ClearChantsC2SPacket::new, ClearChantsC2SPacket::handle);
    }

    public static <MSG extends CustomPacketPayload> void broadcast(MSG message) {
        PacketDistributor.ALL.noArg().send(message);
    }

    public static <MSG extends CustomPacketPayload> void sendToServer(MSG message) {
        PacketDistributor.SERVER.noArg().send(message);
    }

    public static <MSG extends CustomPacketPayload> void sendToClient(MSG message, ServerPlayer player) {
        PacketDistributor.PLAYER.with(player).send(message);
    }
}
