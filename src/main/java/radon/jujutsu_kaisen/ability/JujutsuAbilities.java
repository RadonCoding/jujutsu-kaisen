package radon.jujutsu_kaisen.ability;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;
import radon.jujutsu_kaisen.JujutsuKaisen;
import net.minecraftforge.registries.RegistryBuilder;
import radon.jujutsu_kaisen.ability.gojo.Infinity;
import radon.jujutsu_kaisen.ability.gojo.Red;

import java.util.function.Supplier;

public class JujutsuAbilities {
    public static DeferredRegister<Ability> ABILITIES = DeferredRegister.create(
            new ResourceLocation(JujutsuKaisen.MODID, "ability"), JujutsuKaisen.MODID);
    public static Supplier<IForgeRegistry<Ability>> ABILITY_REGISTRY =
            ABILITIES.makeRegistry(RegistryBuilder::new);

    public static RegistryObject<Ability> INFINITY = ABILITIES.register("infinity", Infinity::new);
    public static RegistryObject<Ability> RED = ABILITIES.register("red", Red::new);

    public static ResourceLocation getKey(Ability ability) {
        return JujutsuAbilities.ABILITY_REGISTRY.get().getKey(ability);
    }

    public static Ability getValue(ResourceLocation key) {
        return JujutsuAbilities.ABILITY_REGISTRY.get().getValue(key);
    }
}
