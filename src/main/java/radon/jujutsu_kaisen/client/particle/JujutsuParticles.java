package radon.jujutsu_kaisen.client.particle;

import com.mojang.serialization.Codec;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;

public class JujutsuParticles {
    public static DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES,
            JujutsuKaisen.MOD_ID);

    public static RegistryObject<ParticleType<SpinningParticle.SpinningParticleOptions>> SPINNING = PARTICLES.register("spinning", () ->
            new ParticleType<>(false, SpinningParticle.SpinningParticleOptions.DESERIALIZER) {
                @Override
                public @NotNull Codec<SpinningParticle.SpinningParticleOptions> codec() {
                    return SpinningParticle.SpinningParticleOptions.CODEC;
                }
            });
}
