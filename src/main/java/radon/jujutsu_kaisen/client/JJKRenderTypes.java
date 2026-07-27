package radon.jujutsu_kaisen.client;


import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.OptionalDouble;
import java.util.function.Function;

public class JJKRenderTypes extends RenderType {
    private static final Function<ResourceLocation, RenderType> GLOW = Util.memoize((pLocation) ->
            create("glow", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, false, CompositeState.builder()
                    .setShaderState(RENDERTYPE_ENERGY_SWIRL_SHADER)
                    .setTextureState(new TextureStateShard(pLocation, false, false))
                    .setTransparencyState(ADDITIVE_TRANSPARENCY)
                    .setCullState(NO_CULL)
                    .setLightmapState(LIGHTMAP)
                    .setOverlayState(OVERLAY)
                    .createCompositeState(false)));
    private static final Function<ResourceLocation, RenderType> ENERGY = Util.memoize((pLocation) ->
            create("energy", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, true, CompositeState.builder()
                    .setShaderState(RENDERTYPE_BEACON_BEAM_SHADER)
                    .setTextureState(new TextureStateShard(pLocation, false, false))
                    .setTransparencyState(ADDITIVE_TRANSPARENCY)
                    .setCullState(NO_CULL)
                    .setOverlayState(OVERLAY)
                    .setWriteMaskState(COLOR_WRITE)
                    .createCompositeState(false)));
    private static final Function<ResourceLocation, RenderType> EYES = Util.memoize((pLocation) ->
            create("eyes", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, true, CompositeState.builder()
                    .setShaderState(RENDERTYPE_EYES_SHADER)
                    .setTextureState(new TextureStateShard(pLocation, false, false))
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setWriteMaskState(COLOR_WRITE)
                    .createCompositeState(false)));
    private static final RenderType LIGHTNING = create("lightning", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, 1536,
            false, true, RenderType.CompositeState.builder()
                    .setShaderState(RENDERTYPE_LIGHTNING_SHADER)
                    .setWriteMaskState(COLOR_DEPTH_WRITE)
                    .setTransparencyState(LIGHTNING_TRANSPARENCY)
                    .setOutputState(WEATHER_TARGET)
                    .createCompositeState(false));
    private static final Function<TextureTarget, RenderType> SKYBOX = Util.memoize((target) ->
            create("skybox", DefaultVertexFormat.POSITION, VertexFormat.Mode.QUADS, 256,
                    false, false, RenderType.CompositeState.builder()
                            .setShaderState(new ShaderStateShard(JJKShaders::getSkyShader))
                            .setTextureState(new EmptyTextureStateShard(() ->
                                    RenderSystem.setShaderTexture(0, target.getColorTextureId()), () -> {}))
                            .createCompositeState(false)));
    private static final RenderType SPIDERWEB = create("spiderweb",
            DefaultVertexFormat.POSITION_COLOR,
            VertexFormat.Mode.TRIANGLES,
            1536,
            false,
            false,
            RenderType.CompositeState.builder()
                    .setShaderState(POSITION_COLOR_SHADER)
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setLayeringState(VIEW_OFFSET_Z_LAYERING)
                    .setOutputState(WEATHER_TARGET)
                    .setWriteMaskState(COLOR_WRITE)
                    .setCullState(NO_CULL)
                    .createCompositeState(false));
    private static final ResourceLocation CLOUDS_LOCATION = new ResourceLocation("textures/environment/clouds.png");
    public static final RenderType CLOUDS = RenderType.create(
            "clouds",
            DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL,
            VertexFormat.Mode.QUADS,
            786432,
            false,
            false,
            RenderType.CompositeState.builder()
                    .setShaderState(new RenderType.ShaderStateShard(GameRenderer::getRendertypeCloudsShader))
                    .setTextureState(new RenderStateShard.TextureStateShard(CLOUDS_LOCATION, false, false))
                    .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                    .setCullState(RenderStateShard.NO_CULL)
                    .setWriteMaskState(RenderStateShard.COLOR_DEPTH_WRITE)
                    .createCompositeState(true)
    );
    public static final RenderType CLOUDS_DEPTH_ONLY = RenderType.create(
            "clouds_depth_only",
            DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL,
            VertexFormat.Mode.QUADS,
            786432,
            false,
            false,
            RenderType.CompositeState.builder()
                    .setShaderState(new RenderType.ShaderStateShard(GameRenderer::getRendertypeCloudsShader))
                    .setTextureState(new RenderStateShard.TextureStateShard(CLOUDS_LOCATION, false, false))
                    .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                    .setCullState(RenderStateShard.NO_CULL)
                    .setWriteMaskState(RenderStateShard.DEPTH_WRITE)
                    .createCompositeState(true)
    );

    public JJKRenderTypes(String pName, VertexFormat pFormat, VertexFormat.Mode pMode, int pBufferSize, boolean pAffectsCrumbling, boolean pSortOnUpload, Runnable pSetupState, Runnable pClearState) {
        super(pName, pFormat, pMode, pBufferSize, pAffectsCrumbling, pSortOnUpload, pSetupState, pClearState);
    }

    public static RenderType glow(ResourceLocation location) {
        return GLOW.apply(location);
    }

    public static RenderType energy(ResourceLocation location) {
        return ENERGY.apply(location);
    }

    public static @NotNull RenderType eyes(@NotNull ResourceLocation pLocation) {
        return EYES.apply(pLocation);
    }

    public static @NotNull RenderType lightning() {
        return LIGHTNING;
    }

    public static RenderType skybox(TextureTarget target) {
        return SKYBOX.apply(target);
    }

    public static RenderType spiderweb() {
        return SPIDERWEB;
    }

    public static @NotNull RenderType clouds() {
        return CLOUDS;
    }

    public static @NotNull RenderType cloudsDepthOnly() {
        return CLOUDS_DEPTH_ONLY;
    }
}