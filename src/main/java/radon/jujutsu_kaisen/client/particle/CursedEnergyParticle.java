package radon.jujutsu_kaisen.client.particle;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import radon.jujutsu_kaisen.client.visual.ClientVisualHandler;
import radon.jujutsu_kaisen.data.sorcerer.Trait;

import java.util.Locale;

public class CursedEnergyParticle extends TextureSheetParticle {
    private final SpriteSet sprites;

    protected CursedEnergyParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, CursedEnergyParticle.CursedEnergyParticleOptions options, SpriteSet pSprites) {
        super(pLevel, pX, pY, pZ);

        this.quadSize = Math.max(options.scalar(), (this.random.nextFloat() - 0.5F) * options.scalar());
        this.lifetime = options.lifetime();

        this.xd = pXSpeed;
        this.yd = pYSpeed;
        this.zd = pZSpeed;

        Vector3f color = options.color();
        this.rCol = color.x;
        this.gCol = color.y;
        this.bCol = color.z;

        this.alpha = options.opacity();

        this.hasPhysics = false;

        this.sprites = pSprites;
        this.setSpriteFromAge(this.sprites);
    }

    @Override
    public void tick() {
        super.tick();

        this.setSpriteFromAge(this.sprites);
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return  JJKParticleRenderTypes.GLOW;
    }

    public record CursedEnergyParticleOptions(Vector3f color, float scalar, float opacity, int lifetime) implements ParticleOptions {
        public static Deserializer<CursedEnergyParticleOptions> DESERIALIZER = new Deserializer<>() {
            public @NotNull CursedEnergyParticleOptions fromCommand(@NotNull ParticleType<CursedEnergyParticleOptions> type, @NotNull StringReader reader) throws CommandSyntaxException {
                Vector3f color = CursedEnergyParticleOptions.readColorVector3f(reader);
                reader.expect(' ');
                return new CursedEnergyParticleOptions(color, reader.readFloat(), reader.readFloat(), reader.readInt());
            }

            public @NotNull CursedEnergyParticleOptions fromNetwork(@NotNull ParticleType<CursedEnergyParticleOptions> type, @NotNull FriendlyByteBuf buf) {
                return new CursedEnergyParticleOptions(CursedEnergyParticleOptions.readColorFromNetwork(buf), buf.readFloat(), buf.readFloat(), buf.readInt());
            }
        };

        @Override
        public @NotNull ParticleType<?> getType() {
            return JJKParticles.CURSED_ENERGY.get();
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

        public static Vector3f readColorFromNetwork(FriendlyByteBuf buf) {
            return new Vector3f(buf.readFloat(), buf.readFloat(), buf.readFloat());
        }

        @Override
        public void writeToNetwork(FriendlyByteBuf buf) {
            buf.writeFloat(this.color.x);
            buf.writeFloat(this.color.y);
            buf.writeFloat(this.color.z);
            buf.writeFloat(this.scalar);
            buf.writeFloat(this.opacity);
            buf.writeInt(this.lifetime);
        }

        @Override
        public @NotNull String writeToString() {
            return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f %.2f %d", BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()),
                    this.color.x, this.color.y, this.color.z, this.scalar, this.opacity, this.lifetime);
        }
    }

    public static class Provider implements ParticleProvider<CursedEnergyParticleOptions> {
        private final SpriteSet sprites;

        public Provider(SpriteSet pSprites) {
            this.sprites = pSprites;
        }

        public Particle createParticle(@NotNull CursedEnergyParticleOptions pType, @NotNull ClientLevel pLevel, double pX, double pY, double pZ,
                                       double pXSpeed, double pYSpeed, double pZSpeed) {
            return new CursedEnergyParticle(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, pType, this.sprites);
        }
    }
}