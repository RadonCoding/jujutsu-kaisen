package radon.jujutsu_kaisen.client.particle;

import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.math.Axis;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class FireParticle extends TextureSheetParticle {
    private final float size;

    protected FireParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, Options options) {
        super(pLevel, pX, pY, pZ);

        this.lifetime = options.lifetime();

        this.xd = pXSpeed;
        this.yd = pYSpeed;
        this.zd = pZSpeed;

        this.size = Math.max(options.scalar(), (this.random.nextFloat() - 0.5F) * options.scalar());

        this.quadSize = this.size * (1.0F - ((float) this.age / this.lifetime));

        this.hasPhysics = false;
    }

    @Override
    public void tick() {
        super.tick();

        this.quadSize = this.size * (1.0F - ((float) this.age / this.lifetime));
    }

    private void fireVertex(PoseStack.Pose pMatrixEntry, VertexConsumer pBuffer, float pX, float pY, float pZ, float pTexU, float pTexV) {
        pBuffer.vertex(pMatrixEntry.pose(), pX, pY, pZ)
                .color(255, 255, 255, 255)
                .uv(pTexU, pTexV)
                .overlayCoords(0, 10)
                .uv2(LightTexture.FULL_BRIGHT)
                .normal(pMatrixEntry, 0.0F, 1.0F, 0.0F)
                .endVertex();
    }

    @Override
    public void render(@NotNull VertexConsumer pBuffer, @NotNull Camera pRenderInfo, float pPartialTicks) {
        Minecraft mc = Minecraft.getInstance();

        PoseStack stack = new PoseStack();

        double d0 = Mth.lerp(pPartialTicks, this.xo, this.x);
        double d1 = Mth.lerp(pPartialTicks, this.yo, this.y);
        double d2 = Mth.lerp(pPartialTicks, this.zo, this.z);

        Vec3 cam = pRenderInfo.getPosition();

        stack.pushPose();
        stack.translate(d0 - cam.x, d1 - cam.y, d2 - cam.z);

        TextureAtlasSprite fire0 = ModelBakery.FIRE_0.sprite();
        TextureAtlasSprite fire1 = ModelBakery.FIRE_1.sprite();
        float f = this.quadSize * 1.4F;
        stack.scale(f, f, f);
        float f1 = 0.5F;
        float f3 = this.quadSize / f;
        float f4 = 0.0F;
        stack.mulPose(Axis.YN.rotationDegrees(pRenderInfo.getYRot()));
        stack.translate(0.0F, 0.0F, -0.3F + (float) ((int) f3) * 0.02F);
        float f5 = 0.0F;
        int i = 0;

        VertexConsumer consumer = mc.renderBuffers().bufferSource().getBuffer(Sheets.cutoutBlockSheet());

        for (PoseStack.Pose posestack$pose = stack.last(); f3 > 0.0F; ++i) {
            TextureAtlasSprite sprite = i % 2 == 0 ? fire0 : fire1;
            float f6 = sprite.getU0();
            float f7 = sprite.getV0();
            float f8 = sprite.getU1();
            float f9 = sprite.getV1();

            if (i / 2 % 2 == 0) {
                float f10 = f8;
                f8 = f6;
                f6 = f10;
            }

            this.fireVertex(posestack$pose, consumer, f1 - 0.0F, 0.0F - f4, f5, f8, f9);
            this.fireVertex(posestack$pose, consumer, -f1 - 0.0F, 0.0F - f4, f5, f6, f9);
            this.fireVertex(posestack$pose, consumer, -f1 - 0.0F, 1.4F - f4, f5, f6, f7);
            this.fireVertex(posestack$pose, consumer, f1 - 0.0F, 1.4F - f4, f5, f8, f7);
            f3 -= 0.45F;
            f4 -= 0.45F;
            f1 *= 0.9F;
            f5 += 0.03F;
        }
        stack.popPose();

        mc.renderBuffers().bufferSource().endBatch();
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return JJKParticleRenderTypes.CUSTOM;
    }

    public record Options(float scalar, boolean glow, int lifetime) implements ParticleOptions {
        public static final MapCodec<Options> CODEC = RecordCodecBuilder.mapCodec(
                builder -> builder.group(
                                Codec.FLOAT.fieldOf("scalar").forGetter(options -> options.scalar),
                                Codec.BOOL.fieldOf("glow").forGetter(options -> options.glow),
                                Codec.INT.fieldOf("lifetime").forGetter(options -> options.lifetime)
                        )
                        .apply(builder, Options::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, Options> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.FLOAT,
                Options::scalar,
                ByteBufCodecs.BOOL,
                Options::glow,
                ByteBufCodecs.INT,
                Options::lifetime,
                Options::new
        );

        @Override
        public @NotNull ParticleType<?> getType() {
            return JJKParticles.FIRE.get();
        }
    }

    public static class Provider implements ParticleProvider<Options> {
        public Provider(SpriteSet ignored) {
        }

        @Override
        public FireParticle createParticle(@NotNull FireParticle.Options options, @NotNull ClientLevel level, double x, double y, double z,
                                           double xSpeed, double ySpeed, double zSpeed) {
            return new FireParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, options);
        }
    }
}
