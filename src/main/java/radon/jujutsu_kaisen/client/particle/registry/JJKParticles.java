package radon.jujutsu_kaisen.client.particle.registry;


import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.particle.*;

import java.util.function.Function;

public class JJKParticles {
    public static DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(Registries.PARTICLE_TYPE,
            JujutsuKaisen.MOD_ID);

    public static DeferredHolder<ParticleType<?>, SimpleParticleType> BLACK_FLASH = PARTICLES.register("black_flash", () ->
            new SimpleParticleType(true));
    public static DeferredHolder<ParticleType<?>, ParticleType<LightningParticle.Options>> LIGHTNING = register("lightning", false,
            type -> LightningParticle.Options.CODEC,
            type -> LightningParticle.Options.STREAM_CODEC);
    public static DeferredHolder<ParticleType<?>, ParticleType<TravelParticle.Options>> TRAVEL = register("travel", false,
            type -> TravelParticle.Options.CODEC,
            type -> TravelParticle.Options.STREAM_CODEC);
    public static DeferredHolder<ParticleType<?>, ParticleType<VaporParticle.Options>> VAPOR = register("vapor", false,
            type -> VaporParticle.Options.CODEC,
            type -> VaporParticle.Options.STREAM_CODEC);
    public static DeferredHolder<ParticleType<?>, ParticleType<CursedEnergyParticle.Options>> CURSED_ENERGY = register("cursed_energy", false,
            type -> CursedEnergyParticle.Options.CODEC,
            type -> CursedEnergyParticle.Options.STREAM_CODEC);
    public static DeferredHolder<ParticleType<?>, ParticleType<MirageParticle.Options>> MIRAGE = register("mirage", false,
            type -> MirageParticle.Options.CODEC,
            type -> MirageParticle.Options.STREAM_CODEC);
    public static DeferredHolder<ParticleType<?>, ParticleType<ProjectionParticle.Options>> PROJECTION = register("projection", false,
            type -> ProjectionParticle.Options.CODEC,
            type -> ProjectionParticle.Options.STREAM_CODEC);
    public static DeferredHolder<ParticleType<?>, ParticleType<EmittingLightningParticle.Options>> EMITTING_LIGHTNING = register("emitting_lightning", false,
            type -> EmittingLightningParticle.Options.CODEC,
            type -> EmittingLightningParticle.Options.STREAM_CODEC);
    public static DeferredHolder<ParticleType<?>, ParticleType<FireParticle.Options>> FIRE = register("fire", false,
            type -> FireParticle.Options.CODEC, type -> FireParticle.Options.STREAM_CODEC);
    public static DeferredHolder<ParticleType<?>, ParticleType<BetterSmokeParticle.Options>> SMOKE = register("smoke", false,
            type -> BetterSmokeParticle.Options.CODEC, type -> BetterSmokeParticle.Options.STREAM_CODEC);
    public static DeferredHolder<ParticleType<?>,SimpleParticleType> CURSED_SPEECH = PARTICLES.register("cursed_speech", () ->
            new SimpleParticleType(true));
    public static DeferredHolder<ParticleType<?>,SimpleParticleType> SLASH = PARTICLES.register("slash", () ->
            new SimpleParticleType(true));

    private static <T extends ParticleOptions> DeferredHolder<ParticleType<?>, ParticleType<T>> register(
            String pName,
            boolean pOverrideLimiter,
            final Function<ParticleType<T>, MapCodec<T>> pCodecGetter,
            final Function<ParticleType<T>, StreamCodec<? super RegistryFriendlyByteBuf, T>> pStreamCodecGetter
    ) {
        return PARTICLES.register(pName, () -> new ParticleType<T>(pOverrideLimiter) {
            @Override
            public @NotNull MapCodec<T> codec() {
                return pCodecGetter.apply(this);
            }

            @Override
            public @NotNull StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec() {
                return pStreamCodecGetter.apply(this);
            }
        });
    }
}
