package radon.jujutsu_kaisen.client;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.util.Lazy;
import radon.jujutsu_kaisen.client.render.entity.curse.RainbowDragonSegmentRenderer;
import radon.jujutsu_kaisen.client.render.entity.effect.BodyRepelSegmentRenderer;
import radon.jujutsu_kaisen.client.render.entity.ten_shadows.GreatSerpentSegmentRenderer;
import radon.jujutsu_kaisen.client.render.entity.curse.WormCurseSegmentRenderer;
import radon.jujutsu_kaisen.entity.registry.JJKEntities;

import java.util.HashMap;
import java.util.Map;

public class JJKPartEntityRenderDispatcher {
    private static final Map<Holder<EntityType<?>>, EntityRenderer<?>> renderers = new HashMap<>();

    public static void bake(EntityRendererProvider.Context ctx) {
        renderers.put(JJKEntities.GREAT_SERPENT, new GreatSerpentSegmentRenderer(ctx));
        renderers.put(JJKEntities.WOOD_SEGMENT, new WormCurseSegmentRenderer(ctx));
        renderers.put(JJKEntities.RAINBOW_DRAGON, new RainbowDragonSegmentRenderer(ctx));
        renderers.put(JJKEntities.BODY_REPEL, new BodyRepelSegmentRenderer(ctx));
    }

    public static EntityRenderer<?> lookup(EntityType<?> type) {
        return renderers.get(Holder.direct(type));
    }
}
