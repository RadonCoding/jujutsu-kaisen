package radon.jujutsu_kaisen.block;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.block.base.JJKFluidType;

public class JJKFluidTypes {
    public static final ResourceLocation WATER_STILL = new ResourceLocation("block/water_still");
    public static final ResourceLocation WATER_FLOWING = new ResourceLocation("block/water_flow");
    public static final ResourceLocation WATER_OVERLAY = new ResourceLocation("block/water_overlay");

    public static DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, JujutsuKaisen.MOD_ID);

    public static final RegistryObject<FluidType> CHIMERA_SHADOW_GARDEN = FLUID_TYPES.register("chimera_shadow_garden",
            () -> new JJKFluidType(WATER_STILL, WATER_FLOWING, WATER_OVERLAY, 0x2B2B2B,
                    Vec3.fromRGB24(0x000000).toVector3f(), FluidType.Properties.create()));
}
