package radon.jujutsu_kaisen.client;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.LazyLoadedValue;
import net.neoforged.neoforge.common.util.Lazy;
import radon.jujutsu_kaisen.client.render.entity.curse.RainbowDragonSegmentRenderer;
import radon.jujutsu_kaisen.client.render.entity.effect.BodyRepelSegmentRenderer;
import radon.jujutsu_kaisen.client.render.entity.ten_shadows.GreatSerpentSegmentRenderer;
import radon.jujutsu_kaisen.client.render.entity.curse.WormCurseSegmentRenderer;
import radon.jujutsu_kaisen.entity.curse.RainbowDragonSegmentEntity;
import radon.jujutsu_kaisen.entity.curse.WormCurseSegmentEntity;
import radon.jujutsu_kaisen.entity.effect.BodyRepelSegmentEntity;
import radon.jujutsu_kaisen.entity.ten_shadows.GreatSerpentSegmentEntity;

import java.util.HashMap;
import java.util.Map;

public class JJKRenderers {
    private static final Map<ResourceLocation, Lazy<EntityRenderer<?>>> renderers = new HashMap<>();

    public static void bake(EntityRendererProvider.Context ctx) {
        renderers.put(GreatSerpentSegmentEntity.RENDERER, Lazy.of(() -> new GreatSerpentSegmentRenderer(ctx)));
        renderers.put(WormCurseSegmentEntity.RENDERER, Lazy.of(() -> new WormCurseSegmentRenderer(ctx)));
        renderers.put(RainbowDragonSegmentEntity.RENDERER, Lazy.of(() -> new RainbowDragonSegmentRenderer(ctx)));
        renderers.put(BodyRepelSegmentEntity.RENDERER, Lazy.of(() -> new BodyRepelSegmentRenderer(ctx)));
    }

    public static EntityRenderer<?> lookup(ResourceLocation location) {
        return renderers.get(location).get();
    }
}
