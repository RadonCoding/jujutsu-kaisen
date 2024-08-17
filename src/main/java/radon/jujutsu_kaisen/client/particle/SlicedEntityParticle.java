package radon.jujutsu_kaisen.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import radon.jujutsu_kaisen.client.FakeEntityRenderer;
import radon.jujutsu_kaisen.client.particle.registry.JJKParticles;
import radon.jujutsu_kaisen.client.slice.CutModelUtil;
import radon.jujutsu_kaisen.client.slice.GJK;
import radon.jujutsu_kaisen.client.slice.RigidBody;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

// https://github.com/Alcatergit/Hbm-s-Nuclear-Tech-GIT/blob/Custom-1.12.2/src/main/java/com/hbm/render/util/ModelRendererUtil.java
public class SlicedEntityParticle extends TextureSheetParticle {
    private final int entityId;
    private final Vector3f plane;
    private final float distance;

    @Nullable
    private LivingEntity entity;

    @Nullable
    private FakeEntityRenderer renderer;

    private final List<RigidBody> parts = new ArrayList<>();

    protected SlicedEntityParticle(ClientLevel pLevel, double pX, double pY, double pZ, Options options) {
        super(pLevel, pX, pY, pZ);

        this.entityId = options.entityId;
        this.plane = options.plane;
        this.distance = options.distance;

        this.lifetime = 60 * 20;
    }

    @Override
    public @NotNull AABB getRenderBoundingBox(float partialTicks) {
        return AABB.INFINITE;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.entity == null) {
            if (this.level.getEntity(this.entityId) instanceof LivingEntity living) {
                this.entity = living;

                this.renderer = new FakeEntityRenderer(this.entity);
            }
        }

        for (RigidBody part : this.parts) part.tick();
    }

    private static void generateChunks(List<List<RigidBody.CutModelData>> chunks, List<RigidBody.CutModelData> toSort) {
        GJK.margin = 0.01F;
        List<RigidBody.CutModelData> chunk = new ArrayList<>();
        boolean removed;

        while (!toSort.isEmpty()) {
            removed = false;

            List<RigidBody.CutModelData> toAdd = new ArrayList<>();

            for (RigidBody.CutModelData a : chunk) {
                Iterator<RigidBody.CutModelData> iter = toSort.iterator();

                while (iter.hasNext()) {
                    RigidBody.CutModelData b = iter.next();

                    if (b.collider.localBox.inflate(0.01F).intersects(a.collider.localBox) &&
                            GJK.collidesAny(null, null, a.collider, b.collider)) {
                        removed = true;
                        toAdd.add(b);
                        iter.remove();
                    }
                }
            }
            chunk.addAll(toAdd);

            if (!removed) {
                if (!chunk.isEmpty()){
                    chunks.add(chunk);
                    chunk = new ArrayList<>();
                }
                chunk.add(toSort.removeFirst());
            }
        }
        if (!chunk.isEmpty()){
            chunks.add(chunk);
        }
        GJK.margin = 0.0F;
    }

    @Override
    public void render(@NotNull VertexConsumer pBuffer, @NotNull Camera pRenderInfo, float pPartialTicks) {

    }

    public void actuallyRender(float partialTicks) {
        if (this.entity == null || this.renderer == null) return;

        if (this.parts.isEmpty()) {
            this.renderer.setup(() -> {
                List<RigidBody.CutModelData> top = new ArrayList<>();
                List<RigidBody.CutModelData> bottom = new ArrayList<>();

                CutModelUtil.collect(this.renderer, this.plane, this.distance, partialTicks, top, bottom);

                List<List<RigidBody.CutModelData>> chunks = new ArrayList<>();
                generateChunks(chunks, top);
                generateChunks(chunks, bottom);

                double d0 = Mth.lerp(partialTicks, this.xo, this.x);
                double d1 = Mth.lerp(partialTicks, this.yo, this.y);
                double d2 = Mth.lerp(partialTicks, this.zo, this.z);

                for (List<RigidBody.CutModelData> chunk : chunks) {
                    RigidBody part = new RigidBody(this.level, d0, d1, d2);
                    part.addChunk(chunk);

                    float direction = chunk.getFirst().flip ? -1.0F : 1.0F;
                    part.impulseVelocityDirect(new Vec3(this.plane.x * direction,
                            this.plane.y * direction,
                            this.plane.z * direction), part.globalCentroid);

                    this.parts.add(part);
                }

                for (RigidBody part : this.parts) {
                    part.addParts(this.parts);
                }
            });
        }

        if (this.parts.isEmpty()) return;

        Minecraft mc = Minecraft.getInstance();
        EntityRenderDispatcher dispatcher = mc.getEntityRenderDispatcher();

        int packedLight = dispatcher.getPackedLightCoords(this.entity, partialTicks);

        for (RigidBody part : this.parts) part.render(packedLight, partialTicks);
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }

    public record Options(int entityId, Vector3f plane, float distance) implements ParticleOptions {
        public static final MapCodec<Options> CODEC = RecordCodecBuilder.mapCodec(
                builder -> builder.group(
                                Codec.INT.fieldOf("entityId").forGetter(options -> options.entityId),
                                ExtraCodecs.VECTOR3F.fieldOf("plane").forGetter(options -> options.plane),
                                Codec.FLOAT.fieldOf("distance").forGetter(options -> options.distance)
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
                Options::new
        );

        @Override
        public @NotNull ParticleType<?> getType() {
            return JJKParticles.SLICE.get();
        }
    }

    public static class Provider implements ParticleProvider<Options> {
        public Provider(SpriteSet ignored) {
        }

        public Particle createParticle(@NotNull Options pType, @NotNull ClientLevel pLevel, double pX, double pY, double pZ,
                                       double pXSpeed, double pYSpeed, double pZSpeed) {
            return new SlicedEntityParticle(pLevel, pX, pY, pZ, pType);
        }
    }
}

