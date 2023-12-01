package radon.jujutsu_kaisen.client;

import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.TheEndPortalRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.client.render.block.SkyRenderer;

import java.util.function.Function;

public class JJKRenderTypes extends RenderType {
    private static final Function<ResourceLocation, RenderType> TRANSPARENT = Util.memoize((pLocation) ->
            create("glow", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, false, CompositeState.builder()
                    .setShaderState(RENDERTYPE_ENERGY_SWIRL_SHADER)
                    .setTextureState(new TextureStateShard(pLocation, false, false))
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setCullState(NO_CULL)
                    .setLightmapState(LIGHTMAP)
                    .setOverlayState(OVERLAY)
                    .createCompositeState(false)));
    private static final Function<ResourceLocation, RenderType> GLOW = Util.memoize((pLocation) ->
            create("glow", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, false, CompositeState.builder()
                    .setShaderState(RENDERTYPE_ENERGY_SWIRL_SHADER)
                    .setTextureState(new TextureStateShard(pLocation, false, false))
                    .setTransparencyState(ADDITIVE_TRANSPARENCY)
                    .setCullState(NO_CULL)
                    .setLightmapState(LIGHTMAP)
                    .setOverlayState(OVERLAY)
                    .createCompositeState(false)));
    private static final Function<ResourceLocation, RenderType> EYES = Util.memoize((pLocation) ->
            create("six_eyes", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, true, CompositeState.builder()
                    .setShaderState(RENDERTYPE_EYES_SHADER)
                    .setTextureState(new TextureStateShard(pLocation, false, false))
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setWriteMaskState(COLOR_WRITE)
                    .createCompositeState(false)));
    private static final RenderType UNLIMITED_VOID = create("unlimited_void", DefaultVertexFormat.POSITION, VertexFormat.Mode.QUADS, 256,
            false, false, RenderType.CompositeState.builder()
                    .setShaderState(new ShaderStateShard(JJKShaders::getUnlimitedVoidShader))
                    .setTextureState(RenderStateShard.MultiTextureStateShard.builder().add(TheEndPortalRenderer.END_SKY_LOCATION, false, false)
                            .add(TheEndPortalRenderer.END_PORTAL_LOCATION, false, false).build())
                    .createCompositeState(false));
    private static final RenderType SKY = create("sky", DefaultVertexFormat.POSITION, VertexFormat.Mode.QUADS, 256,
            false, false, RenderType.CompositeState.builder()
                    .setShaderState(new ShaderStateShard(JJKShaders::getSkyShader))
                    .setTextureState(new EmptyTextureStateShard(() -> {
                        TextureTarget target = SkyHandler.getTarget();

                        if (target != null) {
                            RenderSystem.setShaderTexture(0, target.getColorTextureId());
                        } else {
                            RenderSystem.setShaderTexture(0, 0);
                        }
                    }, () -> {}))
                    .createCompositeState(false));
    private static final RenderType LIGHTNING = create("lightning", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, 256,
            false, true, RenderType.CompositeState.builder()
                    .setShaderState(RENDERTYPE_LIGHTNING_SHADER)
                    .setTransparencyState(LIGHTNING_TRANSPARENCY)
                    .createCompositeState(false));
    private static final Function<ResourceLocation, RenderType> BURNT = Util.memoize((pLocation) -> create("burnt", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256,
            false, true, RenderType.CompositeState.builder()
                    .setTextureState(new RenderStateShard.TextureStateShard(pLocation, false, false))
                    .setShaderState(RenderType.RENDERTYPE_ENTITY_CUTOUT_SHADER)
                    .setTransparencyState(NO_TRANSPARENCY)
                    .setLightmapState(LIGHTMAP)
                    .setOverlayState(OVERLAY)
                    .createCompositeState(true)));
    private static final Function<ResourceLocation, RenderType> CRACK = Util.memoize((pLocation) -> create("crack", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256,
            false, true, RenderType.CompositeState.builder()
                    .setTextureState(new RenderStateShard.TextureStateShard(pLocation, false, false))
                    .setShaderState(RenderType.RENDERTYPE_ENTITY_CUTOUT_SHADER)
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setDepthTestState(EQUAL_DEPTH_TEST)
                    .setCullState(NO_CULL)
                    .setLightmapState(LIGHTMAP)
                    .setOverlayState(OVERLAY)
                    .createCompositeState(false)));

    public JJKRenderTypes(String pName, VertexFormat pFormat, VertexFormat.Mode pMode, int pBufferSize, boolean pAffectsCrumbling, boolean pSortOnUpload, Runnable pSetupState, Runnable pClearState) {
        super(pName, pFormat, pMode, pBufferSize, pAffectsCrumbling, pSortOnUpload, pSetupState, pClearState);
    }

    public static RenderType transparent(ResourceLocation pLocation) {
        return TRANSPARENT.apply(pLocation);
    }

    public static RenderType glow(ResourceLocation pLocation) {
        return GLOW.apply(pLocation);
    }

    public static @NotNull RenderType eyes(@NotNull ResourceLocation pLocation) {
        return EYES.apply(pLocation);
    }

    public static RenderType unlimitedVoid() {
        return UNLIMITED_VOID;
    }

    public static RenderType sky() {
        return SKY;
    }

    public static @NotNull RenderType lightning() {
        return LIGHTNING;
    }

    public static RenderType burnt(ResourceLocation pLocation) {
        return BURNT.apply(pLocation);
    }

    public static RenderType crack(ResourceLocation pLocation) {
        return CRACK.apply(pLocation);
    }
}