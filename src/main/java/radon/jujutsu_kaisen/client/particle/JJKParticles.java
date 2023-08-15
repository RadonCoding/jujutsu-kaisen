package radon.jujutsu_kaisen.client.particle;

import com.mojang.serialization.Codec;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;

public class JJKParticles {
    public static DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES,
            JujutsuKaisen.MOD_ID);

    public static RegistryObject<SimpleParticleType> CURSED_ENERGY = PARTICLES.register("cursed_energy", () ->
            new SimpleParticleType(true));
    public static RegistryObject<SimpleParticleType> BLACK_FLASH = PARTICLES.register("black_flash", () ->
            new SimpleParticleType(true));
    public static RegistryObject<SimpleParticleType> LIGHTNING = PARTICLES.register("lightning", () ->
            new SimpleParticleType(true));

    public static RegistryObject<ParticleType<TravelParticle.TravelParticleOptions>> TRAVEL = PARTICLES.register("travel", () ->
            new ParticleType<>(false, TravelParticle.TravelParticleOptions.DESERIALIZER) {
                @Override
                public @NotNull Codec<TravelParticle.TravelParticleOptions> codec() {
                    return TravelParticle.TravelParticleOptions.CODEC;
                }
            });
}
