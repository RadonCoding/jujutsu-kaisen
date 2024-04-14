package radon.jujutsu_kaisen.client.particle;

import com.mojang.serialization.Codec;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;

public class JJKParticles {
    public static DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE,
            JujutsuKaisen.MOD_ID);

    public static DeferredHolder<ParticleType<?>, SimpleParticleType> BLACK_FLASH = PARTICLES.register("black_flash", () ->
            new SimpleParticleType(true));
    public static DeferredHolder<ParticleType<?>, ParticleType<LightningParticle.LightningParticleOptions>> LIGHTNING = PARTICLES.register("lightning", () ->
            new ParticleType<>(false, LightningParticle.LightningParticleOptions.DESERIALIZER) {
                @Override
                public @NotNull Codec<LightningParticle.LightningParticleOptions> codec() {
                    return null;
                }
            });
    public static DeferredHolder<ParticleType<?>, ParticleType<TravelParticle.TravelParticleOptions>> TRAVEL = PARTICLES.register("travel", () ->
            new ParticleType<>(false, TravelParticle.TravelParticleOptions.DESERIALIZER) {
                @Override
                public @NotNull Codec<TravelParticle.TravelParticleOptions> codec() {
                    return null;
                }
            });
    public static DeferredHolder<ParticleType<?>, ParticleType<VaporParticle.VaporParticleOptions>> VAPOR = PARTICLES.register("vapor", () ->
            new ParticleType<>(false, VaporParticle.VaporParticleOptions.DESERIALIZER) {
                @Override
                public Codec<VaporParticle.VaporParticleOptions> codec() {
                    return null;
                }
            });
    public static DeferredHolder<ParticleType<?>, ParticleType<CursedEnergyParticle.CursedEnergyParticleOptions>> CURSED_ENERGY = PARTICLES.register("cursed_energy", () ->
            new ParticleType<>(false, CursedEnergyParticle.CursedEnergyParticleOptions.DESERIALIZER) {
                @Override
                public Codec<CursedEnergyParticle.CursedEnergyParticleOptions> codec() {
                    return null;
                }
            });
    public static DeferredHolder<ParticleType<?>, ParticleType<MirageParticle.MirageParticleOptions>> MIRAGE = PARTICLES.register("mirage", () ->
            new ParticleType<>(false, MirageParticle.MirageParticleOptions.DESERIALIZER) {
                @Override
                public Codec<MirageParticle.MirageParticleOptions> codec() {
                    return null;
                }
            });
    public static DeferredHolder<ParticleType<?>, ParticleType<ProjectionParticle.ProjectionParticleOptions>> PROJECTION = PARTICLES.register("projection", () ->
            new ParticleType<>(false, ProjectionParticle.ProjectionParticleOptions.DESERIALIZER) {
                @Override
                public Codec<ProjectionParticle.ProjectionParticleOptions> codec() {
                    return null;
                }
            });
    public static DeferredHolder<ParticleType<?>, ParticleType<EmittingLightningParticle.EmittingLightningParticleOptions>> EMITTING_LIGHTNING = PARTICLES.register("emitting_lightning", () ->
            new ParticleType<>(false, EmittingLightningParticle.EmittingLightningParticleOptions.DESERIALIZER) {
                @Override
                public Codec<EmittingLightningParticle.EmittingLightningParticleOptions> codec() {
                    return null;
                }
            });
    public static DeferredHolder<ParticleType<?>, ParticleType<FireParticle.FireParticleOptions>> FIRE = PARTICLES.register("fire", () ->
            new ParticleType<>(false, FireParticle.FireParticleOptions.DESERIALIZER) {
                @Override
                public Codec<FireParticle.FireParticleOptions> codec() {
                    return null;
                }
            });
    public static DeferredHolder<ParticleType<?>, ParticleType<BetterSmokeParticle.BetterSmokeParticleOptions>> SMOKE = PARTICLES.register("smoke", () ->
            new ParticleType<>(false, BetterSmokeParticle.BetterSmokeParticleOptions.DESERIALIZER) {
                @Override
                public Codec<BetterSmokeParticle.BetterSmokeParticleOptions> codec() {
                    return null;
                }
            });
    public static DeferredHolder<ParticleType<?>,SimpleParticleType> CURSED_SPEECH = PARTICLES.register("cursed_speech", () ->
            new SimpleParticleType(true));
    public static DeferredHolder<ParticleType<?>,SimpleParticleType> SLASH = PARTICLES.register("slash", () ->
            new SimpleParticleType(true));
}
