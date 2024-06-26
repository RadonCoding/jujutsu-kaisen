package radon.jujutsu_kaisen.client;


import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

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
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setCullState(NO_CULL)
                    .setOverlayState(OVERLAY)
                    .setWriteMaskState(COLOR_WRITE)
                    .createCompositeState(false)));
    private static final Function<ResourceLocation, RenderType> EYES = Util.memoize((pLocation) ->
            create("six_eyes", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, true, CompositeState.builder()
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

    public JJKRenderTypes(String pName, VertexFormat pFormat, VertexFormat.Mode pMode, int pBufferSize, boolean pAffectsCrumbling, boolean pSortOnUpload, Runnable pSetupState, Runnable pClearState) {
        super(pName, pFormat, pMode, pBufferSize, pAffectsCrumbling, pSortOnUpload, pSetupState, pClearState);
    }

    public static RenderType glow(ResourceLocation pLocation) {
        return GLOW.apply(pLocation);
    }

    public static RenderType energy(ResourceLocation pLocation) {
        return ENERGY.apply(pLocation);
    }

    public static @NotNull RenderType eyes(@NotNull ResourceLocation pLocation) {
        return EYES.apply(pLocation);
    }

    public static @NotNull RenderType lightning() {
        return LIGHTNING;
    }
}