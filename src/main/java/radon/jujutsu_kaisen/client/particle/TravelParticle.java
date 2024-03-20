package radon.jujutsu_kaisen.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.Locale;

public class TravelParticle extends TextureSheetParticle {
    private final SpriteSet sprites;
    private final Vec3 target;
    private final boolean glow;
    private final float opacity;

    protected TravelParticle(ClientLevel pLevel, double pX, double pY, double pZ, TravelParticleOptions options, SpriteSet pSprites) {
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

    public record TravelParticleOptions(Vector3f target, Vector3f color, float scalar, float opacity, boolean glow, int lifetime) implements ParticleOptions {
        public static Deserializer<TravelParticleOptions> DESERIALIZER = new Deserializer<>() {
            public @NotNull TravelParticleOptions fromCommand(@NotNull ParticleType<TravelParticleOptions> type, @NotNull StringReader reader) throws CommandSyntaxException {
                Vector3f target = TravelParticleOptions.readTargetVector3f(reader);
                reader.expect(' ');
                Vector3f color = TravelParticleOptions.readColorVector3f(reader);
                reader.expect(' ');
                return new TravelParticleOptions(target, color, reader.readFloat(), reader.readFloat(), reader.readBoolean(), reader.readInt());
            }

            public @NotNull TravelParticleOptions fromNetwork(@NotNull ParticleType<TravelParticleOptions> type, @NotNull FriendlyByteBuf buf) {
                return new TravelParticleOptions(readTargetFromNetwork(buf), readColorFromNetwork(buf), buf.readFloat(), buf.readFloat(), buf.readBoolean(), buf.readInt());
            }
        };

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

        public static Vector3f readTargetFromNetwork(FriendlyByteBuf buf) {
            return new Vector3f(buf.readFloat(), buf.readFloat(), buf.readFloat());
        }

        public static Vector3f readColorFromNetwork(FriendlyByteBuf buf) {
            return new Vector3f(buf.readFloat(), buf.readFloat(), buf.readFloat());
        }

        @Override
        public @NotNull ParticleType<?> getType() {
            return JJKParticles.TRAVEL.get();
        }

        @Override
        public void writeToNetwork(FriendlyByteBuf buf) {
            buf.writeFloat(this.target.x);
            buf.writeFloat(this.target.y);
            buf.writeFloat(this.target.z);
            buf.writeFloat(this.color.x);
            buf.writeFloat(this.color.y);
            buf.writeFloat(this.color.z);
            buf.writeFloat(this.scalar);
            buf.writeFloat(this.opacity);
            buf.writeBoolean(this.glow);
            buf.writeInt(this.lifetime);
        }

        @Override
        public @NotNull String writeToString() {
            return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f %.2f %.2f %.2f %.2f %b %d", BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()),
                    this.target.x, this.target.y, this.target.z, this.color.x, this.color.y, this.color.z, this.scalar, this.opacity, this.glow, this.lifetime);
        }
    }


    public static class Provider implements ParticleProvider<TravelParticleOptions> {
        private final SpriteSet sprites;

        public Provider(SpriteSet pSpriteSet) {
            this.sprites = pSpriteSet;
        }

        @Override
        public TravelParticle createParticle(@NotNull TravelParticleOptions options, @NotNull ClientLevel level, double x, double y, double z,
                                             double xSpeed, double ySpeed, double zSpeed) {
            return new TravelParticle(level, x, y, z, options, this.sprites);
        }
    }
}
