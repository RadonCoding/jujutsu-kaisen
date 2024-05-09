package radon.jujutsu_kaisen.client.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.Locale;

public class TravelParticle extends TextureSheetParticle {
    private final SpriteSet sprites;
    private final Vec3 target;
    private final boolean glow;
    private final float opacity;

    protected TravelParticle(ClientLevel pLevel, double pX, double pY, double pZ, Options options, SpriteSet pSprites) {
        super(pLevel, pX, pY, pZ);

        this.quadSize = Math.max(options.scalar(), (this.random.nextFloat() - 0.5F) * options.scalar());
        this.lifetime = options.lifetime();

        this.target = new Vec3(options.target());

        Vector3f color = options.color();
        this.rCol = color.x;
        this.gCol = color.y;
        this.bCol = color.z;

        this.opacity = options.opacity();

        this.alpha = this.opacity * (1.0F - ((float) this.age / this.lifetime));

        this.glow = options.glow();

        this.sprites = pSprites;

        this.setSprite(this.sprites.get(this.level.random));
    }

    @Override
    public void tick() {
        super.tick();

        this.alpha = this.opacity * (1.0F - ((float) this.age / this.lifetime));

        this.setSprite(this.sprites.get(this.level.random));

        Vec3 pos = new Vec3(this.x, this.y, this.z);
        Vec3 direction = this.target.subtract(pos);

        double factor = (double) this.age / this.lifetime;

        Vec3 newPos = pos.add(direction.scale(factor));
        this.setPos(newPos.x, newPos.y, newPos.z);
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return this.glow ? JJKParticleRenderTypes.GLOW : JJKParticleRenderTypes.TRANSLUCENT;
    }

    public record Options(Vector3f target, Vector3f color, float scalar, float opacity, boolean glow, int lifetime) implements ParticleOptions {
        public static final MapCodec<Options> CODEC = RecordCodecBuilder.mapCodec(
                builder -> builder.group(
                                ExtraCodecs.VECTOR3F.fieldOf("target").forGetter(options -> options.target),
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
                Options::target,
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

        public static Vector3f readTargetVector3f(StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            float f0 = reader.readFloat();
            reader.expect(' ');
            float f1 = reader.readFloat();
            reader.expect(' ');
            float f2 = reader.readFloat();
            return new Vector3f(f0, f1, f2);
        }

        public static Vector3f readColorVector3f(StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            float f0 = reader.readFloat();
            reader.expect(' ');
            float f1 = reader.readFloat();
            reader.expect(' ');
            float f2 = reader.readFloat();
            return new Vector3f(f0, f1, f2);
        }

        @Override
        public @NotNull ParticleType<?> getType() {
            return JJKParticles.TRAVEL.get();
        }
    }


    public static class Provider implements ParticleProvider<Options> {
        private final SpriteSet sprites;

        public Provider(SpriteSet pSpriteSet) {
            this.sprites = pSpriteSet;
        }

        @Override
        public TravelParticle createParticle(@NotNull TravelParticle.Options options, @NotNull ClientLevel level, double x, double y, double z,
                                             double xSpeed, double ySpeed, double zSpeed) {
            return new TravelParticle(level, x, y, z, options, this.sprites);
        }
    }
}
