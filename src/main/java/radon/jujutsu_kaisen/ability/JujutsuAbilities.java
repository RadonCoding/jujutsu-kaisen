package radon.jujutsu_kaisen.ability;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.gojo.*;

import java.util.function.Supplier;

public class JujutsuAbilities {
    public static DeferredRegister<Ability> ABILITIES = DeferredRegister.create(
            new ResourceLocation(JujutsuKaisen.MOD_ID, "ability"), JujutsuKaisen.MOD_ID);
    public static Supplier<IForgeRegistry<Ability>> ABILITY_REGISTRY =
            ABILITIES.makeRegistry(RegistryBuilder::new);

    public static RegistryObject<Ability> INFINITY = ABILITIES.register("infinity", Infinity::new);
    public static RegistryObject<Ability> RED = ABILITIES.register("red", Red::new);
    public static RegistryObject<Ability> BLUE = ABILITIES.register("blue", Blue::new);
    public static RegistryObject<Ability> HOLLOW_PURPLE = ABILITIES.register("hollow_purple", HollowPurple::new);
    public static RegistryObject<Ability> UNLIMITED_VOID = ABILITIES.register("unlimited_void", UnlimitedVoid::new);

    public static ResourceLocation getKey(Ability ability) {
        return JujutsuAbilities.ABILITY_REGISTRY.get().getKey(ability);
    }

    public static Ability getValue(ResourceLocation key) {
        return JujutsuAbilities.ABILITY_REGISTRY.get().getValue(key);
    }
}
