package radon.jujutsu_kaisen.client.particle;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.client.MixinData;

import java.util.Locale;

public class ProjectionParticle<T extends ProjectionParticle.ProjectionParticleOptions> extends TextureSheetParticle {
    private final int entityId;

    @Nullable
    private Entity entity;

    private float yRot;
    private float yRot0;
    private float yHeadRot;
    private float yHeadRot0;

    private float position;

    protected ProjectionParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, T options) {
        super(pLevel, pX, pY, pZ);

        this.lifetime = 10;

        this.xd = pXSpeed;
        this.yd = pYSpeed;
        this.zd = pZSpeed;

        this.entityId = options.entityId();
    }

    @Override
    public void tick() {
        super.tick();

        this.alpha = 1.0F - ((float) this.age / this.lifetime);

        if (this.entity == null) {
            this.entity = this.level.getEntity(this.entityId);

            if (this.entity == null) return;

            this.yRot = this.entity.getYRot();
            this.yRot0 = this.entity.yRotO;

            if (this.entity instanceof LivingEntity living) {
                this.yHeadRot = living.yHeadRot;
                this.yHeadRot0 = living.yHeadRotO;

                this.position = living.walkAnimation.position();
            }
        }
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }

    @Override
    public void render(@NotNull VertexConsumer pBuffer, @NotNull Camera pRenderInfo, float pPartialTicks) {
        if (this.entity != null) {
            PoseStack stack = new PoseStack();

            MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();

            float yRot = this.entity.getYRot();
            float yRotO = this.entity.yRotO;

            float yHeadRot = 0.0F;
            float yHeadRotO = 0.0F;

            boolean invisible = this.entity.isInvisible();

            MixinData.isCustomWalkAnimationPosition = true;

            this.entity.setInvisible(false);

            if (this.entity instanceof LivingEntity living) {
                yHeadRot = living.yHeadRot;
                yHeadRotO = living.yHeadRotO;
                living.yHeadRot = this.yHeadRot;
                living.yHeadRotO = this.yHeadRot0;

                MixinData.walkAnimationPosition = this.position;
            }

            this.entity.setYRot(this.yRot);
            this.entity.yRotO = this.yRot0;

            RenderSystem.depthMask(false);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);

            EntityRenderDispatcher manager = Minecraft.getInstance().getEntityRenderDispatcher();
            EntityRenderer<? super Entity> renderer = manager.getRenderer(this.entity);

            Vec3 offset = renderer.getRenderOffset(this.entity, pPartialTicks);
            stack.translate((this.x - pRenderInfo.getPosition().x()) + offset.x(), (this.y - pRenderInfo.getPosition().y()) + offset.y(), (this.z - pRenderInfo.getPosition().z()) + offset.z());

            renderer.render(this.entity, 0.0F, pPartialTicks, stack, buffer, 15728880);

            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

            this.entity.yRotO = yRotO;
            this.entity.setYRot(yRot);

            if (this.entity instanceof LivingEntity living) {
                living.yHeadRotO = yHeadRotO;
                living.yHeadRot = yHeadRot;
            }

            this.entity.setInvisible(invisible);

            MixinData.isCustomWalkAnimationPosition = false;

            buffer.endBatch();
        }
    }

    public record ProjectionParticleOptions(int entityId) implements ParticleOptions {
        public static Deserializer<ProjectionParticleOptions> DESERIALIZER = new Deserializer<>() {
            public @NotNull ProjectionParticle.ProjectionParticleOptions fromCommand(@NotNull ParticleType<ProjectionParticleOptions> type, @NotNull StringReader reader) throws CommandSyntaxException {
                return new ProjectionParticleOptions(reader.readInt());
            }

            public @NotNull ProjectionParticle.ProjectionParticleOptions fromNetwork(@NotNull ParticleType<ProjectionParticleOptions> type, @NotNull FriendlyByteBuf buf) {
                return new ProjectionParticleOptions(buf.readInt());
            }
        };

        @Override
        public @NotNull ParticleType<?> getType() {
            return JJKParticles.PROJECTION.get();
        }

        @Override
        public void writeToNetwork(FriendlyByteBuf buf) {
            buf.writeInt(this.entityId);
        }

        @Override
        public @NotNull String writeToString() {
            return String.format(Locale.ROOT, "%s %d", BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()), this.entityId);
        }
    }

    public static class Provider implements ParticleProvider<ProjectionParticleOptions> {
        public Provider(SpriteSet ignored) {}

        public Particle createParticle(@NotNull ProjectionParticle.ProjectionParticleOptions pType, @NotNull ClientLevel pLevel, double pX, double pY, double pZ,
                                       double pXSpeed, double pYSpeed, double pZSpeed) {
            return new ProjectionParticle<>(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, pType);
        }
    }
}
