package radon.jujutsu_kaisen.client.particle;


import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.renderer.RenderType;
import radon.jujutsu_kaisen.client.FakeEntityRenderer;
import radon.jujutsu_kaisen.client.particle.registry.JJKParticles;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.registry.JJKAbilities;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;

public class ProjectionParticle extends TextureSheetParticle {
    private final int entityId;

    @Nullable
    private Entity entity;

    private final float yaw;

    @Nullable
    private FakeEntityRenderer renderer;

    protected ProjectionParticle(ClientLevel pLevel, double pX, double pY, double pZ, Options options) {
        super(pLevel, pX, pY, pZ);

        this.entityId = options.entityId;

        this.yaw = options.yaw;
    }

    @Override
    public void tick() {
        if (this.entity == null) {
            this.entity = this.level.getEntity(this.entityId);

            if (this.entity == null) return;

            this.renderer = new FakeEntityRenderer(this.entity);
            this.renderer.setFullRotation(this.yaw, 0.0F);
            this.renderer.setAlpha(0.25F);
        } else {
            IJujutsuCapability cap = this.entity.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap == null) return;

            IAbilityData data = cap.getAbilityData();

            if (!data.isChanneling(JJKAbilities.PROJECTION_SORCERY.get())) {
                this.remove();
            }
        }
    }

    @Override
    public void render(@NotNull VertexConsumer pBuffer, @NotNull Camera pRenderInfo, float pPartialTicks) {
        if (this.renderer == null) return;

        this.renderer.render(this.getPos(), pPartialTicks);
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }

    public record Options(int entityId, float yaw) implements ParticleOptions {
        public static final MapCodec<Options> CODEC = RecordCodecBuilder.mapCodec(
                builder -> builder.group(
                                Codec.INT.fieldOf("entityId").forGetter(options -> options.entityId),
                                Codec.FLOAT.fieldOf("yaw").forGetter(options -> options.yaw)
                        )
                        .apply(builder, Options::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, Options> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.INT,
                Options::entityId,
                ByteBufCodecs.FLOAT,
                Options::yaw,
                Options::new
        );

        @Override
        public @NotNull ParticleType<?> getType() {
            return JJKParticles.PROJECTION.get();
        }
    }

    public static class Provider implements ParticleProvider<Options> {
        public Provider(SpriteSet ignored) {}

        public Particle createParticle(@NotNull Options pType, @NotNull ClientLevel pLevel, double pX, double pY, double pZ,
                                       double pXSpeed, double pYSpeed, double pZSpeed) {
            return new ProjectionParticle(pLevel, pX, pY, pZ, pType);
        }
    }
}
