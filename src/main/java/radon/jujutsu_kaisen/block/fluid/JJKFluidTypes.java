package radon.jujutsu_kaisen.block.fluid;


import net.minecraft.sounds.SoundEvents;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.block.base.JJKFluidType;

public class JJKFluidTypes {
    public static DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, JujutsuKaisen.MOD_ID);

    public static final DeferredHolder<FluidType, FluidType> CHIMERA_SHADOW_GARDEN = FLUID_TYPES.register("chimera_shadow_garden",
            () -> new JJKFluidType(0x141414, FluidType.Properties.create()
                    .canSwim(false)));
    public static final DeferredHolder<FluidType, FluidType> FAKE_WATER = FLUID_TYPES.register("fake_water",
            () -> new JJKFluidType(0xFF3F76E4, FluidType.Properties.create()
                    .fallDistanceModifier(0.0F)
                    .canExtinguish(true)
                    .canConvertToSource(true)
                    .supportsBoating(true)
                    .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                    .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
                    .sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH)
                    .canHydrate(true)));
}
