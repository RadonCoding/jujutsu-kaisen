package radon.jujutsu_kaisen.client.particle;

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
import net.minecraft.client.renderer.RenderType;
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
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.client.MixinData;

import java.util.Locale;

public class ProjectionParticle<T extends ProjectionParticle.ProjectionParticleOptions> extends TextureSheetParticle {
    private final int entityId;
    private final float yaw;

    @Nullable
    private LivingEntity entity;

    private float position;
    private float speed;

    protected ProjectionParticle(ClientLevel pLevel, double pX, double pY, double pZ, T options) {
        super(pLevel, pX, pY, pZ);

        this.alpha = 0.5F;

        this.entityId = options.entityId();
        this.yaw = options.yaw();
    }

    @Override
    public void tick() {
        if (this.entity == null) {
            if (!(this.level.getEntity(this.entityId) instanceof LivingEntity living)) return;

            this.entity = living;

            this.position = living.walkAnimation.position();
            this.speed = living.walkAnimation.speed();
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
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }

    @Override
    public void render(@NotNull VertexConsumer pBuffer, @NotNull Camera pRenderInfo, float pPartialTicks) {
        if (this.entity != null) {
            PoseStack stack = new PoseStack();

            float yRot = this.entity.getYRot();
            float yRotO = this.entity.yRotO;

            float yHeadRot = this.entity.yHeadRot;
            float yHeadRotO = this.entity.yHeadRotO;

            float yBodyRot = this.entity.yBodyRot;
            float yBodyRotO = this.entity.yBodyRotO;

            boolean invisible = this.entity.isInvisible();

            this.entity.setInvisible(false);

            MixinData.isCustomWalkAnimation = true;
            MixinData.walkAnimationPosition = this.position;
            MixinData.walkAnimationSpeed = this.speed;

            this.entity.setYRot(this.yaw);
            this.entity.yRotO = this.yaw;

            this.entity.yHeadRot = this.yaw;
            this.entity.yHeadRotO = this.yaw;

            this.entity.yBodyRot = this.yaw;
            this.entity.yBodyRotO = this.yaw;

            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);

            EntityRenderDispatcher manager = Minecraft.getInstance().getEntityRenderDispatcher();
            EntityRenderer<? super Entity> renderer = manager.getRenderer(this.entity);

            MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();

            Vec3 offset = renderer.getRenderOffset(this.entity, pPartialTicks);
            stack.translate((this.x - pRenderInfo.getPosition().x) + offset.x, (this.y - pRenderInfo.getPosition().y) + offset.y, (this.z - pRenderInfo.getPosition().z) + offset.z);
            renderer.render(this.entity, 0.0F, pPartialTicks, stack, buffer, manager.getPackedLightCoords(this.entity, pPartialTicks));

            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

            this.entity.yBodyRotO = yBodyRotO;
            this.entity.yBodyRot = yBodyRot;

            this.entity.yHeadRotO = yHeadRotO;
            this.entity.yHeadRot = yHeadRot;

            this.entity.yRotO = yRotO;
            this.entity.setYRot(yRot);

            this.entity.setInvisible(invisible);

            MixinData.isCustomWalkAnimation = false;

            buffer.endBatch();
        }
    }

    public record ProjectionParticleOptions(int entityId, float yaw) implements ParticleOptions {
        public static Deserializer<ProjectionParticleOptions> DESERIALIZER = new Deserializer<>() {
            public @NotNull ProjectionParticle.ProjectionParticleOptions fromCommand(@NotNull ParticleType<ProjectionParticleOptions> type, @NotNull StringReader reader) throws CommandSyntaxException {
                return new ProjectionParticleOptions(reader.readInt(), reader.readFloat());
            }

            public @NotNull ProjectionParticle.ProjectionParticleOptions fromNetwork(@NotNull ParticleType<ProjectionParticleOptions> type, @NotNull FriendlyByteBuf buf) {
                return new ProjectionParticleOptions(buf.readInt(), buf.readFloat());
            }
        };

        @Override
        public @NotNull ParticleType<?> getType() {
            return JJKParticles.PROJECTION.get();
        }

        @Override
        public void writeToNetwork(FriendlyByteBuf buf) {
            buf.writeInt(this.entityId);
            buf.writeFloat(this.yaw);
        }

        @Override
        public @NotNull String writeToString() {
            return String.format(Locale.ROOT, "%s %d %f %f", BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()), this.entityId, this.yaw);
        }
    }

    public static class Provider implements ParticleProvider<ProjectionParticleOptions> {
        public Provider(SpriteSet ignored) {}

        public Particle createParticle(@NotNull ProjectionParticle.ProjectionParticleOptions pType, @NotNull ClientLevel pLevel, double pX, double pY, double pZ,
                                       double pXSpeed, double pYSpeed, double pZSpeed) {
            return new ProjectionParticle<>(pLevel, pX, pY, pZ, pType);
        }
    }
}
