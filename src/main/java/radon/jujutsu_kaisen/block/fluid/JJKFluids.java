package radon.jujutsu_kaisen.block.fluid;

import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import radon.jujutsu_kaisen.JujutsuKaisen;

public class JJKFluids {
    public static DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, JujutsuKaisen.MOD_ID);

    public static final RegistryObject<FlowingFluid> CHIMERA_SHADOW_GARDEN_SOURCE = FLUIDS.register("chimera_shadow_garden_source",
            () -> new ChimeraShadowGardenFluid.Source(JJKFluids.CHIMERA_SHADOW_GARDEN_PROPERTIES));
    public static final RegistryObject<FlowingFluid> CHIMERA_SHADOW_GARDEN_FLOWING = FLUIDS.register("chimera_shadow_garden_flowing",
            () -> new ChimeraShadowGardenFluid.Flowing(JJKFluids.CHIMERA_SHADOW_GARDEN_PROPERTIES));

    public static final ForgeFlowingFluid.Properties CHIMERA_SHADOW_GARDEN_PROPERTIES = new ForgeFlowingFluid.Properties(
            JJKFluidTypes.CHIMERA_SHADOW_GARDEN, CHIMERA_SHADOW_GARDEN_SOURCE, CHIMERA_SHADOW_GARDEN_FLOWING)
            .block(JJKBlocks.CHIMERA_SHADOW_GARDEN);
}
