package radon.jujutsu_kaisen.mixin.client;

import net.minecraft.client.renderer.ItemInHandRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemInHandRenderer.class)
public interface IItemInHandRendererAccessor {
    @Accessor("mainHandHeight")
    float getMainHandHeightAccessor();

    @Accessor("oMainHandHeight")
    float getOMainHandHeightAccessor();
}
