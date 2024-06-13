package radon.jujutsu_kaisen.client.particle;

import com.mojang.blaze3d.shaders.AbstractUniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import radon.jujutsu_kaisen.client.FakeEntityRenderer;
import radon.jujutsu_kaisen.client.particle.registry.JJKParticles;
import radon.jujutsu_kaisen.client.post.PostEffectHandler;
import radon.jujutsu_kaisen.client.post.SelectivePostChain;
import radon.jujutsu_kaisen.client.post.SlicedEntityPostEffect;

public class SlicedEntityParticle extends TextureSheetParticle {
    private final int entityId;
    @Nullable
    private Entity entity;
    private final Vector3f plane;
    private final float distance;
    private final float direction;
    
    @Nullable
    private FakeEntityRenderer renderer;

    protected SlicedEntityParticle(ClientLevel pLevel, double pX, double pY, double pZ, Options options) {
        super(pLevel, pX, pY, pZ);

        this.entityId = options.entityId;
        this.plane = options.plane;
        this.distance = options.distance;
        this.direction = options.direction;
    }

    @Override
    public @NotNull AABB getRenderBoundingBox(float partialTicks) {
        if (this.entity == null) return super.getRenderBoundingBox(partialTicks);

        return AABB.ofSize(this.getPos(), this.entity.getBbWidth(), this.entity.getBbHeight(), this.entity.getBbWidth());
    }

    @Override
    public void tick() {
        if (this.entity == null) {
            this.entity = this.level.getEntity(this.entityId);

            if (this.entity == null) return;

            this.renderer = new FakeEntityRenderer(this.entity);

            // Temporary
            this.renderer.setFullRotation(0.0F, 0.0F);
        }
    }

    @Override
    public void render(@NotNull VertexConsumer pBuffer, @NotNull Camera pRenderInfo, float pPartialTicks) {
        if (this.renderer == null) return;

        SlicedEntityPostEffect effect = new SlicedEntityPostEffect();
        SelectivePostChain postChain = effect.getPostChain();

        for (AbstractUniform uniform : postChain.getUniforms("Plane")) {
            uniform.set(new float[] { this.plane.x, this.plane.y, this.plane.z, this.distance });
        }
        for (AbstractUniform uniform : postChain.getUniforms("Direction")) {
            uniform.set(this.direction);
        }

        double d0 = Mth.lerp(pPartialTicks, this.xo, this.x);
        double d1 = Mth.lerp(pPartialTicks, this.yo, this.y);
        double d2 = Mth.lerp(pPartialTicks, this.zo, this.z);

        PostEffectHandler.bind(effect);
        this.renderer.render(new Vec3(d0, d1, d2), pPartialTicks);
        PostEffectHandler.unbind(effect);

        postChain.setMatrices(new Matrix4f(RenderSystem.getModelViewMatrix()), new Matrix4f(RenderSystem.getProjectionMatrix()));
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }

    public record Options(int entityId, Vector3f plane, float distance, float direction) implements ParticleOptions {
        public static final MapCodec<Options> CODEC = RecordCodecBuilder.mapCodec(
                builder -> builder.group(
                                Codec.INT.fieldOf("entityId").forGetter(options -> options.entityId),
                                ExtraCodecs.VECTOR3F.fieldOf("plane").forGetter(options -> options.plane),
                                Codec.FLOAT.fieldOf("distance").forGetter(options -> options.distance),
                                Codec.FLOAT.fieldOf("direction").forGetter(options -> options.direction)
                        )
                        .apply(builder, Options::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, Options> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.INT,
                Options::entityId,
                ByteBufCodecs.VECTOR3F,
                Options::plane,
                ByteBufCodecs.FLOAT,
                Options::distance,
                ByteBufCodecs.FLOAT,
                Options::direction,
                Options::new
        );

        @Override
        public @NotNull ParticleType<?> getType() {
            return JJKParticles.SLICE.get();
        }
    }

    public static class Provider implements ParticleProvider<Options> {
        public Provider(SpriteSet ignored) {}

        public Particle createParticle(@NotNull Options pType, @NotNull ClientLevel pLevel, double pX, double pY, double pZ,
                                       double pXSpeed, double pYSpeed, double pZSpeed) {
            return new SlicedEntityParticle(pLevel, pX, pY, pZ, pType);
        }
    }
}

