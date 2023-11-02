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

    public static RegistryObject<SimpleParticleType> BLACK_FLASH = PARTICLES.register("black_flash", () ->
            new SimpleParticleType(true));
    public static RegistryObject<ParticleType<LightningParticle.LightningParticleOptions>> LIGHTNING = PARTICLES.register("lightning", () ->
            new ParticleType<>(false, LightningParticle.LightningParticleOptions.DESERIALIZER) {
                @Override
                public @NotNull Codec<LightningParticle.LightningParticleOptions> codec() {
                    return null;
                }
            });
    public static RegistryObject<ParticleType<TravelParticle.TravelParticleOptions>> TRAVEL = PARTICLES.register("travel", () ->
            new ParticleType<>(false, TravelParticle.TravelParticleOptions.DESERIALIZER) {
                @Override
                public @NotNull Codec<TravelParticle.TravelParticleOptions> codec() {
                    return null;
                }
            });
    public static RegistryObject<ParticleType<VaporParticle.VaporParticleOptions>> VAPOR = PARTICLES.register("vapor", () ->
            new ParticleType<>(false, VaporParticle.VaporParticleOptions.DESERIALIZER) {
                @Override
                public Codec<VaporParticle.VaporParticleOptions> codec() {
                    return null;
                }
            });
    public static RegistryObject<ParticleType<CursedEnergyParticle.CursedEnergyParticleOptions>> CURSED_ENERGY = PARTICLES.register("cursed_energy", () ->
            new ParticleType<>(false, CursedEnergyParticle.CursedEnergyParticleOptions.DESERIALIZER) {
                @Override
                public Codec<CursedEnergyParticle.CursedEnergyParticleOptions> codec() {
                    return null;
                }
            });
    public static RegistryObject<ParticleType<MirageParticle.MirageParticleOptions>> MIRAGE = PARTICLES.register("mirage", () ->
            new ParticleType<>(false, MirageParticle.MirageParticleOptions.DESERIALIZER) {
                @Override
                public Codec<MirageParticle.MirageParticleOptions> codec() {
                    return null;
                }
            });
    public static RegistryObject<SimpleParticleType> CURSED_SPEECH = PARTICLES.register("cursed_speech", () ->
            new SimpleParticleType(true));
    public static RegistryObject<SimpleParticleType> BLOOD = PARTICLES.register("blood", () ->
            new SimpleParticleType(true));
}
