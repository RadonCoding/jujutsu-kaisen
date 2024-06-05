package radon.jujutsu_kaisen.client.particle;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.Locale;

public class VaporParticle<T extends VaporParticle.Options> extends TextureSheetParticle {
    private final SpriteSet sprites;
    private final boolean glow;

    protected VaporParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, T options, SpriteSet pSprites) {
        super(pLevel, pX, pY, pZ);

        this.lifetime = options.lifetime();

        this.xd = pXSpeed;
        this.yd = pYSpeed;
        this.zd = pZSpeed;

        Vector3f color = options.color();
        this.rCol = color.x;
        this.gCol = color.y;
        this.bCol = color.z;

        this.alpha = options.opacity();

        this.glow = options.glow();

        this.quadSize = Math.max(options.scalar(), (this.random.nextFloat() - 0.5F) * options.scalar());

        this.sprites = pSprites;
        this.setSprite(this.sprites.get(this.random));

        this.hasPhysics = false;
    }

    @Override
    public void tick() {
        super.tick();

        this.setSprite(this.sprites.get(this.random));
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return this.glow ? JJKParticleRenderTypes.GLOW : JJKParticleRenderTypes.TRANSLUCENT;
    }

    public record Options(Vector3f color, float scalar, float opacity, boolean glow, int lifetime) implements ParticleOptions {
        public static final MapCodec<Options> CODEC = RecordCodecBuilder.mapCodec(
                builder -> builder.group(
                                ExtraCodecs.VECTOR3F.fieldOf("color").forGetter(options -> options.color),
                                Codec.FLOAT.fieldOf("scalar").forGetter(options -> options.scalar),
                                Codec.FLOAT.fieldOf("opacity").forGetter(options -> options.opacity),
                                Codec.BOOL.fieldOf("glow").forGetter(options -> options.glow),
                                Codec.INT.fieldOf("lifetime").forGetter(options -> options.lifetime)
                        )
                        .apply(builder, Options::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, Options> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.VECTOR3F,
                Options::color,
                ByteBufCodecs.FLOAT,
                Options::scalar,
                ByteBufCodecs.FLOAT,
                Options::opacity,
                ByteBufCodecs.BOOL,
                Options::glow,
                ByteBufCodecs.INT,
                Options::lifetime,
                Options::new
        );

        @Override
        public @NotNull ParticleType<?> getType() {
            return JJKParticles.VAPOR.get();
        }
    }

    public static class Provider implements ParticleProvider<Options> {
        private final SpriteSet sprites;

        public Provider(SpriteSet pSprites) {
            this.sprites = pSprites;
        }

        public Particle createParticle(@NotNull VaporParticle.Options pType, @NotNull ClientLevel pLevel, double pX, double pY, double pZ,
                                       double pXSpeed, double pYSpeed, double pZSpeed) {
            return new VaporParticle<>(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, pType, this.sprites);
        }
    }
}