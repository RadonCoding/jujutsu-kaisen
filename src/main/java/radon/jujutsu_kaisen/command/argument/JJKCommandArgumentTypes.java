package radon.jujutsu_kaisen.command.argument;


import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import radon.jujutsu_kaisen.JujutsuKaisen;

public class JJKCommandArgumentTypes {
    public static final DeferredRegister<ArgumentTypeInfo<?, ?>> COMMAND_ARGUMENT_TYPES = DeferredRegister.create(Registries.COMMAND_ARGUMENT_TYPE, JujutsuKaisen.MOD_ID);

    public static final DeferredHolder<ArgumentTypeInfo<?, ?>, ArgumentTypeInfo<?, ?>> CURSED_TECHNIQUE = COMMAND_ARGUMENT_TYPES.register("cursed_technique", () ->
            ArgumentTypeInfos.registerByClass(CursedTechniqueArgument.class, SingletonArgumentInfo.contextFree(CursedTechniqueArgument::cursedTechnique)));
    public static final DeferredHolder<ArgumentTypeInfo<?, ?>, ArgumentTypeInfo<?, ?>> PACT = COMMAND_ARGUMENT_TYPES.register("pact", () ->
            ArgumentTypeInfos.registerByClass(PactArgument.class, SingletonArgumentInfo.contextFree(PactArgument::pact)));
}
