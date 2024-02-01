package radon.jujutsu_kaisen.command;

import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import radon.jujutsu_kaisen.JujutsuKaisen;

public class JJKCommandArgumentTypes {
    public static final DeferredRegister<ArgumentTypeInfo<?, ?>> COMMAND_ARGUMENT_TYPES = DeferredRegister.create(ForgeRegistries.COMMAND_ARGUMENT_TYPES, JujutsuKaisen.MOD_ID);

    public static final RegistryObject<ArgumentTypeInfo<?, ?>> CURSED_TECHNIQUE = COMMAND_ARGUMENT_TYPES.register("cursed_technique", () ->
            ArgumentTypeInfos.registerByClass(CursedTechniqueArgument.class, SingletonArgumentInfo.contextFree(CursedTechniqueArgument::cursedTechnique)));
}
