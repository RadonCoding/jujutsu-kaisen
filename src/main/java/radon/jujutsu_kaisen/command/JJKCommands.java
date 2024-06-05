package radon.jujutsu_kaisen.command;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;


import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import radon.jujutsu_kaisen.JujutsuKaisen;

@EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class JJKCommands {
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        SetGradeCommand.register(event.getDispatcher());
        SetTechniqueCommand.register(event.getDispatcher());
        TraitCommand.register(event.getDispatcher());
        SetTypeCommand.register(event.getDispatcher());
        RerollCommand.register(event.getDispatcher());
        ResetSummonsCommand.register(event.getDispatcher());
        SetNatureCommand.register(event.getDispatcher());
        SetExperienceCommand.register(event.getDispatcher());
        PactCreationAcceptCommand.register(event.getDispatcher());
        PactCreationDeclineCommand.register(event.getDispatcher());
        PactRemovalAcceptCommand.register(event.getDispatcher());
        PactRemovalDeclineCommand.register(event.getDispatcher());
        AddAbilityPointsCommand.register(event.getDispatcher());
        AddSkillPointsCommand.register(event.getDispatcher());
        RefillCommand.register(event.getDispatcher());
        ResetSkillsCommand.register(event.getDispatcher());
        SetVeilOwnerCommand.register(event.getDispatcher());
        AddAdditionalCommand.register(event.getDispatcher());
    }
}