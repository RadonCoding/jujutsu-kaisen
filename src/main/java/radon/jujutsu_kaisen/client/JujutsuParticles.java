package radon.jujutsu_kaisen.client;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import radon.jujutsu_kaisen.JujutsuKaisen;

public class JujutsuParticles {
    public static DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES,
            JujutsuKaisen.MODID);

    public static RegistryObject<SimpleParticleType> EMPTY = PARTICLES.register("empty", () ->
            new SimpleParticleType(false));
}
