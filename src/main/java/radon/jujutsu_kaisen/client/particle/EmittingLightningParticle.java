package radon.jujutsu_kaisen.client.particle;


import net.minecraft.client.particle.*;
import radon.jujutsu_kaisen.client.particle.registry.JJKParticles;
import net.minecraft.world.phys.AABB;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.joml.Vector4f;
import radon.jujutsu_kaisen.client.render.entity.effect.BoltEffect;
import radon.jujutsu_kaisen.client.render.entity.effect.BoltRenderer;
import radon.jujutsu_kaisen.network.codec.JJKByteBufCodecs;

public class EmittingLightningParticle extends Particle {
    private final Vector3f color;
    private final Vec3 direction;
    private final float scale;

    private final BoltRenderer renderer;

    protected EmittingLightningParticle(ClientLevel pLevel, double pX, double pY, double pZ, Options options) {
        super(pLevel, pX, pY, pZ);

        this.color = options.color();

        this.direction = options.direction();

        this.scale = Math.max(options.scalar(), (this.random.nextFloat() - 0.5F) * options.scalar());
        this.setSize(this.scale, this.scale);

        this.lifetime = options.lifetime();

        this.renderer = new BoltRenderer();
    }

    @Override
    public void render(@NotNull VertexConsumer pBuffer, @NotNull Camera pRenderInfo, float pPartialTicks) {
        PoseStack poseStack = new PoseStack();

        Vec3 offset = this.getPos()
                .add(this.direction.scale(this.random.nextFloat() * this.scale));

        poseStack.pushPose();

        double d0 = Mth.lerp(pPartialTicks, this.xo, this.x);
        double d1 = Mth.lerp(pPartialTicks, this.yo, this.y);
        double d2 = Mth.lerp(pPartialTicks, this.zo, this.z);

        Vec3 cam = pRenderInfo.getPosition();
        poseStack.translate(d0 - cam.x, d1 - cam.y, d2 - cam.z);

        Vec3 start = new Vec3(this.x, this.y, this.z);
        Vec3 end = new Vec3(offset.x, offset.y, offset.z);
        BoltEffect.BoltRenderInfo info = new BoltEffect.BoltRenderInfo(0.0F, 0.075F, 0.0F, 0.0F,
                new Vector4f(this.color.x, this.color.y, this.color.z, 0.8F), 1.8F);
        BoltEffect bolt = new BoltEffect(info, start, end, (int) (Math.sqrt(start.distanceTo(end) * 100)))
                .size(0.05F)
                .lifespan(this.lifetime - this.age)
                .fade(BoltEffect.FadeFunction.fade(0.5F))
                .spawn(BoltEffect.SpawnFunction.CONSECUTIVE);
        this.renderer.update(null, bolt, pPartialTicks);
        poseStack.translate(-this.x, -this.y, -this.z);
        this.renderer.render(pPartialTicks, poseStack, Minecraft.getInstance().renderBuffers().bufferSource());
        Minecraft.getInstance().renderBuffers().bufferSource().endBatch();

        poseStack.popPose();
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }

    public record Options(Vector3f color, Vec3 direction, float scalar, int lifetime) implements ParticleOptions {
        public static final MapCodec<Options> CODEC = RecordCodecBuilder.mapCodec(
                builder -> builder.group(
                                ExtraCodecs.VECTOR3F.fieldOf("color").forGetter(options -> options.color),
                                Vec3.CODEC.fieldOf("direction").forGetter(options -> options.direction),
                                Codec.FLOAT.fieldOf("scalar").forGetter(options -> options.scalar),
                                Codec.INT.fieldOf("lifetime").forGetter(options -> options.lifetime)
                        )
                        .apply(builder, Options::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, Options> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.VECTOR3F,
                Options::color,
                JJKByteBufCodecs.VEC3,
                Options::direction,
                ByteBufCodecs.FLOAT,
                Options::scalar,
                ByteBufCodecs.INT,
                Options::lifetime,
                Options::new
        );

        @Override
        public @NotNull ParticleType<?> getType() {
            return JJKParticles.EMITTING_LIGHTNING.get();
        }
    }

    public static class Provider implements ParticleProvider<Options> {
        public Provider(SpriteSet ignored) {
        }

        @Override
        public EmittingLightningParticle createParticle(@NotNull EmittingLightningParticle.Options options, @NotNull ClientLevel level, double x, double y, double z,
                                                        double xSpeed, double ySpeed, double zSpeed) {
            return new EmittingLightningParticle(level, x, y, z, options);
        }
    }
}
